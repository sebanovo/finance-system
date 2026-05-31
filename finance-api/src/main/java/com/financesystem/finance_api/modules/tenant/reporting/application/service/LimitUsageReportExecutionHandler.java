package com.financesystem.finance_api.modules.tenant.reporting.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.limitusage.RunAnalyticLimitUsageReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.limitusage.RunManagerialLimitUsageReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportExecutionPayloadException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportValidationException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class LimitUsageReportExecutionHandler implements ReportExecutionHandler {

    private final LimitUsageReportExecutionService reportExecutionService;
    private final ObjectMapper objectMapper;

    public LimitUsageReportExecutionHandler(LimitUsageReportExecutionService reportExecutionService, ObjectMapper objectMapper) {
        this.reportExecutionService = reportExecutionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.LIMIT_USAGE;
    }

    @Override
    public ReportResultResponse execute(ReportMode mode, JsonNode requestBody) {
        if (mode == ReportMode.ANALYTIC) {
            return reportExecutionService.executeAnalytic(deserializeAnalytic(requestBody));
        }
        if (mode == ReportMode.MANAGERIAL) {
            return reportExecutionService.executeManagerial(deserializeManagerial(requestBody));
        }
        throw new ReportValidationException("Unsupported report mode: " + mode);
    }

    @Override
    public ReportResultResponse rerun(ReportMode mode, JsonNode requestBody, List<ReportOutput> outputs, UUID sourceExecutionId) {
        if (mode == ReportMode.ANALYTIC) {
            return reportExecutionService.executeAnalytic(withOutputs(deserializeAnalytic(requestBody), outputs), sourceExecutionId);
        }
        if (mode == ReportMode.MANAGERIAL) {
            return reportExecutionService.executeManagerial(withOutputs(deserializeManagerial(requestBody), outputs), sourceExecutionId);
        }
        throw new ReportValidationException("Unsupported report mode: " + mode);
    }

    private RunAnalyticLimitUsageReportRequest deserializeAnalytic(JsonNode requestBody) {
        try {
            return objectMapper.treeToValue(requestBody, RunAnalyticLimitUsageReportRequest.class);
        } catch (Exception ex) {
            throw new ReportExecutionPayloadException("Failed to deserialize analytic limit usage report request", ex);
        }
    }

    private RunManagerialLimitUsageReportRequest deserializeManagerial(JsonNode requestBody) {
        try {
            return objectMapper.treeToValue(requestBody, RunManagerialLimitUsageReportRequest.class);
        } catch (Exception ex) {
            throw new ReportExecutionPayloadException("Failed to deserialize managerial limit usage report request", ex);
        }
    }

    private RunAnalyticLimitUsageReportRequest withOutputs(RunAnalyticLimitUsageReportRequest request, List<ReportOutput> outputs) {
        return new RunAnalyticLimitUsageReportRequest(
                request.reportType(),
                request.columns(),
                request.filters(),
                request.sort(),
                request.pagination(),
                outputs
        );
    }

    private RunManagerialLimitUsageReportRequest withOutputs(RunManagerialLimitUsageReportRequest request, List<ReportOutput> outputs) {
        return new RunManagerialLimitUsageReportRequest(
                request.reportType(),
                request.groupBy(),
                request.metrics(),
                request.filters(),
                request.sort(),
                request.pagination(),
                outputs,
                request.visualizations()
        );
    }
}
