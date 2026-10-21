package com.sjc.schooldocuments.user.dto;

import com.sjc.schooldocuments.user.AccountStatus;
import com.sjc.schooldocuments.user.Role;
import com.sjc.schooldocuments.user.User;

import java.time.LocalDateTime;

public class UserRowDto {

    private final Long id;
    private final String fullName;
    private final String identifier;
    private final String email;
    private final Role role;
    private final String courseOrDepartment;
    private final String yearLevelOrSection;
    private final AccountStatus accountStatus;
    private final LocalDateTime lastLoginAt;
    private final LocalDateTime createdAt;
    private final long requestCount;

    @SuppressWarnings("java:S107")
    public UserRowDto(Long id, String fullName, String identifier, String email, Role role,
                      String courseOrDepartment, String yearLevelOrSection, AccountStatus accountStatus,
                      LocalDateTime lastLoginAt, LocalDateTime createdAt, long requestCount) {
        this.id = id;
        this.fullName = fullName;
        this.identifier = identifier;
        this.email = email;
        this.role = role;
        this.courseOrDepartment = courseOrDepartment;
        this.yearLevelOrSection = yearLevelOrSection;
        this.accountStatus = accountStatus;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
        this.requestCount = requestCount;
    }

    public static UserRowDto from(User u, long requestCount) {
        return new UserRowDto(u.getId(), u.getFullName(), u.getDisplayIdentifier(), u.getEmail(), u.getRole(),
                u.getCourseOrDepartment(), u.getYearLevelOrSection(), u.getAccountStatus(),
                u.getLastLoginAt(), u.getCreatedAt(), requestCount);
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public String getCourseOrDepartment() {
        return courseOrDepartment;
    }

    public String getYearLevelOrSection() {
        return yearLevelOrSection;
    }

    public AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public boolean isActive() {
        return accountStatus == AccountStatus.ACTIVE;
    }
}
