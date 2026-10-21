package com.sjc.schooldocuments.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PasswordChangeForm {

    @NotBlank(message = "Enter your current password.")
    private String currentPassword;

    @NotBlank(message = "Enter a new password.")
    @Size(min = 10, max = 100, message = "New password must be at least 10 characters.")
    private String newPassword;

    @NotBlank(message = "Re-enter the new password.")
    private String confirmPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

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
