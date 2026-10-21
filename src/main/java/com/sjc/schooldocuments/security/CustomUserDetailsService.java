package com.sjc.schooldocuments.security;

import com.sjc.schooldocuments.user.AccountStatus;
import com.sjc.schooldocuments.user.User;
import com.sjc.schooldocuments.user.UserRepository;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Accounts sign in with their email address or, for students, their student
 * number. Inactive accounts are disabled rather than deleted.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;

    public CustomUserDetailsService(UserRepository userRepository, LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        String value = identifier == null ? "" : identifier.trim();
        if (loginAttemptService.isBlocked(value)) {
            throw new LockedException("Too many failed sign-in attempts. Try again in a few minutes.");
        }

        User user = userRepository.findByEmailIgnoreCase(value)
                .or(() -> userRepository.findByStudentNumber(value))
                .or(() -> userRepository.findByEmployeeNumber(value))
                .orElseThrow(() -> new UsernameNotFoundException("No account matches those details."));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())))
                .disabled(user.getAccountStatus() != AccountStatus.ACTIVE)
                .build();
    }
}
