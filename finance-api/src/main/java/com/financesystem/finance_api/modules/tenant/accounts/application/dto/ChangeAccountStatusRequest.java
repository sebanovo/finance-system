package com.financesystem.finance_api.modules.tenant.accounts.application.dto;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.AccountStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeAccountStatusRequest(
        @NotNull
        AccountStatus status
) {
}
