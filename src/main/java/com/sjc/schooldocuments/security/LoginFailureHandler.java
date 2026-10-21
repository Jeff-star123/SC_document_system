package com.sjc.schooldocuments.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Turns authentication failures into one generic message, with a separate
 * explanation for locked and deactivated accounts. The message never reveals
 * whether an account exists.
 */
@Component
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final LoginAttemptService loginAttemptService;

    public LoginFailureHandler(LoginAttemptService loginAttemptService) {
        this.loginAttemptService = loginAttemptService;
        setDefaultFailureUrl("/login?error");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws ServletException, IOException {
        String username = request.getParameter("username");
        if (exception instanceof LockedException) {
            setDefaultFailureUrl("/login?locked");
        } else if (exception instanceof DisabledException) {
            setDefaultFailureUrl("/login?disabled");
        } else {
            loginAttemptService.onFailure(username);
            setDefaultFailureUrl(loginAttemptService.isBlocked(username) ? "/login?locked" : "/login?error");
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}
