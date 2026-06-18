package com.financesystem.finance_api.modules.reporting.application.export;

import com.financesystem.finance_api.modules.reporting.domain.ReportColumn;
import com.financesystem.finance_api.modules.reporting.domain.ReportDataType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ReportExportersTest {

    private final List<ReportColumn> columns = List.of(
            new ReportColumn("tenant_slug", ReportDataType.TEXT),
            new ReportColumn("total_amount", ReportDataType.NUMBER),
            new ReportColumn("active", ReportDataType.BOOLEAN)
    );
    private final List<List<Object>> rows = List.of(
            List.of("acme", 1234.5, true),
            List.of("globex", 9876.0, false)
    );

    @Test
    void pdfExporterProducesPdfBytes() {
        byte[] bytes = new PdfReportExporter().export("Ranking", columns, rows);
        assertTrue(bytes.length > 0);
        // PDF magic number: %PDF
        assertTrue(bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F');
    }

    @Test
    void xlsxExporterProducesZipBytes() {
        byte[] bytes = new XlsxReportExporter().export("Ranking", columns, rows);
        assertTrue(bytes.length > 0);
        // XLSX is a ZIP container: PK magic number
        assertTrue(bytes[0] == 'P' && bytes[1] == 'K');
    }
}
