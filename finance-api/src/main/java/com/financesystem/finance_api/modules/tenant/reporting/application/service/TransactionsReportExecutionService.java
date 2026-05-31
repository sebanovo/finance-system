package com.financesystem.finance_api.modules.tenant.reporting.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.*;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactions.*;
import com.financesystem.finance_api.modules.tenant.reporting.application.executor.ReportJdbcExecutor;
import com.financesystem.finance_api.modules.tenant.reporting.application.export.ReportExportArtifact;
import com.financesystem.finance_api.modules.tenant.reporting.application.export.ReportExportService;
import com.financesystem.finance_api.modules.tenant.reporting.application.header.ReportHeaderBuilder;
import com.financesystem.finance_api.modules.tenant.reporting.application.provider.TransactionsReportTemplateProvider;
import com.financesystem.finance_api.modules.tenant.reporting.application.query.ReportSqlQuery;
import com.financesystem.finance_api.modules.tenant.reporting.application.query.TransactionReportSqlBuilder;
import com.financesystem.finance_api.modules.tenant.reporting.application.validator.TransactionReportRequestValidator;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportColumnKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportGroupByKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactions.TransactionReportMetricKey;
import com.financesystem.finance_api.modules.tenant.reporting.domain.repository.ReportExecutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class TransactionsReportExecutionService {

    private final TransactionsReportTemplateProvider templateProvider;
    private final TransactionReportRequestValidator validator;
    private final TransactionReportSqlBuilder sqlBuilder;
    private final ReportJdbcExecutor jdbcExecutor;
    private final ReportHeaderBuilder headerBuilder;
    private final ReportExportService exportService;
    private final AuditTrailService auditTrailService;
    private final ReportExecutionRepository reportExecutionRepository;
    private final ObjectMapper objectMapper;
    private final SecurityContextFacade securityContextFacade;

    public TransactionsReportExecutionService(
            TransactionsReportTemplateProvider templateProvider,
            TransactionReportRequestValidator validator,
            TransactionReportSqlBuilder sqlBuilder,
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
    public ReportResultResponse executeAnalytic(RunAnalyticTransactionReportRequest request) {
        return executeAnalytic(request, null);
    }

    @Transactional
    public ReportResultResponse executeAnalytic(RunAnalyticTransactionReportRequest request, UUID sourceExecutionId) {
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

            return persistAndMapResult(
                    template,
                    ReportMode.ANALYTIC,
                    request,
                    generatedAt,
                    sourceExecutionId,
                    header,
                    columns,
                    rows,
                    artifacts
            );
        } catch (RuntimeException ex) {
            persistFailedExecution(request, ReportMode.ANALYTIC, generatedAt, sourceExecutionId, ex);
            throw ex;
        }
    }

    @Transactional
    public ReportResultResponse executeManagerial(RunManagerialTransactionReportRequest request) {
        return executeManagerial(request, null);
    }

    @Transactional
    public ReportResultResponse executeManagerial(RunManagerialTransactionReportRequest request, UUID sourceExecutionId) {
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

            return persistAndMapResult(
                    template,
                    ReportMode.MANAGERIAL,
                    request,
                    generatedAt,
                    sourceExecutionId,
                    header,
                    columns,
                    rows,
                    artifacts
            );
        } catch (RuntimeException ex) {
            persistFailedExecution(request, ReportMode.MANAGERIAL, generatedAt, sourceExecutionId, ex);
            throw ex;
        }
    }

    private List<ReportColumnResponse> resolveAnalyticColumns(List<TransactionReportColumnKey> requestedColumns) {
        List<ReportColumnResponse> columns = new ArrayList<>();
        for (TransactionReportColumnKey key : requestedColumns) {
            ReportField field = templateProvider.getTemplate().fields().get(key.name());
            columns.add(new ReportColumnResponse(key, field.label(), field.type()));
        }
        return columns;
    }

    private List<ReportColumnResponse> resolveManagerialColumns(
            List<TransactionReportGroupByKey> groupBy,
            List<TransactionReportMetricKey> metrics
    ) {
        List<ReportColumnResponse> columns = new ArrayList<>();
        for (TransactionReportGroupByKey key : groupBy) {
            ReportField field = templateProvider.getTemplate().fields().get(key.name());
            columns.add(new ReportColumnResponse(key, field.label(), field.type()));
        }
        for (TransactionReportMetricKey key : metrics) {
            ReportMetric metric = templateProvider.getTemplate().metrics().get(key.name());
            columns.add(new ReportColumnResponse(key, metric.label(), metric.type()));
        }
        return columns;
    }

    private List<String> summarizeFilters(List<TransactionReportFilterRequest> filters) {
        if (filters == null || filters.isEmpty()) {
            return List.of();
        }

        List<String> descriptions = new ArrayList<>();
        var template = templateProvider.getTemplate();
        for (TransactionReportFilterRequest filter : filters) {
            ReportField field = template.fields().get(filter.field().name());
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

    private List<String> resolveGroupByLabels(List<TransactionReportGroupByKey> groupBy) {
        if (groupBy == null || groupBy.isEmpty()) {
            return List.of();
        }

        var template = templateProvider.getTemplate();
        List<String> labels = new ArrayList<>();
        for (TransactionReportGroupByKey key : groupBy) {
            ReportField field = template.fields().get(key.name());
            labels.add(field == null ? key.name() : field.label());
        }
        return labels;
    }

    private List<String> resolveMetricLabels(List<TransactionReportMetricKey> metrics) {
        if (metrics == null || metrics.isEmpty()) {
            return List.of();
        }

        var template = templateProvider.getTemplate();
        List<String> labels = new ArrayList<>();
        for (TransactionReportMetricKey key : metrics) {
            ReportMetric metric = template.metrics().get(key.name());
            labels.add(metric == null ? key.name() : metric.label());
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
        String payload = serializeRequest(request);
        String outputsJson = serializeOutputs(request);

        ReportExecution execution = new ReportExecution(
                null,
                template.reportType().name(),
                template.title(),
                buildExecutionName(template.title(), generatedAt),
                mode.name(),
                securityContextFacade.getCurrentSubject(),
                payload,
                outputsJson,
                rows == null ? 0 : rows.size(),
                sourceExecutionId,
                ReportExecutionStatus.COMPLETED,
                null,
                generatedAt
        );

        ReportExecution saved = reportExecutionRepository.save(execution);

        if (artifacts != null) {
            for (ReportExportArtifact artifact : artifacts) {
                reportExecutionRepository.saveExport(
                        new com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExport(
                                null,
                                saved.id(),
                                artifact.output().name(),
                                artifact.fileName(),
                                artifact.contentType(),
                                artifact.bytes().length,
                                null
                        )
                );
                auditTrailService.recordTenantEvent(
                        AuditEventTypes.REPORT_EXPORTED,
                        "report",
                        saved.id().toString(),
                        Map.of(
                                "reportType", template.reportType().name(),
                                "mode", mode.name(),
                                "output", artifact.output().name(),
                                "fileName", artifact.fileName(),
                                "fileSizeBytes", artifact.bytes().length
                        )
                );
            }
        }
        auditTrailService.recordTenantEvent(
                AuditEventTypes.REPORT_EXECUTED,
                "report",
                saved.id().toString(),
                Map.of(
                        "reportType", template.reportType().name(),
                        "mode", mode.name(),
                        "outputs", extractOutputs(request),
                        "rowCount", rows == null ? 0 : rows.size()
                )
        );
        if (sourceExecutionId != null) {
            auditTrailService.recordTenantEvent(
                    AuditEventTypes.REPORT_RERUN,
                    "report",
                    saved.id().toString(),
                    Map.of(
                            "reportType", template.reportType().name(),
                            "mode", mode.name(),
                            "sourceExecutionId", sourceExecutionId.toString()
                    )
            );
        }

        return new ReportResultResponse(
                template.reportType(),
                mode,
                extractOutputs(request),
                header,
                columns,
                rows,
                exportService.toResponses(artifacts),
                new ReportResultMetadataResponse(
                        rows == null ? 0 : rows.size(),
                        extractLimit(request),
                        extractOffset(request)
                )
        );
    }

    private void persistFailedExecution(
            Object request,
            ReportMode mode,
            Instant generatedAt,
            UUID sourceExecutionId,
            RuntimeException ex
    ) {
        try {
            ReportTemplate template = templateProvider.getTemplate();
            ReportExecution execution = new ReportExecution(
                    null,
                    template.reportType().name(),
                    template.title(),
                    buildExecutionName(template.title(), generatedAt),
                    mode.name(),
                    securityContextFacade.getCurrentSubject(),
                    serializeRequest(request),
                    serializeOutputs(request),
                    0,
                    sourceExecutionId,
                    ReportExecutionStatus.FAILED,
                    ex.getMessage(),
                    generatedAt
            );
            ReportExecution saved = reportExecutionRepository.save(execution);
            auditTrailService.recordTenantEvent(
                    AuditEventTypes.REPORT_EXECUTED,
                    "report",
                    saved.id() == null ? "pending" : saved.id().toString(),
                    Map.of(
                            "reportType", template.reportType().name(),
                            "mode", mode.name(),
                            "status", ReportExecutionStatus.FAILED.name(),
                            "errorMessage", ex.getMessage()
                    )
            );
        } catch (Exception ignored) {
            // Keep original exception flow intact.
        }
    }

    private List<ReportOutput> extractOutputs(Object request) {
        if (request instanceof RunAnalyticTransactionReportRequest analyticRequest) {
            return analyticRequest.outputs();
        }
        if (request instanceof RunManagerialTransactionReportRequest managerialRequest) {
            return managerialRequest.outputs();
        }
        return List.of();
    }

    private Integer extractLimit(Object request) {
        if (request instanceof RunAnalyticTransactionReportRequest analyticRequest && analyticRequest.pagination() != null) {
            return analyticRequest.pagination().limit();
        }
        if (request instanceof RunManagerialTransactionReportRequest managerialRequest && managerialRequest.pagination() != null) {
            return managerialRequest.pagination().limit();
        }
        return null;
    }

    private Integer extractOffset(Object request) {
        if (request instanceof RunAnalyticTransactionReportRequest analyticRequest && analyticRequest.pagination() != null) {
            return analyticRequest.pagination().offset();
        }
        if (request instanceof RunManagerialTransactionReportRequest managerialRequest && managerialRequest.pagination() != null) {
            return managerialRequest.pagination().offset();
        }
        return null;
    }

    private String serializeRequest(Object request) {
        try {
            return objectMapper.writeValueAsString(request);
        } catch (Exception ex) {
            throw new ReportException("Failed to serialize report request", ex);
        }
    }

    private String serializeOutputs(Object request) {
        try {
            List<ReportOutput> outputs;
            if (request instanceof RunAnalyticTransactionReportRequest analyticRequest) {
                outputs = analyticRequest.outputs();
            } else if (request instanceof RunManagerialTransactionReportRequest managerialRequest) {
                outputs = managerialRequest.outputs();
            } else {
                outputs = List.of();
            }
            return objectMapper.writeValueAsString(outputs);
        } catch (Exception ex) {
            throw new ReportException("Failed to serialize report outputs", ex);
        }
    }

    private String buildExecutionName(String title, Instant generatedAt) {
        return title + " - " + generatedAt;
    }

    private void validateExportPermission(List<ReportOutput> outputs) {
        if (outputs == null) {
            return;
        }
        if (outputs.contains(ReportOutput.PDF) || outputs.contains(ReportOutput.XLSX)) {
            if (!securityContextFacade.hasAuthority("reports.export")) {
                throw new org.springframework.security.access.AccessDeniedException("Missing reports.export authority");
            }
        }
    }
}
