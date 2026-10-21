package com.sjc.schooldocuments.user;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_number", length = 50, unique = true)
    private String studentNumber;

    @Column(name = "employee_number", length = 50, unique = true)
    private String employeeNumber;

    @Column(name = "first_name", length = 100, nullable = false)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", length = 100, nullable = false)
    private String lastName;

    @Column(name = "email", length = 150, nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", length = 255, nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    private Role role;

    @Column(name = "course_or_department", length = 150)
    private String courseOrDepartment;

    @Column(name = "year_level_or_section", length = 100)
    private String yearLevelOrSection;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", length = 20, nullable = false)
    private AccountStatus accountStatus = AccountStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
        if (accountStatus == null) {
            accountStatus = AccountStatus.ACTIVE;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getFullName() {
        StringBuilder sb = new StringBuilder(firstName == null ? "" : firstName);
        if (middleName != null && !middleName.isBlank()) {
            sb.append(' ').append(middleName.trim().charAt(0)).append('.');
        }
        if (lastName != null && !lastName.isBlank()) {
            sb.append(' ').append(lastName);
        }
        return sb.toString().trim();
    }

    public String getDisplayIdentifier() {
        if (role == Role.STUDENT) {
            return studentNumber == null ? email : studentNumber;
        }
        return employeeNumber == null ? email : employeeNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE;
    }
}
