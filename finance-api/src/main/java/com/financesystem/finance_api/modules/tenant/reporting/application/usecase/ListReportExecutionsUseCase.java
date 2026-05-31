package com.financesystem.finance_api.modules.tenant.reporting.application.usecase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportExecutionSummaryResponse;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExecution;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.repository.ReportExecutionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListReportExecutionsUseCase {

    private final ReportExecutionRepository reportExecutionRepository;
    private final ObjectMapper objectMapper;

    public ListReportExecutionsUseCase(
            ReportExecutionRepository reportExecutionRepository,
            ObjectMapper objectMapper
    ) {
        this.reportExecutionRepository = reportExecutionRepository;
        this.objectMapper = objectMapper;
    }

    public List<ReportExecutionSummaryResponse> execute(String reportType, String mode, int limit, int offset) {
        List<ReportExecution> executions = reportExecutionRepository.findRecent(reportType, mode, limit, offset);
        return executions.stream()
                .map(execution -> new ReportExecutionSummaryResponse(
                        execution.id(),
                        execution.reportType(),
                        execution.reportTitle(),
                        execution.executionName(),
                        execution.mode(),
                        execution.requestedBySubject(),
                        parseOutputs(execution.outputs()),
                        execution.rowCount(),
                        execution.status(),
                        execution.createdAt(),
                        execution.sourceExecutionId() != null,
                        execution.sourceExecutionId()
                ))
                .toList();
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
