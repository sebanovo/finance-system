package com.financesystem.finance_api.modules.governance.backups.domain.model;

public enum BackupStatus {
    PENDING,
    RUNNING,
    COMPLETED,
    FAILED,
    RESTORING,
    RESTORED,
    RESTORED_WITH_WARNINGS,
    RESTORE_FAILED
}
