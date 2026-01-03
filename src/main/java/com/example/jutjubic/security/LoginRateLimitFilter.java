package com.example.jutjubic.security;

import com.example.jutjubic.service.IpRateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    private final IpRateLimiterService ipRateLimiterService;

    public LoginRateLimitFilter(IpRateLimiterService ipRateLimiterService) {
        this.ipRateLimiterService = ipRateLimiterService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Tvoj login endpoint je: POST /api/auth/login
        String path = request.getServletPath();
        return !(path.equals("/api/auth/login") && "POST".equalsIgnoreCase(request.getMethod()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String ip = extractClientIp(request);

        if (!ipRateLimiterService.allow(ip)) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write("""
                {"message":"Previse pokusaja prijave sa iste IP adrese. Pokusaj ponovo za minut."}
            """);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String extractClientIp(HttpServletRequest request) {
        // Ako si iza proxy-ja, X-Forwarded-For može sadržati stvarni IP
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
