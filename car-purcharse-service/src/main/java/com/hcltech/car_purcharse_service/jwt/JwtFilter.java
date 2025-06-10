package com.hcltech.car_purcharse_service.jwt;

import io.jsonwebtoken.ExpiredJwtException; // Import for better error handling
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger; // Added for logging
import org.slf4j.LoggerFactory; // Added for logging
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // For handling user not found
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class); // Initialize logger

    private final JwtUtil jwtUtil; // Make final
    private final MyUserDetailsService myUserDetailsService; // Make final

    public JwtFilter(JwtUtil jwtUtil, MyUserDetailsService myUserDetailsService) {
        this.jwtUtil = jwtUtil;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String token = jwtUtil.getJwtFromHeader(request); // This extracts "Bearer " token

        String username = null;

        // Only try to process token if it exists and there's no existing authentication in context
        // The context will be null for a new, unauthenticated request.
        if (token != null && !token.isEmpty()) {
            try {
                username = jwtUtil.getUserNameFromToken(token); // Extract username from token
            } catch (ExpiredJwtException e) {
                logger.warn("JWT token has expired: {}", e.getMessage());
                // Handle expired token: You might want to send a 401 or 403 response here directly
                // e.g., response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                // filterChain.doFilter(request, response); // Continue filter chain if you want Spring Security to handle 403
                // return; // Or return to prevent further processing if you handle it here
            } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
                logger.error("Invalid JWT Token: {}", e.getMessage());
                // Handle invalid token:
                // response.setStatus(HttpServletResponse.SC_FORBIDDEN); // or SC_UNAUTHORIZED
                // filterChain.doFilter(request, response);
                // return;
            }
        }

        // Proceed only if username is found AND no authentication is currently set in SecurityContext
        // This ensures we don't overwrite existing authentication (e.g., from other filters)
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = null;
            try {
                userDetails = this.myUserDetailsService.loadUserByUsername(username);

                // Now validate the token
                if (jwtUtil.validateToken(token, userDetails)) { // This will use the corrected validateToken logic
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the Authentication object in the SecurityContextHolder
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Successfully authenticated user: {}", username);
                } else {
                    logger.warn("JWT token validation failed for user: {}", username);
                }
            } catch (UsernameNotFoundException e) {
                logger.warn("User '{}' not found via UserDetailsService from JWT token.", username);
            }
        }

        // Always continue the filter chain, even if authentication fails
        // This allows subsequent Spring Security filters to handle authorization (e.g., return 403/401)
        filterChain.doFilter(request, response);
    }
}