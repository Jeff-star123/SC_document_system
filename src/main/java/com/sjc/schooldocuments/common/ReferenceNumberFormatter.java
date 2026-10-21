package com.sjc.schooldocuments.common;

import org.springframework.stereotype.Component;
import java.time.LocalDate;

/**
 * Generates collision-resistant request reference numbers in the form
 * {@code SDR-2026-000001}. The sequence lives in {@code system_settings} and is
 * incremented under a row-level lock inside the caller's transaction, so two
 * simultaneous submissions can never receive the same number.
 */
@Component
public class ReferenceNumberFormatter {

    public String format(int year, long sequence) {
        return String.format("SDR-%d-%06d", year, sequence);
    }

    public int currentYear() {
        return LocalDate.now().getYear();
    }
}
