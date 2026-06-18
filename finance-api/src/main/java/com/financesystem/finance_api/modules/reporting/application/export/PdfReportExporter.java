package com.financesystem.finance_api.modules.reporting.application.export;

import com.financesystem.finance_api.modules.reporting.domain.ReportColumn;
import com.financesystem.finance_api.modules.reporting.domain.ReportFormat;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.List;

/** Generic PDF exporter (OpenPDF). Renders columns + rows as a table. */
@Component
public class PdfReportExporter implements ReportExporter {

    @Override
    public ReportFormat format() {
        return ReportFormat.PDF;
    }

    @Override
    public String contentType() {
        return "application/pdf";
    }

    @Override
    public String fileExtension() {
        return "pdf";
    }

    @Override
    public byte[] export(String title, List<ReportColumn> columns, List<List<Object>> rows) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            if (title != null && !title.isBlank()) {
                Paragraph heading = new Paragraph(title);
                heading.setSpacingAfter(12f);
                document.add(heading);
            }

            if (columns.isEmpty()) {
                document.add(new Paragraph("Sin columnas."));
                document.close();
                return out.toByteArray();
            }

            PdfPTable table = new PdfPTable(columns.size());
            table.setWidthPercentage(100);

            for (ReportColumn column : columns) {
                PdfPCell cell = new PdfPCell(new Phrase(column.name()));
                cell.setGrayFill(0.85f);
                table.addCell(cell);
            }

            for (List<Object> row : rows) {
                for (Object value : row) {
                    table.addCell(new PdfPCell(new Phrase(ReportExportValues.asText(value))));
                }
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            if (document.isOpen()) {
                document.close();
            }
            throw new ReportExportException("No se pudo generar el PDF: " + e.getMessage(), e);
        }
    }
}
