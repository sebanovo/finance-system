package com.financesystem.finance_api.modules.reporting.application.executor;

import com.financesystem.finance_api.modules.reporting.application.guard.GuardedSql;
import com.financesystem.finance_api.modules.reporting.domain.LimitKind;
import com.financesystem.finance_api.modules.reporting.domain.ReportColumn;
import com.financesystem.finance_api.modules.reporting.domain.ReportDataType;
import com.financesystem.finance_api.modules.reporting.domain.ReportResult;
import com.financesystem.finance_api.modules.reporting.domain.ReportScope;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Executes already-guarded SQL under the read-only role of its scope.
 *
 * <p>Defense in depth (every execution):
 * <ul>
 *   <li>read-only role datasource for the scope;</li>
 *   <li>{@code SET TRANSACTION READ ONLY} (before any query);</li>
 *   <li>{@code SET LOCAL search_path} bound to the scope schema (never session level);</li>
 *   <li>{@code SET LOCAL statement_timeout};</li>
 *   <li>the LIMIT already injected/validated by the guard.</li>
 * </ul>
 */
@Component
public class ReadOnlySqlExecutor {

    private final DataSource tenantReadOnlyDataSource;
    private final DataSource platformReadOnlyDataSource;

    public ReadOnlySqlExecutor(
            @Qualifier("tenantReadOnlyDataSource") DataSource tenantReadOnlyDataSource,
            @Qualifier("platformReadOnlyDataSource") DataSource platformReadOnlyDataSource
    ) {
        this.tenantReadOnlyDataSource = tenantReadOnlyDataSource;
        this.platformReadOnlyDataSource = platformReadOnlyDataSource;
    }

    public ReportResult execute(ReportScope scope, GuardedSql guarded, List<Object> bindParams,
                                int systemLimit, int timeoutMs) {
        String schema = requireSafeSchema(guarded.schemaUsed());
        DataSource dataSource = scope == ReportScope.GLOBAL ? platformReadOnlyDataSource : tenantReadOnlyDataSource;

        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            connection.setReadOnly(true);
            try (Statement setup = connection.createStatement()) {
                setup.execute("SET TRANSACTION READ ONLY");
                setup.execute("SET LOCAL search_path TO \"" + schema + "\", public");
                setup.execute("SET LOCAL statement_timeout = " + timeoutMs);
            }

            try (PreparedStatement query = connection.prepareStatement(guarded.safeSql())) {
                bindAll(query, bindParams);
                try (ResultSet rs = query.executeQuery()) {
                    List<ReportColumn> columns = readColumns(rs.getMetaData());
                    List<List<Object>> rows = readRows(rs, columns.size());

                    boolean truncated = guarded.limitKind() == LimitKind.SYSTEM && rows.size() >= systemLimit;

                    ReportResult.ReportResultMetadata metadata = new ReportResult.ReportResultMetadata(
                            guarded.safeSql(),
                            scope,
                            schema,
                            guarded.referencedViews(),
                            guarded.limitKind(),
                            null
                    );
                    return new ReportResult(columns, rows, rows.size(), truncated, metadata);
                }
            } finally {
                connection.rollback(); // read-only transaction; nothing to commit
            }
        } catch (SQLException e) {
            throw new ReportExecutionException("Error ejecutando el reporte: " + e.getMessage(), e);
        }
    }

    private void bindAll(PreparedStatement statement, List<Object> bindParams) throws SQLException {
        if (bindParams == null) {
            return;
        }
        for (int i = 0; i < bindParams.size(); i++) {
            statement.setObject(i + 1, bindParams.get(i));
        }
    }

    private List<ReportColumn> readColumns(ResultSetMetaData meta) throws SQLException {
        int count = meta.getColumnCount();
        List<ReportColumn> columns = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            columns.add(new ReportColumn(meta.getColumnLabel(i), mapType(meta.getColumnType(i))));
        }
        return columns;
    }

    private List<List<Object>> readRows(ResultSet rs, int columnCount) throws SQLException {
        List<List<Object>> rows = new ArrayList<>();
        while (rs.next()) {
            List<Object> row = new ArrayList<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                row.add(normalizeValue(rs.getObject(i)));
            }
            rows.add(row);
        }
        return rows;
    }

    /** Make values JSON/exporter-friendly (avoid driver-specific temporal types). */
    private Object normalizeValue(Object value) {
        if (value instanceof java.sql.Timestamp ts) {
            return ts.toInstant();
        }
        if (value instanceof java.sql.Date d) {
            return d.toLocalDate();
        }
        if (value instanceof java.sql.Time t) {
            return t.toLocalTime();
        }
        return value;
    }

    private ReportDataType mapType(int jdbcType) {
        return switch (jdbcType) {
            case Types.DATE -> ReportDataType.DATE;
            case Types.TIME, Types.TIME_WITH_TIMEZONE, Types.TIMESTAMP, Types.TIMESTAMP_WITH_TIMEZONE ->
                    ReportDataType.TIMESTAMP;
            case Types.BOOLEAN, Types.BIT -> ReportDataType.BOOLEAN;
            case Types.TINYINT, Types.SMALLINT, Types.INTEGER, Types.BIGINT,
                 Types.FLOAT, Types.REAL, Types.DOUBLE, Types.NUMERIC, Types.DECIMAL ->
                    ReportDataType.NUMBER;
            default -> ReportDataType.TEXT;
        };
    }

    private String requireSafeSchema(String schema) {
        if (schema == null || !schema.matches("^[a-zA-Z0-9_]+$")) {
            throw new ReportExecutionException("Schema inválido para ejecución: " + schema, null);
        }
        return schema;
    }
}
