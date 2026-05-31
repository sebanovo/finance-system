package com.financesystem.finance_api.modules.tenant.reporting.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.accountingperiods.RunAnalyticAccountingPeriodsReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.accountingperiods.RunManagerialAccountingPeriodsReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportExecutionPayloadException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportValidationException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AccountingPeriodsReportExecutionHandler implements ReportExecutionHandler {

    private final AccountingPeriodsReportExecutionService reportExecutionService;
    private final ObjectMapper objectMapper;

    public AccountingPeriodsReportExecutionHandler(AccountingPeriodsReportExecutionService reportExecutionService, ObjectMapper objectMapper) {
        this.reportExecutionService = reportExecutionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.ACCOUNTING_PERIODS;
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

    private RunAnalyticAccountingPeriodsReportRequest deserializeAnalytic(JsonNode requestBody) {
        try {
            return objectMapper.treeToValue(requestBody, RunAnalyticAccountingPeriodsReportRequest.class);
        } catch (Exception ex) {
            throw new ReportExecutionPayloadException("Failed to deserialize analytic accounting periods report request", ex);
        }
    }

    private RunManagerialAccountingPeriodsReportRequest deserializeManagerial(JsonNode requestBody) {
        try {
            return objectMapper.treeToValue(requestBody, RunManagerialAccountingPeriodsReportRequest.class);
        } catch (Exception ex) {
            throw new ReportExecutionPayloadException("Failed to deserialize managerial accounting periods report request", ex);
        }
    }

    private RunAnalyticAccountingPeriodsReportRequest withOutputs(RunAnalyticAccountingPeriodsReportRequest request, List<ReportOutput> outputs) {
        return new RunAnalyticAccountingPeriodsReportRequest(
                request.reportType(),
                request.columns(),
                request.filters(),
                request.sort(),
                request.pagination(),
                outputs
        );
    }

    private RunManagerialAccountingPeriodsReportRequest withOutputs(RunManagerialAccountingPeriodsReportRequest request, List<ReportOutput> outputs) {
        return new RunManagerialAccountingPeriodsReportRequest(
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
