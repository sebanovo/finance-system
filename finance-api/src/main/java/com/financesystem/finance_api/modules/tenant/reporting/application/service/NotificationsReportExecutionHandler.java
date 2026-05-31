package com.financesystem.finance_api.modules.tenant.reporting.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.notifications.RunAnalyticNotificationsReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.notifications.RunManagerialNotificationsReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportExecutionPayloadException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportValidationException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class NotificationsReportExecutionHandler implements ReportExecutionHandler {

    private final NotificationsReportExecutionService reportExecutionService;
    private final ObjectMapper objectMapper;

    public NotificationsReportExecutionHandler(NotificationsReportExecutionService reportExecutionService, ObjectMapper objectMapper) {
        this.reportExecutionService = reportExecutionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.NOTIFICATIONS;
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

    private RunAnalyticNotificationsReportRequest deserializeAnalytic(JsonNode requestBody) {
        try {
            return objectMapper.treeToValue(requestBody, RunAnalyticNotificationsReportRequest.class);
        } catch (Exception ex) {
            throw new ReportExecutionPayloadException("Failed to deserialize analytic notifications report request", ex);
        }
    }

    private RunManagerialNotificationsReportRequest deserializeManagerial(JsonNode requestBody) {
        try {
            return objectMapper.treeToValue(requestBody, RunManagerialNotificationsReportRequest.class);
        } catch (Exception ex) {
            throw new ReportExecutionPayloadException("Failed to deserialize managerial notifications report request", ex);
        }
    }

    private RunAnalyticNotificationsReportRequest withOutputs(RunAnalyticNotificationsReportRequest request, List<ReportOutput> outputs) {
        return new RunAnalyticNotificationsReportRequest(request.reportType(), request.columns(), request.filters(), request.sort(), request.pagination(), outputs);
    }

    private RunManagerialNotificationsReportRequest withOutputs(RunManagerialNotificationsReportRequest request, List<ReportOutput> outputs) {
        return new RunManagerialNotificationsReportRequest(request.reportType(), request.groupBy(), request.metrics(), request.filters(), request.sort(), request.pagination(), outputs, request.visualizations());
    }
}
