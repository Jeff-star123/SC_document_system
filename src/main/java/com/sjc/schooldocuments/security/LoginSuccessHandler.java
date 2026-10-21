package com.sjc.schooldocuments.security;

import com.sjc.schooldocuments.user.Role;
import com.sjc.schooldocuments.user.User;
import com.sjc.schooldocuments.user.UserRepository;
import com.sjc.schooldocuments.user.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Clears the failed-attempt counter, stamps the last sign-in time, and drops
 * each role into its own panel.
 */
@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final UserService userService;
    private final LoginAttemptService loginAttemptService;

    public LoginSuccessHandler(UserRepository userRepository, UserService userService,
                               LoginAttemptService loginAttemptService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.loginAttemptService = loginAttemptService;
        setDefaultTargetUrl("/");
        setAlwaysUseDefaultTargetUrl(false);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException {
        loginAttemptService.onSuccess(authentication.getName());
        User user = userRepository.findByEmailIgnoreCase(authentication.getName()).orElse(null);
        if (user != null) {
            userService.touchLastLogin(user.getId());
        }
        String target = switch (user == null ? Role.STUDENT : user.getRole()) {
            case STUDENT -> "/student/dashboard";
            case STAFF -> "/staff/dashboard";
            case ADMIN -> "/admin/dashboard";
        };
        if (request.getSession(false) != null) {
            var saved = (org.springframework.security.web.savedrequest.SavedRequest) request.getSession()
                    .getAttribute("SPRING_SECURITY_SAVED_REQUEST");
            if (saved != null && !saved.getRedirectUrl().contains("/login")) {
                super.onAuthenticationSuccess(request, response, authentication);
                return;
            }
        }
        response.sendRedirect(request.getContextPath() + target);
    }
}
