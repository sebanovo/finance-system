package com.financesystem.finance_api.modules.reporting.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.reporting.application.scope.ReportActor;
import com.financesystem.finance_api.modules.reporting.application.scope.ReportScopeResolver;
import com.financesystem.finance_api.modules.reporting.application.service.ReportExportService;
import com.financesystem.finance_api.modules.reporting.application.service.ReportExecutionStore;
import com.financesystem.finance_api.modules.reporting.application.service.ReportRunService;
import com.financesystem.finance_api.modules.reporting.application.registry.ReportDefinitionRegistry;
import com.financesystem.finance_api.modules.reporting.domain.ReportScope;
import com.financesystem.finance_api.modules.reporting.infrastructure.api.dto.ReportApiDtos.*;
import jakarta.validation.Valid;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Platform-scoped reporting API (global path → reporting schema, no tenant header). */
@RestController
@RequestMapping("/api/platform/reports")
@PreAuthorize("@authorizationGuards.isPlatformAdmin()")
public class PlatformReportController {

    private final ReportScopeResolver scopeResolver;
    private final ReportDefinitionRegistry registry;
    private final ReportRunService runService;
    private final ReportExecutionStore executionStore;
    private final ReportExportService exportService;
    private final ReportResponseMapper mapper;

    public PlatformReportController(ReportScopeResolver scopeResolver, ReportDefinitionRegistry registry,
                                    ReportRunService runService, ReportExecutionStore executionStore,
                                    ReportExportService exportService, ReportResponseMapper mapper) {
        this.scopeResolver = scopeResolver;
        this.registry = registry;
        this.runService = runService;
        this.executionStore = executionStore;
        this.exportService = exportService;
        this.mapper = mapper;
    }

    @GetMapping("/definitions")
    public ApiResponse<List<DefinitionResponse>> definitions() {
        List<DefinitionResponse> items = registry.listByScope(ReportScope.GLOBAL).stream()
                .map(mapper::toDefinition).toList();
        return ApiResponse.success("Definiciones de reportes", items);
    }

    @PostMapping("/run/{key}")
    public ApiResponse<ResultResponse> run(@PathVariable String key,
                                           @RequestBody(required = false) RunReportRequest request) {
        ReportActor actor = scopeResolver.resolvePlatform();
        Map<String, Object> params = request == null ? Map.of() : request.params();
        return ApiResponse.success("Reporte ejecutado",
                mapper.toResult(runService.runControlled(actor, key, params)));
    }

    @PostMapping("/ai/text")
    public ApiResponse<ResultResponse> aiText(@Valid @RequestBody AiTextRequest request) {
        ReportActor actor = scopeResolver.resolvePlatform();
        return ApiResponse.success("Reporte IA ejecutado",
                mapper.toResult(runService.runAiText(actor, request.prompt())));
    }

    @PostMapping("/ai/voice")
    public ApiResponse<ResultResponse> aiVoice(@RequestParam("audio") MultipartFile audio) throws IOException {
        ReportActor actor = scopeResolver.resolvePlatform();
        return ApiResponse.success("Reporte IA (voz) ejecutado",
                mapper.toResult(runService.runAiVoice(actor, audio.getBytes(), audio.getContentType())));
    }

    @GetMapping("/executions")
    public ApiResponse<Page<ExecutionSummaryResponse>> executions(@PageableDefault(size = 20) Pageable pageable) {
        ReportActor actor = scopeResolver.resolvePlatform();
        return ApiResponse.success("Historial de ejecuciones",
                executionStore.list(actor, pageable).map(mapper::toSummary));
    }

    @GetMapping("/executions/{id}")
    public ApiResponse<ExecutionDetailResponse> execution(@PathVariable UUID id) {
        ReportActor actor = scopeResolver.resolvePlatform();
        return ApiResponse.success("Ejecución", mapper.toDetail(executionStore.getForActor(actor, id)));
    }

    @PostMapping("/executions/{id}/rerun")
    public ApiResponse<ResultResponse> rerun(@PathVariable UUID id) {
        ReportActor actor = scopeResolver.resolvePlatform();
        return ApiResponse.success("Reporte re-ejecutado", mapper.toResult(runService.rerun(actor, id)));
    }

    @PostMapping("/executions/{id}/exports")
    public ApiResponse<ExportResponse> export(@PathVariable UUID id, @Valid @RequestBody CreateExportRequest request) {
        ReportActor actor = scopeResolver.resolvePlatform();
        return ApiResponse.success("Export generado",
                mapper.toExport(exportService.exportSnapshot(actor, id, request.format())));
    }

    @GetMapping("/exports/{exportId}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID exportId) {
        ReportActor actor = scopeResolver.resolvePlatform();
        ReportExportService.DownloadFile file = exportService.download(actor, exportId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.fileName() + "\"")
                .contentType(MediaType.parseMediaType(file.contentType()))
                .body(new ByteArrayResource(file.content()));
    }
}
