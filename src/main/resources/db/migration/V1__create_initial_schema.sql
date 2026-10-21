CREATE TABLE users (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    student_number      VARCHAR(50)  NULL,
    employee_number     VARCHAR(50)  NULL,
    first_name          VARCHAR(100) NOT NULL,
    middle_name         VARCHAR(100) NULL,
    last_name           VARCHAR(100) NOT NULL,
    email               VARCHAR(150) NOT NULL,
    password_hash       VARCHAR(255) NOT NULL,
    role                ENUM('STUDENT', 'STAFF', 'ADMIN') NOT NULL,
    course_or_department VARCHAR(150) NULL,
    year_level_or_section VARCHAR(100) NULL,
    account_status      ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    last_login_at       DATETIME     NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email),
    UNIQUE KEY uq_users_student_number (student_number),
    UNIQUE KEY uq_users_employee_number (employee_number),
    KEY idx_users_role (role),
    KEY idx_users_account_status (account_status),
    KEY idx_users_name (last_name, first_name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE document_types (
    id                      BIGINT       NOT NULL AUTO_INCREMENT,
    name                    VARCHAR(150) NOT NULL,
    description             TEXT         NULL,
    is_active               BOOLEAN      NOT NULL DEFAULT TRUE,
    requires_admin_approval BOOLEAN      NOT NULL DEFAULT FALSE,
    instructions            TEXT         NULL,
    internal_notes          TEXT         NULL,
    created_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_document_types_name (name),
    KEY idx_document_types_active (is_active)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE document_requests (
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    reference_number    VARCHAR(40)  NOT NULL,
    student_id          BIGINT       NOT NULL,
    document_type_id    BIGINT       NOT NULL,
    purpose             VARCHAR(500) NOT NULL,
    quantity            INT          NOT NULL DEFAULT 1,
    additional_details  TEXT         NULL,
    status              ENUM('PENDING', 'PROCESSING', 'READY_FOR_PICKUP') NOT NULL DEFAULT 'PENDING',
    approval_status     ENUM('NOT_REQUIRED', 'PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'NOT_REQUIRED',
    approved_by         BIGINT       NULL,
    approved_at         DATETIME     NULL,
    rejection_reason    TEXT         NULL,
    assigned_staff_id   BIGINT       NULL,
    staff_notes         TEXT         NULL,
    pickup_instructions TEXT         NULL,
    claimed_at          DATETIME     NULL,
    released_by         BIGINT       NULL,
    created_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_requests_reference_number (reference_number),
    KEY idx_requests_student (student_id),
    KEY idx_requests_document_type (document_type_id),
    KEY idx_requests_status (status),
    KEY idx_requests_approval_status (approval_status),
    KEY idx_requests_created_at (created_at),
    KEY idx_requests_assigned_staff (assigned_staff_id),
    KEY idx_requests_approved_by (approved_by),
    KEY idx_requests_released_by (released_by),
    CONSTRAINT fk_requests_student FOREIGN KEY (student_id) REFERENCES users (id),
    CONSTRAINT fk_requests_document_type FOREIGN KEY (document_type_id) REFERENCES document_types (id),
    CONSTRAINT fk_requests_approved_by FOREIGN KEY (approved_by) REFERENCES users (id),
    CONSTRAINT fk_requests_assigned_staff FOREIGN KEY (assigned_staff_id) REFERENCES users (id),
    CONSTRAINT fk_requests_released_by FOREIGN KEY (released_by) REFERENCES users (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE request_status_history (
    id                  BIGINT      NOT NULL AUTO_INCREMENT,
    request_id          BIGINT      NOT NULL,
    changed_by          BIGINT      NOT NULL,
    old_status          VARCHAR(50) NULL,
    new_status          VARCHAR(50) NULL,
    old_approval_status VARCHAR(50) NULL,
    new_approval_status VARCHAR(50) NULL,
    remarks             TEXT        NULL,
    created_at          DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_history_request (request_id),
    KEY idx_history_changed_by (changed_by),
    KEY idx_history_created_at (created_at),
    CONSTRAINT fk_history_request FOREIGN KEY (request_id) REFERENCES document_requests (id),
    CONSTRAINT fk_history_changed_by FOREIGN KEY (changed_by) REFERENCES users (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE notifications (
    id                BIGINT       NOT NULL AUTO_INCREMENT,
    user_id           BIGINT       NOT NULL,
    request_id        BIGINT       NULL,
    title             VARCHAR(200) NOT NULL,
    message           TEXT         NOT NULL,
    notification_type VARCHAR(50)  NOT NULL,
    is_read           BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at           DATETIME     NULL,
    PRIMARY KEY (id),
    KEY idx_notifications_user_unread (user_id, is_read),
    KEY idx_notifications_request (request_id),
    KEY idx_notifications_created_at (created_at),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_notifications_request FOREIGN KEY (request_id) REFERENCES document_requests (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE audit_logs (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    actor_user_id BIGINT       NULL,
    action        VARCHAR(100) NOT NULL,
    entity_type   VARCHAR(100) NOT NULL,
    entity_id     BIGINT       NULL,
    description   TEXT         NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_audit_actor (actor_user_id),
    KEY idx_audit_action (action),
    KEY idx_audit_created_at (created_at),
    CONSTRAINT fk_audit_actor FOREIGN KEY (actor_user_id) REFERENCES users (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;

CREATE TABLE system_settings (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    setting_key   VARCHAR(100) NOT NULL,
    setting_value TEXT         NOT NULL,
    updated_by    BIGINT       NULL,
    updated_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_system_settings_key (setting_key),
    KEY idx_system_settings_updated_by (updated_by),
    CONSTRAINT fk_system_settings_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci;
