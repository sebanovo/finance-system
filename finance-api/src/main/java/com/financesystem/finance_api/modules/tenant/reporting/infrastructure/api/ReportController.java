package com.financesystem.finance_api.modules.tenant.reporting.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.tenant.reporting.application.dto.*;
import com.financesystem.finance_api.modules.tenant.reporting.application.usecase.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportMode;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
@SecurityRequirement(name = "bearerAuth")
@Validated
@PreAuthorize("isAuthenticated()")
public class ReportController {

    private final ListAnalyticReportsUseCase listAnalyticReportsUseCase;
    private final ListManagerialReportsUseCase listManagerialReportsUseCase;
    private final GetAnalyticReportSchemaUseCase getAnalyticReportSchemaUseCase;
    private final GetManagerialReportSchemaUseCase getManagerialReportSchemaUseCase;
    private final RunReportUseCase runReportUseCase;
    private final ListReportExecutionsUseCase listReportExecutionsUseCase;
    private final GetReportExecutionByIdUseCase getReportExecutionByIdUseCase;
    private final DownloadReportExportUseCase downloadReportExportUseCase;
    private final RerunReportExecutionUseCase rerunReportExecutionUseCase;

    public ReportController(
            ListAnalyticReportsUseCase listAnalyticReportsUseCase,
            ListManagerialReportsUseCase listManagerialReportsUseCase,
            GetAnalyticReportSchemaUseCase getAnalyticReportSchemaUseCase,
            GetManagerialReportSchemaUseCase getManagerialReportSchemaUseCase,
            RunReportUseCase runReportUseCase,
            ListReportExecutionsUseCase listReportExecutionsUseCase,
            GetReportExecutionByIdUseCase getReportExecutionByIdUseCase,
            DownloadReportExportUseCase downloadReportExportUseCase,
            RerunReportExecutionUseCase rerunReportExecutionUseCase
    ) {
        this.listAnalyticReportsUseCase = listAnalyticReportsUseCase;
        this.listManagerialReportsUseCase = listManagerialReportsUseCase;
        this.getAnalyticReportSchemaUseCase = getAnalyticReportSchemaUseCase;
        this.getManagerialReportSchemaUseCase = getManagerialReportSchemaUseCase;
        this.runReportUseCase = runReportUseCase;
        this.listReportExecutionsUseCase = listReportExecutionsUseCase;
        this.getReportExecutionByIdUseCase = getReportExecutionByIdUseCase;
        this.downloadReportExportUseCase = downloadReportExportUseCase;
        this.rerunReportExecutionUseCase = rerunReportExecutionUseCase;
    }

    @GetMapping("/analytic")
    @PreAuthorize("hasAuthority('reports.analytic.read')")
    public ApiResponse<ReportCatalogResponse> listAnalyticReports() {
        return ApiResponse.success("Analytic reports retrieved successfully", listAnalyticReportsUseCase.execute());
    }

    @GetMapping("/managerial")
    @PreAuthorize("hasAuthority('reports.managerial.read')")
    public ApiResponse<ReportCatalogResponse> listManagerialReports() {
        return ApiResponse.success("Managerial reports retrieved successfully", listManagerialReportsUseCase.execute());
    }

    @GetMapping("/analytic/{reportType}/schema")
    @PreAuthorize("hasAuthority('reports.analytic.read')")
    public ApiResponse<ReportSchemaResponse> getAnalyticSchema(@PathVariable ReportType reportType) {
        return ApiResponse.success("Report schema retrieved successfully", getAnalyticReportSchemaUseCase.execute(reportType));
    }

    @GetMapping("/managerial/{reportType}/schema")
    @PreAuthorize("hasAuthority('reports.managerial.read')")
    public ApiResponse<ReportSchemaResponse> getManagerialSchema(@PathVariable ReportType reportType) {
        return ApiResponse.success("Report schema retrieved successfully", getManagerialReportSchemaUseCase.execute(reportType));
    }

    @PostMapping("/analytic/{reportType}/run")
    @PreAuthorize("hasAuthority('reports.analytic.run')")
    public ApiResponse<ReportResultResponse> runAnalytic(
            @PathVariable ReportType reportType,
            @Valid @RequestBody JsonNode request
    ) {
        return ApiResponse.success("Report executed successfully", runReportUseCase.execute(ReportMode.ANALYTIC, reportType, request));
    }

    @PostMapping("/managerial/{reportType}/run")
    @PreAuthorize("hasAuthority('reports.managerial.run')")
    public ApiResponse<ReportResultResponse> runManagerial(
            @PathVariable ReportType reportType,
            @Valid @RequestBody JsonNode request
    ) {
        return ApiResponse.success("Report executed successfully", runReportUseCase.execute(ReportMode.MANAGERIAL, reportType, request));
    }

    @GetMapping("/executions")
    @PreAuthorize("hasAuthority('reports.executions.read')")
    public ApiResponse<Page<ReportExecutionSummaryResponse>> listExecutions(
            @RequestParam(required = false) String reportType,
            @RequestParam(required = false) String mode,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success(
                "Report executions retrieved successfully",
                listReportExecutionsUseCase.execute(reportType, mode, pageable)
        );
    }

    @GetMapping("/executions/{executionId}")
    @PreAuthorize("hasAuthority('reports.executions.read')")
    public ApiResponse<ReportExecutionDetailResponse> getExecutionById(@PathVariable UUID executionId) {
        return ApiResponse.success(
                "Report execution retrieved successfully",
                getReportExecutionByIdUseCase.execute(executionId)
        );
    }

    @GetMapping("/exports/{exportId}/download")
    @PreAuthorize("hasAuthority('reports.executions.read')")
    public ResponseEntity<byte[]> downloadExport(@PathVariable UUID exportId) {
        var export = downloadReportExportUseCase.execute(exportId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(export.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename(export.fileName(), StandardCharsets.UTF_8)
                        .build()
                        .toString())
                .body(export.fileContent());
    }

    @PostMapping("/executions/{executionId}/rerun")
    @PreAuthorize("hasAuthority('reports.executions.rerun')")
    public ApiResponse<ReportResultResponse> rerunExecution(
            @PathVariable UUID executionId,
            @RequestBody(required = false) RerunReportExecutionRequest request
    ) {
        return ApiResponse.success(
                "Report rerun executed successfully",
                rerunReportExecutionUseCase.execute(executionId, request)
        );
    }
}
