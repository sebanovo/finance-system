package com.financesystem.finance_api.modules.tenant.reporting.application.usecase;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactions.RunManagerialTransactionReportRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.service.TransactionsReportExecutionService;
import org.springframework.stereotype.Service;

@Service
public class RunManagerialTransactionReportUseCase {

    private final TransactionsReportExecutionService reportExecutionService;

    public RunManagerialTransactionReportUseCase(TransactionsReportExecutionService reportExecutionService) {
        this.reportExecutionService = reportExecutionService;
    }

    public ReportResultResponse execute(RunManagerialTransactionReportRequest request) {
        return reportExecutionService.executeManagerial(request);
    }
}
