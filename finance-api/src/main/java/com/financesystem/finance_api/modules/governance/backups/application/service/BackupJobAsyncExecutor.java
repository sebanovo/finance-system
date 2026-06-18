package com.financesystem.finance_api.modules.governance.backups.application.service;

import com.financesystem.finance_api.common.tenancy.maintenance.TenantMaintenanceService;
import com.financesystem.finance_api.common.tenancy.reporting.ReportingSecurityService;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.backups.domain.exception.BackupNotFoundException;
import com.financesystem.finance_api.modules.governance.backups.domain.model.*;
import com.financesystem.finance_api.modules.governance.backups.domain.repository.BackupJobRepository;
import com.financesystem.finance_api.modules.governance.backups.infrastructure.engine.BackupEngine;
import com.financesystem.finance_api.modules.governance.backups.infrastructure.storage.BackupStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class BackupJobAsyncExecutor {

    private static final Logger log = LoggerFactory.getLogger(BackupJobAsyncExecutor.class);
    private final BackupJobRepository repo;
    private final BackupEngine engine;
    private final BackupStorage storage;
    private final AuditTrailService audit;
    private final TenantMaintenanceService maintenance;
    private final ReportingSecurityService reportingSecurityService;

    public BackupJobAsyncExecutor(BackupJobRepository repo, BackupEngine engine, BackupStorage storage, AuditTrailService audit, TenantMaintenanceService maintenance, ReportingSecurityService reportingSecurityService) {
        this.repo = repo;
        this.engine = engine;
        this.storage = storage;
        this.audit = audit;
        this.maintenance = maintenance;
        this.reportingSecurityService = reportingSecurityService;
    }

    @Async("backupTaskExecutor")
    public void executeBackup(UUID id) {
        try {
            BackupJob job = get(id);
            BackupJob running = status(job, BackupStatus.RUNNING, null, Instant.now(), null);
            Path target = storage.resolveTargetPath(running.fileName());
            BackupArtifact a = running.scope() == BackupScope.FULL_DATABASE ? engine.dumpFullDatabase(target) : engine.dumpSchema(running.schemaName(), target);
            BackupJob done = new BackupJob(running.id(), running.operationType(), running.scope(), BackupStatus.COMPLETED, running.tenantId(), running.tenantSlug(), running.schemaName(), running.sourceBackupId(), running.preRestoreBackupId(), running.fileName(), storage.toStoragePath(a.path()), running.format(), a.sizeBytes(), a.checksumSha256(), running.requestedBy(), running.reason(), null, running.startedAt(), Instant.now(), running.createdAt(), running.updatedAt());
            repo.save(done);
            record(done, BackupAuditEventTypes.BACKUP_COMPLETED, null);
        } catch (Exception e) {
            log.error("Backup job failed: {}", id, e);
            repo.findById(id).ifPresent(job -> {
                BackupJob failed = status(job, BackupStatus.FAILED, e.getMessage(), null, Instant.now());
                record(failed, BackupAuditEventTypes.BACKUP_FAILED, e.getMessage());
            });
        }
    }

    @Async("backupTaskExecutor")
    public void executeRestore(UUID id) {
        BackupJob restore = null;
        try {
            restore = get(id);
            BackupJob source = repo.findById(restore.sourceBackupId()).orElseThrow(() -> new BackupNotFoundException("Source backup not found"));
            BackupJob running = status(restore, BackupStatus.RESTORING, null, Instant.now(), null);
            record(running, BackupAuditEventTypes.RESTORE_STARTED, null);
            Path src = storage.resolvePath(source.storagePath());
            if (running.scope() == BackupScope.TENANT_SCHEMA) {
                maintenance.enableMaintenance(running.tenantSlug(), "Restoring backup " + source.id());
                engine.restoreSchema(running.schemaName(), src);
                // Re-apply reporting grants (the restored schema may have lost them)
                // and refresh the cross-tenant views with the restored data.
                reportingSecurityService.applyTenantSecurity(running.schemaName());
                maintenance.disableMaintenance(running.tenantSlug());
            } else {
                engine.restoreFullDatabase(src);
                // Full restore: re-grant every tenant and regenerate platform views.
                reportingSecurityService.backfillRegisteredTenants();
            }
            BackupJob done = status(get(id), BackupStatus.RESTORED, null, null, Instant.now());
            record(done, BackupAuditEventTypes.RESTORE_COMPLETED, null);
        } catch (Exception e) {
            log.error("Restore job failed: {}", id, e);
            if (restore != null && restore.tenantSlug() != null) {
                maintenance.disableMaintenance(restore.tenantSlug());
            }
            repo.findById(id).ifPresent(job -> {
                BackupJob failed = status(job, BackupStatus.RESTORE_FAILED, e.getMessage(), null, Instant.now());
                record(failed, BackupAuditEventTypes.RESTORE_FAILED, e.getMessage());
            });
        }
    }

    private BackupJob get(UUID id) {
        return repo.findById(id).orElseThrow(() -> new BackupNotFoundException("Backup job not found with id: " + id));
    }

    private BackupJob status(BackupJob j, BackupStatus s, String fail, Instant started, Instant finished) {
        return repo.save(new BackupJob(j.id(), j.operationType(), j.scope(), s, j.tenantId(), j.tenantSlug(), j.schemaName(), j.sourceBackupId(), j.preRestoreBackupId(), j.fileName(), j.storagePath(), j.format(), j.sizeBytes(), j.checksumSha256(), j.requestedBy(), j.reason(), fail, started != null ? started : j.startedAt(), finished != null ? finished : j.finishedAt(), j.createdAt(), j.updatedAt()));
    }

    private void record(BackupJob j, String type, String error) {
        Map<String, Object> d = Map.of("operationType", j.operationType().name(), "scope", j.scope().name(), "tenantSlug", String.valueOf(j.tenantSlug()), "schemaName", String.valueOf(j.schemaName()), "status", j.status().name(), "fileName", j.fileName(), "error", String.valueOf(error));
        try {
            if (j.scope() == BackupScope.FULL_DATABASE) {
                audit.recordPlatformEvent(type, "BACKUP", j.id().toString(), d);
            } else {
                recordTenantEvent(j, type, d);
            }
        } catch (Exception e) {
            log.warn("Unable to record backup audit event. jobId={}, eventType={}", j.id(), type, e);
        }
    }

    private void recordTenantEvent(BackupJob j, String type, Map<String, Object> details) {
        TenantContext previous = TenantContextHolder.get();
        try {
            TenantContextHolder.set(new TenantContext(j.tenantSlug(), j.schemaName(), false));
            audit.recordTenantEvent(type, "BACKUP", j.id().toString(), details);
        } finally {
            if (previous == null) {
                TenantContextHolder.clear();
            } else {
                TenantContextHolder.set(previous);
            }
        }
    }
}
