package com.financesystem.finance_api.modules.reporting.application.export;

import com.financesystem.finance_api.modules.reporting.domain.ReportColumn;
import com.financesystem.finance_api.modules.reporting.domain.ReportDataType;
import com.financesystem.finance_api.modules.reporting.domain.ReportFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/** Generic XLSX exporter (Apache POI). Renders columns + rows as a sheet. */
@Component
public class XlsxReportExporter implements ReportExporter {

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
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            String sheetName = WorkbookUtil.createSafeSheetName(
                    title == null || title.isBlank() ? "Reporte" : title);
            Sheet sheet = workbook.createSheet(sheetName);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Row header = sheet.createRow(0);
            for (int c = 0; c < columns.size(); c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(columns.get(c).name());
                cell.setCellStyle(headerStyle);
            }

            int r = 1;
            for (List<Object> row : rows) {
                Row sheetRow = sheet.createRow(r++);
                for (int c = 0; c < columns.size(); c++) {
                    Object value = c < row.size() ? row.get(c) : null;
                    writeCell(sheetRow.createCell(c), columns.get(c).type(), value);
                }
            }

            for (int c = 0; c < columns.size(); c++) {
                sheet.autoSizeColumn(c);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new ReportExportException("No se pudo generar el XLSX: " + e.getMessage(), e);
        }
    }

    private void writeCell(Cell cell, ReportDataType type, Object value) {
        if (value == null) {
            cell.setBlank();
            return;
        }
        switch (type) {
            case NUMBER -> {
                if (value instanceof Number number) {
                    cell.setCellValue(number.doubleValue());
                } else {
                    cell.setCellValue(ReportExportValues.asText(value));
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
