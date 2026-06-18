package com.financesystem.finance_api.modules.reporting.domain;

import java.util.List;

/**
 * The outcome of running a report: typed columns + rows + metadata.
 *
 * @param columns     ordered columns with logical types (for charting)
 * @param rows        row-major values (each inner list aligns with {@code columns})
 * @param rowCount    number of rows returned (after truncation)
 * @param truncated   true if the result hit the row cap and more rows exist
 * @param metadata    sql / scope / schema / referenced views / limit kind / explanation
 */
public record ReportResult(
        List<ReportColumn> columns,
        List<List<Object>> rows,
        int rowCount,
        boolean truncated,
        ReportResultMetadata metadata
) {
    public record ReportResultMetadata(
            String sql,
            ReportScope scope,
            String schemaUsed,
            List<String> referencedViews,
            LimitKind limitKind,
            String explanation
    ) {
    }
}
