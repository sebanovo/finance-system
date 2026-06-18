package com.financesystem.finance_api.modules.reporting.application.ai;

/** Response from the NL→SQL brain. {@code transcript} is null for text requests. */
public record AiSqlResponse(String transcript, String sql, String explanation) {
}
