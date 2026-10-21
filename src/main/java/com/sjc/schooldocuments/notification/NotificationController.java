package com.sjc.schooldocuments.notification;

import com.sjc.schooldocuments.common.CurrentUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public NotificationController(NotificationService notificationService,
                                  CurrentUserService currentUserService) {
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public String index(@RequestParam(value = "page", defaultValue = "0") int page, Model model) {
        model.addAttribute("activeNav", "notifications");
        model.addAttribute("notifications", notificationService.pageForCurrentUser(page, 15));
        model.addAttribute("notificationUserId", currentUserService.require().getId());
        return "notifications/index";
    }

    @PostMapping("/{id}/read")
    public String markRead(@PathVariable Long id,
                           @RequestParam(value = "returnTo", required = false) String returnTo,
                           RedirectAttributes redirectAttributes) {
        try {
            notificationService.markRead(id);
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:" + (returnTo != null && returnTo.startsWith("/") ? returnTo : "/notifications");
    }

    @PostMapping("/read-all")
    public String markAllRead(@RequestParam(value = "returnTo", required = false) String returnTo,
                              RedirectAttributes redirectAttributes) {
        int updated = notificationService.markAllRead();
        redirectAttributes.addFlashAttribute("successMessage",
                updated == 0 ? "Everything was already read." : updated + " notifications marked as read.");
        return "redirect:" + (returnTo != null && returnTo.startsWith("/") ? returnTo : "/notifications");
    }

    /**
     * Polled by the header so the unread badge stays current without a reload.
     */
    @GetMapping("/unread-count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> unreadCount() {
        var user = currentUserService.current();
        if (user.isEmpty()) {
            return ResponseEntity.ok(Map.of("count", 0, "authenticated", false));
        }
        return ResponseEntity.ok(Map.of(
                "count", notificationService.unreadCountFor(user.get().getId()),
                "authenticated", true));
    }
}
