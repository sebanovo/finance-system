package com.financesystem.finance_api.modules.tenant.accounts.infraestructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.query.ListAccountTransactionsUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts/{accountId}/transactions")
@SecurityRequirement(name = "bearerAuth")
// @PreAuthorize("hasAnyRole('OWNER_ADMIN', 'ADMIN')")
public class AccountTransactionController {

    private final ListAccountTransactionsUseCase listAccountTransactionsUseCase;

    public AccountTransactionController(ListAccountTransactionsUseCase listAccountTransactionsUseCase) {
        this.listAccountTransactionsUseCase = listAccountTransactionsUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('accounts.transactions.read')")
    public ApiResponse<List<TransactionResponse>> listAccountTransactions(@PathVariable UUID accountId) {
        return ApiResponse.success(
                "Account transactions retrieved successfully",
                listAccountTransactionsUseCase.execute(accountId)
        );
    }
}
