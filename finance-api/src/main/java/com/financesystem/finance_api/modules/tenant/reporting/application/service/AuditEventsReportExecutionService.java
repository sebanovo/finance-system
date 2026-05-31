package com.financesystem.finance_api.modules.tenant.reporting.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.*;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.auditevents.*;
import com.financesystem.finance_api.modules.tenant.reporting.application.executor.ReportJdbcExecutor;
import com.financesystem.finance_api.modules.tenant.reporting.application.export.ReportExportArtifact;
import com.financesystem.finance_api.modules.tenant.reporting.application.export.ReportExportService;
import com.financesystem.finance_api.modules.tenant.reporting.application.header.ReportHeaderBuilder;
import com.financesystem.finance_api.modules.tenant.reporting.application.provider.AuditEventsReportTemplateProvider;
import com.financesystem.finance_api.modules.tenant.reporting.application.query.AuditEventReportSqlBuilder;
import com.financesystem.finance_api.modules.tenant.reporting.application.query.ReportSqlQuery;
import com.financesystem.finance_api.modules.tenant.reporting.application.validator.AuditEventReportRequestValidator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportValidationException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.auditevents.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.repository.ReportExecutionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuditEventsReportExecutionService {

    private final AuditEventsReportTemplateProvider templateProvider;
    private final AuditEventReportRequestValidator validator;
    private final AuditEventReportSqlBuilder sqlBuilder;
    private final ReportJdbcExecutor jdbcExecutor;
    private final ReportHeaderBuilder headerBuilder;
    private final ReportExportService exportService;
    private final AuditTrailService auditTrailService;
    private final ReportExecutionRepository reportExecutionRepository;
    private final ObjectMapper objectMapper;
    private final SecurityContextFacade securityContextFacade;

    public AuditEventsReportExecutionService(
            AuditEventsReportTemplateProvider templateProvider,
            AuditEventReportRequestValidator validator,
            AuditEventReportSqlBuilder sqlBuilder,
            ReportJdbcExecutor jdbcExecutor,
            ReportHeaderBuilder headerBuilder,
            ReportExportService exportService,
            AuditTrailService auditTrailService,
            ReportExecutionRepository reportExecutionRepository,
            ObjectMapper objectMapper,
            SecurityContextFacade securityContextFacade
    ) {
        this.templateProvider = templateProvider;
        this.validator = validator;
        this.sqlBuilder = sqlBuilder;
        this.jdbcExecutor = jdbcExecutor;
        this.headerBuilder = headerBuilder;
        this.exportService = exportService;
        this.auditTrailService = auditTrailService;
        this.reportExecutionRepository = reportExecutionRepository;
        this.objectMapper = objectMapper;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional
    public ReportResultResponse executeAnalytic(RunAnalyticAuditEventsReportRequest request) {
        return executeAnalytic(request, null);
    }

    @Transactional
    public ReportResultResponse executeAnalytic(RunAnalyticAuditEventsReportRequest request, UUID sourceExecutionId) {
        Instant generatedAt = Instant.now();
        validateExportPermission(request.outputs());
        try {
            validator.validateAnalytic(request);

            ReportTemplate template = templateProvider.getTemplate();
            ReportSqlQuery query = sqlBuilder.buildAnalytic(request);
            List<Map<String, Object>> rows = jdbcExecutor.execute(query);

            List<ReportColumnResponse> columns = resolveAnalyticColumns(request.columns());
            List<String> appliedFilters = summarizeFilters(request.filters());
            ReportHeaderResponse header = headerBuilder.build(
                    template.title(),
                    ReportMode.ANALYTIC,
                    generatedAt,
                    appliedFilters,
                    columns.stream().map(ReportColumnResponse::label).toList(),
                    List.of(),
                    List.of()
            );

            List<ReportExportArtifact> artifacts = exportService.export(
                    template.reportType(),
                    ReportMode.ANALYTIC,
                    generatedAt,
                    request.outputs(),
                    template.exportOptions().pdf() == null ? 0 : template.exportOptions().pdf().maxColumns(),
                    header,
                    columns,
                    rows
            );

            return persistAndMapResult(template, ReportMode.ANALYTIC, request, generatedAt, sourceExecutionId, header, columns, rows, artifacts);
        } catch (RuntimeException ex) {
            persistFailedExecution(request, ReportMode.ANALYTIC, generatedAt, sourceExecutionId, ex);
            throw ex;
        }
    }

    @Transactional
    public ReportResultResponse executeManagerial(RunManagerialAuditEventsReportRequest request) {
        return executeManagerial(request, null);
    }

    @Transactional
    public ReportResultResponse executeManagerial(RunManagerialAuditEventsReportRequest request, UUID sourceExecutionId) {
        Instant generatedAt = Instant.now();
        validateExportPermission(request.outputs());
        try {
            validator.validateManagerial(request);

            ReportTemplate template = templateProvider.getTemplate();
            ReportSqlQuery query = sqlBuilder.buildManagerial(request);
            List<Map<String, Object>> rows = jdbcExecutor.execute(query);

            List<ReportColumnResponse> columns = resolveManagerialColumns(request.groupBy(), request.metrics());
            List<String> selectedGroupBy = resolveGroupByLabels(request.groupBy());
            List<String> selectedMetrics = resolveMetricLabels(request.metrics());
            List<String> appliedFilters = summarizeFilters(request.filters());
            ReportHeaderResponse header = headerBuilder.build(
                    template.title(),
                    ReportMode.MANAGERIAL,
                    generatedAt,
                    appliedFilters,
                    List.of(),
                    selectedMetrics,
                    selectedGroupBy
            );

            List<ReportExportArtifact> artifacts = exportService.export(
                    template.reportType(),
                    ReportMode.MANAGERIAL,
                    generatedAt,
                    request.outputs(),
                    template.exportOptions().pdf() == null ? 0 : template.exportOptions().pdf().maxColumns(),
                    header,
                    columns,
                    rows
            );

            return persistAndMapResult(template, ReportMode.MANAGERIAL, request, generatedAt, sourceExecutionId, header, columns, rows, artifacts);
        } catch (RuntimeException ex) {
            persistFailedExecution(request, ReportMode.MANAGERIAL, generatedAt, sourceExecutionId, ex);
            throw ex;
        }
    }

    private List<ReportColumnResponse> resolveAnalyticColumns(List<AuditEventReportColumnKey> requestedColumns) {
        List<ReportColumnResponse> columns = new ArrayList<>();
        for (AuditEventReportColumnKey key : requestedColumns) {
            ReportField field = requireField(key.name());
            columns.add(new ReportColumnResponse(key, field.label(), field.type()));
        }
        return columns;
    }

    private List<ReportColumnResponse> resolveManagerialColumns(List<AuditEventReportGroupByKey> groupBy, List<AuditEventReportMetricKey> metrics) {
        List<ReportColumnResponse> columns = new ArrayList<>();
        for (AuditEventReportGroupByKey key : groupBy) {
            ReportField field = requireField(key.name());
            columns.add(new ReportColumnResponse(key, field.label(), field.type()));
        }
        for (AuditEventReportMetricKey key : metrics) {
            ReportMetric metric = requireMetric(key.name());
            columns.add(new ReportColumnResponse(key, metric.label(), metric.type()));
        }
        return columns;
    }

    private List<String> summarizeFilters(List<AuditEventReportFilterRequest> filters) {
        if (filters == null || filters.isEmpty()) {
            return List.of();
        }

        List<String> descriptions = new ArrayList<>();
        for (AuditEventReportFilterRequest filter : filters) {
            ReportField field = requireField(filter.field().name());
            descriptions.add(
                    headerBuilder.describeFilter(
                            field.label(),
                            filter.operator(),
                            filter.value(),
                            filter.to()
                    )
            );
        }
        return descriptions;
    }

    private List<String> resolveGroupByLabels(List<AuditEventReportGroupByKey> groupBy) {
        if (groupBy == null || groupBy.isEmpty()) {
            return List.of();
        }

        List<String> labels = new ArrayList<>();
        for (AuditEventReportGroupByKey key : groupBy) {
            ReportField field = requireField(key.name());
            labels.add(field.label());
        }
        return labels;
    }

    private List<String> resolveMetricLabels(List<AuditEventReportMetricKey> metrics) {
        if (metrics == null || metrics.isEmpty()) {
            return List.of();
        }

        List<String> labels = new ArrayList<>();
        for (AuditEventReportMetricKey key : metrics) {
            ReportMetric metric = requireMetric(key.name());
            labels.add(metric.label());
        }
        return labels;
    }

    private ReportResultResponse persistAndMapResult(
            ReportTemplate template,
            ReportMode mode,
            Object request,
            Instant generatedAt,
            UUID sourceExecutionId,
            ReportHeaderResponse header,
            List<ReportColumnResponse> columns,
            List<Map<String, Object>> rows,
            List<ReportExportArtifact> artifacts
    ) {
        String requestPayload = serialize(request);
        String outputsJson = serialize(extractOutputs(request));
        String requestedBySubject = securityContextFacade.getCurrentSubject();
        String executionName = template.title() + " - " + mode.name() + " - " + generatedAt;
        ReportExecution savedExecution = reportExecutionRepository.save(new ReportExecution(null, template.reportType().name(), template.title(), executionName, mode.name(), requestedBySubject, requestPayload, outputsJson, rows.size(), sourceExecutionId, ReportExecutionStatus.COMPLETED, null, null));
        if (artifacts != null) {
            for (ReportExportArtifact artifact : artifacts) {
                reportExecutionRepository.saveExport(new ReportExport(null, savedExecution.id(), artifact.output().name(), artifact.fileName(), artifact.contentType(), artifact.bytes().length, null));
            }
        }
        auditTrailService.recordTenantEvent(AuditEventTypes.REPORT_EXECUTED, "report", template.reportType().name(), Map.of("mode", mode.name(), "outputs", extractOutputs(request), "rowCount", rows.size()));
        if (artifacts != null && !artifacts.isEmpty()) {
            for (ReportExportArtifact artifact : artifacts) {
                auditTrailService.recordTenantEvent(AuditEventTypes.REPORT_EXPORTED, "report", template.reportType().name(), Map.of("mode", mode.name(), "output", artifact.output().name(), "fileName", artifact.fileName(), "fileSizeBytes", artifact.bytes().length));
            }
        }
        return new ReportResultResponse(template.reportType(), mode, extractOutputs(request), header, columns, rows, exportService.toResponses(artifacts), new ReportResultMetadataResponse(rows.size(), extractLimit(request), extractOffset(request)));
    }

    private void persistFailedExecution(Object request, ReportMode mode, Instant generatedAt, UUID sourceExecutionId, RuntimeException ex) {
        try {
            ReportTemplate template = templateProvider.getTemplate();
            reportExecutionRepository.save(new ReportExecution(null, template.reportType().name(), template.title(), template.title() + " - " + mode.name() + " - " + generatedAt, mode.name(), securityContextFacade.getCurrentSubject(), serialize(request), serialize(extractOutputs(request)), 0, sourceExecutionId, ReportExecutionStatus.FAILED, safeErrorMessage(ex), null));
        } catch (RuntimeException persistException) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("Failed to persist failed report execution", persistException);
        }
    }

    private List<ReportOutput> extractOutputs(Object request) {
        if (request instanceof RunAnalyticAuditEventsReportRequest analyticRequest) {
            return analyticRequest.outputs();
        }
        if (request instanceof RunManagerialAuditEventsReportRequest managerialRequest) {
            return managerialRequest.outputs();
        }
        return List.of();
    }

    private Integer extractLimit(Object request) {
        if (request instanceof RunAnalyticAuditEventsReportRequest analyticRequest && analyticRequest.pagination() != null) {
            return analyticRequest.pagination().limit();
        }
        if (request instanceof RunManagerialAuditEventsReportRequest managerialRequest && managerialRequest.pagination() != null) {
            return managerialRequest.pagination().limit();
        }
        return null;
    }

    private Integer extractOffset(Object request) {
        if (request instanceof RunAnalyticAuditEventsReportRequest analyticRequest && analyticRequest.pagination() != null) {
            return analyticRequest.pagination().offset();
        }
        if (request instanceof RunManagerialAuditEventsReportRequest managerialRequest && managerialRequest.pagination() != null) {
            return managerialRequest.pagination().offset();
        }
        return null;
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            throw new ReportException("Failed to serialize report payload", ex);
        }
    }

    private String safeErrorMessage(RuntimeException ex) {
        if (ex == null || ex.getMessage() == null || ex.getMessage().isBlank()) {
            return ex == null ? "Unknown report execution error" : ex.getClass().getSimpleName();
        }
        return ex.getMessage();
    }

    private void validateExportPermission(List<ReportOutput> outputs) {
        if (outputs == null) {
            return;
        }
        boolean requiresExport = outputs.stream().anyMatch(output -> output == ReportOutput.PDF || output == ReportOutput.XLSX);
        if (requiresExport && !securityContextFacade.hasAuthority("reports.export")) {
            throw new AccessDeniedException("Missing required authority: reports.export");
        }
    }

    private ReportField requireField(String key) {
        ReportField field = templateProvider.getTemplate().fields().get(key);
        if (field == null) {
            throw new ReportValidationException("Unknown audit event field: " + key);
        }
        return field;
    }

    private ReportMetric requireMetric(String key) {
        ReportMetric metric = templateProvider.getTemplate().metrics().get(key);
        if (metric == null) {
            throw new ReportValidationException("Unknown audit event metric: " + key);
        }
        return metric;
    }
}
