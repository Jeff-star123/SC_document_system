package com.sjc.schooldocuments.auth;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "locked", required = false) String locked,
                        @RequestParam(value = "disabled", required = false) String disabled,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("loginError", "That email or student number and password did not match.");
        }
        if (locked != null) {
            model.addAttribute("loginError",
                    "Too many failed attempts. This account is paused for a few minutes.");
        }
        if (disabled != null) {
            model.addAttribute("loginError",
                    "This account is deactivated. Contact the registrar's office to have it restored.");
        }
        if (logout != null) {
            model.addAttribute("loginNotice", "You have signed out.");
        }
        return "auth/login";
    }

    /**
     * Served by this controller rather than as a view controller so the shared
     * model advice supplies the institution and system names for the banner
     * and page title.
     */
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
