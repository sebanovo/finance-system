package com.financesystem.finance_api.modules.governance.audit.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.governance.audit.application.dto.AuditEventResponse;
import com.financesystem.finance_api.modules.governance.audit.application.usecase.ListPlatformAuditEventsUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform/audit/events")
@SecurityRequirement(name = "bearerAuth")
public class PlatformAuditController {

    private final ListPlatformAuditEventsUseCase listPlatformAuditEventsUseCase;

    public PlatformAuditController(ListPlatformAuditEventsUseCase listPlatformAuditEventsUseCase) {
        this.listPlatformAuditEventsUseCase = listPlatformAuditEventsUseCase;
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<List<AuditEventResponse>> listPlatformAuditEvents(
            @RequestParam(defaultValue = "50") int limit
    ) {
        return ApiResponse.success(
                "Platform audit events retrieved successfully",
                listPlatformAuditEventsUseCase.execute(limit)
        );
    }
}
