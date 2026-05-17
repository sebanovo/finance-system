package com.financesystem.finance_api.modules.tenant.accounting.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class JournalEntryNotFoundException extends ResourceNotFoundException {

    public JournalEntryNotFoundException(String message) {
        super(message);
    }
}
