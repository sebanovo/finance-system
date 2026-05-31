package com.financesystem.finance_api.modules.tenant.reporting.application.export;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportColumnResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportHeaderResponse;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class ReportXlsxExporter {

    private final ReportDisplayValueFormatter valueFormatter;

    public ReportXlsxExporter() {
        this.valueFormatter = new ReportDisplayValueFormatter();
    }

    public byte[] export(
            ReportHeaderResponse header,
            List<ReportColumnResponse> columns,
            List<Map<String, Object>> rows
    ) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            CreationHelper helper = workbook.getCreationHelper();

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            Sheet reportSheet = workbook.createSheet("Reporte");
            writeReportSheet(reportSheet, headerStyle, columns, rows);

            Sheet filtersSheet = workbook.createSheet("Filtros");
            writeFiltersSheet(filtersSheet, header, headerStyle);

            Sheet metadataSheet = workbook.createSheet("Metadata");
            writeMetadataSheet(metadataSheet, header, headerStyle);

            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                workbook.getSheetAt(i).createFreezePane(0, 1);
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (java.io.IOException ex) {
            throw new ReportException("Failed to generate XLSX report", ex);
        }
    }

    private void writeReportSheet(
            Sheet sheet,
            CellStyle headerStyle,
            List<ReportColumnResponse> columns,
            List<Map<String, Object>> rows
    ) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns.get(i).label());
            cell.setCellStyle(headerStyle);
        }

        for (int rowIndex = 0; rowIndex < rows.size(); rowIndex++) {
            Map<String, Object> row = rows.get(rowIndex);
            Row dataRow = sheet.createRow(rowIndex + 1);
            for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                Object value = row.get(columns.get(colIndex).key().name());
                dataRow.createCell(colIndex).setCellValue(valueFormatter.formatCellValue(value));
            }
        }

        autoSize(sheet, columns.size());
    }

    private void writeFiltersSheet(Sheet sheet, ReportHeaderResponse header, CellStyle headerStyle) {
        Row title = sheet.createRow(0);
        title.createCell(0).setCellValue("Filtros aplicados");
        title.getCell(0).setCellStyle(headerStyle);

        for (int i = 0; i < header.appliedFilters().size(); i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(valueFormatter.formatFilterText(header.appliedFilters().get(i)));
        }
        autoSize(sheet, 1);
    }

    private void writeMetadataSheet(Sheet sheet, ReportHeaderResponse header, CellStyle headerStyle) {
        int rowIndex = 0;
        rowIndex = writeMetadataRow(sheet, rowIndex, "Título", header.title(), headerStyle);
        rowIndex = writeMetadataRow(sheet, rowIndex, "Modo", header.modeLabel(), headerStyle);
        rowIndex = writeMetadataRow(sheet, rowIndex, "Generado por", header.generatedBy(), headerStyle);
        rowIndex = writeMetadataRow(sheet, rowIndex, "Generado el", valueFormatter.formatCellValue(header.generatedAt()), headerStyle);
        rowIndex = writeMetadataRow(sheet, rowIndex, "Columnas seleccionadas", String.join(", ", header.selectedColumns()), headerStyle);
        rowIndex = writeMetadataRow(sheet, rowIndex, "Métricas seleccionadas", String.join(", ", header.selectedMetrics()), headerStyle);
        writeMetadataRow(sheet, rowIndex, "GroupBy seleccionados", String.join(", ", header.selectedGroupBy()), headerStyle);
        autoSize(sheet, 2);
    }

    private int writeMetadataRow(Sheet sheet, int rowIndex, String label, String value, CellStyle headerStyle) {
        Row row = sheet.createRow(rowIndex);
        Cell labelCell = row.createCell(0);
        labelCell.setCellValue(label);
        labelCell.setCellStyle(headerStyle);
        row.createCell(1).setCellValue(value == null ? "" : value);
        return rowIndex + 1;
    }

    private void autoSize(Sheet sheet, int columns) {
        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}
