package com.financesystem.finance_api.modules.tenant.accounts.application.dto;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountName;
import jakarta.validation.constraints.Size;

public record UpdateAccountRequest(
        AccountName accountName,

        @Size(max = 100)
        String customAlias,

        Boolean primary
) {
}
