package com.financesystem.finance_api.modules.reporting.application.export;

import com.financesystem.finance_api.modules.reporting.application.ReportLabels;
import com.financesystem.finance_api.modules.reporting.domain.ReportColumn;
import com.financesystem.finance_api.modules.reporting.domain.ReportDataType;
import com.financesystem.finance_api.modules.reporting.domain.ReportFormat;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Executive PDF exporter (OpenPDF): branded header, KPI cards, an auto bar chart
 * (first label column vs. first numeric column) and a styled data table.
 * Charts are drawn with nested tables so no extra charting dependency is needed.
 */
@Component
public class PdfReportExporter implements ReportExporter {

    private static final Color BRAND = new Color(46, 125, 50);     // #2E7D32
    private static final Color BRAND_DARK = new Color(27, 94, 32);  // #1B5E20
    private static final Color BRAND_SOFT = new Color(241, 248, 233); // #F1F8E9
    private static final Color ZEBRA = new Color(248, 251, 246);
    private static final Color BORDER = new Color(200, 230, 201);   // #C8E6C9
    private static final Color MUTED = new Color(95, 111, 95);
    private static final Color BAR = new Color(76, 175, 80);        // #4CAF50

    private static final Color[] PALETTE = {
            new Color(46, 125, 50), new Color(76, 175, 80), new Color(30, 136, 229),
            new Color(251, 140, 0), new Color(229, 57, 53), new Color(142, 36, 170),
            new Color(0, 137, 123), new Color(253, 216, 53), new Color(109, 76, 65),
            new Color(158, 158, 158)
    };

    private static final int MAX_CHART_BARS = 12;
    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC);
    private static final DecimalFormat NUM = new DecimalFormat("#,##0.##");

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
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 110, 48);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new ExecutiveHeaderFooter(safeTitle(title), rows.size()));
            document.open();

            if (columns.isEmpty()) {
                document.add(new Paragraph("Sin columnas para mostrar.",
                        FontFactory.getFont(FontFactory.HELVETICA, 11, MUTED)));
                document.close();
                return out.toByteArray();
            }

            int labelIdx = firstIndexOfType(columns, ReportDataType.TEXT, ReportDataType.DATE, ReportDataType.TIMESTAMP);
            int valueIdx = firstIndexOfType(columns, ReportDataType.NUMBER);

            addKpiCards(document, columns, rows, valueIdx);
            if (labelIdx >= 0 && valueIdx >= 0 && !rows.isEmpty()) {
                addDonutChart(document, writer, columns, rows, labelIdx, valueIdx);
                addBarChart(document, columns, rows, labelIdx, valueIdx);
            }
            addDataTable(document, columns, rows);

            document.close();
            return out.toByteArray();
        } catch (DocumentException e) {
            if (document.isOpen()) {
                document.close();
            }
            throw new ReportExportException("No se pudo generar el PDF: " + e.getMessage(), e);
        }
    }

    // ---- KPI cards (executive summary) ----

    private void addKpiCards(Document document, List<ReportColumn> columns, List<List<Object>> rows, int valueIdx)
            throws DocumentException {
        PdfPTable cards = new PdfPTable(valueIdx >= 0 ? 3 : 1);
        cards.setWidthPercentage(100);
        cards.setSpacingAfter(16f);

        cards.addCell(kpiCard("Registros", NUM.format(rows.size())));
        if (valueIdx >= 0) {
            double sum = 0;
            double max = Double.NEGATIVE_INFINITY;
            for (List<Object> row : rows) {
                Double v = toNumber(valueIdx < row.size() ? row.get(valueIdx) : null);
                if (v != null) {
                    sum += v;
                    max = Math.max(max, v);
                }
            }
            String valueName = ReportLabels.humanize(columns.get(valueIdx).name());
            cards.addCell(kpiCard("Total " + valueName, NUM.format(sum)));
            cards.addCell(kpiCard("Máximo " + valueName, max == Double.NEGATIVE_INFINITY ? "—" : NUM.format(max)));
        }
        document.add(cards);
    }

    private PdfPCell kpiCard(String label, String value) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(BRAND_SOFT);
        cell.setBorderColor(BORDER);
        cell.setPadding(10f);
        Paragraph l = new Paragraph(label.toUpperCase(),
                FontFactory.getFont(FontFactory.HELVETICA, 8, MUTED));
        Paragraph v = new Paragraph(value,
                FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD, BRAND_DARK));
        v.setSpacingBefore(4f);
        cell.addElement(l);
        cell.addElement(v);
        return cell;
    }

    // ---- Donut chart (vector, drawn into a template) ----

    private void addDonutChart(Document document, PdfWriter writer, List<ReportColumn> columns,
                               List<List<Object>> rows, int labelIdx, int valueIdx) throws DocumentException {
        Map<String, Double> totals = new LinkedHashMap<>();
        for (List<Object> row : rows) {
            Double v = toNumber(get(row, valueIdx));
            if (v == null) {
                continue;
            }
            totals.merge(ReportExportValues.asText(get(row, labelIdx)), Math.abs(v), Double::sum);
        }
        List<Map.Entry<String, Double>> entries = new ArrayList<>(totals.entrySet());
        entries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        if (entries.size() > 9) {
            double rest = 0;
            for (int i = 8; i < entries.size(); i++) {
                rest += entries.get(i).getValue();
            }
            entries = new ArrayList<>(entries.subList(0, 8));
            entries.add(Map.entry("Otros", rest));
        }
        double total = entries.stream().mapToDouble(Map.Entry::getValue).sum();
        if (total <= 0 || entries.isEmpty()) {
            return;
        }

        Paragraph heading = new Paragraph(
                "Distribución de " + ReportLabels.humanize(columns.get(valueIdx).name())
                        + " por " + ReportLabels.humanize(columns.get(labelIdx).name()),
                FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, BRAND_DARK));
        heading.setSpacingAfter(6f);
        document.add(heading);

        float size = 150f;
        float cx = size / 2f;
        float cy = size / 2f;
        float radius = 62f;
        float holeRadius = 36f;
        PdfTemplate tpl = writer.getDirectContent().createTemplate(size, size);
        // Draw each slice as a filled polygon wedge (centre -> arc points -> close),
        // then punch a white hole in the middle for the donut effect.
        double startAngle = 90.0; // 12 o'clock
        for (int i = 0; i < entries.size(); i++) {
            double sweep = entries.get(i).getValue() / total * 360.0;
            int steps = Math.max(2, (int) Math.ceil(sweep / 6.0));
            tpl.setColorFill(PALETTE[i % PALETTE.length]);
            tpl.moveTo(cx, cy);
            for (int k = 0; k <= steps; k++) {
                double ang = Math.toRadians(startAngle - sweep * k / steps);
                tpl.lineTo((float) (cx + radius * Math.cos(ang)), (float) (cy + radius * Math.sin(ang)));
            }
            tpl.closePath();
            tpl.fill();
            startAngle -= sweep;
        }
        tpl.setColorFill(Color.WHITE);
        tpl.circle(cx, cy, holeRadius);
        tpl.fill();

        Image donut = Image.getInstance(tpl);
        donut.scaleToFit(130f, 130f);

        PdfPTable legend = new PdfPTable(new float[]{7f, 63f, 30f});
        legend.setWidthPercentage(100);
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, Double> entry = entries.get(i);
            int pct = (int) Math.round(entry.getValue() / total * 100);
            PdfPCell swatch = new PdfPCell(new Phrase(" "));
            swatch.setBackgroundColor(PALETTE[i % PALETTE.length]);
            swatch.setFixedHeight(9f);
            swatch.setBorder(Rectangle.NO_BORDER);
            legend.addCell(swatch);
            legend.addCell(plainCell(entry.getKey(),
                    FontFactory.getFont(FontFactory.HELVETICA, 8, MUTED), Element.ALIGN_LEFT));
            legend.addCell(plainCell(NUM.format(entry.getValue()) + "  (" + pct + "%)",
                    FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD, BRAND_DARK), Element.ALIGN_RIGHT));
        }

        PdfPTable layout = new PdfPTable(new float[]{32f, 68f});
        layout.setWidthPercentage(100);
        layout.setSpacingAfter(16f);

        PdfPCell donutCell = new PdfPCell(donut, false);
        donutCell.setBorder(Rectangle.NO_BORDER);
        donutCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        donutCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        donutCell.setPadding(4f);
        layout.addCell(donutCell);

        PdfPCell legendCell = new PdfPCell(legend);
        legendCell.setBorder(Rectangle.NO_BORDER);
        legendCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        legendCell.setPaddingLeft(12f);
        layout.addCell(legendCell);

        document.add(layout);
    }

    // ---- Bar chart (nested tables, dependency-free) ----

    private void addBarChart(Document document, List<ReportColumn> columns, List<List<Object>> rows,
                             int labelIdx, int valueIdx) throws DocumentException {
        Paragraph heading = new Paragraph(
                ReportLabels.humanize(columns.get(valueIdx).name()) + " por "
                        + ReportLabels.humanize(columns.get(labelIdx).name())
                        + " (top " + MAX_CHART_BARS + ")",
                FontFactory.getFont(FontFactory.HELVETICA, 11, Font.BOLD, BRAND_DARK));
        heading.setSpacingAfter(6f);
        document.add(heading);

        double max = 1;
        int count = Math.min(rows.size(), MAX_CHART_BARS);
        for (int i = 0; i < count; i++) {
            Double v = toNumber(get(rows.get(i), valueIdx));
            if (v != null) {
                max = Math.max(max, Math.abs(v));
            }
        }

        PdfPTable chart = new PdfPTable(new float[]{26f, 60f, 14f});
        chart.setWidthPercentage(100);
        chart.setSpacingAfter(18f);

        for (int i = 0; i < count; i++) {
            List<Object> row = rows.get(i);
            String label = ReportExportValues.asText(get(row, labelIdx));
            Double value = toNumber(get(row, valueIdx));
            double v = value == null ? 0 : value;
            int percent = (int) Math.max(3, Math.round(Math.abs(v) / max * 100));

            chart.addCell(plainCell(label, FontFactory.getFont(FontFactory.HELVETICA, 8, MUTED), Element.ALIGN_LEFT));

            PdfPCell barWrap = new PdfPCell(barTrack(percent));
            barWrap.setBorder(Rectangle.NO_BORDER);
            barWrap.setPaddingTop(2f);
            barWrap.setPaddingBottom(2f);
            barWrap.setVerticalAlignment(Element.ALIGN_MIDDLE);
            chart.addCell(barWrap);

            chart.addCell(plainCell(NUM.format(v),
                    FontFactory.getFont(FontFactory.HELVETICA, 8, Font.BOLD, BRAND_DARK), Element.ALIGN_RIGHT));
        }
        document.add(chart);
    }

    /** A horizontal bar: filled part (percent) + empty remainder. */
    private PdfPTable barTrack(int percent) {
        if (percent >= 100) {
            PdfPTable full = new PdfPTable(1);
            full.setWidthPercentage(100);
            full.addCell(barSegment(BAR, 14f));
            return full;
        }
        PdfPTable track = new PdfPTable(new float[]{percent, 100 - percent});
        track.setWidthPercentage(100);
        track.addCell(barSegment(BAR, 14f));
        track.addCell(barSegment(new Color(238, 245, 234), 14f));
        return track;
    }

    private PdfPCell barSegment(Color color, float height) {
        PdfPCell seg = new PdfPCell(new Phrase(" "));
        seg.setBackgroundColor(color);
        seg.setBorder(Rectangle.NO_BORDER);
        seg.setFixedHeight(height);
        return seg;
    }

    // ---- Data table (styled, zebra, number-aware) ----

    private void addDataTable(Document document, List<ReportColumn> columns, List<List<Object>> rows)
            throws DocumentException {
        PdfPTable table = new PdfPTable(columns.size());
        table.setWidthPercentage(100);
        table.setHeaderRows(1);

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, Color.WHITE);
        for (ReportColumn column : columns) {
            PdfPCell header = new PdfPCell(new Phrase(ReportLabels.humanize(column.name()), headerFont));
            header.setBackgroundColor(BRAND);
            header.setBorderColor(BRAND);
            header.setPadding(6f);
            header.setHorizontalAlignment(
                    column.type() == ReportDataType.NUMBER ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
            table.addCell(header);
        }

        Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8, new Color(40, 50, 40));
        for (int r = 0; r < rows.size(); r++) {
            List<Object> row = rows.get(r);
            Color bg = r % 2 == 0 ? Color.WHITE : ZEBRA;
            for (int c = 0; c < columns.size(); c++) {
                Object value = c < row.size() ? row.get(c) : null;
                PdfPCell cell = new PdfPCell(new Phrase(ReportExportValues.asText(value), cellFont));
                cell.setBackgroundColor(bg);
                cell.setBorderColor(BORDER);
                cell.setPadding(5f);
                cell.setHorizontalAlignment(
                        columns.get(c).type() == ReportDataType.NUMBER ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
                table.addCell(cell);
            }
        }
        document.add(table);
    }

    // ---- Helpers ----

    private PdfPCell plainCell(String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPaddingTop(3f);
        cell.setPaddingBottom(3f);
        return cell;
    }

    private int firstIndexOfType(List<ReportColumn> columns, ReportDataType... types) {
        for (int i = 0; i < columns.size(); i++) {
            for (ReportDataType type : types) {
                if (columns.get(i).type() == type) {
                    return i;
                }
            }
        }
        return -1;
    }

    private Object get(List<Object> row, int index) {
        return index >= 0 && index < row.size() ? row.get(index) : null;
    }

    private Double toNumber(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number n) {
            return n.doubleValue();
        }
        try {
            return new BigDecimal(value.toString().trim()).doubleValue();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String safeTitle(String title) {
        return title == null || title.isBlank() ? "Reporte" : title;
    }

    /** Draws the branded header band on every page and a footer with page numbers. */
    private static final class ExecutiveHeaderFooter extends com.lowagie.text.pdf.PdfPageEventHelper {
        private final String title;
        private final int rowCount;
        private final com.lowagie.text.pdf.BaseFont baseFont;

        private ExecutiveHeaderFooter(String title, int rowCount) {
            this.title = title;
            this.rowCount = rowCount;
            com.lowagie.text.pdf.BaseFont font = null;
            try {
                font = com.lowagie.text.pdf.BaseFont.createFont(
                        com.lowagie.text.pdf.BaseFont.HELVETICA,
                        com.lowagie.text.pdf.BaseFont.WINANSI,
                        com.lowagie.text.pdf.BaseFont.NOT_EMBEDDED);
            } catch (Exception ignored) {
                // header text will be skipped if the base font is unavailable
            }
            this.baseFont = font;
        }

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            Rectangle page = document.getPageSize();

            // Header band (drawn under the flowing content).
            com.lowagie.text.pdf.PdfContentByte cb = writer.getDirectContentUnder();
            cb.setColorFill(BRAND);
            cb.rectangle(0, page.getTop() - 70, page.getWidth(), 70);
            cb.fill();

            if (baseFont == null) {
                return;
            }

            com.lowagie.text.pdf.PdfContentByte over = writer.getDirectContent();
            over.beginText();
            over.setFontAndSize(baseFont, 18);
            over.setColorFill(Color.WHITE);
            over.showTextAligned(Element.ALIGN_LEFT, title, 40, page.getTop() - 40, 0);
            over.setFontAndSize(baseFont, 9);
            over.showTextAligned(Element.ALIGN_LEFT,
                    "Reporte ejecutivo · " + STAMP.format(java.time.Instant.now()) + " UTC · "
                            + rowCount + " registros",
                    40, page.getTop() - 58, 0);
            over.setColorFill(MUTED);
            over.showTextAligned(Element.ALIGN_RIGHT,
                    "Finance System · pagina " + writer.getPageNumber(),
                    page.getWidth() - 40, 28, 0);
            over.endText();
        }
    }
}
