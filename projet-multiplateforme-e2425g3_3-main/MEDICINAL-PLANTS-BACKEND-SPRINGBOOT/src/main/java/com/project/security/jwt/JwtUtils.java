package com.project.security.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @PostConstruct
    public void init() {
        logger.info("JWT Secret: {}", jwtSecret);
        logger.info("Access Token Expiration: {}", accessTokenExpiration);
        logger.info("Refresh Token Expiration: {}", refreshTokenExpiration);
    }

    public String generateToken(String username, boolean isAccessToken) {
        long expiration = isAccessToken ? accessTokenExpiration : refreshTokenExpiration;
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Validates both access and refresh tokens
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // Extracts the username from the token
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Checks if the refresh token has expired
    public boolean isRefreshTokenExpired(String token) {
        try {
            Date expirationDate = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
            return expirationDate.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true; // If token is invalid, assume it has expired
        }
    }
}
