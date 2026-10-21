INSERT INTO document_types (name, description, is_active, requires_admin_approval, instructions, internal_notes)
VALUES
    ('Form 137',
     'Permanent record of a student''s academic performance and cumulative record from the school registrar.',
     TRUE, TRUE,
     'Used for transferring to another school, board or licensure applications, and employment. Bring a valid school ID when claiming the document.',
     'Verify that the student has no outstanding financial or disciplinary hold before releasing.'),
    ('Report Card',
     'Official copy of a student''s grades for a specific school year or grading period issued by the registrar.',
     TRUE, TRUE,
     'State the school year or grade level in the purpose field. Report Cards for the current school year are released after the grading period is finalized.',
     'Confirm the correct school year with the student before preparation.'),
    ('Good Moral Certificate',
     'Certificate that the student is in good standing and free of disciplinary violations while enrolled.',
     TRUE, FALSE,
     'Commonly required for employment, scholarships, and transferring. Processing begins once the request is verified by the registrar.',
     'Flag for the guidance office only when a disciplinary record exists.');

INSERT INTO system_settings (setting_key, setting_value)
VALUES
    ('request_sequence', '0'),
    ('pickup_location', 'Registrar''s Office, Ground Floor, Saint Jude College Manila'),
    ('office_hours', 'Monday to Friday, 8:00 AM to 4:00 PM (except holidays)'),
    ('contact_email', 'registrar@sjudemanila.edu.ph'),
    ('contact_number', '');
