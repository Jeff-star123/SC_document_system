package com.sjc.schooldocuments.staff;

import com.sjc.schooldocuments.request.DocumentRequestRepository;
import com.sjc.schooldocuments.request.RequestStatus;
import com.sjc.schooldocuments.request.dto.RequestSummaryDto;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StaffService {

    private static final int RECENT_LIMIT = 6;

    private final DocumentRequestRepository requestRepository;

    public StaffService(DocumentRequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Transactional(readOnly = true)
    public StaffDashboardData dashboard() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        long pending = requestRepository.countByStatus(RequestStatus.PENDING);
        long processing = requestRepository.countByStatus(RequestStatus.PROCESSING);
        long ready = requestRepository.countByStatus(RequestStatus.READY_FOR_PICKUP);

        List<RequestSummaryDto> recent = requestRepository.findAll(
                        PageRequest.of(0, RECENT_LIMIT, Sort.by(Sort.Direction.DESC, "createdAt")))
                .map(RequestSummaryDto::from).getContent();

        List<RequestSummaryDto> recentlyUpdated = requestRepository.findAll(
                        PageRequest.of(0, RECENT_LIMIT, Sort.by(Sort.Direction.DESC, "updatedAt")))
                .map(RequestSummaryDto::from).getContent();

        return new StaffDashboardData(
                pending + processing,
                pending,
                processing,
                ready,
                requestRepository.countAwaitingApproval(),
                requestRepository.countReadyToProcess(),
                requestRepository.countByClaimedAtBetween(startOfMonth, now),
                recent,
                recentlyUpdated);
    }
}
