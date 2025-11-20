package tw.waterballsa.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import tw.waterballsa.security.JwtAuthenticationFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Spring Security configuration.
 *
 * Security Model:
 * - Stateless session management (no server-side sessions)
 * - JWT-based authentication
 * - OAuth2 for initial authentication
 * - CORS enabled for frontend communication
 * - Method-level security enabled (supports @PreAuthorize, @Secured annotations)
 *
 * Role Hierarchy:
 * - ADMIN (highest privilege)
 * - TEACHER
 * - STUDENT
 * - GUEST (lowest privilege)
 *
 * Public Endpoints:
 * - /auth/oauth/** - OAuth authentication flows
 * - /actuator/health - Health check
 *
 * Protected Endpoints:
 * - /profile/** - User profile management
 * - /linking/** - Third-party account linking
 * - /orders/** - Order history
 * - /courses/owned - Owned courses
 * - /auth/session - Session info
 * - /auth/refresh - Token refresh
 * - /auth/logout - Logout
 * - /admin/** - Admin-only endpoints (requires ROLE_ADMIN)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private tw.waterballsa.security.OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    /**
     * Custom authentication entry point for API endpoints.
     * Returns JSON error instead of redirecting to login page.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> errorData = new HashMap<>();
            errorData.put("error", "Unauthorized");
            errorData.put("message", "請先登入");
            errorData.put("code", "AUTHENTICATION_REQUIRED");
            errorData.put("path", request.getRequestURI());

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(response.getOutputStream(), errorData);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (using JWT tokens, not cookies for auth)
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS with custom configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource))

                // Stateless session management (no HttpSession)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - OAuth authentication (Spring Security OAuth2 endpoints)
                        .requestMatchers(
                                "/auth/oauth/**",
                                "/auth/callback/**",
                                "/oauth2/**",
                                "/login/oauth2/**"
                        ).permitAll()

                        // Public endpoints - Health check
                        .requestMatchers(
                                "/actuator/health",
                                "/actuator/info"
                        ).permitAll()

                        // Public endpoints - Token refresh
                        .requestMatchers("/auth/refresh").permitAll()

                        // Public endpoints - Course listing and details
                        .requestMatchers("/api/courses", "/api/courses/**").permitAll()

                        // Protected endpoints - require authentication
                        .requestMatchers(
                                "/auth/session",
                                "/auth/logout",
                                "/profile/**",
                                "/linking/**",
                                "/orders/**",
                                "/courses/owned"
                        ).authenticated()

                        // All other requests require authentication
                        .anyRequest().authenticated()
                )

                // Enable OAuth2 Login
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(authorization -> authorization
                                .baseUri("/oauth2/authorization")
                        )
                        .redirectionEndpoint(redirection -> redirection
                                .baseUri("/login/oauth2/code/*")
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )

                // Custom authentication entry point for API endpoints
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint())
                )

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
