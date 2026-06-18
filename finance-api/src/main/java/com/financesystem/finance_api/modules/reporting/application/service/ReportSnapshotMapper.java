package com.financesystem.finance_api.modules.reporting.application.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.financesystem.finance_api.modules.reporting.application.config.ReportingProperties;
import com.financesystem.finance_api.modules.reporting.domain.ReportColumn;
import com.financesystem.finance_api.modules.reporting.domain.ReportResult;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Serializes a {@link ReportResult} into the capped {@code result_json} snapshot and back. */
@Component
public class ReportSnapshotMapper {

    private final ObjectMapper objectMapper;
    private final ReportingProperties properties;

    public ReportSnapshotMapper(ObjectMapper objectMapper, ReportingProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    /** Build a snapshot capped to {@code snapshot.maxRows} / {@code snapshot.maxBytes}. */
    public JsonNode toSnapshot(ReportResult result) {
        int maxRows = properties.getSnapshot().getMaxRows();
        long maxBytes = properties.getSnapshot().getMaxBytes();

        List<List<Object>> rows = result.rows();
        boolean rowCapped = rows.size() > maxRows;
        List<List<Object>> capped = rowCapped ? rows.subList(0, maxRows) : rows;

        ObjectNode snapshot = buildSnapshot(result.columns(), capped, result.rowCount(),
                result.truncated() || rowCapped);

        // Byte cap: drop rows until under the limit.
        while (capped.size() > 0 && estimateBytes(snapshot) > maxBytes) {
            capped = capped.subList(0, capped.size() / 2);
            snapshot = buildSnapshot(result.columns(), capped, result.rowCount(), true);
        }
        return snapshot;
    }

    public SnapshotData fromSnapshot(JsonNode snapshot) {
        List<ReportColumn> columns = new ArrayList<>();
        List<List<Object>> rows = new ArrayList<>();
        if (snapshot == null) {
            return new SnapshotData(columns, rows);
        }
        JsonNode columnsNode = snapshot.get("columns");
        if (columnsNode != null && columnsNode.isArray()) {
            for (JsonNode col : columnsNode) {
                columns.add(new ReportColumn(
                        col.path("name").asText(),
                        com.financesystem.finance_api.modules.reporting.domain.ReportDataType.valueOf(
                                col.path("type").asText("TEXT"))));
            }
        }
        JsonNode rowsNode = snapshot.get("rows");
        if (rowsNode != null && rowsNode.isArray()) {
            for (JsonNode rowNode : rowsNode) {
                List<Object> row = new ArrayList<>();
                for (JsonNode cell : rowNode) {
                    row.add(cell.isNull() ? null : cell.asText());
                }
                rows.add(row);
            }
        }
        return new SnapshotData(columns, rows);
    }

    private ObjectNode buildSnapshot(List<ReportColumn> columns, List<List<Object>> rows,
                                     int totalRowCount, boolean truncated) {
        ObjectNode snapshot = objectMapper.createObjectNode();
        ArrayNode columnsNode = snapshot.putArray("columns");
        for (ReportColumn column : columns) {
            ObjectNode columnNode = columnsNode.addObject();
            columnNode.put("name", column.name());
            columnNode.put("type", column.type().name());
        }
        ArrayNode rowsNode = snapshot.putArray("rows");
        for (List<Object> row : rows) {
            ArrayNode rowNode = rowsNode.addArray();
            for (Object value : row) {
                rowNode.add(value == null ? null : objectMapper.valueToTree(value));
            }
        }
        snapshot.put("rowCount", totalRowCount);
        snapshot.put("truncated", truncated);
        return snapshot;
    }

    private long estimateBytes(JsonNode node) {
        try {
            return objectMapper.writeValueAsBytes(node).length;
        } catch (Exception e) {
            return 0L;
        }
    }

    public record SnapshotData(List<ReportColumn> columns, List<List<Object>> rows) {
    }
}
