package tw.waterballsa.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT Authentication Filter for processing JWT tokens in requests.
 *
 * Flow:
 * 1. Extract JWT token from Authorization header
 * 2. Validate token using JwtTokenProvider
 * 3. Extract user ID from token
 * 4. Set authentication in SecurityContext
 *
 * Runs once per request before Spring Security filter chain.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // Extract JWT from Authorization header
            String jwt = getJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // Validate token
                if (jwtTokenProvider.validateToken(jwt)) {
                    // Check token type (only accept access tokens for API calls)
                    String tokenType = jwtTokenProvider.getTokenType(jwt);
                    if ("access".equals(tokenType)) {
                        // Extract user ID and role from token
                        Long userId = jwtTokenProvider.getUserIdFromToken(jwt);
                        String role = jwtTokenProvider.getRoleFromToken(jwt);

                        // Create authority list with role
                        List<GrantedAuthority> authorities;
                        if (role != null && !role.isEmpty()) {
                            authorities = Collections.singletonList(
                                    new SimpleGrantedAuthority("ROLE_" + role)
                            );
                        } else {
                            authorities = Collections.emptyList();
                        }

                        // Create authentication object with role-based authorities
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userId,
                                        null,
                                        authorities
                                );

                        authentication.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                        );

                        // Set authentication in SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // Set user ID in request header for controllers
                        request.setAttribute("userId", userId);

                        logger.debug("Set authentication for user ID: {} with role: {}", userId, role);
                    } else {
                        logger.warn("Invalid token type: {}. Expected 'access'", tokenType);
                    }
                } else {
                    logger.warn("JWT validation failed");
                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        // Continue filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT token from Authorization header.
     *
     * Expected format: "Bearer <token>"
     *
     * @param request HTTP request
     * @return JWT token or null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }

        return null;
    }
}
