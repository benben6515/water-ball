package tw.waterballsa.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.waterballsa.dto.SessionInfoResponse;
import tw.waterballsa.dto.TokenResponse;
import tw.waterballsa.model.OAuthProviderLink;
import tw.waterballsa.model.OAuthProviderLink.ProviderType;
import tw.waterballsa.model.User;
import tw.waterballsa.repository.OAuthProviderLinkRepository;
import tw.waterballsa.repository.UserRepository;
import tw.waterballsa.security.JwtUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Authentication service handling OAuth registration, login, and session management.
 * Implements automatic account merging by email address.
 *
 * @author Water Ball SA
 */
@Service
public class AuthService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final String SESSION_KEY_PREFIX = "session:";
    private static final long SESSION_TTL_SECONDS = 604800; // 7 days

    private final UserRepository userRepository;
    private final OAuthProviderLinkRepository oauthProviderLinkRepository;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public AuthService(
            UserRepository userRepository,
            OAuthProviderLinkRepository oauthProviderLinkRepository,
            JwtUtil jwtUtil,
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.userRepository = userRepository;
        this.oauthProviderLinkRepository = oauthProviderLinkRepository;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Handle OAuth callback after successful authentication with provider.
     * Implements automatic account merging by email.
     *
     * Flow:
     * 1. Check if OAuth account already linked → return existing user
     * 2. Check if user exists with same email → link provider to existing user (account merge)
     * 3. Create new user and link provider
     *
     * @param providerType OAuth provider type (google, facebook)
     * @param providerUserId unique user ID from OAuth provider
     * @param providerEmail email from OAuth provider
     * @param name user's name from OAuth provider
     * @return authentication result with tokens and user info
     */
    @Transactional
    public OAuthResult handleOAuthCallback(
            ProviderType providerType,
            String providerUserId,
            String providerEmail,
            String name
    ) {
        logger.info("Processing OAuth callback: provider={}, providerUserId={}, email={}",
                providerType, providerUserId, providerEmail);

        // Step 1: Check if this OAuth account is already linked
        Optional<OAuthProviderLink> existingLink = oauthProviderLinkRepository
                .findByProviderTypeAndProviderUserId(providerType, providerUserId);

        if (existingLink.isPresent()) {
            User user = existingLink.get().getUser();
            logger.info("Existing OAuth link found for user: userId={}", user.getUserId());
            return createAuthResult(user, false);
        }

        // Step 2: Check if user exists with same email (account merging)
        String emailHash = computeEmailHash(providerEmail);
        Optional<User> existingUser = userRepository.findByEmailHash(emailHash);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            logger.info("Email match found - merging accounts: userId={}, email={}", user.getUserId(), providerEmail);

            // Link this OAuth provider to existing user
            OAuthProviderLink newLink = new OAuthProviderLink(user, providerType, providerUserId, providerEmail);
            oauthProviderLinkRepository.save(newLink);

            logger.info("OAuth provider linked to existing user: userId={}, provider={}",
                    user.getUserId(), providerType);

            return createAuthResult(user, false);
        }

        // Step 3: Create new user and link OAuth provider
        logger.info("Creating new user: email={}", providerEmail);

        String nickname = extractNickname(name, providerEmail);
        User newUser = new User();
        newUser.setNickname(nickname);
        newUser.setEmail(providerEmail);
        newUser.setLevel(1);

        User savedUser = userRepository.save(newUser);

        // Create OAuth provider link
        OAuthProviderLink newLink = new OAuthProviderLink(savedUser, providerType, providerUserId, providerEmail);
        oauthProviderLinkRepository.save(newLink);

        logger.info("New user created: userId={}, nickname={}, provider={}",
                savedUser.getUserId(), nickname, providerType);

        return createAuthResult(savedUser, true);
    }

    /**
     * Create authentication result with JWT tokens and session.
     *
     * @param user authenticated user
     * @param isNewUser whether this is a newly created user
     * @return OAuth result with tokens
     */
    private OAuthResult createAuthResult(User user, boolean isNewUser) {
        // Generate JWT tokens
        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        // Store session in Redis
        storeSession(user.getUserId(), user);

        return new OAuthResult(
                accessToken,
                refreshToken,
                user,
                isNewUser,
                jwtUtil.getAccessTokenExpirationInSeconds()
        );
    }

    /**
     * Get session information for authenticated user.
     *
     * @param userId user ID from JWT
     * @return session info response
     */
    public SessionInfoResponse getSession(Long userId) {
        // Try to get from Redis cache first
        String sessionKey = SESSION_KEY_PREFIX + userId;
        String cachedSession = redisTemplate.opsForValue().get(sessionKey);

        if (cachedSession != null) {
            try {
                return objectMapper.readValue(cachedSession, SessionInfoResponse.class);
            } catch (JsonProcessingException e) {
                logger.warn("Failed to deserialize cached session for userId={}", userId, e);
            }
        }

        // Fallback to database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SessionInfoResponse response = buildSessionInfoResponse(user);

        // Cache in Redis
        storeSession(userId, user);

        return response;
    }

    /**
     * Refresh access token using refresh token.
     *
     * @param refreshToken refresh token
     * @return new token response
     */
    public TokenResponse refreshAccessToken(String refreshToken) {
        // Validate refresh token
        if (!jwtUtil.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        // Extract user ID
        Long userId = jwtUtil.extractUserId(refreshToken);

        // Verify user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Generate new access token with role
        String newAccessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getEmail(), user.getRole().name());

        return new TokenResponse(
                newAccessToken,
                "Bearer",
                jwtUtil.getAccessTokenExpirationInSeconds()
        );
    }

    /**
     * Logout user by invalidating session.
     *
     * @param userId user ID
     */
    public void logout(Long userId) {
        String sessionKey = SESSION_KEY_PREFIX + userId;
        redisTemplate.delete(sessionKey);
        logger.info("User logged out: userId={}", userId);
    }

    /**
     * Store user session in Redis.
     *
     * @param userId user ID
     * @param user user object
     */
    private void storeSession(Long userId, User user) {
        try {
            SessionInfoResponse sessionInfo = buildSessionInfoResponse(user);
            String sessionJson = objectMapper.writeValueAsString(sessionInfo);
            String sessionKey = SESSION_KEY_PREFIX + userId;

            redisTemplate.opsForValue().set(sessionKey, sessionJson, SESSION_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (JsonProcessingException e) {
            logger.error("Failed to store session for userId={}", userId, e);
        }
    }

    /**
     * Build session info response from user entity.
     *
     * @param user user entity
     * @return session info response
     */
    private SessionInfoResponse buildSessionInfoResponse(User user) {
        List<String> oauthProviders = oauthProviderLinkRepository.findByUser_UserId(user.getUserId())
                .stream()
                .map(link -> link.getProviderType().getValue())
                .collect(Collectors.toList());

        return new SessionInfoResponse(
                user.getUserId(),
                user.getNickname(),
                user.getEmail(),
                user.getLevel(),
                user.getExp(),
                user.getExpForNextLevel(),
                user.getExpProgressPercentage(),
                user.getRole().name(), // Convert UserRole enum to string (e.g., "STUDENT")
                oauthProviders
        );
    }

    /**
     * Extract nickname from OAuth user info.
     * Uses name field if available, otherwise uses email prefix.
     *
     * @param name name from OAuth provider
     * @param email email address
     * @return extracted nickname
     */
    private String extractNickname(String name, String email) {
        if (name != null && !name.trim().isEmpty()) {
            // Limit nickname to 50 characters
            return name.length() > 50 ? name.substring(0, 50) : name;
        }

        // Fallback to email prefix
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            String emailPrefix = email.substring(0, atIndex);
            return emailPrefix.length() > 50 ? emailPrefix.substring(0, 50) : emailPrefix;
        }

        return "用戶" + System.currentTimeMillis(); // Last resort fallback
    }

    /**
     * Compute SHA-256 hash of email for uniqueness constraint and lookups.
     *
     * @param email email address
     * @return SHA-256 hash
     */
    private String computeEmailHash(String email) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(email.toLowerCase().getBytes(java.nio.charset.StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute email hash", e);
        }
    }

    /**
     * OAuth authentication result.
     */
    public static class OAuthResult {
        private final String accessToken;
        private final String refreshToken;
        private final User user;
        private final boolean isNewUser;
        private final long expiresIn;

        public OAuthResult(String accessToken, String refreshToken, User user, boolean isNewUser, long expiresIn) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.user = user;
            this.isNewUser = isNewUser;
            this.expiresIn = expiresIn;
        }

        public String getAccessToken() {
            return accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public User getUser() {
            return user;
        }

        public boolean isNewUser() {
            return isNewUser;
        }

        public long getExpiresIn() {
            return expiresIn;
        }
    }
}
