package com.financesystem.finance_api.modules.reporting.domain;

/** A result column: its name plus a logical type for the frontend. */
public record ReportColumn(String name, ReportDataType type) {
}
