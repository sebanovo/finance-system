package com.financesystem.finance_api.modules.reporting.domain;

/**
 * How an export was produced.
 *
 * <ul>
 *   <li>{@link #SNAPSHOT}: rendered from the stored result_json (matches the screen).</li>
 *   <li>{@link #FULL}: re-runs the audited SQL with a larger cap (only widens a SYSTEM limit).</li>
 * </ul>
 */
public enum ReportExportMode {
    SNAPSHOT,
    FULL
}
