package com.financesystem.finance_api.modules.tenant.reporting.application.usecase;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.RerunReportExecutionRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExecution;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportExecutionNotFoundException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportExecutionPayloadException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.repository.ReportExecutionRepository;
import com.financesystem.finance_api.modules.tenant.reporting.application.service.ReportExecutionDispatcher;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

@Service
public class RerunReportExecutionUseCase {

    private final ReportExecutionRepository reportExecutionRepository;
    private final ReportExecutionDispatcher reportExecutionDispatcher;
    private final ObjectMapper objectMapper;
    private final SecurityContextFacade securityContextFacade;

    public RerunReportExecutionUseCase(
            ReportExecutionRepository reportExecutionRepository,
            ReportExecutionDispatcher reportExecutionDispatcher,
            ObjectMapper objectMapper,
            SecurityContextFacade securityContextFacade
    ) {
        this.reportExecutionRepository = reportExecutionRepository;
        this.reportExecutionDispatcher = reportExecutionDispatcher;
        this.objectMapper = objectMapper;
        this.securityContextFacade = securityContextFacade;
    }

    public ReportResultResponse execute(UUID executionId, RerunReportExecutionRequest request) {
        ReportExecution execution = reportExecutionRepository.findById(executionId)
                .orElseThrow(() -> new ReportExecutionNotFoundException(executionId));

        List<ReportOutput> outputs = resolveOutputs(execution, request);
        ReportType reportType = resolveReportType(execution);
        ReportMode mode = resolveReportMode(execution);

        JsonNode payload = readPayload(execution.requestPayload());
        validateRerunAuthority(reportType, mode);

        if (mode == ReportMode.ANALYTIC || mode == ReportMode.MANAGERIAL) {
            return reportExecutionDispatcher.rerun(mode, reportType, payload, outputs, execution.id());
        }

        throw new ReportExecutionPayloadException("Unsupported report mode: " + mode);
    }

    private List<ReportOutput> resolveOutputs(ReportExecution execution, RerunReportExecutionRequest request) {
        if (request != null && request.outputs() != null && !request.outputs().isEmpty()) {
            return request.outputs();
        }
        try {
            return objectMapper.readValue(execution.outputs(), new TypeReference<List<ReportOutput>>() {});
        } catch (Exception ex) {
            throw new ReportExecutionPayloadException("Failed to parse stored report outputs");
        }
    }

    private ReportType resolveReportType(ReportExecution execution) {
        try {
            return ReportType.valueOf(execution.reportType());
        } catch (Exception ex) {
            throw new ReportExecutionPayloadException("Unsupported report type: " + execution.reportType());
        }
    }

    private ReportMode resolveReportMode(ReportExecution execution) {
        try {
            return ReportMode.valueOf(execution.mode());
        } catch (Exception ex) {
            throw new ReportExecutionPayloadException("Unsupported report mode: " + execution.mode());
        }
    }

    private JsonNode readPayload(String payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (Exception ex) {
            throw new ReportExecutionPayloadException("Failed to parse stored report payload");
        }
    }

    private void validateRerunAuthority(ReportType reportType, ReportMode mode) {
        if (!securityContextFacade.hasAuthority("reports.executions.rerun")) {
            throw new AccessDeniedException("Missing required authority: reports.executions.rerun");
        }
    }
}
