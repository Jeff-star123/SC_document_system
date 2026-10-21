package com.sjc.schooldocuments.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Administrator resets another account's password without knowing the old one.
 */
public class PasswordResetForm {

    @NotBlank(message = "Enter a new password.")
    @Size(min = 10, max = 100, message = "New password must be at least 10 characters.")
    private String newPassword;

    @NotBlank(message = "Re-enter the new password.")
    private String confirmPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
