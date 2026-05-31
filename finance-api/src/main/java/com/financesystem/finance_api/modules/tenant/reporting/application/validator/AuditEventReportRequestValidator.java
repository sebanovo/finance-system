package com.financesystem.finance_api.modules.tenant.reporting.application.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.auditevents.*;
import com.financesystem.finance_api.modules.tenant.reporting.application.provider.AuditEventsReportTemplateProvider;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportValidationException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.auditevents.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class AuditEventReportRequestValidator {

    private final ReportTemplate template;

    public AuditEventReportRequestValidator(AuditEventsReportTemplateProvider provider) {
        this.template = provider.getTemplate();
    }

    public void validateAnalytic(RunAnalyticAuditEventsReportRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        requireReportType(request.reportType(), ReportType.AUDIT_EVENTS);
        validateOutputs(request.outputs(), Set.of(ReportOutput.SCREEN, ReportOutput.PDF, ReportOutput.XLSX));
        validatePagination(request.pagination());
        validateColumns(request.columns());
        validateFilters(request.filters());
        validateAnalyticSort(request.sort());
        ensureModeSupported(ReportMode.ANALYTIC);
    }

    public void validateManagerial(RunManagerialAuditEventsReportRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        requireReportType(request.reportType(), ReportType.AUDIT_EVENTS);
        validateOutputs(request.outputs(), Set.of(ReportOutput.SCREEN, ReportOutput.PDF, ReportOutput.XLSX));
        validatePagination(request.pagination());
        validateGroupBy(request.groupBy());
        validateMetrics(request.metrics());
        validateMaxResultColumns(request.groupBy(), request.metrics());
        validateFilters(request.filters());
        validateManagerialSort(request.sort());
        validateVisualizations(request.visualizations());
        ensureModeSupported(ReportMode.MANAGERIAL);
    }

    private void ensureModeSupported(ReportMode mode) {
        if (!template.supportedModes().contains(mode)) {
            throw new ReportValidationException("Report type " + template.reportType().name() + " does not support mode " + mode.name());
        }
    }

    private void requireReportType(ReportType actual, ReportType expected) {
        if (actual != expected) {
            throw new ReportValidationException("Invalid reportType. Expected " + expected.name());
        }
    }

    private void validateOutputs(List<ReportOutput> outputs, Set<ReportOutput> allowedOutputs) {
        if (outputs == null || outputs.isEmpty()) {
            throw new ReportValidationException("outputs must not be empty");
        }
        for (ReportOutput output : outputs) {
            if (output == null || !allowedOutputs.contains(output) || !template.outputs().contains(output)) {
                throw new ReportValidationException("Invalid report output: " + output);
            }
        }
    }

    private void validatePagination(ReportPaginationRequest pagination) {
        if (pagination == null) {
            return;
        }
        if (pagination.limit() != null && pagination.limit() <= 0) {
            throw new ReportValidationException("pagination.limit must be greater than 0");
        }
        if (pagination.offset() != null && pagination.offset() < 0) {
            throw new ReportValidationException("pagination.offset must be greater than or equal to 0");
        }
    }

    private void validateColumns(List<AuditEventReportColumnKey> columns) {
        requireNonEmpty(columns, "columns");
        if (columns.size() > template.limits().maxColumns()) {
            throw new ReportValidationException("columns exceeds maxColumns limit");
        }
        for (AuditEventReportColumnKey column : columns) {
            ReportField field = getField(AuditEventReportFieldKey.valueOf(column.name()));
            if (!field.columnable()) {
                throw new ReportValidationException("Field is not allowed as column: " + column.name());
            }
        }
    }

    private void validateGroupBy(List<AuditEventReportGroupByKey> groupBy) {
        requireNonEmpty(groupBy, "groupBy");
        if (groupBy.size() > template.limits().maxGroupBy()) {
            throw new ReportValidationException("groupBy exceeds maxGroupBy limit");
        }
        for (AuditEventReportGroupByKey key : groupBy) {
            ReportField field = getField(AuditEventReportFieldKey.valueOf(key.name()));
            if (!field.groupable()) {
                throw new ReportValidationException("Field is not allowed in groupBy: " + key.name());
            }
        }
    }

    private void validateMetrics(List<AuditEventReportMetricKey> metrics) {
        requireNonEmpty(metrics, "metrics");
        if (metrics.size() > template.limits().maxMetrics()) {
            throw new ReportValidationException("metrics exceeds maxMetrics limit");
        }
        for (AuditEventReportMetricKey metricKey : metrics) {
            if (!template.metrics().containsKey(metricKey.name())) {
                throw new ReportValidationException("Unknown metric: " + metricKey.name());
            }
        }
    }

    private void validateMaxResultColumns(List<AuditEventReportGroupByKey> groupBy, List<AuditEventReportMetricKey> metrics) {
        int selectedColumns = (groupBy == null ? 0 : groupBy.size()) + (metrics == null ? 0 : metrics.size());
        if (selectedColumns > template.limits().maxColumns()) {
            throw new ReportValidationException("Selected result columns exceed maxColumns limit");
        }
    }

    private void validateFilters(List<AuditEventReportFilterRequest> filters) {
        if (filters == null) {
            return;
        }
        for (AuditEventReportFilterRequest filter : filters) {
            if (filter == null) {
                throw new ReportValidationException("Filter must not be null");
            }
            ReportField field = getField(filter.field());
            if (!field.filterable()) {
                throw new ReportValidationException("Field is not filterable: " + filter.field().name());
            }
            if (!field.operators().contains(filter.operator())) {
                throw new ReportValidationException("Operator " + filter.operator().name() + " is not allowed for field " + filter.field().name());
            }
            validateFilterValue(field, filter.operator(), filter.value(), filter.to());
        }
    }

    private void validateAnalyticSort(List<AuditEventReportSortRequest> sort) {
        if (sort == null) {
            return;
        }
        for (AuditEventReportSortRequest item : sort) {
            if (item == null || item.field() == null || item.direction() == null) {
                throw new ReportValidationException("Analytic sort entries must not be null");
            }
            ReportField field = getField(item.field());
            if (!field.sortable()) {
                throw new ReportValidationException("Field is not sortable: " + item.field().name());
            }
        }
    }

    private void validateManagerialSort(List<AuditEventReportManagerialSortRequest> sort) {
        if (sort == null) {
            return;
        }
        for (AuditEventReportManagerialSortRequest item : sort) {
            if (item == null || item.targetType() == null || item.direction() == null) {
                throw new ReportValidationException("Managerial sort entries must not be null");
            }
            switch (item.targetType()) {
                case FIELD -> {
                    if (item.field() == null || item.metric() != null) {
                        throw new ReportValidationException("FIELD sort requires field and forbids metric");
                    }
                    ReportField field = getField(item.field());
                    if (!field.sortable()) {
                        throw new ReportValidationException("Field is not sortable: " + item.field().name());
                    }
                }
                case METRIC -> {
                    if (item.metric() == null || item.field() != null) {
                        throw new ReportValidationException("METRIC sort requires metric and forbids field");
                    }
                    if (!template.metrics().containsKey(item.metric().name())) {
                        throw new ReportValidationException("Unknown metric: " + item.metric().name());
                    }
                }
            }
        }
    }

    private void validateVisualizations(List<ReportVisualizationType> visualizations) {
        if (visualizations == null || visualizations.isEmpty()) {
            return;
        }
        for (ReportVisualizationType visualization : visualizations) {
            if (visualization == null || !template.visualizations().contains(visualization)) {
                throw new ReportValidationException("Visualization not allowed: " + visualization);
            }
        }
    }

    private void validateFilterValue(ReportField field, ReportOperator operator, JsonNode value, JsonNode to) {
        if (field.type() == ReportFieldType.TEXT) {
            validateTextValue(value, operator);
            return;
        }
        if (field.type() == ReportFieldType.UUID) {
            validateUuidValue(value, operator);
            return;
        }
        if (field.type() == ReportFieldType.DATETIME) {
            validateTemporalValue(value, to, operator);
            return;
        }
        throw new ReportValidationException("Unsupported field type: " + field.type().name());
    }

    private void validateTextValue(JsonNode value, ReportOperator operator) {
        if (operator == ReportOperator.IN) {
            if (value == null || !value.isArray() || value.isEmpty()) {
                throw new ReportValidationException("IN text filter requires a non-empty array");
            }
            return;
        }
        if (value == null || !value.isTextual() || !StringUtils.hasText(value.asText())) {
            throw new ReportValidationException("Text filter requires a non-empty string");
        }
    }

    private void validateUuidValue(JsonNode value, ReportOperator operator) {
        if (operator == ReportOperator.IN) {
            if (value == null || !value.isArray() || value.isEmpty()) {
                throw new ReportValidationException("IN UUID filter requires a non-empty array");
            }
            return;
        }
        if (value == null || !value.isTextual() || !StringUtils.hasText(value.asText())) {
            throw new ReportValidationException("UUID filter requires a non-empty string");
        }
    }

    private void validateTemporalValue(JsonNode value, JsonNode to, ReportOperator operator) {
        if (operator == ReportOperator.IN) {
            if (value == null || !value.isArray() || value.isEmpty()) {
                throw new ReportValidationException("IN temporal filter requires a non-empty array");
            }
            return;
        }
        if (operator == ReportOperator.BETWEEN) {
            if (value == null || to == null) {
                throw new ReportValidationException("BETWEEN temporal filter requires value and to");
            }
            return;
        }
        if (value == null || !value.isTextual() || !StringUtils.hasText(value.asText())) {
            throw new ReportValidationException("Temporal filter requires an ISO-8601 string");
        }
    }

    private void requireNonEmpty(List<?> values, String name) {
        if (values == null || values.isEmpty()) {
            throw new ReportValidationException(name + " must not be empty");
        }
    }

    private ReportField getField(AuditEventReportFieldKey key) {
        ReportField field = template.fields().get(key.name());
        if (field == null) {
            throw new ReportValidationException("Unknown field: " + key.name());
        }
        return field;
    }
}
