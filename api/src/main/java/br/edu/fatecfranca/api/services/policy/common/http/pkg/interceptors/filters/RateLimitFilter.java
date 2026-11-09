package br.edu.fatecfranca.api.services.policy.common.http.pkg.interceptors.filters;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.ObjectMapper;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.edu.fatecfranca.api.services.policy.common.http.pkg.interceptors.middlewares.plugins.SlidingWindow;
import br.edu.fatecfranca.api.services.policy.contracts.Contract;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RateLimitFilter extends OncePerRequestFilter {

    private final SlidingWindow slidingWindow;
    private final ObjectMapper objectMapper;

    public RateLimitFilter(SlidingWindow slidingWindow, ObjectMapper objectMapper) {
        this.slidingWindow = slidingWindow;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        SlidingWindow.Result result = slidingWindow.tryAcquire(request.getRemoteAddr());
        response.setHeader("X-RateLimit-Limit", Integer.toString(slidingWindow.getMaxRequests()));
        response.setHeader("X-RateLimit-Remaining", Integer.toString(result.remaining()));

        if (!result.allowed()) {
            long retryAfterSeconds = (result.retryAfterMillis() + 999) / 1000;
            response.setStatus(429);
            response.setHeader("Retry-After", Long.toString(retryAfterSeconds));
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");
            objectMapper.writeValue(
                    response.getWriter(),
                    Contract.error(429, "RATE_LIMIT_EXCEEDED", "Too many requests. Try again later.")
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
