package com.financesystem.finance_api.modules.tenant.reporting.application.export;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportColumnResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportGeneratedFileResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportHeaderResponse;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportValidationException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class ReportExportService {

    private final ReportPdfExporter pdfExporter;
    private final ReportXlsxExporter xlsxExporter;
    private final ReportFileNameGenerator fileNameGenerator;

    public ReportExportService(
            ReportPdfExporter pdfExporter,
            ReportXlsxExporter xlsxExporter,
            ReportFileNameGenerator fileNameGenerator
    ) {
        this.pdfExporter = pdfExporter;
        this.xlsxExporter = xlsxExporter;
        this.fileNameGenerator = fileNameGenerator;
    }

    public List<ReportExportArtifact> export(
            ReportType reportType,
            ReportMode mode,
            Instant generatedAt,
            List<ReportOutput> outputs,
            int maxPdfColumns,
            ReportHeaderResponse header,
            List<ReportColumnResponse> columns,
            List<Map<String, Object>> rows
    ) {
        if (outputs == null || outputs.isEmpty()) {
            return List.of();
        }

        return outputs.stream()
                .filter(output -> output == ReportOutput.PDF || output == ReportOutput.XLSX)
                .map(output -> exportSingle(reportType, mode, generatedAt, output, maxPdfColumns, header, columns, rows))
                .toList();
    }

    public List<ReportGeneratedFileResponse> toResponses(List<ReportExportArtifact> artifacts) {
        if (artifacts == null || artifacts.isEmpty()) {
            return List.of();
        }

        return artifacts.stream()
                .map(artifact -> new ReportGeneratedFileResponse(
                        artifact.output(),
                        artifact.fileName(),
                        artifact.contentType(),
                        Base64.getEncoder().encodeToString(artifact.bytes()),
                        artifact.bytes().length
                ))
                .toList();
    }

    private ReportExportArtifact exportSingle(
            ReportType reportType,
            ReportMode mode,
            Instant generatedAt,
            ReportOutput output,
            int maxPdfColumns,
            ReportHeaderResponse header,
            List<ReportColumnResponse> columns,
            List<Map<String, Object>> rows
    ) {
        return switch (output) {
            case PDF -> {
                validatePdfColumnLimit(columns, maxPdfColumns);
                byte[] bytes = pdfExporter.export(header, columns, rows);
                yield new ReportExportArtifact(
                        output,
                        fileNameGenerator.generate(reportType, mode, output, generatedAt),
                        "application/pdf",
                        bytes
                );
            }
            case XLSX -> {
                byte[] bytes = xlsxExporter.export(header, columns, rows);
                yield new ReportExportArtifact(
                        output,
                        fileNameGenerator.generate(reportType, mode, output, generatedAt),
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                        bytes
                );
            }
            case SCREEN -> null;
        };
    }

    private void validatePdfColumnLimit(List<ReportColumnResponse> columns, int maxColumns) {
        if (columns == null) {
            return;
        }

        if (maxColumns > 0 && columns.size() > maxColumns) {
            throw new ReportValidationException(
                    "PDF export supports at most " + maxColumns + " columns. " +
                            "Reduce the selected columns or export as SCREEN/XLSX."
            );
        }
    }
}
