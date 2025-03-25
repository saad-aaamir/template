package com.application.config.aop;


import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@WebFilter("/*")
public class TracingFilter implements Filter {

    public static final ThreadLocal<String> tracingNumber = ThreadLocal.withInitial(() -> null);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            String traceId = UUID.randomUUID().toString();
            tracingNumber.set(traceId);
            chain.doFilter(request, response);
        } finally {
            tracingNumber.remove();
        }
    }

    @Override
    public void destroy() {
    }

    public static String getTracingNumber() {
        return tracingNumber.get();
    }
}
