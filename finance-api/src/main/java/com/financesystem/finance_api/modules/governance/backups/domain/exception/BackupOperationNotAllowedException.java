package com.financesystem.finance_api.modules.governance.backups.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class BackupOperationNotAllowedException extends BusinessException {

    public BackupOperationNotAllowedException(String message) {
        super(message);
    }
}
