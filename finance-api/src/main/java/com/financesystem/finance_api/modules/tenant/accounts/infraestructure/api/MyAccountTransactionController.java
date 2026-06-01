package com.financesystem.finance_api.modules.tenant.accounts.infraestructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.pagination.PaginationSupport;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.query.ListMyAccountTransactionsUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/me/accounts/{accountId}/transactions")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class MyAccountTransactionController {

    private final ListMyAccountTransactionsUseCase listMyAccountTransactionsUseCase;

    public MyAccountTransactionController(ListMyAccountTransactionsUseCase listMyAccountTransactionsUseCase) {
        this.listMyAccountTransactionsUseCase = listMyAccountTransactionsUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('me.accounts.transactions.read')")
    public ApiResponse<Page<TransactionResponse>> listMyAccountTransactions(
            @PathVariable UUID accountId,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success(
                "Account transactions retrieved successfully",
                PaginationSupport.page(listMyAccountTransactionsUseCase.execute(accountId), pageable)
        );
    }
}
