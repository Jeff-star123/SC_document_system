package com.sjc.schooldocuments.common;

import com.sjc.schooldocuments.user.User;
import com.sjc.schooldocuments.user.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Resolves the authenticated account backing the current request. Controllers and
 * services always work from this identity — never from a value supplied by the browser.
 */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User require() {
        return current().orElseThrow(() -> new IllegalStateException("No authenticated user in this request context."));
    }

    public Optional<User> current() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(auth.getName());
    }
}
