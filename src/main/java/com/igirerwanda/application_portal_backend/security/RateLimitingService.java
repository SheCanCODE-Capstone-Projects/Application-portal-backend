package com.igirerwanda.application_portal_backend.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitingService {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    // Generic method to consume a token
    private boolean tryConsume(String key, int capacity, int refillTokens, int refillDurationInMinutes) {
        Bucket bucket = cache.computeIfAbsent(key, k -> {
            Bandwidth limit = Bandwidth.classic(capacity, Refill.greedy(refillTokens, Duration.ofMinutes(refillDurationInMinutes)));
            return Bucket.builder().addLimit(limit).build();
        });
        return bucket.tryConsume(1);
    }

    public boolean allowLogin(String clientIp) {

        return tryConsume("login:" + clientIp, 5, 5, 15);
    }

    public boolean allowRegistration(String clientIp) {

        return tryConsume("register:" + clientIp, 3, 3, 60);
    }

    public boolean allowPasswordReset(String clientIp) {

        return tryConsume("reset:" + clientIp, 3, 3, 60);
    }
}