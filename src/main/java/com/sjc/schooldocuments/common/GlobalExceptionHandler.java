package com.sjc.schooldocuments.common;

import com.sjc.schooldocuments.common.exception.BusinessRuleException;
import com.sjc.schooldocuments.common.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.net.URI;

/**
 * Keeps user-facing failures readable: rules the user can fix come back as a
 * flash message on the page they were using; genuine faults get a plain page
 * and a server-side log entry without exposing internals.
 *
 * <p>The banner and page title need the institution and system names, but the
 * shared {@code GlobalModelAdvice} attributes are only applied to normal
 * controller methods — not to views produced here. They are therefore added
 * explicitly for every error view.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final String institutionName;
    private final String systemName;

    public GlobalExceptionHandler(@Value("${app.institution.name}") String institutionName,
                                  @Value("${app.institution.system-name}") String systemName) {
        this.institutionName = institutionName;
        this.systemName = systemName;
    }

    @ExceptionHandler(BusinessRuleException.class)
    public String handleBusinessRule(BusinessRuleException ex, HttpServletRequest request,
                                     RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        String back = safeReferer(request);
        return "redirect:" + back;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model,
                                 HttpServletResponse response) {
        decorate(model, ex.getMessage(), "Not found");
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return "error/not-found";
    }

    /** An unmatched URL falls through to static-resource handling and lands here. */
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResource(NoResourceFoundException ex, Model model,
                                   HttpServletResponse response) {
        decorate(model, "That page could not be found.", "Not found");
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return "error/not-found";
    }

    @ExceptionHandler(AccessDeniedException.class)
    public String handleAccessDenied(Model model, HttpServletResponse response) {
        decorate(model, "You do not have permission to open that page.", "Access denied");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return "error/access-denied";
    }

    @ExceptionHandler(Exception.class)
    public String handleUnexpected(Exception ex, Model model, HttpServletRequest request,
                                   HttpServletResponse response) {
        log.error("Unhandled failure on {} {}", request.getMethod(), request.getRequestURI(), ex);
        decorate(model, "Something went wrong on our side. The registrar's office has been notified.",
                "Something went wrong");
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return "error/generic";
    }

    private void decorate(Model model, String message, String title) {
        model.addAttribute("message", message);
        model.addAttribute("pageTitle", title);
        model.addAttribute("institutionName", institutionName);
        model.addAttribute("systemName", systemName);
    }

    /**
     * Only follows a referer from this same application; anything else returns
     * to the dashboard.
     */
    private String safeReferer(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) {
            return "/";
        }
        try {
            URI uri = URI.create(referer);
            String host = uri.getHost();
            if (host != null && host.equals(request.getServerName())) {
                String path = uri.getRawPath();
                String query = uri.getRawQuery();
                if (path != null && !path.isBlank()) {
                    return query == null ? path : path + "?" + query;
                }
            }
        } catch (IllegalArgumentException ignored) {
            // fall through to the dashboard
        }
        return "/";
    }
}
