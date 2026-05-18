package com.financesystem.finance_api.modules.tenant.transactions.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateDepositTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.ConfirmQrTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateHoldTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreatePaymentTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateWithdrawalTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateReleaseTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateTransferTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateMyDepositTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateMyHoldTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.ConfirmMyQrTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateMyPaymentTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateMyTransferTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateMyWithdrawalTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateMyReleaseTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.query.GetMyTransactionByIdUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.query.ListMyTransactionsUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/me/transactions")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class MyTransactionController {

    private final CreateMyDepositTransactionUseCase createMyDepositTransactionUseCase;
    private final CreateMyHoldTransactionUseCase createMyHoldTransactionUseCase;
    private final ConfirmMyQrTransactionUseCase confirmMyQrTransactionUseCase;
    private final CreateMyPaymentTransactionUseCase createMyPaymentTransactionUseCase;
    private final CreateMyWithdrawalTransactionUseCase createMyWithdrawalTransactionUseCase;
    private final CreateMyReleaseTransactionUseCase createMyReleaseTransactionUseCase;
    private final CreateMyTransferTransactionUseCase createMyTransferTransactionUseCase;
    private final ListMyTransactionsUseCase listMyTransactionsUseCase;
    private final GetMyTransactionByIdUseCase getMyTransactionByIdUseCase;

    public MyTransactionController(
            CreateMyDepositTransactionUseCase createMyDepositTransactionUseCase,
            CreateMyHoldTransactionUseCase createMyHoldTransactionUseCase,
            ConfirmMyQrTransactionUseCase confirmMyQrTransactionUseCase,
            CreateMyPaymentTransactionUseCase createMyPaymentTransactionUseCase,
            CreateMyWithdrawalTransactionUseCase createMyWithdrawalTransactionUseCase,
            CreateMyReleaseTransactionUseCase createMyReleaseTransactionUseCase,
            CreateMyTransferTransactionUseCase createMyTransferTransactionUseCase,
            ListMyTransactionsUseCase listMyTransactionsUseCase,
            GetMyTransactionByIdUseCase getMyTransactionByIdUseCase
    ) {
        this.createMyDepositTransactionUseCase = createMyDepositTransactionUseCase;
        this.createMyHoldTransactionUseCase = createMyHoldTransactionUseCase;
        this.confirmMyQrTransactionUseCase = confirmMyQrTransactionUseCase;
        this.createMyPaymentTransactionUseCase = createMyPaymentTransactionUseCase;
        this.createMyWithdrawalTransactionUseCase = createMyWithdrawalTransactionUseCase;
        this.createMyReleaseTransactionUseCase = createMyReleaseTransactionUseCase;
        this.createMyTransferTransactionUseCase = createMyTransferTransactionUseCase;
        this.listMyTransactionsUseCase = listMyTransactionsUseCase;
        this.getMyTransactionByIdUseCase = getMyTransactionByIdUseCase;
    }

    @PostMapping("/deposits")
    @PreAuthorize("hasAuthority('me.transactions.deposit')")
    public ApiResponse<TransactionResponse> createDeposit(@Valid @RequestBody CreateDepositTransactionRequest request) {
        return ApiResponse.success("Deposit created successfully", createMyDepositTransactionUseCase.execute(request));
    }

    @PostMapping("/holds")
    @PreAuthorize("hasAuthority('me.transactions.hold')")
    public ApiResponse<TransactionResponse> createHold(@Valid @RequestBody CreateHoldTransactionRequest request) {
        return ApiResponse.success("Hold created successfully", createMyHoldTransactionUseCase.execute(request));
    }

    @PostMapping("/qr/{id}/confirm")
    @PreAuthorize("hasAuthority('me.transactions.qr.confirm')")
    public ApiResponse<TransactionResponse> confirmQrTransaction(
            @PathVariable UUID id,
            @Valid @RequestBody ConfirmQrTransactionRequest request
    ) {
        return ApiResponse.success("QR transaction confirmed successfully", confirmMyQrTransactionUseCase.execute(id, request));
    }

    @PostMapping("/payments")
    @PreAuthorize("hasAuthority('me.transactions.payment')")
    public ApiResponse<TransactionResponse> createPayment(@Valid @RequestBody CreatePaymentTransactionRequest request) {
        return ApiResponse.success("Payment created successfully", createMyPaymentTransactionUseCase.execute(request));
    }

    @PostMapping("/withdrawals")
    @PreAuthorize("hasAuthority('me.transactions.withdrawal')")
    public ApiResponse<TransactionResponse> createWithdrawal(@Valid @RequestBody CreateWithdrawalTransactionRequest request) {
        return ApiResponse.success("Withdrawal created successfully", createMyWithdrawalTransactionUseCase.execute(request));
    }

    @PostMapping("/releases")
    @PreAuthorize("hasAuthority('me.transactions.release')")
    public ApiResponse<TransactionResponse> createRelease(@Valid @RequestBody CreateReleaseTransactionRequest request) {
        return ApiResponse.success("Release created successfully", createMyReleaseTransactionUseCase.execute(request));
    }

    @PostMapping("/transfers")
    @PreAuthorize("hasAuthority('me.transactions.transfer')")
    public ApiResponse<TransactionResponse> createTransfer(@Valid @RequestBody CreateTransferTransactionRequest request) {
        return ApiResponse.success("Transfer created successfully", createMyTransferTransactionUseCase.execute(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('me.transactions.read')")
    public ApiResponse<List<TransactionResponse>> listTransactions() {
        return ApiResponse.success("Transactions retrieved successfully", listMyTransactionsUseCase.execute());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('me.transactions.detail')")
    public ApiResponse<TransactionResponse> getTransactionById(@PathVariable UUID id) {
        return ApiResponse.success("Transaction retrieved successfully", getMyTransactionByIdUseCase.execute(id));
    }
}
