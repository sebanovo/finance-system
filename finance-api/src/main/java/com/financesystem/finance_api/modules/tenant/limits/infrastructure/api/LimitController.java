package com.financesystem.finance_api.modules.tenant.limits.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.tenant.limits.application.dto.*;
import com.financesystem.finance_api.modules.tenant.limits.application.usecase.create.CreateLimitRuleUseCase;
import com.financesystem.finance_api.modules.tenant.limits.application.usecase.delete.DeleteLimitRuleUseCase;
import com.financesystem.finance_api.modules.tenant.limits.application.usecase.evaluate.EvaluateLimitUseCase;
import com.financesystem.finance_api.modules.tenant.limits.application.usecase.query.GetLimitRuleByIdUseCase;
import com.financesystem.finance_api.modules.tenant.limits.application.usecase.query.ListLimitRulesUseCase;
import com.financesystem.finance_api.modules.tenant.limits.application.usecase.update.UpdateLimitRuleUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/limits")
@SecurityRequirement(name = "bearerAuth")
public class LimitController {

    private final ListLimitRulesUseCase listLimitRulesUseCase;
    private final GetLimitRuleByIdUseCase getLimitRuleByIdUseCase;
    private final CreateLimitRuleUseCase createLimitRuleUseCase;
    private final UpdateLimitRuleUseCase updateLimitRuleUseCase;
    private final DeleteLimitRuleUseCase deleteLimitRuleUseCase;
    private final EvaluateLimitUseCase evaluateLimitUseCase;

    public LimitController(
            ListLimitRulesUseCase listLimitRulesUseCase,
            GetLimitRuleByIdUseCase getLimitRuleByIdUseCase,
            CreateLimitRuleUseCase createLimitRuleUseCase,
            UpdateLimitRuleUseCase updateLimitRuleUseCase,
            DeleteLimitRuleUseCase deleteLimitRuleUseCase,
            EvaluateLimitUseCase evaluateLimitUseCase
    ) {
        this.listLimitRulesUseCase = listLimitRulesUseCase;
        this.getLimitRuleByIdUseCase = getLimitRuleByIdUseCase;
        this.createLimitRuleUseCase = createLimitRuleUseCase;
        this.updateLimitRuleUseCase = updateLimitRuleUseCase;
        this.deleteLimitRuleUseCase = deleteLimitRuleUseCase;
        this.evaluateLimitUseCase = evaluateLimitUseCase;
    }

    @GetMapping("/rules")
    @PreAuthorize("hasAuthority('limits.read')")
    public ApiResponse<List<LimitRuleResponse>> listRules() {
        return ApiResponse.success("Limit rules retrieved successfully", listLimitRulesUseCase.execute());
    }

    @GetMapping("/rules/{id}")
    @PreAuthorize("hasAuthority('limits.detail')")
    public ApiResponse<LimitRuleResponse> getRuleById(@PathVariable UUID id) {
        return ApiResponse.success("Limit rule retrieved successfully", getLimitRuleByIdUseCase.execute(id));
    }

    @PostMapping("/rules")
    @PreAuthorize("hasAuthority('limits.create')")
    public ApiResponse<LimitRuleResponse> createRule(@Valid @RequestBody CreateLimitRuleRequest request) {
        return ApiResponse.success("Limit rule created successfully", createLimitRuleUseCase.execute(request));
    }

    @PatchMapping("/rules/{id}")
    @PreAuthorize("hasAuthority('limits.update')")
    public ApiResponse<LimitRuleResponse> updateRule(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateLimitRuleRequest request
    ) {
        return ApiResponse.success("Limit rule updated successfully", updateLimitRuleUseCase.execute(id, request));
    }

    @DeleteMapping("/rules/{id}")
    @PreAuthorize("hasAuthority('limits.delete')")
    public ApiResponse<LimitRuleResponse> deleteRule(@PathVariable UUID id) {
        return ApiResponse.success("Limit rule deactivated successfully", deleteLimitRuleUseCase.execute(id));
    }

    @PostMapping("/evaluate")
    @PreAuthorize("hasAuthority('limits.evaluate')")
    public ApiResponse<LimitEvaluationResponse> evaluate(@Valid @RequestBody LimitEvaluationRequest request) {
        return ApiResponse.success("Limit evaluation completed successfully", evaluateLimitUseCase.execute(request));
    }
}
