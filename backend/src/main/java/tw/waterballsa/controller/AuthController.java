package tw.waterballsa.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import tw.waterballsa.dto.ErrorResponse;
import tw.waterballsa.dto.RefreshTokenRequest;
import tw.waterballsa.dto.SessionInfoResponse;
import tw.waterballsa.dto.TokenResponse;
import tw.waterballsa.model.OAuthProviderLink.ProviderType;
import tw.waterballsa.security.JwtUtil;
import tw.waterballsa.service.AuthService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Authentication controller handling OAuth login, session management, and logout.
 * Supports Google and Facebook OAuth 2.0 authentication.
 *
 * @author Water Ball SA
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final JwtUtil jwtUtil;
    private final String frontendUrl;

    public AuthController(
            AuthService authService,
            JwtUtil jwtUtil,
            @Value("${app.frontend.url}") String frontendUrl
    ) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
        this.frontendUrl = frontendUrl;
    }

    /**
     * Initiate OAuth authorization flow.
     * Redirects to Spring Security's OAuth2 authorization endpoint.
     *
     * GET /auth/oauth/{provider}/authorize
     *
     * @param provider OAuth provider (google or facebook)
     * @return redirect to OAuth consent page
     */
    @GetMapping("/oauth/{provider}/authorize")
    public RedirectView authorize(@PathVariable String provider) {
        logger.info("Initiating OAuth flow for provider: {}", provider);

        // Validate provider
        if (!isValidProvider(provider)) {
            return new RedirectView(frontendUrl + "/login?error=" + urlEncode("不支援的登入方式"));
        }

        // Redirect to Spring Security's OAuth2 authorization endpoint
        return new RedirectView("/oauth2/authorization/" + provider);
    }

    /**
     * Handle OAuth callback after successful authentication.
     * This endpoint is called by Spring Security after OAuth authentication.
     *
     * GET /auth/oauth/{provider}/callback
     *
     * @param provider OAuth provider
     * @param authentication OAuth2 authentication token
     * @param response HTTP response for setting cookies
     * @return redirect to frontend with access token
     */
    @GetMapping("/oauth/{provider}/callback")
    public RedirectView callback(
            @PathVariable String provider,
            OAuth2AuthenticationToken authentication,
            HttpServletResponse response
    ) {
        try {
            logger.info("Processing OAuth callback for provider: {}", provider);

            OAuth2User oauthUser = authentication.getPrincipal();
            String providerUserId = oauthUser.getAttribute("sub"); // Google uses "sub", Facebook uses "id"
            if (providerUserId == null) {
                providerUserId = oauthUser.getAttribute("id"); // Facebook
            }

            String email = oauthUser.getAttribute("email");
            String name = oauthUser.getAttribute("name");

            if (email == null || providerUserId == null) {
                logger.error("Missing required OAuth attributes: email or providerUserId");
                return new RedirectView(frontendUrl + "/login?error=" + urlEncode("無法取得使用者資訊"));
            }

            // Handle OAuth callback through service
            ProviderType providerType = ProviderType.fromValue(provider);
            AuthService.OAuthResult result = authService.handleOAuthCallback(
                    providerType,
                    providerUserId,
                    email,
                    name
            );

            // Set refresh token as HTTP-only cookie
            setRefreshTokenCookie(response, result.getRefreshToken());

            // Redirect to frontend with access token
            String redirectUrl = frontendUrl + "/oauth/callback?access_token=" + result.getAccessToken();

            logger.info("OAuth authentication successful: userId={}, isNewUser={}",
                    result.getUser().getUserId(), result.isNewUser());

            return new RedirectView(redirectUrl);

        } catch (Exception e) {
            logger.error("OAuth callback failed", e);
            return new RedirectView(frontendUrl + "/login?error=" + urlEncode("登入失敗，請稍後再試"));
        }
    }

    /**
     * Get current user session information.
     *
     * GET /auth/session
     *
     * @param authentication user authentication from SecurityContext (set by JwtAuthenticationFilter)
     * @return session info response
     */
    @GetMapping("/session")
    public ResponseEntity<SessionInfoResponse> getSession(
            @AuthenticationPrincipal Long userId
    ) {
        try {
            if (userId == null) {
                logger.error("No authenticated user found");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            SessionInfoResponse session = authService.getSession(userId);
            return ResponseEntity.ok(session);
        } catch (Exception e) {
            logger.error("Failed to get session for userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /**
     * Refresh access token using refresh token.
     *
     * POST /auth/refresh
     *
     * @param request refresh token request
     * @return new token response
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            TokenResponse tokenResponse = authService.refreshAccessToken(request.getRefreshToken());
            return ResponseEntity.ok(tokenResponse);
        } catch (Exception e) {
            logger.error("Token refresh failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("INVALID_REFRESH_TOKEN", "無效的刷新權杖"));
        }
    }

    /**
     * Logout current user.
     *
     * POST /auth/logout
     *
     * @param userId user ID from SecurityContext
     * @param response HTTP response for clearing cookies
     * @return no content response
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @AuthenticationPrincipal Long userId,
            HttpServletResponse response
    ) {
        try {
            if (userId == null) {
                logger.error("No authenticated user found for logout");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            authService.logout(userId);

            // Clear refresh token cookie
            clearRefreshTokenCookie(response);

            logger.info("User logged out successfully: userId={}", userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Logout failed for userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper methods

    private boolean isValidProvider(String provider) {
        return "google".equalsIgnoreCase(provider) || "facebook".equalsIgnoreCase(provider);
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
    }

    private void clearRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refresh_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}
