package com.financesystem.finance_api.modules.tenant.reporting.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.journallines.RunAnalyticJournalLinesReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.journallines.RunManagerialJournalLinesReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportExecutionPayloadException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportValidationException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JournalLinesReportExecutionHandler implements ReportExecutionHandler {

    private final JournalLinesReportExecutionService reportExecutionService;
    private final ObjectMapper objectMapper;

    public JournalLinesReportExecutionHandler(JournalLinesReportExecutionService reportExecutionService, ObjectMapper objectMapper) {
        this.reportExecutionService = reportExecutionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.JOURNAL_LINES;
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

    private RunAnalyticJournalLinesReportRequest deserializeAnalytic(JsonNode requestBody) {
        try {
            return objectMapper.treeToValue(requestBody, RunAnalyticJournalLinesReportRequest.class);
        } catch (Exception ex) {
            throw new ReportExecutionPayloadException("Failed to deserialize analytic journal lines report request", ex);
        }
    }

    private RunManagerialJournalLinesReportRequest deserializeManagerial(JsonNode requestBody) {
        try {
            return objectMapper.treeToValue(requestBody, RunManagerialJournalLinesReportRequest.class);
        } catch (Exception ex) {
            throw new ReportExecutionPayloadException("Failed to deserialize managerial journal lines report request", ex);
        }
    }

    private RunAnalyticJournalLinesReportRequest withOutputs(RunAnalyticJournalLinesReportRequest request, List<ReportOutput> outputs) {
        return new RunAnalyticJournalLinesReportRequest(
                request.reportType(),
                request.columns(),
                request.filters(),
                request.sort(),
                request.pagination(),
                outputs
        );
    }

    private RunManagerialJournalLinesReportRequest withOutputs(RunManagerialJournalLinesReportRequest request, List<ReportOutput> outputs) {
        return new RunManagerialJournalLinesReportRequest(
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
