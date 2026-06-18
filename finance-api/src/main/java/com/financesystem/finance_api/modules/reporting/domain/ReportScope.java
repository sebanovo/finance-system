package com.financesystem.finance_api.modules.reporting.domain;

/**
 * Execution scope of a report.
 *
 * <ul>
 *   <li>{@link #TENANT}: runs under the tenant's {@code search_path} against its
 *       {@code reporting_*} views, using the {@code finance_tenant_readonly} role.</li>
 *   <li>{@link #GLOBAL}: runs against the cross-tenant {@code reporting.*} views,
 *       using the {@code finance_platform_readonly} role.</li>
 * </ul>
 */
public enum ReportScope {
    TENANT,
    GLOBAL
}
