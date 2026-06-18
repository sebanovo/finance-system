package com.financesystem.finance_api.modules.reporting.domain;

/** A sort instruction: a whitelisted column plus a direction. */
public record ReportSort(String field, SortDirection direction) {
}
