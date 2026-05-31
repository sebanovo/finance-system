package com.financesystem.finance_api.modules.tenant.reporting.application.usecase;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.accounts.RunManagerialAccountReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.service.ReportExecutionService;
import org.springframework.stereotype.Service;

@Service
public class RunManagerialReportUseCase {

    private final ReportExecutionService reportExecutionService;

    public RunManagerialReportUseCase(ReportExecutionService reportExecutionService) {
        this.reportExecutionService = reportExecutionService;
    }

    public ReportResultResponse execute(RunManagerialAccountReportRequest request) {
        return reportExecutionService.executeManagerial(request);
    }
}
