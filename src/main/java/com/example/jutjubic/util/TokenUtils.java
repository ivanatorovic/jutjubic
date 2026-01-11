package com.example.jutjubic.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class TokenUtils {

    private final Key key;
    private final long expiresInSeconds;

    public TokenUtils(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-seconds}") long expiresInSeconds) {


        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiresInSeconds = expiresInSeconds;
    }

    public String generateToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiresInSeconds * 1000);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public String getEmailFromTokenSafe(String token) {
        try {
            return parse(token).getBody().getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }

    public long getExpiredIn() {
        return expiresInSeconds;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Jws<Claims> claims = parse(token);
            String email = claims.getBody().getSubject();
            Date exp = claims.getBody().getExpiration();

            return email.equals(userDetails.getUsername()) && exp.after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }
}
