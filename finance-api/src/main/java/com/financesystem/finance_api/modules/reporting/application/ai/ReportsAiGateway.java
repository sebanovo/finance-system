package com.financesystem.finance_api.modules.reporting.application.ai;

import com.financesystem.finance_api.modules.reporting.domain.ReportScope;

/**
 * Gateway to the NL→SQL brain. Spring Boot never trusts the returned SQL —
 * it always passes through {@code AiSqlGuard} before execution.
 */
public interface ReportsAiGateway {

    AiSqlResponse generateFromText(String prompt, ReportScope scope, String schemaDescription);

    AiSqlResponse transcribeAndGenerate(byte[] audio, String mimeType, ReportScope scope, String schemaDescription);
}
