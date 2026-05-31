package com.financesystem.finance_api.modules.governance.backups.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class BackupNotFoundException extends ResourceNotFoundException {

    public BackupNotFoundException(String message) {
        super(message);
    }
}
