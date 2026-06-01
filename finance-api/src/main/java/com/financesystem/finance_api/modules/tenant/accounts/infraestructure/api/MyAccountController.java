package com.financesystem.finance_api.modules.tenant.accounts.infraestructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.pagination.PaginationSupport;
import com.financesystem.finance_api.modules.tenant.accounts.application.dto.*;
import com.financesystem.finance_api.modules.tenant.accounts.application.usecase.create.*;
import com.financesystem.finance_api.modules.tenant.accounts.application.usecase.query.*;
import com.financesystem.finance_api.modules.tenant.accounts.application.usecase.update.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/me/accounts")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class MyAccountController {

    private final CreateMyAccountUseCase createMyAccountUseCase;
    private final ListMyAccountsUseCase listMyAccountsUseCase;
    private final GetMyAccountByIdUseCase getMyAccountByIdUseCase;
    private final LookupMyAccountByNumberUseCase lookupMyAccountByNumberUseCase;
    private final GetMyAccountBalanceUseCase getMyAccountBalanceUseCase;
    private final UpdateMyAccountAliasUseCase updateMyAccountAliasUseCase;

    public MyAccountController(
            CreateMyAccountUseCase createMyAccountUseCase,
            ListMyAccountsUseCase listMyAccountsUseCase,
            GetMyAccountByIdUseCase getMyAccountByIdUseCase,
            LookupMyAccountByNumberUseCase lookupMyAccountByNumberUseCase,
            GetMyAccountBalanceUseCase getMyAccountBalanceUseCase,
            UpdateMyAccountAliasUseCase updateMyAccountAliasUseCase
    ) {
        this.createMyAccountUseCase = createMyAccountUseCase;
        this.listMyAccountsUseCase = listMyAccountsUseCase;
        this.getMyAccountByIdUseCase = getMyAccountByIdUseCase;
        this.lookupMyAccountByNumberUseCase = lookupMyAccountByNumberUseCase;
        this.getMyAccountBalanceUseCase = getMyAccountBalanceUseCase;
        this.updateMyAccountAliasUseCase = updateMyAccountAliasUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('me.accounts.create')")
    public ApiResponse<AccountOwnerResponse> createMyAccount(
            @Valid @RequestBody CreateMyAccountRequest request
    ) {
        return ApiResponse.success(
                "Account request processed successfully",
                createMyAccountUseCase.execute(request)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('me.accounts.list')")
    public ApiResponse<Page<AccountOwnerResponse>> listMyAccounts(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success(
                "Accounts retrieved successfully",
                PaginationSupport.page(listMyAccountsUseCase.execute(), pageable)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('me.accounts.view')")
    public ApiResponse<AccountOwnerResponse> getMyAccountById(
            @PathVariable UUID id
    ) {
        return ApiResponse.success(
                "Account retrieved successfully",
                getMyAccountByIdUseCase.execute(id)
        );
    }

    @GetMapping("/lookup/{accountNumber}")
    @PreAuthorize("hasAuthority('me.transactions.transfer')")
    public ApiResponse<AccountLookupResponse> lookupMyAccountByNumber(@PathVariable String accountNumber) {
        return ApiResponse.success(
                "Account retrieved successfully",
                lookupMyAccountByNumberUseCase.execute(accountNumber)
        );
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAuthority('me.accounts.balance.read')")
    public ApiResponse<AccountBalanceResponse> getMyAccountBalance(
            @PathVariable UUID id
    ) {
        return ApiResponse.success(
                "Account balance retrieved successfully",
                getMyAccountBalanceUseCase.execute(id)
        );
    }

    @PatchMapping("/{id}/alias")
    @PreAuthorize("hasAuthority('me.accounts.update.alias')")
    public ApiResponse<AccountOwnerResponse> updateMyAccountAlias(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountAliasRequest request
    ) {
        return ApiResponse.success(
                "Account alias updated successfully",
                updateMyAccountAliasUseCase.execute(id, request)
        );
    }
}
