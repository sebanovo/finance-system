package com.financesystem.finance_api.modules.tenant.reporting.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportType {
    ACCOUNTS,
    TRANSACTIONS,
    TRANSACTION_MOVEMENTS,
    LIMIT_RULES,
    LIMIT_USAGE,
    ACCOUNTING_PERIODS,
    JOURNAL_ENTRIES,
    JOURNAL_LINES,
    QR_TRANSACTION_INTENTS,
    FX_EXCHANGE_RATES,
    OPERATION_FEES,
    NOTIFICATIONS,
    NOTIFICATION_DELIVERIES,
    NOTIFICATION_DEVICES,
    AUDIT_EVENTS;

    @JsonCreator
    public static ReportType fromValue(String value) {
        return value == null ? null : ReportType.valueOf(value);
    }

    @JsonValue
    public String toValue() {
        return name();
    }
}
