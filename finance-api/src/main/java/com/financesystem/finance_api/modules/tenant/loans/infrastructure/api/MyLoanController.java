package com.financesystem.finance_api.modules.tenant.loans.infrastructure.api;

import com.financesystem.finance_api.common.pagination.PaginationSupport;
import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.CreateMyLoanRequest;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanDetailResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanInstallmentResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.LoanResponse;
import com.financesystem.finance_api.modules.tenant.loans.application.dto.RecordLoanPaymentRequest;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.create.RequestMyLoanUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.lifecycle.RecordMyLoanPaymentUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.query.GetMyLoanByIdUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.query.GetMyLoanScheduleUseCase;
import com.financesystem.finance_api.modules.tenant.loans.application.usecase.query.ListMyLoansUseCase;
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
@RequestMapping("/api/me/loans")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("isAuthenticated()")
public class MyLoanController {

    private final RequestMyLoanUseCase requestMyLoanUseCase;
    private final ListMyLoansUseCase listMyLoansUseCase;
    private final GetMyLoanByIdUseCase getMyLoanByIdUseCase;
    private final GetMyLoanScheduleUseCase getMyLoanScheduleUseCase;
    private final RecordMyLoanPaymentUseCase recordMyLoanPaymentUseCase;

    public MyLoanController(
            RequestMyLoanUseCase requestMyLoanUseCase,
            ListMyLoansUseCase listMyLoansUseCase,
            GetMyLoanByIdUseCase getMyLoanByIdUseCase,
            GetMyLoanScheduleUseCase getMyLoanScheduleUseCase,
            RecordMyLoanPaymentUseCase recordMyLoanPaymentUseCase
    ) {
        this.requestMyLoanUseCase = requestMyLoanUseCase;
        this.listMyLoansUseCase = listMyLoansUseCase;
        this.getMyLoanByIdUseCase = getMyLoanByIdUseCase;
        this.getMyLoanScheduleUseCase = getMyLoanScheduleUseCase;
        this.recordMyLoanPaymentUseCase = recordMyLoanPaymentUseCase;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('me.loans.request')")
    public ApiResponse<LoanResponse> requestMyLoan(@Valid @RequestBody CreateMyLoanRequest request) {
        return ApiResponse.success("Loan requested successfully", requestMyLoanUseCase.execute(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('me.loans.list')")
    public ApiResponse<Page<LoanResponse>> listMyLoans(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success(
                "Loans retrieved successfully",
                PaginationSupport.page(listMyLoansUseCase.execute(), pageable)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('me.loans.view')")
    public ApiResponse<LoanDetailResponse> getMyLoan(@PathVariable UUID id) {
        return ApiResponse.success("Loan retrieved successfully", getMyLoanByIdUseCase.execute(id));
    }

    @GetMapping("/{id}/schedule")
    @PreAuthorize("hasAuthority('me.loans.view')")
    public ApiResponse<List<LoanInstallmentResponse>> getMyLoanSchedule(@PathVariable UUID id) {
        return ApiResponse.success("Loan schedule retrieved successfully", getMyLoanScheduleUseCase.execute(id));
    }

    @PostMapping("/{id}/payments")
    @PreAuthorize("hasAuthority('me.loans.pay')")
    public ApiResponse<LoanDetailResponse> payMyLoan(
            @PathVariable UUID id,
            @Valid @RequestBody RecordLoanPaymentRequest request
    ) {
        return ApiResponse.success("Loan payment recorded successfully", recordMyLoanPaymentUseCase.execute(id, request));
    }
}
