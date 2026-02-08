package com.example.jutjubic.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class ActiveUserTrackingFilter extends OncePerRequestFilter {

    private final ActiveUsers24hMetrics metrics;

    public ActiveUserTrackingFilter(ActiveUsers24hMetrics metrics) {
        this.metrics = metrics;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String p = request.getServletPath();
        return p.startsWith("/actuator") || p.startsWith("/ws");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        filterChain.doFilter(request, response);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && auth.getName() != null
                && !"anonymousUser".equals(auth.getName())) {


            metrics.markSeen(auth.getName());
        }
    }
}