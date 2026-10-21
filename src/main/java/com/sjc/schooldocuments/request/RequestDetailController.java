package com.sjc.schooldocuments.request;

import com.sjc.schooldocuments.common.CurrentUserService;
import com.sjc.schooldocuments.request.dto.RequestDetailDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * One detail view serves all three roles; the template reveals staff and
 * administrator actions only to the role allowed to use them, and the service
 * re-checks ownership before anything is returned.
 */
@Controller
public class RequestDetailController {

    private final RequestService requestService;
    private final CurrentUserService currentUserService;

    public RequestDetailController(RequestService requestService, CurrentUserService currentUserService) {
        this.requestService = requestService;
        this.currentUserService = currentUserService;
    }

    @GetMapping({"/student/requests/{id}", "/staff/requests/{id}", "/admin/requests/{id}"})
    public String detail(@PathVariable Long id, Model model) {
        var viewer = currentUserService.require();
        RequestDetailDto request = requestService.getDetail(id, viewer);
        model.addAttribute("request", request);
        model.addAttribute("activeNav", "requests");
        model.addAttribute("viewerRole", viewer.getRole());
        return "requests/detail";
    }
}
