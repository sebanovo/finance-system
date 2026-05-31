package com.financesystem.finance_api.modules.tenant.reporting.application.usecase;

import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportCatalogResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.registry.ReportTemplateRegistry;
import com.financesystem.finance_api.modules.tenant.reporting.application.schema.ReportSchemaBuilder;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportTemplate;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListManagerialReportsUseCase {

    private final ReportTemplateRegistry reportTemplateRegistry;
    private final ReportSchemaBuilder reportSchemaBuilder;

    public ListManagerialReportsUseCase(
            ReportTemplateRegistry reportTemplateRegistry,
            ReportSchemaBuilder reportSchemaBuilder
    ) {
        this.reportTemplateRegistry = reportTemplateRegistry;
        this.reportSchemaBuilder = reportSchemaBuilder;
    }

    public ReportCatalogResponse execute() {
        List<ReportTemplate> templates = reportTemplateRegistry.getTemplates();
        return reportSchemaBuilder.buildCatalog(
                ReportMode.MANAGERIAL,
                templates
        );
    }
}
