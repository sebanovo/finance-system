package com.financesystem.finance_api.modules.governance.backups.application.service;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.backups.domain.exception.*;
import com.financesystem.finance_api.modules.governance.backups.domain.model.*;
import com.financesystem.finance_api.modules.governance.backups.domain.repository.BackupJobRepository;
import com.financesystem.finance_api.modules.platform.tenants.domain.exception.PlatformTenantNotFoundException;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BackupApplicationService {

    private final BackupJobRepository repo;
    private final PlatformTenantRepository tenants;
    private final SecurityContextFacade security;
    private final BackupJobAsyncExecutor async;
    private final AuditTrailService audit;

    public BackupApplicationService(BackupJobRepository repo, PlatformTenantRepository tenants, SecurityContextFacade security, BackupJobAsyncExecutor async, AuditTrailService audit) {
        this.repo = repo;
        this.tenants = tenants;
        this.security = security;
        this.async = async;
        this.audit = audit;
    }

    @Transactional
    public BackupJob createTenantBackup(String reason) {
        TenantContext ctx = TenantContextHolder.getRequired();
        PlatformTenant t = tenants.findBySlug(ctx.tenantSlug()).orElseThrow(() -> new PlatformTenantNotFoundException("Tenant not found: " + ctx.tenantSlug()));
        BackupJob created = save(BackupJobFactory.tenantBackup(t.id(), t.slug(), t.schemaName(), actor(), reason));
        audit.recordTenantEvent(BackupAuditEventTypes.BACKUP_CREATED, "BACKUP", created.id().toString(), Map.of("tenantSlug", t.slug(), "schemaName", t.schemaName()));
        executeAfterCommit(() -> async.executeBackup(created.id()));
        return created;
    }

    @Transactional
    public BackupJob createPlatformFullBackup(String reason) {
        BackupJob created = save(BackupJobFactory.fullBackup(actor(), reason));
        audit.recordPlatformEvent(BackupAuditEventTypes.BACKUP_CREATED, "BACKUP", created.id().toString(), Map.of("scope", BackupScope.FULL_DATABASE.name()));
        executeAfterCommit(() -> async.executeBackup(created.id()));
        return created;
    }

    @Transactional
    public BackupJob createPlatformTenantBackup(UUID tenantId, String reason) {
        PlatformTenant t = tenants.findById(tenantId).orElseThrow(() -> new PlatformTenantNotFoundException("Tenant not found with id: " + tenantId));
        BackupJob created = save(BackupJobFactory.tenantBackup(t.id(), t.slug(), t.schemaName(), actor(), reason));
        audit.recordPlatformEvent(BackupAuditEventTypes.BACKUP_CREATED, "BACKUP", created.id().toString(), Map.of("tenantId", t.id().toString(), "tenantSlug", t.slug(), "schemaName", t.schemaName()));
        executeAfterCommit(() -> async.executeBackup(created.id()));
        return created;
    }

    @Transactional
    public BackupJob createTenantRestoreJob(UUID id, String reason) {
        TenantContext ctx = TenantContextHolder.getRequired();
        BackupJob src = repo.findById(id).orElseThrow(() -> new BackupNotFoundException("Backup not found with id: " + id));
        assertRestorable(src);
        if (src.tenantSlug() == null || !src.tenantSlug().equals(ctx.tenantSlug())) {
            throw new BackupOperationNotAllowedException("You can only restore backups from your own tenant");
        }
        BackupJob created = save(BackupJobFactory.restoreJobFromBackup(src, null, actor(), reason));
        executeAfterCommit(() -> async.executeRestore(created.id()));
        return created;
    }

    @Transactional
    public BackupJob createPlatformRestoreJob(UUID id, String reason) {
        BackupJob src = repo.findById(id).orElseThrow(() -> new BackupNotFoundException("Backup not found with id: " + id));
        assertRestorable(src);
        BackupJob created = save(BackupJobFactory.restoreJobFromBackup(src, null, actor(), reason));
        executeAfterCommit(() -> async.executeRestore(created.id()));
        return created;
    }

    public List<BackupJob> listTenantBackups() {
        return repo.findByTenantSlug(TenantContextHolder.getRequired().tenantSlug());
    }

    public List<BackupJob> listPlatformBackups() {
        return repo.findAll();
    }

    public BackupJob getTenantBackupById(UUID id) {
        TenantContext ctx = TenantContextHolder.getRequired();
        BackupJob b = repo.findById(id).orElseThrow(() -> new BackupNotFoundException("Backup not found with id: " + id));
        if (b.tenantSlug() == null || !b.tenantSlug().equals(ctx.tenantSlug())) {
            throw new BackupNotFoundException("Backup not found with id: " + id);
        }
        return b;
    }

    public BackupJob getPlatformBackupById(UUID id) {
        return repo.findById(id).orElseThrow(() -> new BackupNotFoundException("Backup not found with id: " + id));
    }

    public BackupJob registerTenantDownload(UUID id) {
        BackupJob b = getTenantBackupById(id);
        audit.recordTenantEvent(BackupAuditEventTypes.BACKUP_DOWNLOADED, "BACKUP", b.id().toString(), Map.of("fileName", b.fileName(), "tenantSlug", b.tenantSlug()));
        return b;
    }

    public BackupJob registerPlatformDownload(UUID id) {
        BackupJob b = getPlatformBackupById(id);
        audit.recordPlatformEvent(BackupAuditEventTypes.BACKUP_DOWNLOADED, "BACKUP", b.id().toString(), Map.of("fileName", b.fileName(), "scope", b.scope().name()));
        return b;
    }

    private BackupJob save(BackupJob j) {
        try {
            return repo.save(j);
        } catch (DataIntegrityViolationException e) {
            throw new BackupOperationNotAllowedException("There is already a backup or restore job running for this scope. Try again later.");
        }
    }

    private void assertRestorable(BackupJob s) {
        if (s.operationType() != BackupOperationType.BACKUP || s.status() != BackupStatus.COMPLETED) {
            throw new BackupOperationNotAllowedException("Only completed backup jobs can be restored");
        }
        if (s.storagePath() == null || s.storagePath().isBlank()) {
            throw new BackupOperationNotAllowedException("Backup artifact path is missing");
        }
    }

    private void executeAfterCommit(Runnable task) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            task.run();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                task.run();
            }
        });
    }

    private String actor() {
        String s = security.getCurrentSubject();
        return s == null || s.isBlank() ? "SYSTEM" : s;
    }
}
