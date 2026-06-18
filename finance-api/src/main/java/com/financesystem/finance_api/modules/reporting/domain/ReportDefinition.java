package com.financesystem.finance_api.modules.reporting.domain;

import java.util.List;
import java.util.Set;

/**
 * Declarative definition of a controlled report (Riel 1).
 *
 * <p>Prefer {@link #sourceView()} + typed {@link #params()}. {@link #sqlTemplate()}
 * is reserved for hand-reviewed special cases (versioned config only, still runs
 * through {@code AiSqlGuard}, bind variables only). Exactly one of
 * {@code sourceView} / {@code sqlTemplate} must be set.
 *
 * <p>Deliberately carries NO visualizations or per-report column/metric enums —
 * columns come from the view and charts are decided by the frontend.
 */
public record ReportDefinition(
        String key,
        String title,
        String description,
        ReportScope scope,
        String sourceView,
        String sqlTemplate,
        List<ReportParam> params,
        ReportSort defaultSort,
        Set<String> requiredPermissions
) {
    public boolean usesSourceView() {
        return sourceView != null && !sourceView.isBlank();
    }

    public boolean usesSqlTemplate() {
        return sqlTemplate != null && !sqlTemplate.isBlank();
    }
}
