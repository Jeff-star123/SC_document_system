package com.sjc.schooldocuments.staff;

import com.sjc.schooldocuments.common.CurrentUserService;
import com.sjc.schooldocuments.documenttype.DocumentTypeService;
import com.sjc.schooldocuments.report.ReportData;
import com.sjc.schooldocuments.report.ReportFilter;
import com.sjc.schooldocuments.report.ReportService;
import com.sjc.schooldocuments.request.RequestService;
import com.sjc.schooldocuments.request.dto.ProcessRequestForm;
import com.sjc.schooldocuments.request.dto.ReadyForPickupForm;
import com.sjc.schooldocuments.request.dto.ReleaseRecordForm;
import com.sjc.schooldocuments.request.dto.RequestFilter;
import com.sjc.schooldocuments.request.dto.StaffNotesForm;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;
    private final RequestService requestService;
    private final DocumentTypeService documentTypeService;
    private final ReportService reportService;
    private final CurrentUserService currentUserService;

    public StaffController(StaffService staffService, RequestService requestService,
                           DocumentTypeService documentTypeService, ReportService reportService,
                           CurrentUserService currentUserService) {
        this.staffService = staffService;
        this.requestService = requestService;
        this.documentTypeService = documentTypeService;
        this.reportService = reportService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("activeNav", "dashboard");
        model.addAttribute("data", staffService.dashboard());
        return "staff/dashboard";
    }

    @GetMapping("/requests")
    public String requests(@ModelAttribute("filter") RequestFilter filter, Model model) {
        model.addAttribute("activeNav", "requests");
        model.addAttribute("page", requestService.search(filter));
        model.addAttribute("documentTypes", documentTypeService.all());
        return "staff/requests";
    }

    @PostMapping("/requests/{id}/process")
    public String process(@PathVariable Long id, @ModelAttribute ProcessRequestForm form,
                          RedirectAttributes redirectAttributes) {
        requestService.startProcessing(id, currentUserService.require(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Request moved to processing.");
        return "redirect:/staff/requests/" + id;
    }

    @PostMapping("/requests/{id}/ready")
    public String ready(@PathVariable Long id, @Valid @ModelAttribute ReadyForPickupForm form,
                        BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", bindingResult.getFieldErrors().stream()
                    .map(org.springframework.validation.FieldError::getDefaultMessage)
                    .findFirst().orElse("Check the pick-up instructions and try again."));
            return "redirect:/staff/requests/" + id;
        }
        requestService.markReadyForPickup(id, currentUserService.require(), form);
        redirectAttributes.addFlashAttribute("successMessage",
                "Marked ready for pick-up. The student has been notified.");
        return "redirect:/staff/requests/" + id;
    }

    @PostMapping("/requests/{id}/release")
    public String release(@PathVariable Long id, @ModelAttribute ReleaseRecordForm form,
                          RedirectAttributes redirectAttributes) {
        requestService.recordRelease(id, currentUserService.require(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Physical release recorded.");
        return "redirect:/staff/requests/" + id;
    }

    @PostMapping("/requests/{id}/notes")
    public String notes(@PathVariable Long id, @ModelAttribute StaffNotesForm form,
                        RedirectAttributes redirectAttributes) {
        requestService.updateStaffNotes(id, currentUserService.require(), form);
        redirectAttributes.addFlashAttribute("successMessage", "Internal notes saved.");
        return "redirect:/staff/requests/" + id;
    }

    @GetMapping("/requests/export.csv")
    public void exportRequests(@ModelAttribute RequestFilter filter, HttpServletResponse response)
            throws IOException {
        prepareCsv(response, "document-requests.csv");
        try (PrintWriter writer = response.getWriter()) {
            writer.print('\ufeff');
            requestService.exportCsv(writer, filter, true);
        }
    }

    @GetMapping("/reports")
    public String reports(@ModelAttribute("filter") ReportFilter filter, Model model) {
        model.addAttribute("activeNav", "reports");
        model.addAttribute("report", reportService.build(filter));
        model.addAttribute("documentTypes", documentTypeService.all());
        return "staff/reports";
    }

    @GetMapping("/reports/export.csv")
    public void exportReport(@ModelAttribute ReportFilter filter, HttpServletResponse response)
            throws IOException {
        ReportData report = reportService.build(filter);
        prepareCsv(response, "registrar-report.csv");
        try (PrintWriter writer = response.getWriter()) {
            writer.print('\ufeff');
            reportService.exportCsv(writer, report);
        }
    }

    static void prepareCsv(HttpServletResponse response, String filename) {
        response.setContentType("text/csv; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
    }
}
