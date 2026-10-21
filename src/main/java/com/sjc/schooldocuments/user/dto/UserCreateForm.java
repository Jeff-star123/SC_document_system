package com.sjc.schooldocuments.user.dto;

import com.sjc.schooldocuments.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Administrator creates a student, staff, or administrator account.
 */
public class UserCreateForm {

    @NotNull(message = "Select an account role.")
    private Role role;

    @NotBlank(message = "First name is required.")
    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String middleName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Enter a valid email address.")
    @Size(max = 150)
    private String email;

    @Pattern(regexp = "^$|^[A-Za-z0-9-]{4,50}$", message = "Student number must be 4-50 letters, digits, or hyphens.")
    private String studentNumber;

    @Pattern(regexp = "^$|^[A-Za-z0-9-]{4,50}$", message = "Employee number must be 4-50 letters, digits, or hyphens.")
    private String employeeNumber;

    @Size(max = 150)
    private String courseOrDepartment;

    @Size(max = 100)
    private String yearLevelOrSection;

    @NotBlank(message = "Set an initial password.")
    @Size(min = 10, max = 100, message = "Password must be at least 10 characters.")
    private String password;

    @NotBlank(message = "Re-enter the password.")
    private String confirmPassword;

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getCourseOrDepartment() {
        return courseOrDepartment;
    }

    public void setCourseOrDepartment(String courseOrDepartment) {
        this.courseOrDepartment = courseOrDepartment;
    }

    public String getYearLevelOrSection() {
        return yearLevelOrSection;
    }

    public void setYearLevelOrSection(String yearLevelOrSection) {
        this.yearLevelOrSection = yearLevelOrSection;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
