package com.financesystem.finance_api.modules.tenant.accounts.application.dto;

import jakarta.validation.constraints.Size;

public record UpdateAccountAliasRequest(
        @Size(max = 100)
        String customAlias
) {
}