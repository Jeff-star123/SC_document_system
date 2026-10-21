package com.sjc.schooldocuments.user;

import com.sjc.schooldocuments.audit.AuditService;
import com.sjc.schooldocuments.common.exception.BusinessRuleException;
import com.sjc.schooldocuments.common.exception.ResourceNotFoundException;
import com.sjc.schooldocuments.user.dto.PasswordChangeForm;
import com.sjc.schooldocuments.user.dto.PasswordResetForm;
import com.sjc.schooldocuments.user.dto.UserCreateForm;
import com.sjc.schooldocuments.user.dto.UserEditForm;
import com.sjc.schooldocuments.user.dto.UserRowDto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;
    private final com.sjc.schooldocuments.request.DocumentRequestRepository requestRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuditService auditService,
                       com.sjc.schooldocuments.request.DocumentRequestRepository requestRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
        this.requestRepository = requestRepository;
    }

    @Transactional(readOnly = true)
    public User require(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("That account could not be found."));
    }

    @Transactional(readOnly = true)
    public Page<UserRowDto> search(String query, Role role, AccountStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.min(Math.max(1, size), 100),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Specification<User> spec = (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query != null && !query.isBlank()) {
                String needle = "%" + query.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), needle),
                        cb.like(cb.lower(root.get("lastName")), needle),
                        cb.like(cb.lower(root.get("email")), needle),
                        cb.like(cb.lower(cb.coalesce(root.get("studentNumber"), "")), needle),
                        cb.like(cb.lower(cb.coalesce(root.get("employeeNumber"), "")), needle)));
            }
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("accountStatus"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return userRepository.findAll(spec, pageable).map(user -> UserRowDto.from(user,
                user.getRole() == Role.STUDENT ? requestRepository.countByStudentId(user.getId()) : 0));
    }

    @Transactional(readOnly = true)
    public List<User> activeStaff() {
        return userRepository.findByRoleAndAccountStatus(Role.STAFF, AccountStatus.ACTIVE);
    }

    @Transactional
    public User create(UserCreateForm form) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new BusinessRuleException("The two password fields do not match.");
        }
        String email = form.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessRuleException("An account already uses " + email + ".");
        }
        if (form.getRole() == Role.STUDENT) {
            if (isBlank(form.getStudentNumber())) {
                throw new BusinessRuleException("A student account needs a student number.");
            }
            if (userRepository.existsByStudentNumber(form.getStudentNumber().trim())) {
                throw new BusinessRuleException("Student number " + form.getStudentNumber() + " is already registered.");
            }
        } else if (!isBlank(form.getEmployeeNumber())
                && userRepository.existsByEmployeeNumber(form.getEmployeeNumber().trim())) {
            throw new BusinessRuleException("Employee number " + form.getEmployeeNumber() + " is already registered.");
        }

        User user = new User();
        user.setRole(form.getRole());
        user.setFirstName(form.getFirstName().trim());
        user.setMiddleName(blankToNull(form.getMiddleName()));
        user.setLastName(form.getLastName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        user.setStudentNumber(form.getRole() == Role.STUDENT ? form.getStudentNumber().trim() : null);
        user.setEmployeeNumber(form.getRole() == Role.STUDENT ? null : blankToNull(form.getEmployeeNumber()));
        user.setCourseOrDepartment(blankToNull(form.getCourseOrDepartment()));
        user.setYearLevelOrSection(blankToNull(form.getYearLevelOrSection()));
        user.setAccountStatus(AccountStatus.ACTIVE);
        User saved = userRepository.save(user);

        auditService.record("USER_CREATED", "User", saved.getId(),
                "Created " + saved.getRole().getLabel() + " account for " + saved.getFullName()
                        + " (" + saved.getEmail() + ")");
        return saved;
    }

    @Transactional
    public void update(UserEditForm form) {
        User user = require(form.getId());
        Role previousRole = user.getRole();
        AccountStatus previousStatus = user.getAccountStatus();

        String email = form.getEmail().trim().toLowerCase();
        userRepository.findByEmailIgnoreCase(email).ifPresent(existing -> {
            if (!existing.getId().equals(user.getId())) {
                throw new BusinessRuleException("An account already uses " + email + ".");
            }
        });
        if (form.getRole() == Role.STUDENT) {
            if (isBlank(form.getStudentNumber())) {
                throw new BusinessRuleException("A student account needs a student number.");
            }
            userRepository.findByStudentNumber(form.getStudentNumber().trim()).ifPresent(existing -> {
                if (!existing.getId().equals(user.getId())) {
                    throw new BusinessRuleException("Student number " + form.getStudentNumber() + " is already registered.");
                }
            });
        }

        user.setRole(form.getRole());
        user.setFirstName(form.getFirstName().trim());
        user.setMiddleName(blankToNull(form.getMiddleName()));
        user.setLastName(form.getLastName().trim());
        user.setEmail(email);
        user.setStudentNumber(form.getRole() == Role.STUDENT ? form.getStudentNumber().trim() : null);
        user.setEmployeeNumber(form.getRole() == Role.STUDENT ? null : blankToNull(form.getEmployeeNumber()));
        user.setCourseOrDepartment(blankToNull(form.getCourseOrDepartment()));
        user.setYearLevelOrSection(blankToNull(form.getYearLevelOrSection()));
        user.setAccountStatus(form.getAccountStatus());
        userRepository.save(user);

        if (previousRole != user.getRole()) {
            auditService.record("USER_ROLE_CHANGED", "User", user.getId(),
                    user.getFullName() + " moved from " + previousRole.getLabel() + " to " + user.getRole().getLabel());
        }
        if (previousStatus != user.getAccountStatus()) {
            auditService.record(
                    user.getAccountStatus() == AccountStatus.ACTIVE ? "USER_ACTIVATED" : "USER_DEACTIVATED",
                    "User", user.getId(),
                    user.getFullName() + " (" + user.getEmail() + ") set to "
                            + user.getAccountStatus().name().toLowerCase());
        }
        auditService.record("USER_UPDATED", "User", user.getId(), "Updated profile details for " + user.getFullName());
    }

    @Transactional
    public void setActive(Long userId, boolean active, User actor) {
        User user = require(userId);
        if (user.getId().equals(actor.getId()) && !active) {
            throw new BusinessRuleException("You cannot deactivate the account you are signed in with.");
        }
        user.setAccountStatus(active ? AccountStatus.ACTIVE : AccountStatus.INACTIVE);
        userRepository.save(user);
        auditService.record(active ? "USER_ACTIVATED" : "USER_DEACTIVATED", "User", user.getId(),
                user.getFullName() + " (" + user.getEmail() + ") "
                        + (active ? "activated" : "deactivated") + " by " + actor.getFullName());
    }

    @Transactional
    public void resetPassword(Long userId, PasswordResetForm form) {
        User user = require(userId);
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new BusinessRuleException("The two password fields do not match.");
        }
        user.setPasswordHash(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);
        auditService.record("PASSWORD_RESET", "User", user.getId(),
                "Password reset for " + user.getFullName() + " by an administrator.");
    }

    @Transactional
    public void changeOwnPassword(User user, PasswordChangeForm form) {
        if (!passwordEncoder.matches(form.getCurrentPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException("Your current password is incorrect.");
        }
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            throw new BusinessRuleException("The two new password fields do not match.");
        }
        if (passwordEncoder.matches(form.getNewPassword(), user.getPasswordHash())) {
            throw new BusinessRuleException("Choose a password different from your current one.");
        }
        User managed = require(user.getId());
        managed.setPasswordHash(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(managed);
        auditService.record("PASSWORD_CHANGED", "User", managed.getId(), "Password changed by the account owner.");
    }

    @Transactional
    public void touchLastLogin(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginAt(java.time.LocalDateTime.now());
            userRepository.save(user);
        });
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String blankToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }
}
