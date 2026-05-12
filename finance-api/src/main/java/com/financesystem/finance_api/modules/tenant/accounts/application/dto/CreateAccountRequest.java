package com.financesystem.finance_api.modules.tenant.accounts.application.dto;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountName;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountType;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateAccountRequest(
        @NotNull
        UUID userId,

        @NotNull
        AccountName accountName,

        @Size(max = 100)
        String customAlias,

        @NotNull
        AccountType accountType,

        @NotNull
        CurrencyCode currency,

        Boolean primary
) {
}
