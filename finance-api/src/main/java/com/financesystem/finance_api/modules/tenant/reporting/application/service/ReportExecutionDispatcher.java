package com.financesystem.finance_api.modules.tenant.reporting.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportResultResponse;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportTemplateNotFoundException;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ReportExecutionDispatcher {

    private final Map<ReportType, ReportExecutionHandler> handlersByType;

    public ReportExecutionDispatcher(List<ReportExecutionHandler> handlers) {
        this.handlersByType = new EnumMap<>(ReportType.class);
        if (handlers != null) {
            for (ReportExecutionHandler handler : handlers) {
                if (handler == null) {
                    continue;
                }
                handlersByType.put(handler.getReportType(), handler);
            }
        }
    }

    public ReportResultResponse execute(ReportMode mode, ReportType reportType, JsonNode requestBody) {
        return getHandler(reportType).execute(mode, requestBody);
    }

    public ReportResultResponse rerun(ReportMode mode, ReportType reportType, JsonNode requestBody, List<com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportOutput> outputs, UUID sourceExecutionId) {
        return getHandler(reportType).rerun(mode, requestBody, outputs, sourceExecutionId);
    }

    private ReportExecutionHandler getHandler(ReportType reportType) {
        ReportExecutionHandler handler = handlersByType.get(reportType);
        if (handler == null) {
            throw new ReportTemplateNotFoundException(reportType.name());
        }
        return handler;
    }
}
