package com.project.security.jwt;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component // Ensure JwtAuthFilter is a Spring bean
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Autowired
    public JwtAuthFilter(JwtUtils jwtUtils) {  // Constructor injection for better testability
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractTokenFromHeader(request);
        String refreshToken = extractRefreshTokenFromHeader(request);

        if (token != null && jwtUtils.validateToken(token)) {
            // Valid access token
            String username = jwtUtils.extractUsername(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else if (refreshToken != null && jwtUtils.validateToken(refreshToken)) {
            // Refresh token is valid, issue a new access token
            String username = jwtUtils.extractUsername(refreshToken);
            String newAccessToken = jwtUtils.generateToken(username, true);

            response.setHeader("Authorization", "Bearer " + newAccessToken);  // Send the new access token back

            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromHeader(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }

    private String extractRefreshTokenFromHeader(HttpServletRequest request) {
        // Check if the refresh token is passed in the request (for example, in a specific header or body)
        String refreshToken = request.getHeader("Refresh-Token");
        if (refreshToken != null) {
            return refreshToken;
        }
        return null;
    }
}
