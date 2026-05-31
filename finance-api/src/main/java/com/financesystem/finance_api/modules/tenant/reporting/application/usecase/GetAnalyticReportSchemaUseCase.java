package com.financesystem.finance_api.modules.tenant.reporting.application.usecase;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportSchemaResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.registry.ReportTemplateRegistry;
import com.financesystem.finance_api.modules.tenant.reporting.application.schema.ReportSchemaBuilder;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import org.springframework.stereotype.Service;

@Service
public class GetAnalyticReportSchemaUseCase {

    private final ReportTemplateRegistry reportTemplateRegistry;
    private final ReportSchemaBuilder reportSchemaBuilder;

    public GetAnalyticReportSchemaUseCase(
            ReportTemplateRegistry reportTemplateRegistry,
            ReportSchemaBuilder reportSchemaBuilder
    ) {
        this.reportTemplateRegistry = reportTemplateRegistry;
        this.reportSchemaBuilder = reportSchemaBuilder;
    }

    public ReportSchemaResponse execute(ReportType reportType) {
        return reportSchemaBuilder.build(reportTemplateRegistry.getTemplate(reportType), ReportMode.ANALYTIC);
    }
}
