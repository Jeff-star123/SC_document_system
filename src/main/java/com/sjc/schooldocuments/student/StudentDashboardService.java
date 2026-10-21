package com.sjc.schooldocuments.student;

import com.sjc.schooldocuments.common.CurrentUserService;
import com.sjc.schooldocuments.notification.NotificationService;
import com.sjc.schooldocuments.request.DocumentRequestRepository;
import com.sjc.schooldocuments.request.RequestStatus;
import com.sjc.schooldocuments.request.dto.RequestSummaryDto;
import com.sjc.schooldocuments.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StudentDashboardService {

    private final DocumentRequestRepository requestRepository;
    private final NotificationService notificationService;
    private final CurrentUserService currentUserService;

    public StudentDashboardService(DocumentRequestRepository requestRepository,
                                   NotificationService notificationService,
                                   CurrentUserService currentUserService) {
        this.requestRepository = requestRepository;
        this.notificationService = notificationService;
        this.currentUserService = currentUserService;
    }

    @Transactional(readOnly = true)
    public StudentDashboardData forCurrentStudent() {
        User student = currentUserService.require();
        Long studentId = student.getId();

        List<RequestSummaryDto> recent = requestRepository.findTop5ByStudentIdOrderByCreatedAtDesc(studentId)
                .stream().map(RequestSummaryDto::from).toList();

        return new StudentDashboardData(
                requestRepository.countByStudentId(studentId),
                requestRepository.countByStudentIdAndStatus(studentId, RequestStatus.PENDING),
                requestRepository.countByStudentIdAndStatus(studentId, RequestStatus.PROCESSING),
                requestRepository.countByStudentIdAndStatus(studentId, RequestStatus.READY_FOR_PICKUP),
                notificationService.unreadCountFor(studentId),
                recent,
                notificationService.latestForCurrentUser(5));
    }
}
