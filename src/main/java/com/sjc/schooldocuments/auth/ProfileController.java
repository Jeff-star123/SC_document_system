package com.sjc.schooldocuments.auth;

import com.sjc.schooldocuments.common.CurrentUserService;
import com.sjc.schooldocuments.user.User;
import com.sjc.schooldocuments.user.UserService;
import com.sjc.schooldocuments.user.dto.PasswordChangeForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final CurrentUserService currentUserService;
    private final UserService userService;

    public ProfileController(CurrentUserService currentUserService, UserService userService) {
        this.currentUserService = currentUserService;
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = currentUserService.require();
        model.addAttribute("profile", user);
        model.addAttribute("activeNav", "profile");
        if (!model.containsAttribute("passwordForm")) {
            model.addAttribute("passwordForm", new PasswordChangeForm());
        }
        return "profile/index";
    }

    @PostMapping("/profile/password")
    public String changePassword(@Valid @ModelAttribute("passwordForm") PasswordChangeForm form,
                                 BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        User user = currentUserService.require();
        if (!bindingResult.hasErrors()) {
            try {
                userService.changeOwnPassword(user, form);
                redirectAttributes.addFlashAttribute("successMessage", "Your password has been changed.");
                return "redirect:/profile";
            } catch (RuntimeException ex) {
                redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
                return "redirect:/profile";
            }
        }
        redirectAttributes.addFlashAttribute("errorMessage",
                bindingResult.getFieldErrors().stream()
                        .map(org.springframework.validation.FieldError::getDefaultMessage)
                        .findFirst().orElse("Check the password fields and try again."));
        return "redirect:/profile";
    }
}
