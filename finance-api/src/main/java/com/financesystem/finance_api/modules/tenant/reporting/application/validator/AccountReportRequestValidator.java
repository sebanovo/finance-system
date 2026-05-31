package com.financesystem.finance_api.modules.tenant.reporting.application.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.accounts.*;
import com.financesystem.finance_api.modules.tenant.reporting.application.provider.AccountsReportTemplateProvider;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportValidationException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.accounts.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
public class AccountReportRequestValidator {

    private final ReportTemplate template;

    public AccountReportRequestValidator(AccountsReportTemplateProvider provider) {
        this.template = provider.getTemplate();
    }

    public void validateAnalytic(RunAnalyticAccountReportRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        requireReportType(request.reportType(), ReportType.ACCOUNTS);
        validateOutputs(request.outputs(), Set.of(ReportOutput.SCREEN, ReportOutput.PDF, ReportOutput.XLSX));
        validatePagination(request.pagination());
        validateColumns(request.columns());
        validateFilters(request.filters());
        validateAnalyticSort(request.sort());
        ensureModeSupported(ReportMode.ANALYTIC);
    }

    public void validateManagerial(RunManagerialAccountReportRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        requireReportType(request.reportType(), ReportType.ACCOUNTS);
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
            throw new ReportValidationException("Report type ACCOUNTS does not support mode " + mode.name());
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

    private void validateColumns(List<AccountReportColumnKey> columns) {
        requireNonEmpty(columns, "columns");

        if (columns.size() > template.limits().maxColumns()) {
            throw new ReportValidationException("columns exceeds maxColumns limit");
        }

        for (AccountReportColumnKey column : columns) {
            AccountReportFieldKey fieldKey = AccountReportFieldKey.valueOf(column.name());
            ReportField field = getField(fieldKey);
            if (!field.columnable()) {
                throw new ReportValidationException("Field is not allowed as column: " + column.name());
            }
        }
    }

    private void validateGroupBy(List<AccountReportGroupByKey> groupBy) {
        requireNonEmpty(groupBy, "groupBy");

        if (groupBy.size() > template.limits().maxGroupBy()) {
            throw new ReportValidationException("groupBy exceeds maxGroupBy limit");
        }

        for (AccountReportGroupByKey key : groupBy) {
            ReportField field = getField(AccountReportFieldKey.valueOf(key.name()));
            if (!field.groupable()) {
                throw new ReportValidationException("Field is not allowed in groupBy: " + key.name());
            }
        }
    }

    private void validateMetrics(List<AccountReportMetricKey> metrics) {
        requireNonEmpty(metrics, "metrics");

        if (metrics.size() > template.limits().maxMetrics()) {
            throw new ReportValidationException("metrics exceeds maxMetrics limit");
        }

        for (AccountReportMetricKey metricKey : metrics) {
            if (!template.metrics().containsKey(metricKey.name())) {
                throw new ReportValidationException("Unknown metric: " + metricKey.name());
            }
        }
    }

    private void validateMaxResultColumns(List<AccountReportGroupByKey> groupBy, List<AccountReportMetricKey> metrics) {
        int selectedColumns = (groupBy == null ? 0 : groupBy.size()) + (metrics == null ? 0 : metrics.size());
        if (selectedColumns > template.limits().maxColumns()) {
            throw new ReportValidationException("Selected result columns exceed maxColumns limit");
        }
    }

    private void validateFilters(List<AccountReportFilterRequest> filters) {
        if (filters == null) {
            return;
        }

        for (AccountReportFilterRequest filter : filters) {
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

    private void validateAnalyticSort(List<AccountReportSortRequest> sort) {
        if (sort == null) {
            return;
        }

        for (AccountReportSortRequest item : sort) {
            if (item == null || item.field() == null || item.direction() == null) {
                throw new ReportValidationException("Analytic sort entries must not be null");
            }

            ReportField field = getField(item.field());
            if (!field.sortable()) {
                throw new ReportValidationException("Field is not sortable: " + item.field().name());
            }
        }
    }

    private void validateManagerialSort(List<AccountReportManagerialSortRequest> sort) {
        if (sort == null) {
            return;
        }

        for (AccountReportManagerialSortRequest item : sort) {
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
        if (operator == ReportOperator.BETWEEN) {
            throw new ReportValidationException("BETWEEN is not allowed for TEXT fields");
        }

        if (value == null || !value.isTextual() || !StringUtils.hasText(value.asText())) {
            throw new ReportValidationException("Text filter value must be a non-empty string");
        }
    }

    private void validateBooleanValue(JsonNode value, ReportOperator operator) {
        if (operator != ReportOperator.EQ) {
            throw new ReportValidationException("Only EQ is allowed for BOOLEAN fields");
        }

        if (value == null || (!value.isBoolean() && !isBooleanString(value))) {
            throw new ReportValidationException("Boolean filter value must be boolean");
        }
    }

    private void validateUuidValue(JsonNode value, ReportOperator operator) {
        if (operator != ReportOperator.EQ && operator != ReportOperator.IN) {
            throw new ReportValidationException("Only EQ and IN are allowed for UUID fields");
        }

        if (operator == ReportOperator.IN) {
            if (value == null || !value.isArray() || value.isEmpty()) {
                throw new ReportValidationException("UUID IN filter value must be a non-empty array");
            }
            value.forEach(this::assertUuidText);
            return;
        }

        assertUuidText(value);
    }

    private void validateNumericValue(JsonNode value, JsonNode to, ReportOperator operator) {
        if (operator == ReportOperator.BETWEEN) {
            assertNumeric(value);
            assertNumeric(to);
            return;
        }

        if (operator == ReportOperator.IN) {
            if (value == null || !value.isArray() || value.isEmpty()) {
                throw new ReportValidationException("Numeric IN filter value must be a non-empty array");
            }
            value.forEach(this::assertNumeric);
            return;
        }

        assertNumeric(value);
    }

    private void validateTemporalValue(JsonNode value, JsonNode to, ReportOperator operator) {
        if (operator == ReportOperator.BETWEEN) {
            assertTemporal(value);
            assertTemporal(to);
            return;
        }

        if (operator == ReportOperator.IN) {
            if (value == null || !value.isArray() || value.isEmpty()) {
                throw new ReportValidationException("Temporal IN filter value must be a non-empty array");
            }
            value.forEach(this::assertTemporal);
            return;
        }

        assertTemporal(value);
    }

    private void validateEnumValue(ReportField field, ReportOperator operator, JsonNode value, JsonNode to) {
        if (operator == ReportOperator.BETWEEN) {
            throw new ReportValidationException("BETWEEN is not allowed for ENUM fields");
        }

        if (operator == ReportOperator.IN) {
            if (value == null || !value.isArray() || value.isEmpty()) {
                throw new ReportValidationException("ENUM IN filter value must be a non-empty array");
            }
            value.forEach(node -> assertEnumValue(field, node));
            return;
        }

        assertEnumValue(field, value);
    }

    private void assertUuidText(JsonNode node) {
        if (node == null || !node.isTextual() || !StringUtils.hasText(node.asText())) {
            throw new ReportValidationException("UUID filter value must be a non-empty string");
        }
        try {
            java.util.UUID.fromString(node.asText());
        } catch (IllegalArgumentException ex) {
            throw new ReportValidationException("Invalid UUID value: " + node.asText());
        }
    }

    private void assertNumeric(JsonNode node) {
        if (node == null || !node.isNumber()) {
            throw new ReportValidationException("Numeric filter value must be a number");
        }
        new BigDecimal(node.asText());
    }

    private void assertTemporal(JsonNode node) {
        if (node == null || !node.isTextual() || !StringUtils.hasText(node.asText())) {
            throw new ReportValidationException("Temporal filter value must be a non-empty string");
        }

        String value = node.asText();
        try {
            Instant.parse(value);
            return;
        } catch (Exception ignored) {
        }

        try {
            OffsetDateTime.parse(value);
        } catch (Exception ex) {
            throw new ReportValidationException("Invalid temporal value: " + value);
        }
    }

    private void assertEnumValue(ReportField field, JsonNode node) {
        if (node == null || !node.isTextual() || !StringUtils.hasText(node.asText())) {
            throw new ReportValidationException("ENUM filter value must be a non-empty string");
        }

        String value = node.asText();
        boolean matches = field.options().stream()
                .map(Enum::name)
                .anyMatch(value::equals);
        if (!matches) {
            throw new ReportValidationException("Invalid enum value for field " + field.key().name() + ": " + value);
        }
    }

    private boolean isBooleanString(JsonNode value) {
        String text = value.asText();
        return "true".equalsIgnoreCase(text) || "false".equalsIgnoreCase(text);
    }

    private ReportField getField(AccountReportFieldKey key) {
        ReportField field = template.fields().get(key.name());
        if (field == null) {
            throw new ReportValidationException("Unknown field: " + key.name());
        }
        return field;
    }

    private <T> void requireNonEmpty(List<T> values, String name) {
        if (values == null || values.isEmpty()) {
            throw new ReportValidationException(name + " must not be empty");
        }
    }
}
