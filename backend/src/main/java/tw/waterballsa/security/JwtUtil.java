package tw.waterballsa.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT utility class for generating and validating JWT tokens.
 * Supports access tokens (15 minutes) and refresh tokens (7 days).
 *
 * @author Water Ball SA
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${app.jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * Generate an access token for a user.
     *
     * @param userId the user ID
     * @param email the user's email
     * @param role the user's role (e.g., "STUDENT", "TEACHER", "ADMIN")
     * @return JWT access token
     */
    public String generateAccessToken(Long userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        claims.put("email", email);
        claims.put("role", role);
        claims.put("type", "access");

        return createToken(claims, userId.toString(), accessTokenExpiration);
    }

    /**
     * Generate a refresh token for a user.
     *
     * @param userId the user ID
     * @return JWT refresh token
     */
    public String generateRefreshToken(Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", userId);
        claims.put("type", "refresh");

        return createToken(claims, userId.toString(), refreshTokenExpiration);
    }

    /**
     * Extract user ID from token.
     *
     * @param token JWT token
     * @return user ID
     */
    public Long extractUserId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("user_id", Long.class);
    }

    /**
     * Extract email from token.
     *
     * @param token JWT token
     * @return email address
     */
    public String extractEmail(String token) {
        return extractClaim(token, claims -> claims.get("email", String.class));
    }

    /**
     * Extract role from token.
     *
     * @param token JWT token
     * @return user role (e.g., "STUDENT", "TEACHER", "ADMIN")
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    /**
     * Extract token type (access or refresh).
     *
     * @param token JWT token
     * @return token type
     */
    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    /**
     * Extract expiration date from token.
     *
     * @param token JWT token
     * @return expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from token.
     *
     * @param token JWT token
     * @param claimsResolver function to extract claim
     * @return claim value
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token.
     *
     * @param token JWT token
     * @return all claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if token is expired.
     *
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate token.
     *
     * @param token JWT token
     * @param userId expected user ID
     * @return true if valid, false otherwise
     */
    public Boolean validateToken(String token, Long userId) {
        final Long tokenUserId = extractUserId(token);
        return (tokenUserId.equals(userId) && !isTokenExpired(token));
    }

    /**
     * Validate access token.
     *
     * @param token JWT token
     * @return true if valid access token, false otherwise
     */
    public Boolean validateAccessToken(String token) {
        try {
            String tokenType = extractTokenType(token);
            return "access".equals(tokenType) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validate refresh token.
     *
     * @param token JWT token
     * @return true if valid refresh token, false otherwise
     */
    public Boolean validateRefreshToken(String token) {
        try {
            String tokenType = extractTokenType(token);
            return "refresh".equals(tokenType) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Create a JWT token.
     *
     * @param claims token claims
     * @param subject token subject (user ID)
     * @param expiration expiration time in milliseconds
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Get access token expiration in seconds.
     *
     * @return expiration in seconds
     */
    public long getAccessTokenExpirationInSeconds() {
        return accessTokenExpiration / 1000;
    }

    /**
     * Get refresh token expiration in seconds.
     *
     * @return expiration in seconds
     */
    public long getRefreshTokenExpirationInSeconds() {
        return refreshTokenExpiration / 1000;
    }
}
