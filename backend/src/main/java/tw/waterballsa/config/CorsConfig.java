package tw.waterballsa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * CORS (Cross-Origin Resource Sharing) configuration.
 *
 * Purpose:
 * - Allow frontend (Next.js on localhost:3000) to make requests to backend API
 * - Enable credentials (cookies, authorization headers) to be sent cross-origin
 * - Support OAuth callback redirects
 *
 * Security:
 * - Restricts allowed origins to frontend URL only
 * - Enables credentials for JWT tokens and cookies
 * - Limits allowed methods to necessary HTTP verbs
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Parse comma-separated origins from configuration
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        configuration.setAllowedOrigins(origins);

        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET",
            "POST",
            "PUT",
            "PATCH",
            "DELETE",
            "OPTIONS"
        ));

        // Allow all headers (frontend may send custom headers)
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // CRITICAL: Allow credentials (cookies, Authorization header)
        // Required for:
        // - JWT tokens in Authorization header
        // - Refresh tokens in HTTP-only cookies
        // - OAuth state cookies
        configuration.setAllowCredentials(true);

        // Expose headers that frontend needs to read
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Total-Count"
        ));

        // Cache preflight requests for 1 hour
        configuration.setMaxAge(3600L);

        // Apply CORS configuration to all endpoints
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
