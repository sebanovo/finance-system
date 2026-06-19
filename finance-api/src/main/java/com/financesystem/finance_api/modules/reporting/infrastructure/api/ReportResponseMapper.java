package com.financesystem.finance_api.modules.reporting.infrastructure.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.modules.reporting.application.service.ReportRunService.RunOutcome;
import com.financesystem.finance_api.modules.reporting.application.service.ReportSnapshotMapper;
import com.financesystem.finance_api.modules.reporting.domain.ReportColumn;
import com.financesystem.finance_api.modules.reporting.domain.ReportDefinition;
import com.financesystem.finance_api.modules.reporting.domain.ReportResult;
import com.financesystem.finance_api.modules.reporting.infrastructure.api.dto.ReportApiDtos.*;
import com.financesystem.finance_api.modules.reporting.infrastructure.persistence.ReportExecutionEntity;
import com.financesystem.finance_api.modules.reporting.infrastructure.persistence.ReportExportEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** Maps domain/entities to API DTOs. */
@Component
public class ReportResponseMapper {

    private final ReportSnapshotMapper snapshotMapper;
    private final ObjectMapper objectMapper;

    public ReportResponseMapper(ReportSnapshotMapper snapshotMapper, ObjectMapper objectMapper) {
        this.snapshotMapper = snapshotMapper;
        this.objectMapper = objectMapper;
    }

    public DefinitionResponse toDefinition(ReportDefinition definition) {
        List<ParamResponse> params = definition.params().stream()
                .map(p -> new ParamResponse(p.name(), p.type().name(), p.operator().name(), p.required(), p.options()))
                .toList();
        SortResponse sort = definition.defaultSort() == null ? null
                : new SortResponse(definition.defaultSort().field(), definition.defaultSort().direction().name());
        return new DefinitionResponse(definition.key(), definition.title(), definition.description(),
                definition.scope().name(), params, sort);
    }

    public ResultResponse toResult(RunOutcome outcome) {
        ReportResult result = outcome.result();
        ReportExecutionEntity execution = outcome.execution();
        return new ResultResponse(
                execution.getId(),
                execution.getKind().name(),
                toColumns(result.columns()),
                result.rows(),
                result.rowCount(),
                result.truncated(),
                outcome.explanation(),
                execution.getTranscript(),
                result.metadata().schemaUsed(),
                result.metadata().limitKind().name()
        );
    }

    public ExecutionSummaryResponse toSummary(ReportExecutionEntity e) {
        return new ExecutionSummaryResponse(
                e.getId(),
                e.getKind().name(),
                e.getDefinitionKey(),
                e.getStatus().name(),
                e.getActorScope().name(),
                e.getTenantSlug(),
                e.getRowCount(),
                e.isTruncated(),
                e.getCreatedAt(),
                e.getExecutedAt()
        );
    }

    public ExecutionDetailResponse toDetail(ReportExecutionEntity e) {
        ReportSnapshotMapper.SnapshotData snapshot = snapshotMapper.fromSnapshot(e.getResultJson());
        return new ExecutionDetailResponse(
                toSummary(e),
                toColumns(snapshot.columns()),
                snapshot.rows(),
                e.getSql(),
                toStringList(e.getReferencedViews()),
                e.getPrompt(),
                e.getTranscript()
        );
    }

    public ExportResponse toExport(ReportExportEntity e) {
        return new ExportResponse(
                e.getId(),
                e.getExecutionId(),
                e.getFormat().name(),
                e.getMode().name(),
                e.getFileName(),
                e.getContentType(),
                e.getFileSizeBytes(),
                e.getCreatedAt()
        );
    }

    private List<ColumnResponse> toColumns(List<ReportColumn> columns) {
        return columns.stream().map(c -> new ColumnResponse(c.name(), c.type().name())).toList();
    }

    private List<String> toStringList(JsonNode node) {
        List<String> values = new ArrayList<>();
        if (node != null && node.isArray()) {
            node.forEach(n -> values.add(n.asText()));
        }
        return values;
    }
}
