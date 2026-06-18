package com.financesystem.finance_api.modules.reporting.infrastructure.api.dto;

import jakarta.validation.constraints.NotNull;
import com.financesystem.finance_api.modules.reporting.domain.ReportFormat;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Request/response DTOs for the reporting API (grouped to keep the surface compact). */
public final class ReportApiDtos {

    private ReportApiDtos() {
    }

    // ----- requests -----

    public record RunReportRequest(Map<String, Object> params) {
    }

    public record AiTextRequest(String prompt) {
    }

    public record CreateExportRequest(@NotNull ReportFormat format) {
    }

    // ----- responses -----

    public record ColumnResponse(String name, String type) {
    }

    public record ParamResponse(String name, String type, String operator, boolean required, List<String> options) {
    }

    public record SortResponse(String field, String direction) {
    }

    public record DefinitionResponse(
            String key,
            String title,
            String description,
            String scope,
            List<ParamResponse> params,
            SortResponse defaultSort
    ) {
    }

    public record ResultResponse(
            UUID executionId,
            String kind,
            List<ColumnResponse> columns,
            List<List<Object>> rows,
            int rowCount,
            boolean truncated,
            String explanation,
            String schemaUsed,
            String limitKind
    ) {
    }

    public record ExecutionSummaryResponse(
            UUID id,
            String kind,
            String definitionKey,
            String status,
            String scope,
            String tenantSlug,
            Integer rowCount,
            boolean truncated,
            Instant createdAt,
            Instant executedAt
    ) {
    }

    public record ExecutionDetailResponse(
            ExecutionSummaryResponse summary,
            List<ColumnResponse> columns,
            List<List<Object>> rows,
            String sql,
            List<String> referencedViews,
            String prompt,
            String transcript
    ) {
    }

    public record ExportResponse(
            UUID id,
            UUID executionId,
            String format,
            String mode,
            String fileName,
            String contentType,
            Long fileSizeBytes,
            Instant createdAt
    ) {
    }
}
