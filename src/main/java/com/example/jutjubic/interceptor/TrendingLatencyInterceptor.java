package com.example.jutjubic.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TrendingLatencyInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        request.setAttribute(START_TIME, System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {

        Long start = (Long) request.getAttribute(START_TIME);
        if (start == null) return;

        long durationMs = System.currentTimeMillis() - start;

        System.out.println(
                "[LATENCY] " +
                        request.getMethod() + " " +
                        request.getRequestURI() +
                        " -> " + durationMs + " ms"
        );
    }
}
