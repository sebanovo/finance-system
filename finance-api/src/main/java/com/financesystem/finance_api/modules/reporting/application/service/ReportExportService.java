package com.financesystem.finance_api.modules.reporting.application.service;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.reporting.application.config.ReportingProperties;
import com.financesystem.finance_api.modules.reporting.application.exception.ReportNotFoundException;
import com.financesystem.finance_api.modules.reporting.domain.ReportActorScope;
import com.financesystem.finance_api.modules.reporting.application.export.ReportExportException;
import com.financesystem.finance_api.modules.reporting.application.export.ReportExporter;
import com.financesystem.finance_api.modules.reporting.application.scope.ReportActor;
import com.financesystem.finance_api.modules.reporting.domain.ReportExportMode;
import com.financesystem.finance_api.modules.reporting.domain.ReportFormat;
import com.financesystem.finance_api.modules.reporting.infrastructure.persistence.ReportExecutionEntity;
import com.financesystem.finance_api.modules.reporting.infrastructure.persistence.ReportExportEntity;
import com.financesystem.finance_api.modules.reporting.infrastructure.persistence.ReportExportJpaRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Generates exports from the audited snapshot and serves them back for download. */
@Service
public class ReportExportService {

    private static final DateTimeFormatter STAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss").withZone(java.time.ZoneOffset.UTC);

    private final ReportExecutionStore executionStore;
    private final ReportSnapshotMapper snapshotMapper;
    private final ReportExportJpaRepository exportRepository;
    private final ReportingProperties properties;
    private final AuditTrailService auditTrailService;
    private final Map<ReportFormat, ReportExporter> exporters = new EnumMap<>(ReportFormat.class);

    public ReportExportService(ReportExecutionStore executionStore, ReportSnapshotMapper snapshotMapper,
                               ReportExportJpaRepository exportRepository, ReportingProperties properties,
                               AuditTrailService auditTrailService, List<ReportExporter> exporterBeans) {
        this.executionStore = executionStore;
        this.snapshotMapper = snapshotMapper;
        this.exportRepository = exportRepository;
        this.properties = properties;
        this.auditTrailService = auditTrailService;
        for (ReportExporter exporter : exporterBeans) {
            exporters.put(exporter.format(), exporter);
        }
    }

    public record DownloadFile(String fileName, String contentType, byte[] content) {
    }

    /** Export the stored snapshot (matches the screen) to disk and record metadata. */
    public ReportExportEntity exportSnapshot(ReportActor actor, UUID executionId, ReportFormat format) {
        ReportExecutionEntity execution = executionStore.getForActor(actor, executionId);
        ReportExporter exporter = exporters.get(format);
        if (exporter == null) {
            throw new ReportNotFoundException("Formato de export no soportado: " + format);
        }

        ReportSnapshotMapper.SnapshotData snapshot = snapshotMapper.fromSnapshot(execution.getResultJson());
        String title = execution.getDefinitionKey() != null ? execution.getDefinitionKey() : "Reporte IA";
        byte[] content = exporter.export(title, snapshot.columns(), snapshot.rows());

        String fileName = (title + "-" + STAMP.format(java.time.Instant.now()) + "." + exporter.fileExtension())
                .replaceAll("[^A-Za-z0-9._-]", "_");
        Path path = writeFile(executionId, exporter.fileExtension(), content);

        ReportExportEntity entity = new ReportExportEntity();
        entity.setExecutionId(executionId);
        entity.setFormat(format);
        entity.setMode(ReportExportMode.SNAPSHOT);
        entity.setFileName(fileName);
        entity.setContentType(exporter.contentType());
        entity.setFileSizeBytes((long) content.length);
        entity.setStoragePath(path.toString());
        entity.setCreatedByUserId(actor.userId());
        ReportExportEntity saved = exportRepository.save(entity);
        auditExport(actor, saved);
        return saved;
    }

    private void auditExport(ReportActor actor, ReportExportEntity export) {
        try {
            Map<String, Object> details = Map.of(
                    "executionId", String.valueOf(export.getExecutionId()),
                    "format", export.getFormat().name(),
                    "mode", export.getMode().name(),
                    "fileName", export.getFileName(),
                    "scope", actor.actorScope().name());
            String resourceId = export.getId() != null ? export.getId().toString() : null;
            if (actor.actorScope() == ReportActorScope.PLATFORM) {
                auditTrailService.recordPlatformEvent("REPORT_EXPORTED", "REPORT_EXPORT", resourceId, details);
            } else {
                auditTrailService.recordTenantEvent("REPORT_EXPORTED", "REPORT_EXPORT", resourceId, details);
            }
        } catch (Exception ignored) {
            // Auditing must never break the export.
        }
    }

    public List<ReportExportEntity> listExports(ReportActor actor, UUID executionId) {
        executionStore.getForActor(actor, executionId); // access check
        return exportRepository.findByExecutionIdOrderByCreatedAtDesc(executionId);
    }

    public DownloadFile download(ReportActor actor, UUID exportId) {
        ReportExportEntity export = exportRepository.findById(exportId)
                .orElseThrow(() -> new ReportNotFoundException("Export no encontrado: " + exportId));
        // Access control flows through the parent execution.
        executionStore.getForActor(actor, export.getExecutionId());
        try {
            byte[] content = Files.readAllBytes(Path.of(export.getStoragePath()));
            return new DownloadFile(export.getFileName(), export.getContentType(), content);
        } catch (IOException e) {
            throw new ReportExportException("No se pudo leer el archivo del export.", e);
        }
    }

    private Path writeFile(UUID executionId, String extension, byte[] content) {
        try {
            Path dir = Path.of(properties.getExport().getStoragePath(), executionId.toString());
            Files.createDirectories(dir);
            Path file = dir.resolve(UUID.randomUUID() + "." + extension);
            Files.write(file, content);
            return file;
        } catch (IOException e) {
            throw new ReportExportException("No se pudo escribir el archivo del export.", e);
        }
    }
}
