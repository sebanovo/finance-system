package com.financesystem.finance_api.modules.reporting.application.export;

import com.financesystem.finance_api.modules.reporting.application.ReportLabels;
import com.financesystem.finance_api.modules.reporting.domain.ReportColumn;
import com.financesystem.finance_api.modules.reporting.domain.ReportDataType;
import com.financesystem.finance_api.modules.reporting.domain.ReportFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;
import org.apache.poi.xddf.usermodel.chart.BarDirection;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.LegendPosition;
import org.apache.poi.xddf.usermodel.chart.XDDFBarChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFChartLegend;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * XLSX exporter (Apache POI): a tidy data sheet plus, when the data has a
 * category + numeric column, a native bar chart anchored next to the table so
 * the workbook is both data-complete and visual.
 */
@Component
public class XlsxReportExporter implements ReportExporter {

    private static final Logger log = LoggerFactory.getLogger(XlsxReportExporter.class);
    private static final int MAX_CHART_CATEGORIES = 12;

    @Override
    public ReportFormat format() {
        return ReportFormat.XLSX;
    }

    @Override
    public String contentType() {
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    @Override
    public String fileExtension() {
        return "xlsx";
    }

    @Override
    public byte[] export(String title, List<ReportColumn> columns, List<List<Object>> rows) {
        try (XSSFWorkbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String sheetName = WorkbookUtil.createSafeSheetName(
                    title == null || title.isBlank() ? "Reporte" : title);
            XSSFSheet sheet = workbook.createSheet(sheetName);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            DataFormat dataFormat = workbook.createDataFormat();
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setDataFormat(dataFormat.getFormat("#,##0.##"));

            Row header = sheet.createRow(0);
            for (int c = 0; c < columns.size(); c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(ReportLabels.humanize(columns.get(c).name()));
                cell.setCellStyle(headerStyle);
            }

            int r = 1;
            for (List<Object> row : rows) {
                Row sheetRow = sheet.createRow(r++);
                for (int c = 0; c < columns.size(); c++) {
                    Object value = c < row.size() ? row.get(c) : null;
                    writeCell(sheetRow.createCell(c), columns.get(c).type(), value, numberStyle);
                }
            }

            sheet.createFreezePane(0, 1);
            for (int c = 0; c < columns.size(); c++) {
                sheet.autoSizeColumn(c);
            }

            addChartIfPossible(sheet, title, columns, rows);

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ReportExportException("No se pudo generar el XLSX: " + e.getMessage(), e);
        }
    }

    private void addChartIfPossible(XSSFSheet sheet, String title, List<ReportColumn> columns, List<List<Object>> rows) {
        int labelIdx = firstIndexOfType(columns, ReportDataType.TEXT, ReportDataType.DATE, ReportDataType.TIMESTAMP);
        int valueIdx = firstIndexOfType(columns, ReportDataType.NUMBER);
        if (labelIdx < 0 || valueIdx < 0 || rows.isEmpty()) {
            return;
        }
        int lastRow = Math.min(rows.size(), MAX_CHART_CATEGORIES);
        try {
            int anchorCol = columns.size() + 1;
            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0,
                    anchorCol, 1, anchorCol + 9, 21);
            XSSFChart chart = drawing.createChart(anchor);
            chart.setTitleText((title == null || title.isBlank() ? "Reporte" : title));
            chart.setTitleOverlay(false);

            XDDFChartLegend legend = chart.getOrAddLegend();
            legend.setPosition(LegendPosition.BOTTOM);

            XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
            bottomAxis.setTitle(ReportLabels.humanize(columns.get(labelIdx).name()));
            XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
            leftAxis.setTitle(ReportLabels.humanize(columns.get(valueIdx).name()));

            XDDFDataSource<String> categories = XDDFDataSourcesFactory.fromStringCellRange(
                    sheet, new CellRangeAddress(1, lastRow, labelIdx, labelIdx));
            XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(
                    sheet, new CellRangeAddress(1, lastRow, valueIdx, valueIdx));

            XDDFBarChartData data = (XDDFBarChartData) chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
            data.setBarDirection(BarDirection.COL);
            XDDFBarChartData.Series series = (XDDFBarChartData.Series) data.addSeries(categories, values);
            series.setTitle(ReportLabels.humanize(columns.get(valueIdx).name()), null);
            chart.plot(data);
        } catch (Exception e) {
            // A chart problem must never break the data export.
            log.warn("No se pudo agregar el gráfico al XLSX: {}", e.getMessage());
        }
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

    private void writeCell(Cell cell, ReportDataType type, Object value, CellStyle numberStyle) {
        if (value == null) {
            cell.setBlank();
            return;
        }
        switch (type) {
            case NUMBER -> {
                if (value instanceof Number number) {
                    cell.setCellValue(number.doubleValue());
                    cell.setCellStyle(numberStyle);
                } else {
                    try {
                        cell.setCellValue(Double.parseDouble(ReportExportValues.asText(value)));
                        cell.setCellStyle(numberStyle);
                    } catch (NumberFormatException e) {
                        cell.setCellValue(ReportExportValues.asText(value));
                    }
                }
            }
            case BOOLEAN -> {
                if (value instanceof Boolean bool) {
                    cell.setCellValue(bool);
                } else {
                    cell.setCellValue(ReportExportValues.asText(value));
                }
            }
            default -> cell.setCellValue(ReportExportValues.asText(value));
        }
    }
}
