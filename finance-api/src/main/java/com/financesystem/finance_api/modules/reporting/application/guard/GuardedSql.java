package com.financesystem.finance_api.modules.reporting.application.guard;

import com.financesystem.finance_api.modules.reporting.domain.LimitKind;

import java.util.List;

/**
 * Result of passing SQL through {@link AiSqlGuard}: the normalized, safe-to-run
 * SQL plus what it touched and which limit applies.
 *
 * @param safeSql         the re-serialized single SELECT, with a LIMIT guaranteed
 * @param referencedViews whitelisted views the query reads (excludes CTE names)
 * @param schemaUsed      logical scope schema: {@code <tenant>} or {@code reporting}
 * @param limitKind       USER (query carried its own LIMIT) or SYSTEM (injected)
 */
public record GuardedSql(
        String safeSql,
        List<String> referencedViews,
        String schemaUsed,
        LimitKind limitKind
) {
}
