package com.financesystem.finance_api.modules.reporting.application.service;

/** Audit event types for the reporting module. */
public final class ReportAuditEvents {

    public static final String RESOURCE_TYPE = "REPORT";
    public static final String EXECUTED = "REPORT_EXECUTED";
    public static final String FAILED = "REPORT_FAILED";
    public static final String EXPORTED = "REPORT_EXPORTED";

    private ReportAuditEvents() {
    }
}
