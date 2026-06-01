package com.financesystem.finance_api.modules.tenant.accounts.application.dto;

import java.util.UUID;

public record AccountLookupResponse(
        UUID id,
        String accountNumber,
        String accountName,
        String accountNameLabel,
        String customAlias,
        String displayName,
        String accountType,
        String currency,
        String status,
        boolean active
) {
}
