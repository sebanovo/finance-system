package com.financesystem.finance_api.modules.tenant.reporting.application.query;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.ReportPaginationRequest;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.transactionmovements.*;
import com.financesystem.finance_api.modules.tenant.reporting.application.provider.TransactionMovementsReportTemplateProvider;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportValidationException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.*;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.transactionmovements.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Collectors;

@Component
public class TransactionMovementReportSqlBuilder {

    private final ReportTemplate template;

    public TransactionMovementReportSqlBuilder(TransactionMovementsReportTemplateProvider provider) {
        this.template = provider.getTemplate();
    }

    public ReportSqlQuery buildAnalytic(RunAnalyticTransactionMovementsReportRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        List<TransactionMovementReportColumnKey> columns = request.columns();
        if (columns == null || columns.isEmpty()) {
            throw new ReportValidationException("columns must not be empty");
        }

        Map<String, ReportField> fieldsByKey = template.fields();
        Map<String, ReportRelation> requiredRelations = resolveRequiredRelations(
                columns.stream().map(column -> fieldsByKey.get(column.name())).toList(),
                request.filters() == null ? List.of() : request.filters().stream()
                        .map(filter -> fieldsByKey.get(filter.field().name()))
                        .toList(),
                request.sort() == null ? List.of() : request.sort().stream()
                        .map(sort -> fieldsByKey.get(sort.field().name()))
                        .toList()
        );

        List<Object> parameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(columns.stream()
                .map(column -> selectExpression(fieldsByKey.get(column.name()), column.name()))
                .collect(Collectors.joining(", ")));
        sql.append(" FROM ");
        sql.append(template.root().table()).append(" ").append(template.root().alias());
        appendJoins(sql, requiredRelations.values());
        appendWhere(sql, parameters, request.filters());
        appendOrderBy(sql, fieldsByKey, request.sort());
        appendPagination(sql, request.pagination());

        return new ReportSqlQuery(sql.toString(), parameters);
    }

    public ReportSqlQuery buildManagerial(RunManagerialTransactionMovementsReportRequest request) {
        Objects.requireNonNull(request, "request must not be null");

        List<TransactionMovementReportGroupByKey> groupBy = request.groupBy();
        List<TransactionMovementReportMetricKey> metrics = request.metrics();

        if (groupBy == null || groupBy.isEmpty()) {
            throw new ReportValidationException("groupBy must not be empty");
        }
        if (metrics == null || metrics.isEmpty()) {
            throw new ReportValidationException("metrics must not be empty");
        }

        Map<String, ReportField> fieldsByKey = template.fields();
        Map<String, ReportRelation> requiredRelations = resolveRequiredRelations(
                groupBy.stream().map(key -> fieldsByKey.get(key.name())).toList(),
                request.filters() == null ? List.of() : request.filters().stream()
                        .map(filter -> fieldsByKey.get(filter.field().name()))
                        .toList(),
                request.sort() == null ? List.of() : request.sort().stream()
                        .map(sort -> {
                            if (sort.targetType() == ReportSortTargetType.FIELD && sort.field() != null) {
                                return fieldsByKey.get(sort.field().name());
                            }
                            return null;
                        })
                        .toList()
        );

        List<Object> parameters = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(groupBy.stream()
                .map(key -> selectExpression(fieldsByKey.get(key.name()), key.name()))
                .collect(Collectors.joining(", ")));
        sql.append(", ");
        sql.append(metrics.stream()
                .map(this::metricSelectExpression)
                .collect(Collectors.joining(", ")));
        sql.append(" FROM ");
        sql.append(template.root().table()).append(" ").append(template.root().alias());
        appendJoins(sql, requiredRelations.values());
        appendWhere(sql, parameters, request.filters());
        appendGroupBy(sql, groupBy, fieldsByKey);
        appendOrderBy(sql, fieldsByKey, request.sort());
        appendPagination(sql, request.pagination());

        return new ReportSqlQuery(sql.toString(), parameters);
    }

    private void appendJoins(StringBuilder sql, Iterable<ReportRelation> relations) {
        for (ReportRelation relation : relations) {
            if (relation == null) {
                continue;
            }
            sql.append(" ").append(relation.joinSql());
        }
    }

    private void appendWhere(StringBuilder sql, List<Object> parameters, List<TransactionMovementReportFilterRequest> filters) {
        if (filters == null || filters.isEmpty()) {
            return;
        }

        StringJoiner where = new StringJoiner(" AND ", " WHERE ", "");
        for (TransactionMovementReportFilterRequest filter : filters) {
            ReportField field = getField(filter.field());
            where.add(buildFilterClause(field, filter.operator(), filter.value(), filter.to(), parameters));
        }

        sql.append(where);
    }

    private void appendGroupBy(StringBuilder sql, List<TransactionMovementReportGroupByKey> groupBy, Map<String, ReportField> fieldsByKey) {
        sql.append(" GROUP BY ");
        sql.append(groupBy.stream()
                .map(key -> fieldsByKey.get(key.name()).sqlExpression())
                .collect(Collectors.joining(", ")));
    }

    private void appendOrderBy(StringBuilder sql, Map<String, ReportField> fieldsByKey, List<?> sortItems) {
        if (sortItems == null || sortItems.isEmpty()) {
            return;
        }

        List<String> orderParts = new ArrayList<>();
        for (Object sortItem : sortItems) {
            if (sortItem instanceof TransactionMovementReportSortRequest analyticSort) {
                ReportField field = fieldsByKey.get(analyticSort.field().name());
                if (field == null) {
                    throw new ReportValidationException("Unknown sort field: " + analyticSort.field().name());
                }
                orderParts.add(field.sqlExpression() + " " + analyticSort.direction().name());
            } else if (sortItem instanceof TransactionMovementReportManagerialSortRequest managerialSort) {
                if (managerialSort.targetType() == ReportSortTargetType.FIELD) {
                    ReportField field = fieldsByKey.get(managerialSort.field().name());
                    if (field == null) {
                        throw new ReportValidationException("Unknown sort field: " + managerialSort.field().name());
                    }
                    orderParts.add(field.sqlExpression() + " " + managerialSort.direction().name());
                } else {
                    orderParts.add(managerialSort.metric().name() + " " + managerialSort.direction().name());
                }
            } else {
                throw new ReportValidationException("Unsupported sort item type: " + sortItem.getClass().getName());
            }
        }

        sql.append(" ORDER BY ");
        sql.append(String.join(", ", orderParts));
    }

    private void appendPagination(StringBuilder sql, ReportPaginationRequest pagination) {
        if (pagination == null) {
            return;
        }

        if (pagination.limit() != null) {
            sql.append(" LIMIT ").append(pagination.limit());
        }

        if (pagination.offset() != null) {
            sql.append(" OFFSET ").append(pagination.offset());
        }
    }

    private String buildFilterClause(
            ReportField field,
            ReportOperator operator,
            JsonNode value,
            JsonNode to,
            List<Object> parameters
    ) {
        String expression = field.sqlExpression();
        return switch (operator) {
            case EQ -> {
                parameters.add(convertValue(field, value));
                yield expression + " = ?";
            }
            case IN -> {
                List<Object> values = convertArrayValues(field, value);
                parameters.addAll(values);
                yield expression + " IN (" + placeholders(values.size()) + ")";
            }
            case CONTAINS -> {
                parameters.add("%" + requireText(value) + "%");
                yield "LOWER(" + expression + ") LIKE LOWER(?)";
            }
            case STARTS_WITH -> {
                parameters.add(requireText(value) + "%");
                yield "LOWER(" + expression + ") LIKE LOWER(?)";
            }
            case ENDS_WITH -> {
                parameters.add("%" + requireText(value));
                yield "LOWER(" + expression + ") LIKE LOWER(?)";
            }
            case GT -> {
                parameters.add(convertValue(field, value));
                yield expression + " > ?";
            }
            case GTE -> {
                parameters.add(convertValue(field, value));
                yield expression + " >= ?";
            }
            case LT -> {
                parameters.add(convertValue(field, value));
                yield expression + " < ?";
            }
            case LTE -> {
                parameters.add(convertValue(field, value));
                yield expression + " <= ?";
            }
            case BETWEEN -> {
                parameters.add(convertValue(field, value));
                parameters.add(convertValue(field, to));
                yield expression + " BETWEEN ? AND ?";
            }
        };
    }

    private Object convertValue(ReportField field, JsonNode node) {
        if (node == null || node.isNull()) {
            throw new ReportValidationException("Filter value must not be null for field " + field.key().name());
        }

        return switch (field.type()) {
            case TEXT -> requireText(node);
            case BOOLEAN -> requireBoolean(node);
            case UUID -> java.util.UUID.fromString(requireText(node));
            case NUMBER, MONEY -> new BigDecimal(requireNumericText(node));
            case DATE -> Date.valueOf(requireDateText(node));
            case DATETIME -> convertDatetime(node);
            case ENUM -> requireText(node);
        };
    }

    private List<Object> convertArrayValues(ReportField field, JsonNode value) {
        if (value == null || !value.isArray() || value.isEmpty()) {
            throw new ReportValidationException("IN filter value must be a non-empty array for field " + field.key().name());
        }

        List<Object> values = new ArrayList<>();
        value.forEach(node -> values.add(convertValue(field, node)));
        return values;
    }

    private Object convertDatetime(JsonNode node) {
        String text = requireText(node);
        try {
            return Timestamp.from(Instant.parse(text));
        } catch (Exception ignored) {
        }
        try {
            return Timestamp.from(OffsetDateTime.parse(text).toInstant());
        } catch (Exception ex) {
            throw new ReportValidationException("Invalid datetime value: " + text);
        }
    }

    private String requireText(JsonNode node) {
        if (node == null || !node.isTextual() || !StringUtils.hasText(node.asText())) {
            throw new ReportValidationException("Expected a non-empty text value");
        }
        return node.asText();
    }

    private boolean requireBoolean(JsonNode node) {
        if (node == null) {
            throw new ReportValidationException("Expected a boolean value");
        }
        if (node.isBoolean()) {
            return node.asBoolean();
        }
        if (node.isTextual()) {
            String text = node.asText();
            if ("true".equalsIgnoreCase(text) || "false".equalsIgnoreCase(text)) {
                return Boolean.parseBoolean(text);
            }
        }
        throw new ReportValidationException("Expected a boolean value");
    }

    private String requireNumericText(JsonNode node) {
        if (node == null || (!node.isNumber() && !node.isTextual())) {
            throw new ReportValidationException("Expected a numeric value");
        }
        return node.asText();
    }

    private String requireDateText(JsonNode node) {
        String text = requireText(node);
        try {
            java.time.LocalDate.parse(text);
            return text;
        } catch (Exception ex) {
            throw new ReportValidationException("Invalid date value: " + text);
        }
    }

    private String selectExpression(ReportField field, String alias) {
        if (field == null) {
            throw new ReportValidationException("Unknown select field: " + alias);
        }
        return field.sqlExpression() + " AS " + alias;
    }

    private String metricSelectExpression(TransactionMovementReportMetricKey metricKey) {
        ReportMetric metric = template.metrics().get(metricKey.name());
        if (metric == null) {
            throw new ReportValidationException("Unknown metric: " + metricKey.name());
        }
        return metric.sqlExpression() + " AS " + metricKey.name();
    }

    private ReportField getField(TransactionMovementReportFieldKey key) {
        ReportField field = template.fields().get(key.name());
        if (field == null) {
            throw new ReportValidationException("Unknown field: " + key.name());
        }
        return field;
    }

    private Map<String, ReportRelation> resolveRequiredRelations(List<ReportField>... fieldGroups) {
        Map<String, ReportRelation> relations = new LinkedHashMap<>();
        for (List<ReportField> fields : fieldGroups) {
            if (fields == null) {
                continue;
            }
            for (ReportField field : fields) {
                if (field != null && StringUtils.hasText(field.relationKey())) {
                    ReportRelation relation = template.relations().get(field.relationKey());
                    if (relation == null) {
                        throw new ReportValidationException("Unknown relation: " + field.relationKey());
                    }
                    relations.putIfAbsent(relation.key(), relation);
                }
            }
        }
        return relations;
    }

    private String placeholders(int count) {
        if (count <= 0) {
            throw new ReportValidationException("IN clause requires at least one value");
        }
        return java.util.stream.IntStream.range(0, count)
                .mapToObj(index -> "?")
                .collect(Collectors.joining(", "));
    }
}
