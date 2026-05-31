package com.financesystem.finance_api.modules.tenant.reporting.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.fxexchangerates.RunAnalyticFxExchangeRateReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.fxexchangerates.RunManagerialFxExchangeRateReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportExecutionPayloadException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportValidationException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class FxExchangeRatesReportExecutionHandler implements ReportExecutionHandler {
    private final FxExchangeRatesReportExecutionService reportExecutionService;
    private final ObjectMapper objectMapper;

    public FxExchangeRatesReportExecutionHandler(FxExchangeRatesReportExecutionService reportExecutionService, ObjectMapper objectMapper) {
        this.reportExecutionService = reportExecutionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.FX_EXCHANGE_RATES;
    }

    @Override
    public ReportResultResponse execute(ReportMode mode, JsonNode requestBody) {
        if (mode == ReportMode.ANALYTIC) return reportExecutionService.executeAnalytic(deserializeAnalytic(requestBody));
        if (mode == ReportMode.MANAGERIAL) return reportExecutionService.executeManagerial(deserializeManagerial(requestBody));
        throw new ReportValidationException("Unsupported report mode: " + mode);
    }

    @Override
    public ReportResultResponse rerun(ReportMode mode, JsonNode requestBody, List<ReportOutput> outputs, UUID sourceExecutionId) {
        if (mode == ReportMode.ANALYTIC) return reportExecutionService.executeAnalytic(withOutputs(deserializeAnalytic(requestBody), outputs), sourceExecutionId);
        if (mode == ReportMode.MANAGERIAL) return reportExecutionService.executeManagerial(withOutputs(deserializeManagerial(requestBody), outputs), sourceExecutionId);
        throw new ReportValidationException("Unsupported report mode: " + mode);
    }

    private RunAnalyticFxExchangeRateReportRequest deserializeAnalytic(JsonNode requestBody) {
        try { return objectMapper.treeToValue(requestBody, RunAnalyticFxExchangeRateReportRequest.class); } catch (Exception ex) { throw new ReportExecutionPayloadException("Failed to deserialize analytic FX exchange rates report request", ex); }
    }

    private RunManagerialFxExchangeRateReportRequest deserializeManagerial(JsonNode requestBody) {
        try { return objectMapper.treeToValue(requestBody, RunManagerialFxExchangeRateReportRequest.class); } catch (Exception ex) { throw new ReportExecutionPayloadException("Failed to deserialize managerial FX exchange rates report request", ex); }
    }

    private RunAnalyticFxExchangeRateReportRequest withOutputs(RunAnalyticFxExchangeRateReportRequest request, List<ReportOutput> outputs) {
        return new RunAnalyticFxExchangeRateReportRequest(request.reportType(), request.columns(), request.filters(), request.sort(), request.pagination(), outputs);
    }

    private RunManagerialFxExchangeRateReportRequest withOutputs(RunManagerialFxExchangeRateReportRequest request, List<ReportOutput> outputs) {
        return new RunManagerialFxExchangeRateReportRequest(request.reportType(), request.groupBy(), request.metrics(), request.filters(), request.sort(), request.pagination(), outputs, request.visualizations());
    }
}
