-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Oct 16, 2026 at 12:20 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.1.17

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `school_document_request`
--

-- --------------------------------------------------------

--
-- Table structure for table `audit_logs`
--

CREATE TABLE `audit_logs` (
  `id` bigint(20) NOT NULL,
  `actor_user_id` bigint(20) DEFAULT NULL,
  `action` varchar(100) NOT NULL,
  `entity_type` varchar(100) NOT NULL,
  `entity_id` bigint(20) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `audit_logs`
--

INSERT INTO `audit_logs` (`id`, `actor_user_id`, `action`, `entity_type`, `entity_id`, `description`, `created_at`) VALUES
(1, NULL, 'USER_CREATED', 'User', 1, 'Created Administrator account for System Administrator (admin@sjudemanila.edu.ph)', '2026-10-16 16:58:22'),
(2, NULL, 'USER_CREATED', 'User', 2, 'Created Student account for Juan Dela Cruz (ma.delacruz@student.sjudemanila.edu.ph)', '2026-10-16 16:58:22'),
(3, NULL, 'USER_CREATED', 'User', 3, 'Created Student account for Maria Santos (li.santos@student.sjudemanila.edu.ph)', '2026-10-16 16:58:23'),
(4, NULL, 'USER_CREATED', 'User', 4, 'Created Registrar Staff account for Ana Reyes (a.reyes@sjudemanila.edu.ph)', '2026-10-16 16:58:23'),
(5, NULL, 'REQUEST_SUBMITTED', 'DocumentRequest', 1, 'Request SDR-2026-000001 submitted for Form 137', '2026-10-16 16:58:23'),
(6, NULL, 'REQUEST_SUBMITTED', 'DocumentRequest', 2, 'Request SDR-2026-000002 submitted for Report Card', '2026-10-16 16:58:23'),
(7, NULL, 'REQUEST_APPROVED', 'DocumentRequest', 2, 'Request SDR-2026-000002 approved.', '2026-10-16 16:58:23'),
(8, NULL, 'REQUEST_PROCESSING', 'DocumentRequest', 2, 'Request SDR-2026-000002 moved to processing.', '2026-10-16 16:58:23'),
(9, NULL, 'REQUEST_SUBMITTED', 'DocumentRequest', 3, 'Request SDR-2026-000003 submitted for Good Moral Certificate', '2026-10-16 16:58:23'),
(10, NULL, 'REQUEST_PROCESSING', 'DocumentRequest', 3, 'Request SDR-2026-000003 moved to processing.', '2026-10-16 16:58:23'),
(11, NULL, 'REQUEST_READY', 'DocumentRequest', 3, 'Request SDR-2026-000003 marked ready for pick-up by Ana Reyes', '2026-10-16 16:58:23'),
(12, 2, 'REQUEST_SUBMITTED', 'DocumentRequest', 4, 'Request SDR-2026-000004 submitted for Good Moral Certificate', '2026-10-16 17:16:56'),
(13, 4, 'REQUEST_PROCESSING', 'DocumentRequest', 4, 'Request SDR-2026-000004 moved to processing.', '2026-10-16 17:17:32'),
(14, 4, 'REQUEST_READY', 'DocumentRequest', 4, 'Request SDR-2026-000004 marked ready for pick-up by Ana Reyes', '2026-10-16 17:17:32'),
(15, 4, 'REQUEST_RELEASED', 'DocumentRequest', 4, 'Release recorded for SDR-2026-000004 on 2026-10-16', '2026-10-16 17:17:32'),
(16, 1, 'REQUEST_APPROVED', 'DocumentRequest', 1, 'Request SDR-2026-000001 approved.', '2026-10-16 17:18:01'),
(17, 4, 'REQUEST_PROCESSING', 'DocumentRequest', 1, 'Request SDR-2026-000001 moved to processing.', '2026-10-16 17:18:01'),
(18, 4, 'REQUEST_READY', 'DocumentRequest', 1, 'Request SDR-2026-000001 marked ready for pick-up by Ana Reyes', '2026-10-16 17:18:01'),
(19, 2, 'REQUEST_SUBMITTED', 'DocumentRequest', 5, 'Request SDR-2026-000005 submitted for Form 137', '2026-10-16 17:18:01'),
(20, 1, 'REQUEST_REJECTED', 'DocumentRequest', 5, 'Request SDR-2026-000005 rejected.', '2026-10-16 17:18:01'),
(21, 1, 'USER_CREATED', 'User', 5, 'Created Student account for Test Student (test.student@sjudemanila.edu.ph)', '2026-10-16 17:18:29'),
(22, 1, 'SETTINGS_UPDATED', 'SystemSetting', NULL, 'Updated system settings: pickup_location, office_hours, contact_email, contact_number', '2026-10-16 17:18:29'),
(23, 1, 'DOCUMENT_TYPE_DISABLED', 'DocumentType', 3, 'Document type \"Good Moral Certificate\" disabled', '2026-10-16 17:18:52'),
(24, 1, 'DOCUMENT_TYPE_ENABLED', 'DocumentType', 3, 'Document type \"Good Moral Certificate\" enabled', '2026-10-16 17:18:52'),
(25, 1, 'PASSWORD_RESET', 'User', 5, 'Password reset for Test Student by an administrator.', '2026-10-16 17:19:06'),
(26, 1, 'USER_UPDATED', 'User', 5, 'Updated profile details for Test StudentRenamed', '2026-10-16 17:19:45'),
(27, 1, 'USER_DEACTIVATED', 'User', 5, 'Test StudentRenamed (test.student@sjudemanila.edu.ph) deactivated by System Administrator', '2026-10-16 17:19:45'),
(28, 1, 'USER_ACTIVATED', 'User', 5, 'Test StudentRenamed (test.student@sjudemanila.edu.ph) activated by System Administrator', '2026-10-16 17:19:45'),
(29, 1, 'DOCUMENT_TYPE_CREATED', 'DocumentType', 4, 'Created document type \"Certificate of Enrollment\"', '2026-10-16 17:19:46'),
(30, 2, 'PASSWORD_CHANGED', 'User', 2, 'Password changed by the account owner.', '2026-10-16 17:20:02'),
(31, 1, 'PASSWORD_RESET', 'User', 2, 'Password reset for Juan Dela Cruz by an administrator.', '2026-10-16 17:21:47'),
(32, NULL, 'REQUEST_SUBMITTED', 'DocumentRequest', 6, 'Request SDR-2026-000001 submitted for Form 137', '2026-10-16 17:22:42'),
(33, NULL, 'REQUEST_SUBMITTED', 'DocumentRequest', 7, 'Request SDR-2026-000002 submitted for Report Card', '2026-10-16 17:22:42'),
(34, NULL, 'REQUEST_APPROVED', 'DocumentRequest', 7, 'Request SDR-2026-000002 approved.', '2026-10-16 17:22:42'),
(35, NULL, 'REQUEST_PROCESSING', 'DocumentRequest', 7, 'Request SDR-2026-000002 moved to processing.', '2026-10-16 17:22:42'),
(36, NULL, 'REQUEST_SUBMITTED', 'DocumentRequest', 8, 'Request SDR-2026-000003 submitted for Good Moral Certificate', '2026-10-16 17:22:42'),
(37, NULL, 'REQUEST_PROCESSING', 'DocumentRequest', 8, 'Request SDR-2026-000003 moved to processing.', '2026-10-16 17:22:42'),
(38, NULL, 'REQUEST_READY', 'DocumentRequest', 8, 'Request SDR-2026-000003 marked ready for pick-up by Ana Reyes', '2026-10-16 17:22:42');

-- --------------------------------------------------------

--
-- Table structure for table `document_requests`
--

CREATE TABLE `document_requests` (
  `id` bigint(20) NOT NULL,
  `reference_number` varchar(40) NOT NULL,
  `student_id` bigint(20) NOT NULL,
  `document_type_id` bigint(20) NOT NULL,
  `purpose` varchar(500) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `additional_details` text DEFAULT NULL,
  `status` enum('PENDING','PROCESSING','READY_FOR_PICKUP') NOT NULL DEFAULT 'PENDING',
  `approval_status` enum('NOT_REQUIRED','PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'NOT_REQUIRED',
  `approved_by` bigint(20) DEFAULT NULL,
  `approved_at` datetime DEFAULT NULL,
  `rejection_reason` text DEFAULT NULL,
  `assigned_staff_id` bigint(20) DEFAULT NULL,
  `staff_notes` text DEFAULT NULL,
  `pickup_instructions` text DEFAULT NULL,
  `claimed_at` datetime DEFAULT NULL,
  `released_by` bigint(20) DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `document_requests`
--

INSERT INTO `document_requests` (`id`, `reference_number`, `student_id`, `document_type_id`, `purpose`, `quantity`, `additional_details`, `status`, `approval_status`, `approved_by`, `approved_at`, `rejection_reason`, `assigned_staff_id`, `staff_notes`, `pickup_instructions`, `claimed_at`, `released_by`, `created_at`, `updated_at`) VALUES
(6, 'SDR-2026-000001', 2, 1, 'Board examination application', 2, NULL, 'PENDING', 'PENDING', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '2026-10-16 17:22:42', '2026-10-16 17:22:42'),
(7, 'SDR-2026-000002', 3, 2, 'Transfer to another school', 1, NULL, 'PROCESSING', 'APPROVED', 1, '2026-10-16 17:22:42', NULL, 4, NULL, NULL, NULL, NULL, '2026-10-16 17:22:42', '2026-10-16 17:22:42'),
(8, 'SDR-2026-000003', 2, 3, 'Employment requirement', 1, NULL, 'READY_FOR_PICKUP', 'NOT_REQUIRED', NULL, NULL, NULL, 4, NULL, 'Bring your school ID and this reference number to the counter.', NULL, NULL, '2026-10-16 17:22:42', '2026-10-16 17:22:42');

-- --------------------------------------------------------

--
-- Table structure for table `document_types`
--

CREATE TABLE `document_types` (
  `id` bigint(20) NOT NULL,
  `name` varchar(150) NOT NULL,
  `description` text DEFAULT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `requires_admin_approval` tinyint(1) NOT NULL DEFAULT 0,
  `instructions` text DEFAULT NULL,
  `internal_notes` text DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `document_types`
--

INSERT INTO `document_types` (`id`, `name`, `description`, `is_active`, `requires_admin_approval`, `instructions`, `internal_notes`, `created_at`, `updated_at`) VALUES
(1, 'Form 137', 'Permanent record of a student\'s academic performance and cumulative record from the school registrar.', 1, 1, 'Used for transferring to another school, board or licensure applications, and employment. Bring a valid school ID when claiming the document.', 'Verify that the student has no outstanding financial or disciplinary hold before releasing.', '2026-10-16 16:51:25', '2026-10-16 16:51:25'),
(2, 'Report Card', 'Official copy of a student\'s grades for a specific school year or grading period issued by the registrar.', 1, 1, 'State the school year or grade level in the purpose field. Report Cards for the current school year are released after the grading period is finalized.', 'Confirm the correct school year with the student before preparation.', '2026-10-16 16:51:25', '2026-10-16 16:51:25'),
(3, 'Good Moral Certificate', 'Certificate that the student is in good standing and free of disciplinary violations while enrolled.', 1, 0, 'Commonly required for employment, scholarships, and transferring. Processing begins once the request is verified by the registrar.', 'Flag for the guidance office only when a disciplinary record exists.', '2026-10-16 16:51:25', '2026-10-16 17:18:52');

-- --------------------------------------------------------

--
-- Table structure for table `flyway_schema_history`
--

CREATE TABLE `flyway_schema_history` (
  `installed_rank` int(11) NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int(11) DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT current_timestamp(),
  `execution_time` int(11) NOT NULL,
  `success` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `flyway_schema_history`
--

INSERT INTO `flyway_schema_history` (`installed_rank`, `version`, `description`, `type`, `script`, `checksum`, `installed_by`, `installed_on`, `execution_time`, `success`) VALUES
(1, '1', 'create initial schema', 'SQL', 'V1__create_initial_schema.sql', -151845293, 'root', '2026-10-16 08:51:25', 74, 1),
(2, '2', 'seed reference data', 'SQL', 'V2__seed_reference_data.sql', -2060809710, 'root', '2026-10-16 08:51:25', 6, 1);

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `request_id` bigint(20) DEFAULT NULL,
  `title` varchar(200) NOT NULL,
  `message` text NOT NULL,
  `notification_type` varchar(50) NOT NULL,
  `is_read` tinyint(1) NOT NULL DEFAULT 0,
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `read_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`id`, `user_id`, `request_id`, `title`, `message`, `notification_type`, `is_read`, `created_at`, `read_at`) VALUES
(1, 2, NULL, 'Welcome to the School Documents Request System', 'Your account is ready. Submit a document request from your dashboard at any time.', 'ACCOUNT_UPDATED', 1, '2026-10-16 16:58:23', '2026-10-16 17:18:29'),
(2, 3, NULL, 'Welcome to the School Documents Request System', 'Your account is ready. Submit a document request from your dashboard at any time.', 'ACCOUNT_UPDATED', 0, '2026-10-16 16:58:23', NULL),
(3, 4, NULL, 'Welcome to the School Documents Request System', 'Your account is ready. Submit a document request from your dashboard at any time.', 'ACCOUNT_UPDATED', 0, '2026-10-16 16:58:23', NULL),
(22, 2, 6, 'Request received — SDR-2026-000001', 'We received your request for Form 137. It is now waiting for administrator approval.', 'REQUEST_SUBMITTED', 0, '2026-10-16 17:22:42', NULL),
(23, 1, 6, 'Approval needed — SDR-2026-000001', 'Juan Dela Cruz (2026-0001) requested Form 137. Review it in the approval queue.', 'APPROVAL_REQUIRED', 0, '2026-10-16 17:22:42', NULL),
(24, 3, 7, 'Request received — SDR-2026-000002', 'We received your request for Report Card. It is now waiting for administrator approval.', 'REQUEST_SUBMITTED', 0, '2026-10-16 17:22:42', NULL),
(25, 1, 7, 'Approval needed — SDR-2026-000002', 'Maria Santos (2026-0002) requested Report Card. Review it in the approval queue.', 'APPROVAL_REQUIRED', 0, '2026-10-16 17:22:42', NULL),
(26, 3, 7, 'Request approved — SDR-2026-000002', 'Your request for Report Card was approved. The registrar will begin preparing it.', 'REQUEST_APPROVED', 0, '2026-10-16 17:22:42', NULL),
(27, 3, 7, 'Processing started — SDR-2026-000002', 'The registrar is now preparing your Report Card. We will notify you again when it is ready for pick-up.', 'REQUEST_PROCESSING', 0, '2026-10-16 17:22:42', NULL),
(28, 2, 8, 'Request received — SDR-2026-000003', 'We received your request for Good Moral Certificate. The registrar will begin processing it shortly.', 'REQUEST_SUBMITTED', 0, '2026-10-16 17:22:42', NULL),
(29, 2, 8, 'Processing started — SDR-2026-000003', 'The registrar is now preparing your Good Moral Certificate. We will notify you again when it is ready for pick-up.', 'REQUEST_PROCESSING', 0, '2026-10-16 17:22:42', NULL),
(30, 2, 8, 'Ready for pick-up — SDR-2026-000003', 'Good Moral Certificate (x1) is ready for pick-up. Pick up at Registrar Window 2. Office hours: Mon-Fri 8am-5pm. Instructions: Bring your school ID and this reference number to the counter. Questions? Email registrar@sjudemanila.edu.ph.', 'REQUEST_READY', 0, '2026-10-16 17:22:42', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `request_status_history`
--

CREATE TABLE `request_status_history` (
  `id` bigint(20) NOT NULL,
  `request_id` bigint(20) NOT NULL,
  `changed_by` bigint(20) NOT NULL,
  `old_status` varchar(50) DEFAULT NULL,
  `new_status` varchar(50) DEFAULT NULL,
  `old_approval_status` varchar(50) DEFAULT NULL,
  `new_approval_status` varchar(50) DEFAULT NULL,
  `remarks` text DEFAULT NULL,
  `created_at` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `request_status_history`
--

INSERT INTO `request_status_history` (`id`, `request_id`, `changed_by`, `old_status`, `new_status`, `old_approval_status`, `new_approval_status`, `remarks`, `created_at`) VALUES
(17, 6, 2, NULL, 'PENDING', NULL, 'PENDING', 'Request submitted by the student.', '2026-10-16 17:22:42'),
(18, 7, 3, NULL, 'PENDING', NULL, 'PENDING', 'Request submitted by the student.', '2026-10-16 17:22:42'),
(19, 7, 1, 'PENDING', 'PENDING', 'PENDING', 'APPROVED', 'Records check complete.', '2026-10-16 17:22:42'),
(20, 7, 4, 'PENDING', 'PROCESSING', 'APPROVED', 'APPROVED', 'Verified against the student\'s record.', '2026-10-16 17:22:42'),
(21, 8, 2, NULL, 'PENDING', NULL, 'NOT_REQUIRED', 'Request submitted by the student.', '2026-10-16 17:22:42'),
(22, 8, 4, 'PENDING', 'PROCESSING', 'NOT_REQUIRED', 'NOT_REQUIRED', 'Verified and prepared.', '2026-10-16 17:22:42'),
(23, 8, 4, 'PROCESSING', 'READY_FOR_PICKUP', 'NOT_REQUIRED', 'NOT_REQUIRED', 'Document prepared. Student notified for pick-up.', '2026-10-16 17:22:42');

-- --------------------------------------------------------

--
-- Table structure for table `system_settings`
--

CREATE TABLE `system_settings` (
  `id` bigint(20) NOT NULL,
  `setting_key` varchar(100) NOT NULL,
  `setting_value` text NOT NULL,
  `updated_by` bigint(20) DEFAULT NULL,
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `system_settings`
--

INSERT INTO `system_settings` (`id`, `setting_key`, `setting_value`, `updated_by`, `updated_at`) VALUES
(1, 'request_sequence', '3', NULL, '2026-10-16 17:22:42'),
(2, 'pickup_location', 'Registrar Window 2', 1, '2026-10-16 17:18:29'),
(3, 'office_hours', 'Mon-Fri 8am-5pm', 1, '2026-10-16 17:18:29'),
(4, 'contact_email', 'registrar@sjudemanila.edu.ph', 1, '2026-10-16 17:18:29'),
(5, 'contact_number', '(02) 1234-5678', 1, '2026-10-16 17:18:29');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `student_number` varchar(50) DEFAULT NULL,
  `employee_number` varchar(50) DEFAULT NULL,
  `first_name` varchar(100) NOT NULL,
  `middle_name` varchar(100) DEFAULT NULL,
  `last_name` varchar(100) NOT NULL,
  `email` varchar(150) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` enum('STUDENT','STAFF','ADMIN') NOT NULL,
  `course_or_department` varchar(150) DEFAULT NULL,
  `year_level_or_section` varchar(100) DEFAULT NULL,
  `account_status` enum('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE',
  `created_at` datetime NOT NULL DEFAULT current_timestamp(),
  `updated_at` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `last_login_at` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `student_number`, `employee_number`, `first_name`, `middle_name`, `last_name`, `email`, `password_hash`, `role`, `course_or_department`, `year_level_or_section`, `account_status`, `created_at`, `updated_at`, `last_login_at`) VALUES
(1, NULL, NULL, 'System', NULL, 'Administrator', 'admin@sjudemanila.edu.ph', '$2a$12$hsM1YEP8vwwkkyt6jmEjhO4FbVHip6jqifHmgHX/qVcDnz9fs.kt6', 'ADMIN', 'Administration', NULL, 'ACTIVE', '2026-10-16 16:58:22', '2026-10-16 18:17:29', '2026-10-16 18:17:29'),
(2, '2026-0001', NULL, 'Juan', NULL, 'Dela Cruz', 'ma.delacruz@student.sjudemanila.edu.ph', '$2a$12$BMiKqwwgg1VYqEIBogK71OB4TBis.mzjCURjY2Kqa8GpcQw8AOaoq', 'STUDENT', 'BS Information Technology', '3rd Year - Section A', 'ACTIVE', '2026-10-16 16:58:22', '2026-10-16 17:59:23', '2026-10-16 17:59:23'),
(3, '2026-0002', NULL, 'Maria', NULL, 'Santos', 'li.santos@student.sjudemanila.edu.ph', '$2a$12$pWgffdovSVvaund337xS..pUIbLU4yowzjPG3VHplNrpbOLZ1rLhm', 'STUDENT', 'BS Accountancy', '2nd Year - Section B', 'ACTIVE', '2026-10-16 16:58:23', '2026-10-16 16:58:23', NULL),
(4, NULL, NULL, 'Ana', NULL, 'Reyes', 'a.reyes@sjudemanila.edu.ph', '$2a$12$z5d89ea4/aTZWzblMk0kH.qYNgm8R84MMO2Y0vY8gg4HbChBIqlwa', 'STAFF', 'Registrar\'s Office', NULL, 'ACTIVE', '2026-10-16 16:58:23', '2026-10-16 18:11:14', '2026-10-16 18:11:14');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_audit_actor` (`actor_user_id`),
  ADD KEY `idx_audit_action` (`action`),
  ADD KEY `idx_audit_created_at` (`created_at`);

--
-- Indexes for table `document_requests`
--
ALTER TABLE `document_requests`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_requests_reference_number` (`reference_number`),
  ADD KEY `idx_requests_student` (`student_id`),
  ADD KEY `idx_requests_document_type` (`document_type_id`),
  ADD KEY `idx_requests_status` (`status`),
  ADD KEY `idx_requests_approval_status` (`approval_status`),
  ADD KEY `idx_requests_created_at` (`created_at`),
  ADD KEY `idx_requests_assigned_staff` (`assigned_staff_id`),
  ADD KEY `idx_requests_approved_by` (`approved_by`),
  ADD KEY `idx_requests_released_by` (`released_by`);

--
-- Indexes for table `document_types`
--
ALTER TABLE `document_types`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_document_types_name` (`name`),
  ADD KEY `idx_document_types_active` (`is_active`);

--
-- Indexes for table `flyway_schema_history`
--
ALTER TABLE `flyway_schema_history`
  ADD PRIMARY KEY (`installed_rank`),
  ADD KEY `flyway_schema_history_s_idx` (`success`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_notifications_user_unread` (`user_id`,`is_read`),
  ADD KEY `idx_notifications_request` (`request_id`),
  ADD KEY `idx_notifications_created_at` (`created_at`);

--
-- Indexes for table `request_status_history`
--
ALTER TABLE `request_status_history`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_history_request` (`request_id`),
  ADD KEY `idx_history_changed_by` (`changed_by`),
  ADD KEY `idx_history_created_at` (`created_at`);

--
-- Indexes for table `system_settings`
--
ALTER TABLE `system_settings`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_system_settings_key` (`setting_key`),
  ADD KEY `idx_system_settings_updated_by` (`updated_by`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uq_users_email` (`email`),
  ADD UNIQUE KEY `uq_users_student_number` (`student_number`),
  ADD UNIQUE KEY `uq_users_employee_number` (`employee_number`),
  ADD KEY `idx_users_role` (`role`),
  ADD KEY `idx_users_account_status` (`account_status`),
  ADD KEY `idx_users_name` (`last_name`,`first_name`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `audit_logs`
--
ALTER TABLE `audit_logs`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=39;

--
-- AUTO_INCREMENT for table `document_requests`
--
ALTER TABLE `document_requests`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `document_types`
--
ALTER TABLE `document_types`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `request_status_history`
--
ALTER TABLE `request_status_history`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `system_settings`
--
ALTER TABLE `system_settings`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `audit_logs`
--
ALTER TABLE `audit_logs`
  ADD CONSTRAINT `fk_audit_actor` FOREIGN KEY (`actor_user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `document_requests`
--
ALTER TABLE `document_requests`
  ADD CONSTRAINT `fk_requests_approved_by` FOREIGN KEY (`approved_by`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `fk_requests_assigned_staff` FOREIGN KEY (`assigned_staff_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `fk_requests_document_type` FOREIGN KEY (`document_type_id`) REFERENCES `document_types` (`id`),
  ADD CONSTRAINT `fk_requests_released_by` FOREIGN KEY (`released_by`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `fk_requests_student` FOREIGN KEY (`student_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `notifications`
--
ALTER TABLE `notifications`
  ADD CONSTRAINT `fk_notifications_request` FOREIGN KEY (`request_id`) REFERENCES `document_requests` (`id`),
  ADD CONSTRAINT `fk_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `request_status_history`
--
ALTER TABLE `request_status_history`
  ADD CONSTRAINT `fk_history_changed_by` FOREIGN KEY (`changed_by`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `fk_history_request` FOREIGN KEY (`request_id`) REFERENCES `document_requests` (`id`);

--
-- Constraints for table `system_settings`
--
ALTER TABLE `system_settings`
  ADD CONSTRAINT `fk_system_settings_updated_by` FOREIGN KEY (`updated_by`) REFERENCES `users` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
