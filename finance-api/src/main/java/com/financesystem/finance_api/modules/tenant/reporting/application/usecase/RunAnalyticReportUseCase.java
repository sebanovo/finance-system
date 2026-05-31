package com.financesystem.finance_api.modules.tenant.reporting.application.usecase;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.accounts.RunAnalyticAccountReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.service.ReportExecutionService;
import org.springframework.stereotype.Service;

@Service
public class RunAnalyticReportUseCase {

    private final ReportExecutionService reportExecutionService;

    public RunAnalyticReportUseCase(ReportExecutionService reportExecutionService) {
        this.reportExecutionService = reportExecutionService;
    }

    public ReportResultResponse execute(RunAnalyticAccountReportRequest request) {
        return reportExecutionService.executeAnalytic(request);
    }
}
