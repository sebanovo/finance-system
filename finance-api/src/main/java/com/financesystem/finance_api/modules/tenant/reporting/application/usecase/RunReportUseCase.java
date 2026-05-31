package com.financesystem.finance_api.modules.tenant.reporting.application.usecase;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.service.ReportExecutionDispatcher;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import org.springframework.stereotype.Service;

@Service
public class RunReportUseCase {

    private final ReportExecutionDispatcher dispatcher;

    public RunReportUseCase(ReportExecutionDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public ReportResultResponse execute(ReportMode mode, ReportType reportType, JsonNode requestBody) {
        return dispatcher.execute(mode, reportType, requestBody);
    }
}
