package com.financesystem.finance_api.modules.tenant.reporting.application.provider;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportTemplate;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;

import java.util.Set;

public interface ReportTemplateProvider {

    ReportTemplate getTemplate();

    default ReportType getReportType() {
        return getTemplate().reportType();
    }

    default Set<ReportMode> getSupportedModes() {
        return getTemplate().supportedModes();
    }

    default boolean supports(ReportMode mode) {
        return getSupportedModes().contains(mode);
    }
}
