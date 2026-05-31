package com.financesystem.finance_api.modules.governance.backups.application.usecase;

import com.financesystem.finance_api.modules.governance.backups.application.dto.BackupJobResponse;
import com.financesystem.finance_api.modules.governance.backups.application.mapper.BackupJobMapper;
import com.financesystem.finance_api.modules.governance.backups.application.service.BackupApplicationService;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
public class GetTenantBackupByIdUseCase {

    private final BackupApplicationService backupApplicationService;
    private final BackupJobMapper backupJobMapper;

    public GetTenantBackupByIdUseCase(BackupApplicationService backupApplicationService, BackupJobMapper backupJobMapper) {
        this.backupApplicationService = backupApplicationService;
        this.backupJobMapper = backupJobMapper;
    }

    public BackupJobResponse execute(UUID id) {
        return backupJobMapper.toResponse(backupApplicationService.getTenantBackupById(id));
    }
}
