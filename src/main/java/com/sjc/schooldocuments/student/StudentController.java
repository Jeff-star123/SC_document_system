package com.sjc.schooldocuments.student;

import com.sjc.schooldocuments.common.CurrentUserService;
import com.sjc.schooldocuments.common.exception.BusinessRuleException;
import com.sjc.schooldocuments.documenttype.DocumentTypeService;
import com.sjc.schooldocuments.request.RequestService;
import com.sjc.schooldocuments.request.dto.RequestCreateForm;
import com.sjc.schooldocuments.request.dto.RequestFilter;
import com.sjc.schooldocuments.user.User;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final StudentDashboardService dashboardService;
    private final RequestService requestService;
    private final DocumentTypeService documentTypeService;
    private final CurrentUserService currentUserService;

    public StudentController(StudentDashboardService dashboardService, RequestService requestService,
                             DocumentTypeService documentTypeService, CurrentUserService currentUserService) {
        this.dashboardService = dashboardService;
        this.requestService = requestService;
        this.documentTypeService = documentTypeService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeNav", "dashboard");
        model.addAttribute("data", dashboardService.forCurrentStudent());
        return "student/dashboard";
    }

    @GetMapping("/requests")
    public String requests(@ModelAttribute("filter") RequestFilter filter, Model model) {
        User student = currentUserService.require();
        model.addAttribute("activeNav", "requests");
        model.addAttribute("page", requestService.findForStudent(student.getId(), filter));
        model.addAttribute("documentTypes", documentTypeService.active());
        return "student/requests";
    }

    @GetMapping("/requests/new")
    public String newRequest(Model model) {
        model.addAttribute("activeNav", "new");
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RequestCreateForm());
        }
        model.addAttribute("documentTypes", documentTypeService.active());
        model.addAttribute("student", currentUserService.require());
        return "student/new-request";
    }

    @PostMapping("/requests")
    public String submit(@Valid @ModelAttribute("form") RequestCreateForm form, BindingResult bindingResult,
                         Model model, RedirectAttributes redirectAttributes) {
        User student = currentUserService.require();
        if (bindingResult.hasErrors()) {
            model.addAttribute("activeNav", "new");
            model.addAttribute("documentTypes", documentTypeService.active());
            model.addAttribute("student", student);
            return "student/new-request";
        }
        try {
            String reference = requestService.createRequest(student, form);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Request " + reference + " submitted. Track its progress here.");
            return "redirect:/student/requests";
        } catch (BusinessRuleException ex) {
            model.addAttribute("activeNav", "new");
            model.addAttribute("documentTypes", documentTypeService.active());
            model.addAttribute("student", student);
            model.addAttribute("errorMessage", ex.getMessage());
            return "student/new-request";
        }
    }
}
