package com.financesystem.finance_api.modules.tenant.accounting.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.pagination.PaginationSupport;
import com.financesystem.finance_api.modules.tenant.accounting.application.dto.*;
import com.financesystem.finance_api.modules.tenant.accounting.application.usecase.journal.GetJournalEntryByIdUseCase;
import com.financesystem.finance_api.modules.tenant.accounting.application.usecase.journal.ListJournalEntriesUseCase;
import com.financesystem.finance_api.modules.tenant.accounting.application.usecase.period.CloseAccountingPeriodUseCase;
import com.financesystem.finance_api.modules.tenant.accounting.application.usecase.period.CreateAccountingPeriodUseCase;
import com.financesystem.finance_api.modules.tenant.accounting.application.usecase.period.ListAccountingPeriodsUseCase;
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
@RequestMapping("/api/accounting")
@SecurityRequirement(name = "bearerAuth")
// @PreAuthorize("hasRole('OWNER_ADMIN')")
public class AccountingController {

    private final ListAccountingPeriodsUseCase listAccountingPeriodsUseCase;
    private final CreateAccountingPeriodUseCase createAccountingPeriodUseCase;
    private final CloseAccountingPeriodUseCase closeAccountingPeriodUseCase;
    private final ListJournalEntriesUseCase listJournalEntriesUseCase;
    private final GetJournalEntryByIdUseCase getJournalEntryByIdUseCase;

    public AccountingController(
            ListAccountingPeriodsUseCase listAccountingPeriodsUseCase,
            CreateAccountingPeriodUseCase createAccountingPeriodUseCase,
            CloseAccountingPeriodUseCase closeAccountingPeriodUseCase,
            ListJournalEntriesUseCase listJournalEntriesUseCase,
            GetJournalEntryByIdUseCase getJournalEntryByIdUseCase
    ) {
        this.listAccountingPeriodsUseCase = listAccountingPeriodsUseCase;
        this.createAccountingPeriodUseCase = createAccountingPeriodUseCase;
        this.closeAccountingPeriodUseCase = closeAccountingPeriodUseCase;
        this.listJournalEntriesUseCase = listJournalEntriesUseCase;
        this.getJournalEntryByIdUseCase = getJournalEntryByIdUseCase;
    }

    @GetMapping("/periods")
    @PreAuthorize("hasAuthority('accounting.periods.read')")
    public ApiResponse<Page<AccountingPeriodResponse>> listPeriods(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success("Accounting periods retrieved successfully", PaginationSupport.page(listAccountingPeriodsUseCase.execute(), pageable));
    }

    @PostMapping("/periods")
    @PreAuthorize("hasAuthority('accounting.periods.create')")
    public ApiResponse<AccountingPeriodResponse> createPeriod(@Valid @RequestBody CreateAccountingPeriodRequest request) {
        return ApiResponse.success("Accounting period created successfully", createAccountingPeriodUseCase.execute(request));
    }

    @PatchMapping("/periods/{id}/close")
    @PreAuthorize("hasAuthority('accounting.periods.close')")
    public ApiResponse<AccountingPeriodResponse> closePeriod(
            @PathVariable UUID id,
            @Valid @RequestBody CloseAccountingPeriodRequest request
    ) {
        return ApiResponse.success("Accounting period closed successfully", closeAccountingPeriodUseCase.execute(id, request));
    }

    @GetMapping("/journal-entries")
    @PreAuthorize("hasAuthority('accounting.journal.read')")
    public ApiResponse<Page<JournalEntryResponse>> listJournalEntries(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success("Journal entries retrieved successfully", PaginationSupport.page(listJournalEntriesUseCase.execute(), pageable));
    }

    @GetMapping("/journal-entries/{id}")
    @PreAuthorize("hasAuthority('accounting.journal.detail')")
    public ApiResponse<JournalEntryResponse> getJournalEntryById(@PathVariable UUID id) {
        return ApiResponse.success("Journal entry retrieved successfully", getJournalEntryByIdUseCase.execute(id));
    }
}
