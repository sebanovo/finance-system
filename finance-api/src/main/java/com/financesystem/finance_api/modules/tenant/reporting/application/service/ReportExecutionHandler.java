package com.financesystem.finance_api.modules.tenant.reporting.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;

import java.util.List;
import java.util.UUID;

public interface ReportExecutionHandler {

    ReportType getReportType();

    ReportResultResponse execute(ReportMode mode, JsonNode requestBody);

    ReportResultResponse rerun(ReportMode mode, JsonNode requestBody, List<com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput> outputs, UUID sourceExecutionId);
}
