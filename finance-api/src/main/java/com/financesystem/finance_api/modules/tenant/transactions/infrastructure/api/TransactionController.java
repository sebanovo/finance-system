package com.financesystem.finance_api.modules.tenant.transactions.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateAdjustmentTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateDepositTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateFeeTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateHoldTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateReleaseTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateQrTransactionIntentRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreatePaymentTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateRefundTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateReversalTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateWithdrawalTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreateTransferTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.QrTransactionIntentResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateAdjustmentTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateDepositTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateFeeTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateHoldTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateQrTransactionIntentUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreatePaymentTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.RefundTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.ReverseTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateReleaseTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateTransferTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create.CreateWithdrawalTransactionUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.query.GetTransactionByIdUseCase;
import com.financesystem.finance_api.modules.tenant.transactions.application.usecase.query.ListTransactionsUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAnyRole('OWNER_ADMIN', 'ADMIN')")
public class TransactionController {

    private final CreateDepositTransactionUseCase createDepositTransactionUseCase;
    private final CreateFeeTransactionUseCase createFeeTransactionUseCase;
    private final CreateHoldTransactionUseCase createHoldTransactionUseCase;
    private final CreateAdjustmentTransactionUseCase createAdjustmentTransactionUseCase;
    private final CreateQrTransactionIntentUseCase createQrTransactionIntentUseCase;
    private final CreatePaymentTransactionUseCase createPaymentTransactionUseCase;
    private final CreateWithdrawalTransactionUseCase createWithdrawalTransactionUseCase;
    private final CreateReleaseTransactionUseCase createReleaseTransactionUseCase;
    private final CreateTransferTransactionUseCase createTransferTransactionUseCase;
    private final ReverseTransactionUseCase reverseTransactionUseCase;
    private final RefundTransactionUseCase refundTransactionUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final GetTransactionByIdUseCase getTransactionByIdUseCase;

    public TransactionController(
            CreateDepositTransactionUseCase createDepositTransactionUseCase,
            CreateFeeTransactionUseCase createFeeTransactionUseCase,
            CreateHoldTransactionUseCase createHoldTransactionUseCase,
            CreateAdjustmentTransactionUseCase createAdjustmentTransactionUseCase,
            CreateQrTransactionIntentUseCase createQrTransactionIntentUseCase,
            CreatePaymentTransactionUseCase createPaymentTransactionUseCase,
            CreateWithdrawalTransactionUseCase createWithdrawalTransactionUseCase,
            CreateReleaseTransactionUseCase createReleaseTransactionUseCase,
            CreateTransferTransactionUseCase createTransferTransactionUseCase,
            ReverseTransactionUseCase reverseTransactionUseCase,
            RefundTransactionUseCase refundTransactionUseCase,
            ListTransactionsUseCase listTransactionsUseCase,
            GetTransactionByIdUseCase getTransactionByIdUseCase
    ) {
        this.createDepositTransactionUseCase = createDepositTransactionUseCase;
        this.createFeeTransactionUseCase = createFeeTransactionUseCase;
        this.createHoldTransactionUseCase = createHoldTransactionUseCase;
        this.createAdjustmentTransactionUseCase = createAdjustmentTransactionUseCase;
        this.createQrTransactionIntentUseCase = createQrTransactionIntentUseCase;
        this.createPaymentTransactionUseCase = createPaymentTransactionUseCase;
        this.createWithdrawalTransactionUseCase = createWithdrawalTransactionUseCase;
        this.createReleaseTransactionUseCase = createReleaseTransactionUseCase;
        this.createTransferTransactionUseCase = createTransferTransactionUseCase;
        this.reverseTransactionUseCase = reverseTransactionUseCase;
        this.refundTransactionUseCase = refundTransactionUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.getTransactionByIdUseCase = getTransactionByIdUseCase;
    }

    @PostMapping("/deposits")
    public ApiResponse<TransactionResponse> createDeposit(@Valid @RequestBody CreateDepositTransactionRequest request) {
        return ApiResponse.success("Deposit created successfully", createDepositTransactionUseCase.execute(request));
    }

    @PostMapping("/fees")
    public ApiResponse<TransactionResponse> createFee(@Valid @RequestBody CreateFeeTransactionRequest request) {
        return ApiResponse.success("Fee created successfully", createFeeTransactionUseCase.execute(request));
    }

    @PostMapping("/holds")
    public ApiResponse<TransactionResponse> createHold(@Valid @RequestBody CreateHoldTransactionRequest request) {
        return ApiResponse.success("Hold created successfully", createHoldTransactionUseCase.execute(request));
    }

    @PostMapping("/qr/intents")
    public ApiResponse<QrTransactionIntentResponse> createQrIntent(@Valid @RequestBody CreateQrTransactionIntentRequest request) {
        return ApiResponse.success("QR intent created successfully", createQrTransactionIntentUseCase.execute(request));
    }

    @PostMapping("/payments")
    public ApiResponse<TransactionResponse> createPayment(@Valid @RequestBody CreatePaymentTransactionRequest request) {
        return ApiResponse.success("Payment created successfully", createPaymentTransactionUseCase.execute(request));
    }

    @PostMapping("/adjustments")
    public ApiResponse<TransactionResponse> createAdjustment(@Valid @RequestBody CreateAdjustmentTransactionRequest request) {
        return ApiResponse.success("Adjustment created successfully", createAdjustmentTransactionUseCase.execute(request));
    }

    @PostMapping("/withdrawals")
    public ApiResponse<TransactionResponse> createWithdrawal(@Valid @RequestBody CreateWithdrawalTransactionRequest request) {
        return ApiResponse.success("Withdrawal created successfully", createWithdrawalTransactionUseCase.execute(request));
    }

    @PostMapping("/releases")
    public ApiResponse<TransactionResponse> createRelease(@Valid @RequestBody CreateReleaseTransactionRequest request) {
        return ApiResponse.success("Release created successfully", createReleaseTransactionUseCase.execute(request));
    }

    @PostMapping("/transfers")
    public ApiResponse<TransactionResponse> createTransfer(@Valid @RequestBody CreateTransferTransactionRequest request) {
        return ApiResponse.success("Transfer created successfully", createTransferTransactionUseCase.execute(request));
    }

    @PostMapping("/{id}/reversal")
    public ApiResponse<TransactionResponse> reverseTransaction(
            @PathVariable UUID id,
            @Valid @RequestBody CreateReversalTransactionRequest request
    ) {
        return ApiResponse.success("Transaction reversed successfully", reverseTransactionUseCase.execute(id, request));
    }

    @PostMapping("/{id}/refunds")
    public ApiResponse<TransactionResponse> refundTransaction(
            @PathVariable UUID id,
            @Valid @RequestBody CreateRefundTransactionRequest request
    ) {
        return ApiResponse.success("Transaction refunded successfully", refundTransactionUseCase.execute(id, request));
    }

    @GetMapping
    public ApiResponse<List<TransactionResponse>> listTransactions() {
        return ApiResponse.success("Transactions retrieved successfully", listTransactionsUseCase.execute());
    }

    @GetMapping("/{id}")
    public ApiResponse<TransactionResponse> getTransactionById(@PathVariable UUID id) {
        return ApiResponse.success("Transaction retrieved successfully", getTransactionByIdUseCase.execute(id));
    }
}
