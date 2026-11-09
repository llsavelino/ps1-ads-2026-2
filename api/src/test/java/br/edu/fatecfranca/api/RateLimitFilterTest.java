package br.edu.fatecfranca.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.json.JsonMapper;
import br.edu.fatecfranca.api.services.policy.common.http.pkg.interceptors.filters.RateLimitFilter;
import br.edu.fatecfranca.api.services.policy.common.http.pkg.interceptors.middlewares.plugins.SlidingWindow;

class RateLimitFilterTest {

    @Test
    void rejectsExcessRequestsWithStandardErrorAndRetryAfter() throws Exception {
        SlidingWindow window = new SlidingWindow(
                1,
                Duration.ofSeconds(60),
                Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
        );
        RateLimitFilter filter = new RateLimitFilter(window, JsonMapper.builder().build());
        AtomicInteger chainCalls = new AtomicInteger();

        filter.doFilter(
                requestFrom("192.0.2.1"),
                new MockHttpServletResponse(),
                (request, response) -> chainCalls.incrementAndGet()
        );

        MockHttpServletResponse response = new MockHttpServletResponse();
        filter.doFilter(requestFrom("192.0.2.1"), response, (request, result) ->
                chainCalls.incrementAndGet());

        assertEquals(1, chainCalls.get());
        assertEquals(429, response.getStatus());
        assertEquals("60", response.getHeader("Retry-After"));
        assertEquals("1", response.getHeader("X-RateLimit-Limit"));
        assertEquals("0", response.getHeader("X-RateLimit-Remaining"));
        assertTrue(response.getContentAsString().contains("RATE_LIMIT_EXCEEDED"));
        assertFalse(response.getContentAsString().contains("\"success\":true"));
    }

    private MockHttpServletRequest requestFrom(String address) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr(address);
        return request;
    }
}
