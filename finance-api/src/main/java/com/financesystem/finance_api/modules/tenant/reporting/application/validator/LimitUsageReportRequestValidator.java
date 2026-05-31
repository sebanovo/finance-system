package com.financesystem.finance_api.modules.tenant.reporting.application.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.limitusage.*;
import com.financesystem.finance_api.modules.tenant.reporting.application.provider.LimitUsageReportTemplateProvider;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportValidationException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.limitusage.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class LimitUsageReportRequestValidator {

    private final ReportTemplate template;

    public LimitUsageReportRequestValidator(LimitUsageReportTemplateProvider provider) {
        this.template = provider.getTemplate();
    }

    public void validateAnalytic(RunAnalyticLimitUsageReportRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        requireReportType(request.reportType(), ReportType.LIMIT_USAGE);
        validateOutputs(request.outputs(), Set.of(ReportOutput.SCREEN, ReportOutput.PDF, ReportOutput.XLSX));
        validatePagination(request.pagination());
        validateColumns(request.columns());
        validateFilters(request.filters());
        validateAnalyticSort(request.sort());
        ensureModeSupported(ReportMode.ANALYTIC);
    }

    public void validateManagerial(RunManagerialLimitUsageReportRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        requireReportType(request.reportType(), ReportType.LIMIT_USAGE);
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

    private void validateColumns(List<LimitUsageReportColumnKey> columns) {
        requireNonEmpty(columns, "columns");
        if (columns.size() > template.limits().maxColumns()) {
            throw new ReportValidationException("columns exceeds maxColumns limit");
        }
        for (LimitUsageReportColumnKey column : columns) {
            ReportField field = getField(LimitUsageReportFieldKey.valueOf(column.name()));
            if (!field.columnable()) {
                throw new ReportValidationException("Field is not allowed as column: " + column.name());
            }
        }
    }

    private void validateGroupBy(List<LimitUsageReportGroupByKey> groupBy) {
        requireNonEmpty(groupBy, "groupBy");
        if (groupBy.size() > template.limits().maxGroupBy()) {
            throw new ReportValidationException("groupBy exceeds maxGroupBy limit");
        }
        for (LimitUsageReportGroupByKey key : groupBy) {
            ReportField field = getField(LimitUsageReportFieldKey.valueOf(key.name()));
            if (!field.groupable()) {
                throw new ReportValidationException("Field is not allowed in groupBy: " + key.name());
            }
        }
    }

    private void validateMetrics(List<LimitUsageReportMetricKey> metrics) {
        requireNonEmpty(metrics, "metrics");
        if (metrics.size() > template.limits().maxMetrics()) {
            throw new ReportValidationException("metrics exceeds maxMetrics limit");
        }
        for (LimitUsageReportMetricKey metricKey : metrics) {
            if (!template.metrics().containsKey(metricKey.name())) {
                throw new ReportValidationException("Unknown metric: " + metricKey.name());
            }
        }
    }

    private void validateMaxResultColumns(List<LimitUsageReportGroupByKey> groupBy, List<LimitUsageReportMetricKey> metrics) {
        int selectedColumns = (groupBy == null ? 0 : groupBy.size()) + (metrics == null ? 0 : metrics.size());
        if (selectedColumns > template.limits().maxColumns()) {
            throw new ReportValidationException("Selected result columns exceed maxColumns limit");
        }
    }

    private void validateFilters(List<LimitUsageReportFilterRequest> filters) {
        if (filters == null) {
            return;
        }
        for (LimitUsageReportFilterRequest filter : filters) {
            if (filter == null) {
                throw new ReportValidationException("Filter must not be null");
            }
            ReportField field = getField(filter.field());
            if (!field.filterable()) {
                throw new ReportValidationException("Field is not filterable: " + filter.field().name());
            }
            if (!field.operators().contains(filter.operator())) {
                throw new ReportValidationException(
                        "Operator " + filter.operator().name() + " is not allowed for field " + filter.field().name()
                );
            }
            validateFilterValue(field, filter.operator(), filter.value(), filter.to());
        }
    }

    private void validateAnalyticSort(List<LimitUsageReportSortRequest> sort) {
        if (sort == null) {
            return;
        }
        for (LimitUsageReportSortRequest item : sort) {
            if (item == null || item.field() == null || item.direction() == null) {
                throw new ReportValidationException("Analytic sort entries must not be null");
            }
            ReportField field = getField(item.field());
            if (!field.sortable()) {
                throw new ReportValidationException("Field is not sortable: " + item.field().name());
            }
        }
    }

    private void validateManagerialSort(List<LimitUsageReportManagerialSortRequest> sort) {
        if (sort == null) {
            return;
        }
        for (LimitUsageReportManagerialSortRequest item : sort) {
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
        if (field.type() == ReportFieldType.BOOLEAN) {
            validateBooleanValue(value, operator);
            return;
        }
        if (field.type() == ReportFieldType.UUID) {
            validateUuidValue(value, operator);
            return;
        }
        if (field.type() == ReportFieldType.NUMBER || field.type() == ReportFieldType.MONEY) {
            validateNumericValue(value, to, operator);
            return;
        }
        if (field.type() == ReportFieldType.DATE || field.type() == ReportFieldType.DATETIME) {
            validateTemporalValue(value, to, operator);
            return;
        }
        if (field.type() == ReportFieldType.ENUM) {
            validateEnumValue(field, operator, value, to);
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

    private void validateBooleanValue(JsonNode value, ReportOperator operator) {
        if (operator != ReportOperator.EQ) {
            throw new ReportValidationException("Boolean filters only support EQ");
        }
        if (value == null || (!value.isBoolean() && !value.isTextual())) {
            throw new ReportValidationException("Boolean filter requires a boolean value");
        }
    }

    private void validateUuidValue(JsonNode value, ReportOperator operator) {
        if (operator == ReportOperator.IN) {
            if (value == null || !value.isArray() || value.isEmpty()) {
                throw new ReportValidationException("IN UUID filter requires a non-empty array");
            }
            for (JsonNode node : value) {
                requireUuidText(node);
            }
            return;
        }
        requireUuidText(value);
    }

    private void validateNumericValue(JsonNode value, JsonNode to, ReportOperator operator) {
        if (operator == ReportOperator.BETWEEN) {
            requireNumericText(value);
            requireNumericText(to);
            return;
        }
        if (operator == ReportOperator.IN) {
            if (value == null || !value.isArray() || value.isEmpty()) {
                throw new ReportValidationException("IN numeric filter requires a non-empty array");
            }
            for (JsonNode node : value) {
                requireNumericText(node);
            }
            return;
        }
        requireNumericText(value);
    }

    private void validateTemporalValue(JsonNode value, JsonNode to, ReportOperator operator) {
        if (operator == ReportOperator.BETWEEN) {
            requireTemporalText(value);
            requireTemporalText(to);
            return;
        }
        requireTemporalText(value);
    }

    private void validateEnumValue(ReportField field, ReportOperator operator, JsonNode value, JsonNode to) {
        if (operator == ReportOperator.IN) {
            if (value == null || !value.isArray() || value.isEmpty()) {
                throw new ReportValidationException("IN enum filter requires a non-empty array");
            }
            for (JsonNode node : value) {
                requireEnumValue(field, node);
            }
            return;
        }
        if (operator == ReportOperator.BETWEEN) {
            throw new ReportValidationException("BETWEEN is not supported for enum fields");
        }
        requireEnumValue(field, value);
    }

    private void requireEnumValue(ReportField field, JsonNode node) {
        String text = requireText(node);
        boolean matches = field.options().stream().anyMatch(option -> option.name().equals(text));
        if (!matches) {
            throw new ReportValidationException("Invalid enum value '" + text + "' for field " + field.key().name());
        }
    }

    private String requireText(JsonNode node) {
        if (node == null || !node.isTextual() || !StringUtils.hasText(node.asText())) {
            throw new ReportValidationException("Expected a non-empty text value");
        }
        return node.asText();
    }

    private void requireUuidText(JsonNode node) {
        String text = requireText(node);
        try {
            java.util.UUID.fromString(text);
        } catch (Exception ex) {
            throw new ReportValidationException("Invalid UUID value: " + text);
        }
    }

    private void requireNumericText(JsonNode node) {
        if (node == null || (!node.isNumber() && !node.isTextual())) {
            throw new ReportValidationException("Expected a numeric value");
        }
        new BigDecimal(node.asText());
    }

    private void requireTemporalText(JsonNode node) {
        String text = requireText(node);
        try {
            Instant.parse(text);
            return;
        } catch (Exception ignored) {
        }
        try {
            OffsetDateTime.parse(text);
        } catch (Exception ex) {
            throw new ReportValidationException("Invalid datetime value: " + text);
        }
    }

    private void requireNonEmpty(List<?> values, String fieldName) {
        if (values == null || values.isEmpty()) {
            throw new ReportValidationException(fieldName + " must not be empty");
        }
    }

    private ReportField getField(LimitUsageReportFieldKey key) {
        ReportField field = template.fields().get(key.name());
        if (field == null) {
            throw new ReportValidationException("Unknown field: " + key.name());
        }
        return field;
    }
}
