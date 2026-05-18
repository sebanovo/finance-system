package com.financesystem.finance_api.modules.tenant.accounts.infraestructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.tenant.accounts.application.dto.*;
import com.financesystem.finance_api.modules.tenant.accounts.application.usecase.create.*;
import com.financesystem.finance_api.modules.tenant.accounts.application.usecase.lifecycle.*;
import com.financesystem.finance_api.modules.tenant.accounts.application.usecase.update.*;
import com.financesystem.finance_api.modules.tenant.accounts.application.usecase.query.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@SecurityRequirement(name = "bearerAuth")
public class AccountController {

    private final CreateAccountUseCase createAccountUseCase;
    private final ListAccountsUseCase listAccountsUseCase;
    private final GetAccountByIdUseCase getAccountByIdUseCase;
    private final GetAccountBalanceUseCase getAccountBalanceUseCase;

    private final UpdateAccountUseCase updateAccountUseCase;

    private final ApproveAccountUseCase approveAccountUseCase;
    private final ActivateAccountUseCase activateAccountUseCase;

    private final BlockAccountUseCase blockAccountUseCase;
    private final FreezeAccountUseCase freezeAccountUseCase;
    private final CloseAccountUseCase closeAccountUseCase;

    public AccountController(
            CreateAccountUseCase createAccountUseCase,
            ListAccountsUseCase listAccountsUseCase,
            GetAccountByIdUseCase getAccountByIdUseCase,
            GetAccountBalanceUseCase getAccountBalanceUseCase,
            UpdateAccountUseCase updateAccountUseCase,
            ApproveAccountUseCase approveAccountUseCase,
            ActivateAccountUseCase activateAccountUseCase,
            BlockAccountUseCase blockAccountUseCase,
            FreezeAccountUseCase freezeAccountUseCase,
            CloseAccountUseCase closeAccountUseCase
    ) {
        this.createAccountUseCase = createAccountUseCase;
        this.listAccountsUseCase = listAccountsUseCase;
        this.getAccountByIdUseCase = getAccountByIdUseCase;
        this.getAccountBalanceUseCase = getAccountBalanceUseCase;
        this.updateAccountUseCase = updateAccountUseCase;
        this.approveAccountUseCase = approveAccountUseCase;
        this.activateAccountUseCase = activateAccountUseCase;
        this.blockAccountUseCase = blockAccountUseCase;
        this.freezeAccountUseCase = freezeAccountUseCase;
        this.closeAccountUseCase = closeAccountUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('accounts.create')")
    public ApiResponse<AccountOwnerResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        return ApiResponse.success(
                "Account created successfully",
                createAccountUseCase.execute(request)
        );
    }

    @GetMapping
    @PreAuthorize("hasAuthority('accounts.list')")
    public ApiResponse<List<AccountOwnerResponse>> listAccounts() {
        return ApiResponse.success(
                "Accounts retrieved successfully",
                listAccountsUseCase.execute()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('accounts.view')")
    public ApiResponse<AccountOwnerResponse> getAccountById(
            @PathVariable UUID id
    ) {
        return ApiResponse.success(
                "Account retrieved successfully",
                getAccountByIdUseCase.execute(id)
        );
    }

    @GetMapping("/{id}/balance")
    @PreAuthorize("hasAuthority('accounts.balance.read')")
    public ApiResponse<AccountBalanceResponse> getAccountBalance(
            @PathVariable UUID id
    ) {
        return ApiResponse.success(
                "Account balance retrieved successfully",
                getAccountBalanceUseCase.execute(id)
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('accounts.update')")
    public ApiResponse<AccountOwnerResponse> updateAccount(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateAccountRequest request
    ) {
        return ApiResponse.success(
                "Account updated successfully",
                updateAccountUseCase.execute(id, request)
        );
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('accounts.approve')")
    public ApiResponse<AccountOwnerResponse> approveAccount(
            @PathVariable UUID id
    ) {
        return ApiResponse.success(
                "Account approved successfully",
                approveAccountUseCase.execute(id)
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAuthority('accounts.activate')")
    public ApiResponse<AccountOwnerResponse> activateAccount(
            @PathVariable UUID id
    ) {
        return ApiResponse.success(
                "Account activated successfully",
                activateAccountUseCase.execute(id)
        );
    }

    @PatchMapping("/{id}/block")
    @PreAuthorize("hasAuthority('accounts.block')")
    public ApiResponse<AccountOwnerResponse> blockAccount(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason
    ) {
        return ApiResponse.success(
                "Account blocked successfully",
                blockAccountUseCase.execute(id, reason)
        );
    }

    @PatchMapping("/{id}/freeze")
    @PreAuthorize("hasAuthority('accounts.freeze')")
    public ApiResponse<AccountOwnerResponse> freezeAccount(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason
    ) {
        return ApiResponse.success(
                "Account frozen successfully",
                freezeAccountUseCase.execute(id, reason)
        );
    }

    @PatchMapping("/{id}/close")
    @PreAuthorize("hasAuthority('accounts.close')")
    public ApiResponse<AccountOwnerResponse> closeAccount(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason
    ) {
        return ApiResponse.success(
                "Account closed successfully",
                closeAccountUseCase.execute(id, reason)
        );
    }
}
