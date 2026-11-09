package br.edu.fatecfranca.api.services.configs;

import java.time.Clock;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import br.edu.fatecfranca.api.services.policy.common.http.pkg.interceptors.middlewares.plugins.SlidingWindow;

@Configuration
public class RateLimitConfig {

    @Bean
    public SlidingWindow slidingWindow(
            @Value("${app.rate-limit.max-requests:100}") int maxRequests,
            @Value("${app.rate-limit.window-seconds:60}") long windowSeconds
    ) {
        return new SlidingWindow(maxRequests, Duration.ofSeconds(windowSeconds), Clock.systemUTC());
    }
}
