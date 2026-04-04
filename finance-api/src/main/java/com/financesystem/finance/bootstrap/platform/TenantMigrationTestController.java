package com.financesystem.finance.bootstrap.platform;

import com.financesystem.finance.common.response.ApiResponse;
import com.financesystem.finance.common.tenancy.migration.TenantSchemaMigrationService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/public/dev-tenancy")
@Profile("dev")
@Hidden
public class TenantMigrationTestController {

    private final TenantSchemaMigrationService tenantSchemaMigrationService;

    public TenantMigrationTestController(TenantSchemaMigrationService tenantSchemaMigrationService) {
        this.tenantSchemaMigrationService = tenantSchemaMigrationService;
    }

    @PostMapping("/migrate-schema/{schemaName}")
    public ApiResponse<Map<String, String>> migrateTenantSchema(@PathVariable String schemaName) {
        tenantSchemaMigrationService.migrateSchema(schemaName);

        return ApiResponse.success(
                "Tenant schema migrated successfully",
                Map.of("schemaName", schemaName)
        );
    }
}