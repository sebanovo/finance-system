package com.financesystem.finance_api.modules.tenant.reporting.application.usecase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportExecutionDetailResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportExportSummaryResponse;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportExecutionNotFoundException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExecution;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExport;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.repository.ReportExecutionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetReportExecutionByIdUseCase {

    private final ReportExecutionRepository reportExecutionRepository;
    private final ObjectMapper objectMapper;

    public GetReportExecutionByIdUseCase(
            ReportExecutionRepository reportExecutionRepository,
            ObjectMapper objectMapper
    ) {
        this.reportExecutionRepository = reportExecutionRepository;
        this.objectMapper = objectMapper;
    }

    public ReportExecutionDetailResponse execute(UUID executionId) {
        ReportExecution execution = reportExecutionRepository.findById(executionId)
                .orElseThrow(() -> new ReportExecutionNotFoundException(executionId));

        List<ReportExportSummaryResponse> exports = reportExecutionRepository.findExportsByExecutionId(executionId)
                .stream()
                .map(this::toExportResponse)
                .toList();

        return new ReportExecutionDetailResponse(
                execution.id(),
                execution.reportType(),
                execution.reportTitle(),
                execution.executionName(),
                execution.mode(),
                execution.requestedBySubject(),
                execution.requestPayload(),
                parseOutputs(execution.outputs()),
                execution.rowCount(),
                execution.status(),
                execution.errorMessage(),
                execution.sourceExecutionId(),
                exports,
                execution.createdAt()
        );
    }

    private ReportExportSummaryResponse toExportResponse(ReportExport export) {
        return new ReportExportSummaryResponse(
                export.id(),
                export.output(),
                export.fileName(),
                export.contentType(),
                export.fileSizeBytes(),
                export.createdAt()
        );
    }

    private List<ReportOutput> parseOutputs(String outputsJson) {
        if (outputsJson == null || outputsJson.isBlank()) {
            return List.of();
        }

        try {
            return objectMapper.readValue(outputsJson, new TypeReference<List<ReportOutput>>() {});
        } catch (Exception ex) {
            return List.of();
        }
    }
}
