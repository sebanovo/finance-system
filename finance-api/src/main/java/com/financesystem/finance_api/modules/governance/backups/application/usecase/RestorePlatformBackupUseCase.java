package com.financesystem.finance_api.modules.governance.backups.application.usecase;

import com.financesystem.finance_api.modules.governance.backups.application.dto.BackupJobResponse;
import com.financesystem.finance_api.modules.governance.backups.application.mapper.BackupJobMapper;
import com.financesystem.finance_api.modules.governance.backups.application.service.BackupApplicationService;
import com.financesystem.finance_api.modules.governance.backups.domain.exception.BackupOperationNotAllowedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class RestorePlatformBackupUseCase {

    private static final String CONFIRMATION_TENANT = "RESTORE_PLATFORM_BACKUP";
    private static final String CONFIRMATION_FULL = "RESTORE_FULL_DATABASE";
    private final BackupApplicationService backupApplicationService;
    private final BackupJobMapper backupJobMapper;

    public RestorePlatformBackupUseCase(BackupApplicationService backupApplicationService, BackupJobMapper backupJobMapper) {
        this.backupApplicationService = backupApplicationService;
        this.backupJobMapper = backupJobMapper;
    }

    @Transactional
    public BackupJobResponse execute(UUID backupId, String confirmationText, String reason) {
        if (!CONFIRMATION_TENANT.equals(confirmationText) && !CONFIRMATION_FULL.equals(confirmationText)) {
            throw new BackupOperationNotAllowedException("Invalid confirmation text. Expected: " + CONFIRMATION_TENANT + " or " + CONFIRMATION_FULL);
        
        }return backupJobMapper.toResponse(backupApplicationService.createPlatformRestoreJob(backupId, reason));
    }
}
