package com.financesystem.finance_api.modules.tenant.reporting.application.usecase;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactions.RunAnalyticTransactionReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.service.TransactionsReportExecutionService;
import org.springframework.stereotype.Service;

@Service
public class RunAnalyticTransactionReportUseCase {

    private final TransactionsReportExecutionService reportExecutionService;

    public RunAnalyticTransactionReportUseCase(TransactionsReportExecutionService reportExecutionService) {
        this.reportExecutionService = reportExecutionService;
    }

    public ReportResultResponse execute(RunAnalyticTransactionReportRequest request) {
        return reportExecutionService.executeAnalytic(request);
    }
}
