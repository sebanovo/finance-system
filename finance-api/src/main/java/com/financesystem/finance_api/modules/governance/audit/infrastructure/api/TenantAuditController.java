package com.financesystem.finance_api.modules.governance.audit.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.governance.audit.application.dto.AuditEventResponse;
import com.financesystem.finance_api.modules.governance.audit.application.usecase.ListTenantAuditEventsUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit/events")
@SecurityRequirement(name = "bearerAuth")
public class TenantAuditController {

    private final ListTenantAuditEventsUseCase listTenantAuditEventsUseCase;

    public TenantAuditController(ListTenantAuditEventsUseCase listTenantAuditEventsUseCase) {
        this.listTenantAuditEventsUseCase = listTenantAuditEventsUseCase;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<AuditEventResponse>> listTenantAuditEvents(
            @RequestParam(defaultValue = "50") int limit
    ) {
        return ApiResponse.success(
                "Tenant audit events retrieved successfully",
                listTenantAuditEventsUseCase.execute(limit)
        );
    }
}