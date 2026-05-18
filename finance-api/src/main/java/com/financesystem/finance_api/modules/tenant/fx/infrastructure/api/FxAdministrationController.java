package com.financesystem.finance_api.modules.tenant.fx.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.tenant.fx.application.dto.*;
import com.financesystem.finance_api.modules.tenant.fx.application.service.FxAdministrationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/fx")
@SecurityRequirement(name = "bearerAuth")
// @PreAuthorize("hasRole('OWNER_ADMIN')")
public class FxAdministrationController {

    private final FxAdministrationService fxAdministrationService;

    public FxAdministrationController(FxAdministrationService fxAdministrationService) {
        this.fxAdministrationService = fxAdministrationService;
    }

    @GetMapping("/rates")
    public ApiResponse<List<FxExchangeRateResponse>> listRates() {
        return ApiResponse.success(
                "FX rates retrieved successfully",
                fxAdministrationService.listRates()
        );
    }

    @GetMapping("/rates/{id}")
    @PreAuthorize("hasAuthority('fx.rates.read')")
    public ApiResponse<FxExchangeRateResponse> getRate(@PathVariable UUID id) {
        return ApiResponse.success(
                "FX rate retrieved successfully",
                fxAdministrationService.getRate(id)
        );
    }

    @PostMapping("/rates")
    public ApiResponse<FxExchangeRateResponse> createRate(@Valid @RequestBody CreateFxExchangeRateRequest request) {
        return ApiResponse.success(
                "FX rate created successfully",
                fxAdministrationService.createRate(request)
        );
    }

    @PutMapping("/rates/{id}")
    public ApiResponse<FxExchangeRateResponse> updateRate(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateFxExchangeRateRequest request
    ) {
        return ApiResponse.success(
                "FX rate updated successfully",
                fxAdministrationService.updateRate(id, request)
        );
    }

    @DeleteMapping("/rates/{id}")
    public ApiResponse<Void> deleteRate(@PathVariable UUID id) {
        fxAdministrationService.deleteRate(id);
        return ApiResponse.success("FX rate deleted successfully", null);
    }

    @GetMapping("/fees")
    public ApiResponse<List<OperationFeeResponse>> listFees() {
        return ApiResponse.success(
                "FX fees retrieved successfully",
                fxAdministrationService.listFees()
        );
    }

    @GetMapping("/fees/{id}")
    @PreAuthorize("hasAuthority('fx.fees.read')")
    public ApiResponse<OperationFeeResponse> getFee(@PathVariable UUID id) {
        return ApiResponse.success(
                "FX fee retrieved successfully",
                fxAdministrationService.getFee(id)
        );
    }

    @PostMapping("/fees")
    public ApiResponse<OperationFeeResponse> createFee(@Valid @RequestBody CreateOperationFeeRequest request) {
        return ApiResponse.success(
                "FX fee created successfully",
                fxAdministrationService.createFee(request)
        );
    }

    @PutMapping("/fees/{id}")
    public ApiResponse<OperationFeeResponse> updateFee(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOperationFeeRequest request
    ) {
        return ApiResponse.success(
                "FX fee updated successfully",
                fxAdministrationService.updateFee(id, request)
        );
    }

    @DeleteMapping("/fees/{id}")
    public ApiResponse<Void> deleteFee(@PathVariable UUID id) {
        fxAdministrationService.deleteFee(id);
        return ApiResponse.success("FX fee deleted successfully", null);
    }
}
