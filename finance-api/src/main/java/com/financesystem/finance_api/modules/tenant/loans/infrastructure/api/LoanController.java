package com.financesystem.finance_api.modules.tenant.loans.infrastructure.api;

import com.financesystem.finance_api.common.pagination.PaginationSupport;
import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.CreateLoanRequest;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanDetailResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanInstallmentResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanSummaryResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.ProcessOverdueLoansResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.RecordLoanPaymentRequest;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.create.RequestLoanUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.lifecycle.ApproveLoanUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.lifecycle.DisburseLoanUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.lifecycle.ProcessOverdueLoansUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.lifecycle.RecordLoanPaymentUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.lifecycle.RejectLoanUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.query.GetLoanByIdUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.query.GetLoanPortfolioSummaryUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.query.GetLoanScheduleUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.query.ListLoansUseCase;
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
@RequestMapping("/api/loans")
@SecurityRequirement(name = "bearerAuth")
public class LoanController {

    private final RequestLoanUseCase requestLoanUseCase;
    private final ListLoansUseCase listLoansUseCase;
    private final GetLoanByIdUseCase getLoanByIdUseCase;
    private final GetLoanScheduleUseCase getLoanScheduleUseCase;
    private final ApproveLoanUseCase approveLoanUseCase;
    private final RejectLoanUseCase rejectLoanUseCase;
    private final DisburseLoanUseCase disburseLoanUseCase;
    private final RecordLoanPaymentUseCase recordLoanPaymentUseCase;
    private final GetLoanPortfolioSummaryUseCase getLoanPortfolioSummaryUseCase;
    private final ProcessOverdueLoansUseCase processOverdueLoansUseCase;

    public LoanController(
            RequestLoanUseCase requestLoanUseCase,
            ListLoansUseCase listLoansUseCase,
            GetLoanByIdUseCase getLoanByIdUseCase,
            GetLoanScheduleUseCase getLoanScheduleUseCase,
            ApproveLoanUseCase approveLoanUseCase,
            RejectLoanUseCase rejectLoanUseCase,
            DisburseLoanUseCase disburseLoanUseCase,
            RecordLoanPaymentUseCase recordLoanPaymentUseCase,
            GetLoanPortfolioSummaryUseCase getLoanPortfolioSummaryUseCase,
            ProcessOverdueLoansUseCase processOverdueLoansUseCase
    ) {
        this.requestLoanUseCase = requestLoanUseCase;
        this.listLoansUseCase = listLoansUseCase;
        this.getLoanByIdUseCase = getLoanByIdUseCase;
        this.getLoanScheduleUseCase = getLoanScheduleUseCase;
        this.approveLoanUseCase = approveLoanUseCase;
        this.rejectLoanUseCase = rejectLoanUseCase;
        this.disburseLoanUseCase = disburseLoanUseCase;
        this.recordLoanPaymentUseCase = recordLoanPaymentUseCase;
        this.getLoanPortfolioSummaryUseCase = getLoanPortfolioSummaryUseCase;
        this.processOverdueLoansUseCase = processOverdueLoansUseCase;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('loans.view')")
    public ApiResponse<LoanSummaryResponse> summary() {
        return ApiResponse.success("Loan portfolio summary retrieved", getLoanPortfolioSummaryUseCase.execute());
    }

    @PostMapping("/process-overdue")
    @PreAuthorize("hasAuthority('loans.disburse')")
    public ApiResponse<ProcessOverdueLoansResponse> processOverdue() {
        return ApiResponse.success("Overdue installments processed", processOverdueLoansUseCase.execute());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('loans.request')")
    public ApiResponse<LoanResponse> requestLoan(@Valid @RequestBody CreateLoanRequest request) {
        return ApiResponse.success("Loan requested successfully", requestLoanUseCase.execute(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('loans.list')")
    public ApiResponse<Page<LoanResponse>> listLoans(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success(
                "Loans retrieved successfully",
                PaginationSupport.page(listLoansUseCase.execute(), pageable)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('loans.view')")
    public ApiResponse<LoanDetailResponse> getLoan(@PathVariable UUID id) {
        return ApiResponse.success("Loan retrieved successfully", getLoanByIdUseCase.execute(id));
    }

    @GetMapping("/{id}/schedule")
    @PreAuthorize("hasAuthority('loans.view')")
    public ApiResponse<List<LoanInstallmentResponse>> getLoanSchedule(@PathVariable UUID id) {
        return ApiResponse.success("Loan schedule retrieved successfully", getLoanScheduleUseCase.execute(id));
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasAuthority('loans.approve')")
    public ApiResponse<LoanResponse> approveLoan(@PathVariable UUID id) {
        return ApiResponse.success("Loan approved successfully", approveLoanUseCase.execute(id));
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasAuthority('loans.reject')")
    public ApiResponse<LoanResponse> rejectLoan(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason
    ) {
        return ApiResponse.success("Loan rejected successfully", rejectLoanUseCase.execute(id, reason));
    }

    @PostMapping("/{id}/disburse")
    @PreAuthorize("hasAuthority('loans.disburse')")
    public ApiResponse<LoanDetailResponse> disburseLoan(@PathVariable UUID id) {
        return ApiResponse.success("Loan disbursed successfully", disburseLoanUseCase.execute(id));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAuthority('loans.repay')")
    public ApiResponse<LoanDetailResponse> recordPayment(
            @PathVariable UUID id,
            @Valid @RequestBody RecordLoanPaymentRequest request
    ) {
        return ApiResponse.success("Loan payment recorded successfully", recordLoanPaymentUseCase.execute(id, request));
    }
}
