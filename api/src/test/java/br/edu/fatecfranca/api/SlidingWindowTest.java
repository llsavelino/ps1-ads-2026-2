package br.edu.fatecfranca.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import br.edu.fatecfranca.api.services.policy.common.http.pkg.interceptors.middlewares.plugins.SlidingWindow;

class SlidingWindowTest {

    @Test
    void allowsRequestsUpToLimitAndRejectsNextRequest() {
        SlidingWindow window = new SlidingWindow(
                2,
                Duration.ofSeconds(60),
                Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
        );

        SlidingWindow.Result first = window.tryAcquire("client");
        SlidingWindow.Result second = window.tryAcquire("client");
        SlidingWindow.Result third = window.tryAcquire("client");

        assertTrue(first.allowed());
        assertEquals(1, first.remaining());
        assertTrue(second.allowed());
        assertEquals(0, second.remaining());
        assertFalse(third.allowed());
        assertEquals(60_000, third.retryAfterMillis());
    }

    @Test
    void expiresRequestsAtTheSlidingWindowBoundary() {
        var clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        SlidingWindow window = new SlidingWindow(1, Duration.ofSeconds(60), clock);

        assertTrue(window.tryAcquire("client").allowed());
        clock.advance(Duration.ofSeconds(60));

        assertTrue(window.tryAcquire("client").allowed());
    }

    @Test
    void tracksEachClientIndependently() {
        SlidingWindow window = new SlidingWindow(
                1,
                Duration.ofSeconds(60),
                Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC)
        );

        assertTrue(window.tryAcquire("first-client").allowed());
        assertTrue(window.tryAcquire("second-client").allowed());
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        private MutableClock(Instant instant) {
            this.instant = instant;
        }

        private void advance(Duration duration) {
            instant = instant.plus(duration);
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
