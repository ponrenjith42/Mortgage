package com.company.mortgage.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class TraceIdConfiguration extends OncePerRequestFilter {

    private static final String TRACE_ID = "traceId";
    private static final String X_TRACE_ID ="X-Trace-Id";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String traceId = Optional.ofNullable(request.getHeader(X_TRACE_ID))
                .orElse(UUID.randomUUID().toString());

        MDC.put(TRACE_ID, traceId);
        response.setHeader(X_TRACE_ID, traceId);

        try (MDC.MDCCloseable ignore = MDC.putCloseable(TRACE_ID, traceId)) {
            filterChain.doFilter(request, response);
        }
    }
}