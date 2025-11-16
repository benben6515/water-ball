package tw.waterballsa.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tw.waterballsa.model.OAuthProviderLink.ProviderType;
import tw.waterballsa.service.AuthService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Custom OAuth2 authentication success handler.
 * Handles successful OAuth2 authentication by:
 * 1. Processing OAuth callback through AuthService
 * 2. Creating JWT tokens
 * 3. Setting refresh token cookie
 * 4. Redirecting to frontend with access token
 *
 * @author Water Ball SA
 */
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);

    private final AuthService authService;
    private final String frontendUrl;

    public OAuth2AuthenticationSuccessHandler(
            AuthService authService,
            @Value("${app.frontend.url}") String frontendUrl
    ) {
        this.authService = authService;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        try {
            if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
                // Extract OAuth provider
                String registrationId = oauthToken.getAuthorizedClientRegistrationId();
                ProviderType providerType = ProviderType.fromValue(registrationId);

                // Extract OAuth user info
                OAuth2User oauthUser = oauthToken.getPrincipal();
                String providerUserId = extractProviderUserId(oauthUser);
                String email = oauthUser.getAttribute("email");
                String name = oauthUser.getAttribute("name");

                if (email == null || providerUserId == null) {
                    logger.error("Missing required OAuth attributes: email or providerUserId");
                    redirectToErrorPage(response, "無法取得使用者資訊");
                    return;
                }

                logger.info("Processing OAuth callback for provider: {}, email: {}", providerType, email);

                // Handle OAuth callback through service
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

                getRedirectStrategy().sendRedirect(request, response, redirectUrl);

            } else {
                logger.error("Authentication is not an OAuth2AuthenticationToken");
                redirectToErrorPage(response, "登入失敗");
            }
        } catch (Exception e) {
            logger.error("OAuth callback failed", e);
            redirectToErrorPage(response, "登入失敗，請稍後再試");
        }
    }

    /**
     * Extract provider user ID from OAuth attributes.
     * Google uses "sub", Facebook uses "id".
     */
    private String extractProviderUserId(OAuth2User oauthUser) {
        String providerUserId = oauthUser.getAttribute("sub"); // Google
        if (providerUserId == null) {
            providerUserId = oauthUser.getAttribute("id"); // Facebook
        }
        return providerUserId;
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refresh_token", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
        response.addCookie(cookie);
    }

    private void redirectToErrorPage(HttpServletResponse response, String errorMessage) throws IOException {
        String redirectUrl = frontendUrl + "/login?error=" + urlEncode(errorMessage);
        response.sendRedirect(redirectUrl);
    }

    private String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return value;
        }
    }
}
