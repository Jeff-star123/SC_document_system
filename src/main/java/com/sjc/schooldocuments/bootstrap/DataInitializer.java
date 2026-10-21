package com.sjc.schooldocuments.bootstrap;

import com.sjc.schooldocuments.documenttype.DocumentTypeRepository;
import com.sjc.schooldocuments.notification.NotificationService;
import com.sjc.schooldocuments.request.RequestService;
import com.sjc.schooldocuments.request.dto.ProcessRequestForm;
import com.sjc.schooldocuments.request.dto.ReadyForPickupForm;
import com.sjc.schooldocuments.request.dto.RequestCreateForm;
import com.sjc.schooldocuments.request.DocumentRequestRepository;
import com.sjc.schooldocuments.user.AccountStatus;
import com.sjc.schooldocuments.user.Role;
import com.sjc.schooldocuments.user.User;
import com.sjc.schooldocuments.user.UserRepository;
import com.sjc.schooldocuments.user.UserService;
import com.sjc.schooldocuments.user.dto.UserCreateForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Creates the first administrator and, only when explicitly enabled, a small
 * set of clearly-labelled demonstration accounts. No default password ships
 * with the application: both steps require values supplied through the
 * environment.
 */
@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final DocumentTypeRepository documentTypeRepository;
    private final DocumentRequestRepository requestRepository;
    private final RequestService requestService;
    private final NotificationService notificationService;

    private final String adminEmail;
    private final String adminPassword;
    private final String adminFirstName;
    private final String adminLastName;
    private final boolean demoEnabled;
    private final String demoPassword;

    public DataInitializer(UserRepository userRepository, UserService userService,
                           DocumentTypeRepository documentTypeRepository,
                           DocumentRequestRepository requestRepository, RequestService requestService,
                           NotificationService notificationService,
                           @Value("${app.bootstrap.admin-email:}") String adminEmail,
                           @Value("${app.bootstrap.admin-password:}") String adminPassword,
                           @Value("${app.bootstrap.admin-first-name:System}") String adminFirstName,
                           @Value("${app.bootstrap.admin-last-name:Administrator}") String adminLastName,
                           @Value("${app.demo-data.enabled:false}") boolean demoEnabled,
                           @Value("${app.demo-data.password:}") String demoPassword) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.documentTypeRepository = documentTypeRepository;
        this.requestRepository = requestRepository;
        this.requestService = requestService;
        this.notificationService = notificationService;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.adminFirstName = adminFirstName;
        this.adminLastName = adminLastName;
        this.demoEnabled = demoEnabled;
        this.demoPassword = demoPassword;
    }

    @Override
    public void run(ApplicationArguments args) {
        User admin = bootstrapAdministrator().orElse(null);
        if (demoEnabled) {
            seedDemoData(admin);
        }
    }

    private Optional<User> bootstrapAdministrator() {
        if (userRepository.countByRole(Role.ADMIN) > 0) {
            log.info("An administrator account already exists; bootstrap step skipped.");
            return userRepository.findByRoleAndAccountStatus(Role.ADMIN, AccountStatus.ACTIVE)
                    .stream().findFirst();
        }
        if (adminEmail.isBlank() || adminPassword.isBlank()) {
            log.warn("No administrator account exists yet. Set BOOTSTRAP_ADMIN_EMAIL and "
                    + "BOOTSTRAP_ADMIN_PASSWORD, then restart to create the first administrator. "
                    + "The password must be at least 10 characters.");
            return Optional.empty();
        }
        if (adminPassword.length() < 10) {
            log.error("BOOTSTRAP_ADMIN_PASSWORD is shorter than 10 characters. "
                    + "No administrator was created.");
            return Optional.empty();
        }

        UserCreateForm form = new UserCreateForm();
        form.setRole(Role.ADMIN);
        form.setFirstName(adminFirstName);
        form.setLastName(adminLastName);
        form.setEmail(adminEmail);
        form.setPassword(adminPassword);
        form.setConfirmPassword(adminPassword);
        form.setCourseOrDepartment("Administration");
        User created = userService.create(form);
        log.info("Bootstrap administrator created for {}. Change this password after signing in.",
                created.getEmail());
        return Optional.of(created);
    }

    private void seedDemoData(User admin) {
        if (demoPassword.isBlank() || demoPassword.length() < 10) {
            log.warn("SEED_DEMO_USERS is enabled but SEED_PASSWORD is missing or shorter than 10 characters; "
                    + "no demonstration accounts were created.");
            return;
        }
        if (requestRepository.count() > 0) {
            log.info("Requests already exist; demonstration data step skipped.");
            return;
        }

        User studentA = createIfAbsent("ma.delacruz@student.sjudemanila.edu.ph", "2026-0001",
                "Juan", "Dela Cruz", Role.STUDENT, "BS Information Technology", "3rd Year - Section A");
        User studentB = createIfAbsent("li.santos@student.sjudemanila.edu.ph", "2026-0002",
                "Maria", "Santos", Role.STUDENT, "BS Accountancy", "2nd Year - Section B");
        User staff = createIfAbsent("a.reyes@sjudemanila.edu.ph", null,
                "Ana", "Reyes", Role.STAFF, "Registrar's Office", null);

        if (documentTypeRepository.count() == 0) {
            log.warn("Reference document types are missing; demonstration requests were not created.");
            return;
        }
        try {
            Long form137 = documentTypeRepository.findByNameIgnoreCase("Form 137")
                    .map(dt -> dt.getId()).orElse(null);
            Long goodMoral = documentTypeRepository.findByNameIgnoreCase("Good Moral Certificate")
                    .map(dt -> dt.getId()).orElse(null);
            Long reportCard = documentTypeRepository.findByNameIgnoreCase("Report Card")
                    .map(dt -> dt.getId()).orElse(null);

            if (form137 != null) {
                submit(studentA, form137, "Board examination application", 2);
            }
            if (reportCard != null) {
                String reference = submit(studentB, reportCard, "Transfer to another school", 1);
                if (admin != null) {
                    var approval = new com.sjc.schooldocuments.request.dto.ApprovalDecisionForm();
                    approval.setApprove(true);
                    approval.setRemarks("Records check complete.");
                    requestService.decide(idOf(reference), admin, approval);
                    ProcessRequestForm process = new ProcessRequestForm();
                    process.setRemarks("Verified against the student's record.");
                    requestService.startProcessing(idOf(reference), staff, process);
                }
            }
            if (goodMoral != null) {
                String reference = submit(studentA, goodMoral, "Employment requirement", 1);
                ProcessRequestForm process = new ProcessRequestForm();
                process.setRemarks("Verified and prepared.");
                requestService.startProcessing(idOf(reference), staff, process);
                ReadyForPickupForm ready = new ReadyForPickupForm();
                ready.setPickupInstructions("Bring your school ID and this reference number to the counter.");
                requestService.markReadyForPickup(idOf(reference), staff, ready);
            }
            log.info("Demonstration accounts and requests created (SEED_DEMO_USERS=true).");
        } catch (RuntimeException ex) {
            log.warn("Demonstration data could not be created: {}", ex.getMessage());
        }
    }

    private Long idOf(String reference) {
        return requestRepository.findByReferenceNumber(reference)
                .orElseThrow(() -> new IllegalStateException("Missing seed request " + reference))
                .getId();
    }

    private String submit(User student, Long documentTypeId, String purpose, int quantity) {
        RequestCreateForm form = new RequestCreateForm();
        form.setDocumentTypeId(documentTypeId);
        form.setPurpose(purpose);
        form.setQuantity(quantity);
        form.setConfirmAccurate(true);
        return requestService.createRequest(student, form);
    }

    private User createIfAbsent(String email, String studentNumber, String firstName, String lastName,
                                Role role, String course, String yearLevel) {
        return userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            UserCreateForm form = new UserCreateForm();
            form.setRole(role);
            form.setFirstName(firstName);
            form.setLastName(lastName);
            form.setEmail(email);
            form.setStudentNumber(studentNumber);
            form.setCourseOrDepartment(course);
            form.setYearLevelOrSection(yearLevel);
            form.setPassword(demoPassword);
            form.setConfirmPassword(demoPassword);
            User user = userService.create(form);
            notificationService.notifyUser(user, null, com.sjc.schooldocuments.notification.NotificationType.ACCOUNT_UPDATED,
                    "Welcome to the School Documents Request System",
                    "Your account is ready. Submit a document request from your dashboard at any time.");
            return user;
        });
    }
}
