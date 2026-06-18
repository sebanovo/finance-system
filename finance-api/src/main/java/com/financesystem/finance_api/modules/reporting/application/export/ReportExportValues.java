package com.financesystem.finance_api.modules.reporting.application.export;

import java.time.format.DateTimeFormatter;

/** Shared value-to-string formatting for exporters. */
final class ReportExportValues {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private ReportExportValues() {
    }

    static String asText(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof java.time.LocalDate d) {
            return DATE.format(d);
        }
        return String.valueOf(value);
    }
}
