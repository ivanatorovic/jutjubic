package com.example.jutjubic.config;

import com.example.jutjubic.interceptor.TrendingLatencyInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TrendingLatencyInterceptor latencyInterceptor;

    public WebConfig(TrendingLatencyInterceptor latencyInterceptor) {
        this.latencyInterceptor = latencyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(latencyInterceptor)
                .addPathPatterns("/api/trending/**");
    }
}
