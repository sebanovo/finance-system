package com.financesystem.finance_api.modules.tenant.reporting.application.export;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportColumnResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportHeaderResponse;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportException;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class ReportPdfExporter {

    private final ReportDisplayValueFormatter valueFormatter;

    public ReportPdfExporter() {
        this.valueFormatter = new ReportDisplayValueFormatter();
    }

    public byte[] export(
            ReportHeaderResponse header,
            List<ReportColumnResponse> columns,
            List<Map<String, Object>> rows
    ) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, outputStream);
            document.open();

            document.add(new Paragraph(header.title()));
            document.add(new Paragraph("Modo: " + header.modeLabel()));
            document.add(new Paragraph("Generado por: " + header.generatedBy()));
            document.add(new Paragraph("Generado el: " + valueFormatter.formatCellValue(header.generatedAt())));
            document.add(new Paragraph(" "));

            if (!header.appliedFilters().isEmpty()) {
                document.add(new Paragraph("Filtros aplicados"));
                for (String filter : header.appliedFilters()) {
                    document.add(new Paragraph("- " + valueFormatter.formatFilterText(filter)));
                }
                document.add(new Paragraph(" "));
            }

            PdfPTable table = new PdfPTable(Math.max(columns.size(), 1));
            table.setWidthPercentage(100);
            for (ReportColumnResponse column : columns) {
                PdfPCell cell = new PdfPCell(new Phrase(column.label()));
                table.addCell(cell);
            }

            for (Map<String, Object> row : rows) {
                for (ReportColumnResponse column : columns) {
                    table.addCell(valueFormatter.formatCellValue(row.get(column.key().name())));
                }
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (DocumentException | java.io.IOException ex) {
            throw new ReportException("Failed to generate PDF report", ex);
        }
    }

}
