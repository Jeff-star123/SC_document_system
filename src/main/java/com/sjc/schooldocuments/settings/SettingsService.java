package com.sjc.schooldocuments.settings;

import com.sjc.schooldocuments.audit.AuditService;
import com.sjc.schooldocuments.common.CurrentUserService;
import com.sjc.schooldocuments.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class SettingsService {

    public static final String REQUEST_SEQUENCE = "request_sequence";
    public static final String PICKUP_LOCATION = "pickup_location";
    public static final String OFFICE_HOURS = "office_hours";
    public static final String CONTACT_EMAIL = "contact_email";
    public static final String CONTACT_NUMBER = "contact_number";

    private final SystemSettingRepository repository;
    private final CurrentUserService currentUserService;
    private final AuditService auditService;

    public SettingsService(SystemSettingRepository repository, CurrentUserService currentUserService,
                           AuditService auditService) {
        this.repository = repository;
        this.currentUserService = currentUserService;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public String value(String key, String fallback) {
        return repository.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .filter(v -> !v.isBlank())
                .orElse(fallback);
    }

    @Transactional(readOnly = true)
    public Map<String, String> all() {
        Map<String, String> values = new LinkedHashMap<>();
        for (SystemSetting setting : repository.findAllByOrderBySettingKeyAsc()) {
            values.put(setting.getSettingKey(), setting.getSettingValue());
        }
        return values;
    }

    /**
     * Reserves the next request sequence value. Must run inside the same
     * transaction as the request insert so the number is never wasted on a
     * rolled-back submission.
     */
    @Transactional
    public long nextRequestSequence() {
        SystemSetting setting = repository.findBySettingKeyForUpdate(REQUEST_SEQUENCE)
                .orElseGet(() -> {
                    SystemSetting created = new SystemSetting();
                    created.setSettingKey(REQUEST_SEQUENCE);
                    created.setSettingValue("0");
                    return repository.save(created);
                });
        long next = Long.parseLong(setting.getSettingValue().trim()) + 1;
        setting.setSettingValue(Long.toString(next));
        repository.save(setting);
        return next;
    }

    @Transactional
    public void updateAll(Map<String, String> updates) {
        User actor = currentUserService.require();
        for (Map.Entry<String, String> entry : updates.entrySet()) {
            if (REQUEST_SEQUENCE.equals(entry.getKey())) {
                continue;
            }
            repository.findBySettingKey(entry.getKey()).ifPresent(setting -> {
                setting.setSettingValue(entry.getValue() == null ? "" : entry.getValue().trim());
                setting.setUpdatedBy(actor);
                repository.save(setting);
            });
        }
        auditService.record("SETTINGS_UPDATED", "SystemSetting", null,
                "Updated system settings: " + String.join(", ", updates.keySet()));
    }
}
