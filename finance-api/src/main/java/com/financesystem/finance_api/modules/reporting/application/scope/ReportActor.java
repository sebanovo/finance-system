package com.financesystem.finance_api.modules.reporting.application.scope;

import com.financesystem.finance_api.modules.reporting.domain.ReportActorScope;
import com.financesystem.finance_api.modules.reporting.domain.ReportScope;

import java.util.UUID;

/** Resolved caller + scope for an execution. Snapshotted into report_executions. */
public record ReportActor(
        UUID userId,
        String email,
        String displayName,
        ReportScope scope,
        ReportActorScope actorScope,
        String tenantSlug,
        String tenantSchema
) {
}
