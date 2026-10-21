package com.sjc.schooldocuments.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Kept separate from {@link SecurityConfig} so that beans which only need the
 * password encoder (for example {@code UserService}) do not have to be built
 * through the security filter-chain configuration, which would otherwise form
 * a dependency cycle with the login success handler.
 */
@Configuration
public class PasswordConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
