package br.edu.fatecfranca.api.services.policy.common.http.pkg.interceptors.middlewares.plugins;

import java.time.Clock;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class SlidingWindow {

    private static final long CLEANUP_INTERVAL = 256;

    private final int maxRequests;
    private final long windowMillis;
    private final Clock clock;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final AtomicLong requests = new AtomicLong();

    public SlidingWindow(int maxRequests, Duration window, Clock clock) {
        if (maxRequests < 1) {
            throw new IllegalArgumentException("maxRequests must be greater than zero");
        }
        if (window == null || window.isZero() || window.isNegative() || window.toMillis() < 1) {
            throw new IllegalArgumentException("window must be at least one millisecond");
        }
        if (clock == null) {
            throw new IllegalArgumentException("clock must not be null");
        }

        this.maxRequests = maxRequests;
        this.windowMillis = window.toMillis();
        this.clock = clock;
    }

    public Result tryAcquire(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }

        long now = clock.millis();
        Result[] result = new Result[1];
        buckets.compute(key, (ignored, bucket) -> {
            Bucket current = bucket == null ? new Bucket() : bucket;
            current.removeExpired(now, windowMillis);

            if (current.timestamps.size() >= maxRequests) {
                long retryAfterMillis = current.timestamps.peekFirst() + windowMillis - now;
                result[0] = new Result(false, 0, Math.max(1, retryAfterMillis));
            } else {
                current.timestamps.addLast(now);
                result[0] = new Result(true, maxRequests - current.timestamps.size(), 0);
            }
            return current;
        });

        if (requests.incrementAndGet() % CLEANUP_INTERVAL == 0) {
            removeExpiredBuckets(now);
        }

        return result[0];
    }

    public int getMaxRequests() {
        return maxRequests;
    }

    private void removeExpiredBuckets(long now) {
        for (String key : buckets.keySet()) {
            buckets.computeIfPresent(key, (ignored, bucket) -> {
                bucket.removeExpired(now, windowMillis);
                return bucket.timestamps.isEmpty() ? null : bucket;
            });
        }
    }

    public record Result(boolean allowed, int remaining, long retryAfterMillis) {}

    private static final class Bucket {
        private final ArrayDeque<Long> timestamps = new ArrayDeque<>();

        private void removeExpired(long now, long windowMillis) {
            while (!timestamps.isEmpty() && timestamps.peekFirst() <= now - windowMillis) {
                timestamps.removeFirst();
            }
        }
    }
}
