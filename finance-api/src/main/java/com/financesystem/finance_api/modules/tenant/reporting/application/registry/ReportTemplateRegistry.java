package com.financesystem.finance_api.modules.tenant.reporting.application.registry;

import com.financesystem.finance_api.modules.tenant.reporting.application.provider.ReportTemplateProvider;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.DuplicateReportTemplateException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportTemplateNotFoundException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportTemplate;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class ReportTemplateRegistry {

    private final Map<ReportType, ReportTemplateProvider> providersByType;

    public ReportTemplateRegistry(List<ReportTemplateProvider> providers) {
        this.providersByType = new EnumMap<>(ReportType.class);

        if (providers != null) {
            for (ReportTemplateProvider provider : providers) {
                if (provider == null) {
                    continue;
                }

                ReportType reportType = provider.getReportType();
                ReportTemplateProvider previous = providersByType.putIfAbsent(reportType, provider);
                if (previous != null) {
                    throw new DuplicateReportTemplateException(reportType.name());
                }
            }
        }
    }

    public ReportTemplate getTemplate(ReportType reportType) {
        Objects.requireNonNull(reportType, "reportType must not be null");

        ReportTemplateProvider provider = providersByType.get(reportType);
        if (provider == null) {
            throw new ReportTemplateNotFoundException(reportType.name());
        }

        return provider.getTemplate();
    }

    public List<ReportTemplate> getTemplates() {
        return providersByType.values().stream()
                .map(ReportTemplateProvider::getTemplate)
                .toList();
    }

    public List<ReportTemplate> getTemplatesByMode(ReportMode mode) {
        Objects.requireNonNull(mode, "mode must not be null");

        return providersByType.values().stream()
                .map(ReportTemplateProvider::getTemplate)
                .filter(template -> template.supportedModes().contains(mode))
                .toList();
    }

    public Collection<ReportTemplateProvider> getProviders() {
        return new ArrayList<>(providersByType.values());
    }
}
