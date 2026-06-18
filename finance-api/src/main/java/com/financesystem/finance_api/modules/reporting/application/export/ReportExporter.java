package com.financesystem.finance_api.modules.reporting.application.export;

import com.financesystem.finance_api.modules.reporting.domain.ReportColumn;
import com.financesystem.finance_api.modules.reporting.domain.ReportFormat;

import java.util.List;

/**
 * Generic exporter: turns columns + rows into a downloadable artifact.
 * Reused by both rails (controlled and AI). No per-report logic.
 */
public interface ReportExporter {

    ReportFormat format();

    String contentType();

    String fileExtension();

    byte[] export(String title, List<ReportColumn> columns, List<List<Object>> rows);
}
