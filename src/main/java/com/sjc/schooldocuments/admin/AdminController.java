package com.sjc.schooldocuments.admin;

import com.sjc.schooldocuments.audit.AuditService;
import com.sjc.schooldocuments.common.CurrentUserService;
import com.sjc.schooldocuments.common.exception.BusinessRuleException;
import com.sjc.schooldocuments.documenttype.DocumentTypeService;
import com.sjc.schooldocuments.documenttype.dto.DocumentTypeForm;
import com.sjc.schooldocuments.report.ReportData;
import com.sjc.schooldocuments.report.ReportFilter;
import com.sjc.schooldocuments.report.ReportService;
import com.sjc.schooldocuments.request.RequestService;
import com.sjc.schooldocuments.request.dto.ApprovalDecisionForm;
import com.sjc.schooldocuments.request.dto.RequestFilter;
import com.sjc.schooldocuments.settings.SettingsService;
import com.sjc.schooldocuments.user.AccountStatus;
import com.sjc.schooldocuments.user.Role;
import com.sjc.schooldocuments.user.UserService;
import com.sjc.schooldocuments.user.dto.PasswordResetForm;
import com.sjc.schooldocuments.user.dto.UserCreateForm;
import com.sjc.schooldocuments.user.dto.UserEditForm;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;
    private final RequestService requestService;
    private final DocumentTypeService documentTypeService;
    private final ReportService reportService;
    private final SettingsService settingsService;
    private final AuditService auditService;
    private final CurrentUserService currentUserService;

    public AdminController(AdminService adminService, UserService userService, RequestService requestService,
                           DocumentTypeService documentTypeService, ReportService reportService,
                           SettingsService settingsService, AuditService auditService,
                           CurrentUserService currentUserService) {
        this.adminService = adminService;
        this.userService = userService;
        this.requestService = requestService;
        this.documentTypeService = documentTypeService;
        this.reportService = reportService;
        this.settingsService = settingsService;
        this.auditService = auditService;
        this.currentUserService = currentUserService;
    }

    // ------------------------------------------------------------------
    // Dashboard
    // ------------------------------------------------------------------

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeNav", "dashboard");
        model.addAttribute("data", adminService.dashboard());
        return "admin/dashboard";
    }

    // ------------------------------------------------------------------
    // Approval queue
    // ------------------------------------------------------------------

    @GetMapping("/approvals")
    public String approvals(@ModelAttribute("filter") RequestFilter filter, Model model) {
        filter.setAwaitingApprovalOnly(true);
        model.addAttribute("activeNav", "approvals");
        model.addAttribute("page", requestService.search(filter));
        model.addAttribute("documentTypes", documentTypeService.all());
        return "admin/approvals";
    }

    @PostMapping("/approvals/{id}")
    public String decide(@PathVariable Long id, @ModelAttribute ApprovalDecisionForm form,
                         RedirectAttributes redirectAttributes) {
        try {
            requestService.decide(id, currentUserService.require(), form);
            redirectAttributes.addFlashAttribute("successMessage",
                    form.isApprove() ? "Request approved. The registrar can now process it."
                            : "Request rejected. The student has been told why.");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/requests/" + id;
        }
        return "redirect:/admin/approvals";
    }

    // ------------------------------------------------------------------
    // All requests (read-only, with full history available on each record)
    // ------------------------------------------------------------------

    @GetMapping("/requests")
    public String requests(@ModelAttribute("filter") RequestFilter filter, Model model) {
        model.addAttribute("activeNav", "requests");
        model.addAttribute("page", requestService.search(filter));
        model.addAttribute("documentTypes", documentTypeService.all());
        return "admin/requests";
    }

    @GetMapping("/requests/export.csv")
    public void exportRequests(@ModelAttribute RequestFilter filter, HttpServletResponse response)
            throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"all-requests.csv\"");
        try (PrintWriter writer = response.getWriter()) {
            writer.print('\ufeff');
            requestService.exportCsv(writer, filter, true);
        }
    }

    // ------------------------------------------------------------------
    // User management
    // ------------------------------------------------------------------

    @GetMapping("/users")
    public String users(@RequestParam(value = "q", required = false) String q,
                        @RequestParam(value = "role", required = false) Role role,
                        @RequestParam(value = "status", required = false) AccountStatus status,
                        @RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        model.addAttribute("activeNav", "users");
        model.addAttribute("page", userService.search(q, role, status, page, 12));
        model.addAttribute("q", q);
        model.addAttribute("roleFilter", role);
        model.addAttribute("statusFilter", status);
        model.addAttribute("roles", Role.values());
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        model.addAttribute("activeNav", "users");
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new UserCreateForm());
        }
        model.addAttribute("roles", Role.values());
        model.addAttribute("mode", "create");
        return "admin/user-form";
    }

    @PostMapping("/users")
    public String createUser(@Valid @ModelAttribute("form") UserCreateForm form, BindingResult bindingResult,
                             Model model, RedirectAttributes redirectAttributes) {
        if (!form.getPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "The two password fields do not match.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeNav", "users");
            model.addAttribute("roles", Role.values());
            model.addAttribute("mode", "create");
            return "admin/user-form";
        }
        try {
            userService.create(form);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Account created for " + form.getEmail() + ". Share the password securely.");
            return "redirect:/admin/users";
        } catch (BusinessRuleException ex) {
            model.addAttribute("activeNav", "users");
            model.addAttribute("roles", Role.values());
            model.addAttribute("mode", "create");
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/user-form";
        }
    }

    @GetMapping("/users/{id}/edit")
    public String editUser(@PathVariable Long id, Model model) {
        var user = userService.require(id);
        UserEditForm form = new UserEditForm();
        form.setId(user.getId());
        form.setRole(user.getRole());
        form.setFirstName(user.getFirstName());
        form.setMiddleName(user.getMiddleName());
        form.setLastName(user.getLastName());
        form.setEmail(user.getEmail());
        form.setStudentNumber(user.getStudentNumber());
        form.setEmployeeNumber(user.getEmployeeNumber());
        form.setCourseOrDepartment(user.getCourseOrDepartment());
        form.setYearLevelOrSection(user.getYearLevelOrSection());
        form.setAccountStatus(user.getAccountStatus());
        model.addAttribute("activeNav", "users");
        model.addAttribute("form", form);
        model.addAttribute("roles", Role.values());
        model.addAttribute("statuses", AccountStatus.values());
        model.addAttribute("mode", "edit");
        model.addAttribute("account", user);
        return "admin/user-form";
    }

    @PostMapping("/users/{id}")
    public String updateUser(@PathVariable Long id, @Valid @ModelAttribute("form") UserEditForm form,
                             BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        form.setId(id);
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeNav", "users");
            model.addAttribute("roles", Role.values());
            model.addAttribute("statuses", AccountStatus.values());
            model.addAttribute("mode", "edit");
            model.addAttribute("account", userService.require(id));
            return "admin/user-form";
        }
        try {
            userService.update(form);
            redirectAttributes.addFlashAttribute("successMessage", "Account details saved.");
            return "redirect:/admin/users";
        } catch (BusinessRuleException ex) {
            model.addAttribute("activeNav", "users");
            model.addAttribute("roles", Role.values());
            model.addAttribute("statuses", AccountStatus.values());
            model.addAttribute("mode", "edit");
            model.addAttribute("account", userService.require(id));
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/user-form";
        }
    }

    @PostMapping("/users/{id}/toggle-active")
    public String toggleActive(@PathVariable Long id, @RequestParam boolean active,
                               RedirectAttributes redirectAttributes) {
        try {
            userService.setActive(id, active, currentUserService.require());
            redirectAttributes.addFlashAttribute("successMessage",
                    active ? "Account activated." : "Account deactivated. The holder cannot sign in.");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/reset-password")
    public String resetPassword(@PathVariable Long id, @Valid @ModelAttribute PasswordResetForm form,
                                BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (!form.getNewPassword().equals(form.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "mismatch", "The two password fields do not match.");
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getFieldErrors().stream()
                    .map(org.springframework.validation.FieldError::getDefaultMessage)
                    .findFirst().orElse("Check the password fields and try again."));
            return "redirect:/admin/users/" + id + "/edit";
        }
        try {
            userService.resetPassword(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Password reset. Share it securely.");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/users/" + id + "/edit";
    }

    // ------------------------------------------------------------------
    // Document types
    // ------------------------------------------------------------------

    @GetMapping("/document-types")
    public String documentTypes(Model model) {
        model.addAttribute("activeNav", "documentTypes");
        model.addAttribute("documentTypes", documentTypeService.all());
        return "admin/document-types";
    }

    @GetMapping("/document-types/new")
    public String newDocumentType(Model model) {
        model.addAttribute("activeNav", "documentTypes");
        if (!model.containsAttribute("form")) {
            DocumentTypeForm form = new DocumentTypeForm();
            form.setActive(true);
            model.addAttribute("form", form);
        }
        model.addAttribute("mode", "create");
        return "admin/document-type-form";
    }

    @GetMapping("/document-types/{id}/edit")
    public String editDocumentType(@PathVariable Long id, Model model) {
        model.addAttribute("activeNav", "documentTypes");
        model.addAttribute("form", documentTypeService.toForm(id));
        model.addAttribute("mode", "edit");
        return "admin/document-type-form";
    }

    @PostMapping("/document-types")
    public String saveDocumentType(@Valid @ModelAttribute("form") DocumentTypeForm form,
                                   BindingResult bindingResult, Model model,
                                   RedirectAttributes redirectAttributes) {
        boolean creating = form.getId() == null;
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeNav", "documentTypes");
            model.addAttribute("mode", creating ? "create" : "edit");
            return "admin/document-type-form";
        }
        try {
            documentTypeService.save(form);
            redirectAttributes.addFlashAttribute("successMessage",
                    creating ? "Document type added." : "Document type updated.");
            return "redirect:/admin/document-types";
        } catch (BusinessRuleException ex) {
            model.addAttribute("activeNav", "documentTypes");
            model.addAttribute("mode", creating ? "create" : "edit");
            model.addAttribute("errorMessage", ex.getMessage());
            return "admin/document-type-form";
        }
    }

    @PostMapping("/document-types/{id}/toggle")
    public String toggleDocumentType(@PathVariable Long id, @RequestParam boolean active,
                                     RedirectAttributes redirectAttributes) {
        try {
            documentTypeService.setActive(id, active);
            redirectAttributes.addFlashAttribute("successMessage",
                    active ? "Document type enabled for students." : "Document type disabled.");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/document-types";
    }

    // ------------------------------------------------------------------
    // Reports
    // ------------------------------------------------------------------

    @GetMapping("/reports")
    public String reports(@ModelAttribute("filter") ReportFilter filter, Model model) {
        model.addAttribute("activeNav", "reports");
        model.addAttribute("report", reportService.build(filter));
        model.addAttribute("documentTypes", documentTypeService.all());
        return "admin/reports";
    }

    @GetMapping("/reports/export.csv")
    public void exportReport(@ModelAttribute ReportFilter filter, HttpServletResponse response)
            throws IOException {
        ReportData report = reportService.build(filter);
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"system-report.csv\"");
        try (PrintWriter writer = response.getWriter()) {
            writer.print('\ufeff');
            reportService.exportCsv(writer, report);
        }
    }

    // ------------------------------------------------------------------
    // Audit log and settings
    // ------------------------------------------------------------------

    @GetMapping("/audit-logs")
    public String auditLogs(@RequestParam(value = "action", required = false) String action,
                            @RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        model.addAttribute("activeNav", "audit");
        model.addAttribute("page", auditService.search(action, page, 20));
        model.addAttribute("actionFilter", action);
        model.addAttribute("actions", auditService.distinctActions());
        return "admin/audit-logs";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("activeNav", "settings");
        model.addAttribute("settings", settingsService.all());
        return "admin/settings";
    }

    @PostMapping("/settings")
    public String saveSettings(@RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
        Map<String, String> updates = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("setting_")) {
                updates.put(entry.getKey().substring("setting_".length()), entry.getValue());
            }
        }
        settingsService.updateAll(updates);
        redirectAttributes.addFlashAttribute("successMessage", "Settings saved.");
        return "redirect:/admin/settings";
    }
}
