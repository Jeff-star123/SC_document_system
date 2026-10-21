package com.sjc.schooldocuments.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Small in-memory guard against repeated login guessing. Keyed by the submitted
 * username; entries expire on their own, so no lockout needs manual clearing.
 * A multi-instance deployment would move this to a shared store.
 */
@Component
public class LoginAttemptService {

    private final int maxAttempts;
    private final Duration lockout;

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    public LoginAttemptService(@Value("${app.security.max-login-attempts:5}") int maxAttempts,
                               @Value("${app.security.lockout-minutes:10}") long lockoutMinutes) {
        this.maxAttempts = maxAttempts;
        this.lockout = Duration.ofMinutes(lockoutMinutes);
    }

    public boolean isBlocked(String username) {
        if (username == null) {
            return false;
        }
        Attempt attempt = attempts.get(key(username));
        if (attempt == null) {
            return false;
        }
        if (attempt.blockedUntil != null && attempt.blockedUntil.isAfter(Instant.now())) {
            return true;
        }
        if (attempt.blockedUntil != null) {
            attempts.remove(key(username));
        }
        return false;
    }

    public void onFailure(String username) {
        if (username == null) {
            return;
        }
        attempts.compute(key(username), (k, existing) -> {
            Attempt attempt = existing == null ? new Attempt() : existing;
            attempt.failures++;
            if (attempt.failures >= maxAttempts) {
                attempt.blockedUntil = Instant.now().plus(lockout);
                attempt.failures = 0;
            }
            return attempt;
        });
    }

    public void onSuccess(String username) {
        if (username != null) {
            attempts.remove(key(username));
        }
    }

    private String key(String username) {
        return username.trim().toLowerCase();
    }

    private static final class Attempt {
        private int failures;
        private Instant blockedUntil;
    }
}
