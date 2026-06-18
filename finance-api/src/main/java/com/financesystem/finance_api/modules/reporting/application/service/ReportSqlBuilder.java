package com.financesystem.finance_api.modules.reporting.application.service;

import com.financesystem.finance_api.modules.reporting.domain.ReportDefinition;
import com.financesystem.finance_api.modules.reporting.domain.ReportParam;
import com.financesystem.finance_api.modules.reporting.domain.ReportSort;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Builds a parameterized SELECT for a controlled report from its
 * {@code sourceView} + typed params. The frontend only sends parameter
 * <b>names</b>; columns and operators come from the definition. All values are
 * bound as {@code ?} (never concatenated). The resulting SQL still passes
 * through {@code AiSqlGuard} before execution.
 */
@Component
public class ReportSqlBuilder {

    private static final Pattern IDENTIFIER = Pattern.compile("^[a-z_][a-z0-9_]*$");

    public record BuiltSql(String sql, List<Object> bindParams) {
    }

    public BuiltSql build(ReportDefinition definition, Map<String, Object> rawParams, ReportSort requestedSort) {
        if (!definition.usesSourceView()) {
            throw new IllegalArgumentException("La definición '" + definition.key() + "' no tiene sourceView.");
        }
        String view = requireIdentifier(definition.sourceView());

        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(view);
        List<String> conditions = new ArrayList<>();
        List<Object> binds = new ArrayList<>();

        Map<String, Object> params = rawParams == null ? Map.of() : rawParams;
        for (ReportParam param : definition.params()) {
            Object raw = params.get(param.name());
            if (isBlank(raw)) {
                if (param.required()) {
                    throw new IllegalArgumentException("Falta el parámetro requerido: " + param.name());
                }
                continue;
            }
            conditions.add("\"" + requireIdentifier(param.column()) + "\" " + param.operator().sql() + " ?");
            binds.add(coerce(param, raw));
        }

        if (!conditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        ReportSort sort = requestedSort != null ? requestedSort : definition.defaultSort();
        if (sort != null) {
            String field = requireAllowedSort(definition, sort.field());
            sql.append(" ORDER BY \"").append(field).append("\" ").append(sort.direction().name());
        }

        return new BuiltSql(sql.toString(), binds);
    }

    private Object coerce(ReportParam param, Object raw) {
        String value = String.valueOf(raw).trim();
        try {
            return switch (param.type()) {
                case TEXT -> value;
                case NUMBER -> new BigDecimal(value);
                case BOOLEAN -> Boolean.parseBoolean(value);
                case DATE -> java.sql.Date.valueOf(LocalDate.parse(value));
                case TIMESTAMP -> Timestamp.from(parseInstant(value));
            };
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Valor inválido para el parámetro '" + param.name() + "'.");
        }
    }

    private Instant parseInstant(String value) {
        try {
            return OffsetDateTime.parse(value).toInstant();
        } catch (RuntimeException ignored) {
            // fall through
        }
        try {
            return Instant.parse(value);
        } catch (RuntimeException ignored) {
            // fall through
        }
        return LocalDate.parse(value).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    /** A requested sort field is only allowed if it maps to a param column or the default sort. */
    private String requireAllowedSort(ReportDefinition definition, String field) {
        Set<String> allowed = new HashSet<>();
        for (ReportParam param : definition.params()) {
            allowed.add(param.column());
        }
        if (definition.defaultSort() != null) {
            allowed.add(definition.defaultSort().field());
        }
        String identifier = requireIdentifier(field);
        if (!allowed.contains(identifier)) {
            throw new IllegalArgumentException("Campo de ordenamiento no permitido: " + field);
        }
        return identifier;
    }

    private boolean isBlank(Object raw) {
        return raw == null || (raw instanceof String s && s.isBlank());
    }

    private String requireIdentifier(String identifier) {
        if (identifier == null || !IDENTIFIER.matcher(identifier).matches()) {
            throw new IllegalArgumentException("Identificador inválido: " + identifier);
        }
        return identifier;
    }
}
