package com.financesystem.finance_api.modules.reporting.domain;

import java.util.List;

/**
 * A typed, whitelisted filter parameter for a controlled report.
 *
 * <p>The frontend sends parameter <b>names</b> (never raw column names or SQL).
 * The backend maps {@link #name()} → {@link #column()} (a column of the source
 * view) and applies {@link #operator()} as a parameterized {@code WHERE} clause.
 *
 * <p>{@link #options()} are the allowed values for categorical params so the
 * frontend renders a dropdown instead of a free-text input (empty = free input).
 */
public record ReportParam(
        String name,
        String column,
        ReportDataType type,
        ReportParamOperator operator,
        boolean required,
        List<String> options
) {
    /** Convenience for params without a fixed option set (dates, numbers, free text). */
    public ReportParam(String name, String column, ReportDataType type, ReportParamOperator operator,
                       boolean required) {
        this(name, column, type, operator, required, List.of());
    }

    public enum ReportParamOperator {
        EQUALS("="),
        GREATER_THAN_OR_EQUAL(">="),
        LESS_THAN_OR_EQUAL("<="),
        LIKE("LIKE");

        private final String sql;

        ReportParamOperator(String sql) {
            this.sql = sql;
        }

        public String sql() {
            return sql;
        }
    }
}
