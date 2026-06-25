# Directory Export: /home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/modules/platform

_Generated on 2026-06-25 00:29:46Z_

## Summary

- Source directory: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/modules/platform`
- Output file: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/modules/platform.md`

## Files

### `audit/PlatformAuditPayloads.java`

```java
package com.financesystem.finance_api.modules.platform.audit;

import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenantsettings.domain.model.TenantSetting;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PlatformAuditPayloads {

    private PlatformAuditPayloads() {
    }

    public static Map<String, Object> details(Object... keyValues) {
        if (keyValues == null || keyValues.length == 0) {
            return Map.of();
        }

        if (keyValues.length % 2 != 0) {
            throw new IllegalArgumentException("Audit details must contain key-value pairs");
        }

        Map<String, Object> payload = new LinkedHashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            payload.put(String.valueOf(keyValues[i]), keyValues[i + 1]);
        }

        return payload;
    }

    public static Map<String, Object> tenantState(PlatformTenant tenant) {
        return details(
                "id", tenant.id(),
                "name", tenant.name(),
                "slug", tenant.slug(),
                "schemaName", tenant.schemaName(),
                "status", tenant.status(),
                "planId", tenant.planId(),
                "active", tenant.active(),
                "createdAt", tenant.createdAt(),
                "updatedAt", tenant.updatedAt()
        );
    }

    public static Map<String, Object> planState(PlatformPlan plan) {
        return details(
                "id", plan.id(),
                "code", plan.code(),
                "name", plan.name(),
                "description", plan.description(),
                "maxUsers", plan.maxUsers(),
                "maxRoles", plan.maxRoles(),
                "planType", plan.planType(),
                "trialDays", plan.trialDays(),
                "active", plan.active(),
                "createdAt", plan.createdAt(),
                "updatedAt", plan.updatedAt()
        );
    }

    public static Map<String, Object> subscriptionState(PlatformSubscription subscription) {
        return details(
                "id", subscription.id(),
                "tenantId", subscription.tenantId(),
                "planId", subscription.planId(),
                "status", subscription.status(),
                "trial", subscription.trial(),
                "currentSubscription", subscription.currentSubscription(),
                "startedAt", subscription.startedAt(),
                "expiresAt", subscription.expiresAt(),
                "createdAt", subscription.createdAt(),
                "updatedAt", subscription.updatedAt()
        );
    }

    public static Map<String, Object> settingsState(List<TenantSetting> settings) {
        Map<String, Object> payload = new LinkedHashMap<>();
        if (settings == null) {
            return payload;
        }

        for (TenantSetting setting : settings) {
            payload.put(setting.settingKey(), setting.settingValue());
        }

        return payload;
    }

    public static Map<String, Object> superadminState(PlatformSuperadmin superadmin) {
        return details(
                "id", superadmin.id(),
                "email", superadmin.email(),
                "firstName", superadmin.firstName(),
                "lastName", superadmin.lastName(),
                "active", superadmin.active(),
                "createdAt", superadmin.createdAt(),
                "updatedAt", superadmin.updatedAt()
        );
    }
}

```

### `auth/application/dto/AuthenticatedPlatformSuperadminResponse.java`

```java
package com.financesystem.finance_api.modules.platform.auth.application.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AuthenticatedPlatformSuperadminResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        boolean active,
        List<String> roles,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `auth/application/dto/PlatformAuthTokenResponse.java`

```java
package com.financesystem.finance_api.modules.platform.auth.application.dto;

public record PlatformAuthTokenResponse(
        String tokenType,
        String accessToken,
        String refreshToken,
        long accessExpiresInMs
) {
}

```

### `auth/application/dto/PlatformChangePasswordRequest.java`

```java
package com.financesystem.finance_api.modules.platform.auth.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlatformChangePasswordRequest(
        @NotBlank
        String currentPassword,

        @NotBlank
        @Size(min = 8, max = 100)
        String newPassword
) {
}

```

### `auth/application/dto/PlatformLoginRequest.java`

```java
package com.financesystem.finance_api.modules.platform.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlatformLoginRequest(
        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password
) {
}

```

### `auth/application/dto/PlatformRefreshTokenRequest.java`

```java
package com.financesystem.finance_api.modules.platform.auth.application.dto;

import jakarta.validation.constraints.NotBlank;

public record PlatformRefreshTokenRequest(
        @NotBlank
        String refreshToken
) {
}

```

### `auth/application/usecase/ChangePlatformPasswordUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.auth.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.auth.application.dto.PlatformChangePasswordRequest;
import com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ChangePlatformPasswordUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditTrailService auditTrailService;

    public ChangePlatformPasswordUseCase(
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository,
            PasswordEncoder passwordEncoder,
            AuditTrailService auditTrailService
    ) {
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public void execute(PlatformChangePasswordRequest request) {
        String currentSubject = securityContextFacade.getCurrentSubject();

        PlatformSuperadmin superadmin = platformSuperadminRepository.findByEmail(currentSubject)
                .orElseThrow(() -> new BusinessException("Authenticated platform superadmin was not found"));

        if (!passwordEncoder.matches(request.currentPassword(), superadmin.passwordHash())) {
            throw new BusinessException("Current password is incorrect");
        }

        if (request.currentPassword().equals(request.newPassword())) {
            throw new BusinessException("New password must be different from current password");
        }

        PlatformSuperadmin updated = new PlatformSuperadmin(
                superadmin.id(),
                superadmin.email(),
                passwordEncoder.encode(request.newPassword()),
                superadmin.firstName(),
                superadmin.lastName(),
                superadmin.active(),
                superadmin.createdAt(),
                superadmin.updatedAt()
        );

        platformSuperadminRepository.save(updated);

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.PASSWORD_CHANGED,
                "PLATFORM_SUPERADMIN",
                superadmin.id().toString(),
                PlatformAuditPayloads.details(
                        "email", superadmin.email(),
                        "changedBy", currentSubject
                )
        );
    }
}

```

### `auth/application/usecase/GetCurrentAuthenticatedPlatformSuperadminUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.auth.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.platform.auth.application.dto.AuthenticatedPlatformSuperadminResponse;
import com.financesystem.finance_api.modules.platform.auth.domain.model.PlatformAuthConstants;
import com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentAuthenticatedPlatformSuperadminUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;

    public GetCurrentAuthenticatedPlatformSuperadminUseCase(
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository
    ) {
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
    }

    public AuthenticatedPlatformSuperadminResponse execute() {
        String currentSubject = securityContextFacade.getCurrentSubject();
        String currentTenantSlug = securityContextFacade.getCurrentTenantSlug();

        if (currentSubject == null || currentSubject.isBlank()) {
            throw new AuthenticationFailedException("Authenticated platform subject is not available");
        }

        if (!PlatformAuthConstants.PLATFORM_TENANT_SLUG.equalsIgnoreCase(currentTenantSlug)) {
            throw new AuthenticationFailedException("Current token does not belong to platform auth");
        }

        PlatformSuperadmin superadmin = platformSuperadminRepository.findByEmail(currentSubject)
                .orElseThrow(() -> new AuthenticationFailedException("Authenticated platform superadmin was not found"));

        return new AuthenticatedPlatformSuperadminResponse(
                superadmin.id(),
                superadmin.email(),
                superadmin.firstName(),
                superadmin.lastName(),
                superadmin.active(),
                PlatformAuthConstants.PLATFORM_ROLES,
                superadmin.createdAt(),
                superadmin.updatedAt()
        );
    }
}

```

### `auth/application/usecase/LoginPlatformSuperadminUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.auth.application.usecase;

import com.financesystem.finance_api.common.security.JwtProperties;
import com.financesystem.finance_api.common.security.jwt.JwtTokenService;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.platform.auth.application.dto.PlatformAuthTokenResponse;
import com.financesystem.finance_api.modules.platform.auth.application.dto.PlatformLoginRequest;
import com.financesystem.finance_api.modules.platform.auth.domain.model.PlatformAuthConstants;
import com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginPlatformSuperadminUseCase {

    private final PlatformSuperadminRepository platformSuperadminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final AuditTrailService auditTrailService;

    public LoginPlatformSuperadminUseCase(
            PlatformSuperadminRepository platformSuperadminRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties,
            AuditTrailService auditTrailService
    ) {
        this.platformSuperadminRepository = platformSuperadminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
        this.auditTrailService = auditTrailService;
    }

    public PlatformAuthTokenResponse execute(PlatformLoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        PlatformSuperadmin superadmin = platformSuperadminRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new AuthenticationFailedException("Invalid credentials"));

        if (!superadmin.active()) {
            throw new AuthenticationFailedException("Platform superadmin is inactive");
        }

        if (!passwordEncoder.matches(request.password(), superadmin.passwordHash())) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        String accessToken = jwtTokenService.generateAccessToken(
                superadmin.email(),
                superadmin.email(),
                fullName(superadmin.firstName(), superadmin.lastName()),
                PlatformAuthConstants.PLATFORM_TENANT_SLUG,
                PlatformAuthConstants.PLATFORM_ROLES,
                List.of()
        );

        String refreshToken = jwtTokenService.generateRefreshToken(
                superadmin.email(),
                PlatformAuthConstants.PLATFORM_TENANT_SLUG
        );

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.LOGIN,
                "PLATFORM_SUPERADMIN",
                superadmin.id().toString(),
                PlatformAuditPayloads.details(
                        "email", superadmin.email(),
                        "fullName", fullName(superadmin.firstName(), superadmin.lastName()),
                        "tenantSlug", PlatformAuthConstants.PLATFORM_TENANT_SLUG
                )
        );

        return new PlatformAuthTokenResponse(
                "Bearer",
                accessToken,
                refreshToken,
                jwtProperties.getAccessExpirationMs()
        );
    }

    private String fullName(String firstName, String lastName) {
        StringBuilder builder = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            builder.append(firstName.trim());
        }
        if (lastName != null && !lastName.isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(lastName.trim());
        }
        String value = builder.toString().trim();
        return value.isEmpty() ? null : value;
    }
}

```

### `auth/application/usecase/LogoutPlatformSuperadminUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.auth.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import org.springframework.stereotype.Service;

@Service
public class LogoutPlatformSuperadminUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final AuditTrailService auditTrailService;

    public LogoutPlatformSuperadminUseCase(
            SecurityContextFacade securityContextFacade,
            AuditTrailService auditTrailService
    ) {
        this.securityContextFacade = securityContextFacade;
        this.auditTrailService = auditTrailService;
    }

    public void execute() {
        String currentSubject = securityContextFacade.getCurrentSubject();

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.LOGOUT,
                "PLATFORM_SUPERADMIN",
                currentSubject,
                PlatformAuditPayloads.details(
                        "subject", currentSubject,
                        "email", securityContextFacade.getCurrentEmail()
                )
        );
    }
}

```

### `auth/application/usecase/RefreshPlatformTokenUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.auth.application.usecase;

import com.financesystem.finance_api.common.security.JwtProperties;
import com.financesystem.finance_api.common.security.jwt.JwtClaimNames;
import com.financesystem.finance_api.common.security.jwt.JwtTokenService;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import com.financesystem.finance_api.modules.platform.auth.application.dto.PlatformAuthTokenResponse;
import com.financesystem.finance_api.modules.platform.auth.application.dto.PlatformRefreshTokenRequest;
import com.financesystem.finance_api.modules.platform.auth.domain.model.PlatformAuthConstants;
import com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefreshPlatformTokenUseCase {

    private final PlatformSuperadminRepository platformSuperadminRepository;
    private final JwtTokenService jwtTokenService;
    private final JwtProperties jwtProperties;
    private final AuditTrailService auditTrailService;

    public RefreshPlatformTokenUseCase(
            PlatformSuperadminRepository platformSuperadminRepository,
            JwtTokenService jwtTokenService,
            JwtProperties jwtProperties,
            AuditTrailService auditTrailService
    ) {
        this.platformSuperadminRepository = platformSuperadminRepository;
        this.jwtTokenService = jwtTokenService;
        this.jwtProperties = jwtProperties;
        this.auditTrailService = auditTrailService;
    }

    public PlatformAuthTokenResponse execute(PlatformRefreshTokenRequest request) {
        Claims claims = jwtTokenService.parseRefreshToken(request.refreshToken());

        String subject = claims.getSubject();
        String tokenTenant = claims.get(JwtClaimNames.TENANT, String.class);

        if (subject == null || subject.isBlank()) {
            throw new AuthenticationFailedException("Invalid refresh token");
        }

        if (!PlatformAuthConstants.PLATFORM_TENANT_SLUG.equalsIgnoreCase(tokenTenant)) {
            throw new AuthenticationFailedException("Refresh token does not belong to platform auth");
        }

        PlatformSuperadmin superadmin = platformSuperadminRepository.findByEmail(subject)
                .orElseThrow(() -> new AuthenticationFailedException("Platform superadmin not found"));

        if (!superadmin.active()) {
            throw new AuthenticationFailedException("Platform superadmin is inactive");
        }

        String accessToken = jwtTokenService.generateAccessToken(
                superadmin.email(),
                superadmin.email(),
                fullName(superadmin.firstName(), superadmin.lastName()),
                PlatformAuthConstants.PLATFORM_TENANT_SLUG,
                PlatformAuthConstants.PLATFORM_ROLES,
                List.of()
        );

        String refreshToken = jwtTokenService.generateRefreshToken(
                superadmin.email(),
                PlatformAuthConstants.PLATFORM_TENANT_SLUG
        );

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.TOKEN_REFRESHED,
                "PLATFORM_SUPERADMIN",
                superadmin.id().toString(),
                PlatformAuditPayloads.details(
                        "email", superadmin.email(),
                        "tenantSlug", PlatformAuthConstants.PLATFORM_TENANT_SLUG
                )
        );

        return new PlatformAuthTokenResponse(
                "Bearer",
                accessToken,
                refreshToken,
                jwtProperties.getAccessExpirationMs()
        );
    }

    private String fullName(String firstName, String lastName) {
        StringBuilder builder = new StringBuilder();
        if (firstName != null && !firstName.isBlank()) {
            builder.append(firstName.trim());
        }
        if (lastName != null && !lastName.isBlank()) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(lastName.trim());
        }
        String value = builder.toString().trim();
        return value.isEmpty() ? null : value;
    }
}

```

### `auth/domain/model/PlatformAuthConstants.java`

```java
package com.financesystem.finance_api.modules.platform.auth.domain.model;

import java.util.List;

public final class PlatformAuthConstants {

    public static final String PLATFORM_TENANT_SLUG = "platform";
    public static final List<String> PLATFORM_ROLES = List.of("SUPERADMIN", "ADMIN");

    private PlatformAuthConstants() {
    }
}

```

### `auth/infrastructure/api/PlatformAuthController.java`

```java
package com.financesystem.finance_api.modules.platform.auth.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.auth.application.dto.*;
import com.financesystem.finance_api.modules.platform.auth.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/platform/auth")
public class PlatformAuthController {

    private final LoginPlatformSuperadminUseCase loginPlatformSuperadminUseCase;
    private final RefreshPlatformTokenUseCase refreshPlatformTokenUseCase;
    private final GetCurrentAuthenticatedPlatformSuperadminUseCase getCurrentAuthenticatedPlatformSuperadminUseCase;
    private final ChangePlatformPasswordUseCase changePlatformPasswordUseCase;
    private final LogoutPlatformSuperadminUseCase logoutPlatformSuperadminUseCase;

    public PlatformAuthController(
            LoginPlatformSuperadminUseCase loginPlatformSuperadminUseCase,
            RefreshPlatformTokenUseCase refreshPlatformTokenUseCase,
            GetCurrentAuthenticatedPlatformSuperadminUseCase getCurrentAuthenticatedPlatformSuperadminUseCase,
            ChangePlatformPasswordUseCase changePlatformPasswordUseCase,
            LogoutPlatformSuperadminUseCase logoutPlatformSuperadminUseCase
    ) {
        this.loginPlatformSuperadminUseCase = loginPlatformSuperadminUseCase;
        this.refreshPlatformTokenUseCase = refreshPlatformTokenUseCase;
        this.getCurrentAuthenticatedPlatformSuperadminUseCase = getCurrentAuthenticatedPlatformSuperadminUseCase;
        this.changePlatformPasswordUseCase = changePlatformPasswordUseCase;
        this.logoutPlatformSuperadminUseCase = logoutPlatformSuperadminUseCase;
    }

    @PostMapping("/login")
    public ApiResponse<PlatformAuthTokenResponse> login(@Valid @RequestBody PlatformLoginRequest request) {
        return ApiResponse.success(
                "Platform login successful",
                loginPlatformSuperadminUseCase.execute(request)
        );
    }

    @PostMapping("/refresh")
    public ApiResponse<PlatformAuthTokenResponse> refresh(@Valid @RequestBody PlatformRefreshTokenRequest request) {
        return ApiResponse.success(
                "Platform token refreshed successfully",
                refreshPlatformTokenUseCase.execute(request)
        );
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@authorizationGuards.isPlatformAuthenticated()")
    public ApiResponse<AuthenticatedPlatformSuperadminResponse> me() {
        return ApiResponse.success(
                "Authenticated platform superadmin retrieved successfully",
                getCurrentAuthenticatedPlatformSuperadminUseCase.execute()
        );
    }

    @PostMapping("/change-password")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@authorizationGuards.isPlatformAuthenticated()")
    public ApiResponse<Map<String, String>> changePassword(
            @Valid @RequestBody PlatformChangePasswordRequest request
    ) {
        changePlatformPasswordUseCase.execute(request);

        return ApiResponse.success(
                "Platform password changed successfully",
                Map.of("status", "ok")
        );
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("@authorizationGuards.isPlatformAuthenticated()")
    public ApiResponse<Map<String, String>> logout() {
        logoutPlatformSuperadminUseCase.execute();

        return ApiResponse.success(
                "Platform logout successful",
                Map.of("status", "ok")
        );
    }
}

```

### `dashboard/application/dto/SuperadminDashboardResponse.java`

```java
package com.financesystem.finance_api.modules.platform.dashboard.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SuperadminDashboardResponse(
        Metadata metadata,
        Filters filters,
        Comparisons comparisons,
        Summary summary,
        TenantsSection tenants,
        SubscriptionsSection subscriptions,
        PlansSection plans,
        UsersSection users,
        AuditSection audit,
        List<AlertItem> alerts,
        List<InsightItem> insights,
        List<ActivityItem> recentActivity
) {

    public record Metadata(
            OffsetDateTime generatedAt,
            String timezone,
            String dataCompleteness
    ) {
    }

    public record Filters(
            String period,
            Integer year,
            Integer month,
            LocalDate date,
            OffsetDateTime from,
            OffsetDateTime to
    ) {
    }

    public record Comparisons(
            PeriodRange previousPeriod,
            ComparisonSummary summary
    ) {
    }

    public record PeriodRange(
            OffsetDateTime from,
            OffsetDateTime to
    ) {
    }

    public record ComparisonSummary(
            BigDecimal tenantsChangePercent,
            BigDecimal activeSubscriptionsChangePercent,
            BigDecimal newTenantsChangePercent,
            BigDecimal expiredSubscriptionsChangePercent
    ) {
    }

    public record Summary(
            TenantsSummary tenants,
            SubscriptionsSummary subscriptions,
            PlansSummary plans,
            UsersSummary users,
            AuditSummary audit,
            SystemSummary system
    ) {
    }

    public record TenantsSummary(
            long total,
            long active,
            long inactive,
            long newThisPeriod
    ) {
    }

    public record SubscriptionsSummary(
            long active,
            long trial,
            long expired,
            long cancelled,
            long expiringSoon
    ) {
    }

    public record PlansSummary(
            long total,
            long active
    ) {
    }

    public record UsersSummary(
            long total,
            long active,
            long inactive,
            long platformAdmins
    ) {
    }

    public record AuditSummary(
            long events,
            long failedEvents
    ) {
    }

    public record SystemSummary(
            long registeredTenantSchemas,
            long activeTenantSchemas,
            long inactiveTenantSchemas
    ) {
    }

    public record TenantsSection(
            List<TenantStatusItem> byStatus,
            List<TenantItem> recent,
            List<TenantItem> inactive
    ) {
    }

    public record TenantStatusItem(
            String status,
            String label,
            long total
    ) {
    }

    public record TenantItem(
            UUID id,
            String name,
            String slug,
            String schemaName,
            String status,
            String planCode,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
    }

    public record SubscriptionsSection(
            List<SubscriptionStatusItem> byStatus,
            List<SubscriptionPlanItem> byPlan,
            SectionBucket expiringSoon,
            SectionBucket expired
    ) {
    }

    public record SubscriptionStatusItem(
            String status,
            long total
    ) {
    }

    public record SubscriptionPlanItem(
            String planCode,
            long total
    ) {
    }

    public record SectionBucket(
            long total,
            List<SubscriptionItem> items
    ) {
    }

    public record SubscriptionItem(
            UUID id,
            UUID tenantId,
            String tenantName,
            String tenantSlug,
            String planCode,
            String status,
            OffsetDateTime expiresAt,
            OffsetDateTime expiredAt
    ) {
    }

    public record PlansSection(
            List<PlanItem> items
    ) {
    }

    public record PlanItem(
            String code,
            String name,
            String type,
            boolean active,
            int maxUsers,
            int maxRoles,
            Integer trialDays,
            long tenants
    ) {
    }

    public record UsersSection(
            long total,
            long active,
            long inactive,
            long platformAdmins
    ) {
    }

    public record AuditSection(
            List<AuditEventItem> byEventType,
            SectionBucketRecent recent
    ) {
    }

    public record AuditEventItem(
            String eventType,
            long total
    ) {
    }

    public record SectionBucketRecent(
            long total,
            List<AuditItem> items
    ) {
    }

    public record AuditItem(
            UUID id,
            String actorEmail,
            String eventType,
            String resourceType,
            String resourceId,
            String outcome,
            OffsetDateTime createdAt
    ) {
    }

    public record AlertItem(
            String id,
            String type,
            String severity,
            String title,
            String description,
            OffsetDateTime createdAt
    ) {
    }

    public record InsightItem(
            String type,
            String severity,
            String title,
            String description
    ) {
    }

    public record ActivityItem(
            String id,
            String type,
            String title,
            String description,
            String actorName,
            OffsetDateTime createdAt
    ) {
    }
}

```

### `dashboard/application/dto/TenantSummaryResponse.java`

```java
package com.financesystem.finance_api.modules.platform.dashboard.application.dto;

public record TenantSummaryResponse(
        long totalUsers,
        int maxUsers,
        String activePlan,
        long trialDaysLeft
) {
}

```

### `dashboard/application/usecase/GetSuperadminDashboardUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.dashboard.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.domain.model.PlatformAuditEvent;
import com.financesystem.finance_api.modules.governance.audit.domain.repository.PlatformAuditEventRepository;
import com.financesystem.finance_api.modules.platform.dashboard.application.dto.SuperadminDashboardResponse;
import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscriptionStatus;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenantStatus;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class GetSuperadminDashboardUseCase {

    private static final ZoneId DASHBOARD_ZONE = ZoneId.of("America/La_Paz");
    private static final int MAX_RECENT_ITEMS = 5;
    private static final int MAX_ALERT_ITEMS = 5;

    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformSubscriptionRepository platformSubscriptionRepository;
    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformSuperadminRepository platformSuperadminRepository;
    private final PlatformAuditEventRepository platformAuditEventRepository;

    public GetSuperadminDashboardUseCase(
            PlatformTenantRepository platformTenantRepository,
            PlatformSubscriptionRepository platformSubscriptionRepository,
            PlatformPlanRepository platformPlanRepository,
            PlatformSuperadminRepository platformSuperadminRepository,
            PlatformAuditEventRepository platformAuditEventRepository
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.platformSubscriptionRepository = platformSubscriptionRepository;
        this.platformPlanRepository = platformPlanRepository;
        this.platformSuperadminRepository = platformSuperadminRepository;
        this.platformAuditEventRepository = platformAuditEventRepository;
    }

    @Transactional(readOnly = true)
    public SuperadminDashboardResponse execute() {
        OffsetDateTime generatedAt = OffsetDateTime.now(DASHBOARD_ZONE);
        PeriodWindow currentMonth = currentMonthWindow(generatedAt);
        PeriodWindow previousMonth = previousMonthWindow(generatedAt);

        List<PlatformTenant> tenants = platformTenantRepository.findAll();
        List<PlatformSubscription> subscriptions = platformSubscriptionRepository.findAll();
        List<PlatformPlan> plans = platformPlanRepository.findAll();
        List<com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin> platformUsers = platformSuperadminRepository.findAll();
        List<PlatformAuditEvent> auditEvents = platformAuditEventRepository.findAll();
        List<PlatformAuditEvent> recentAudit = platformAuditEventRepository.findRecent(MAX_RECENT_ITEMS);
        long platformAdmins = platformUsers.size();

        List<PlatformTenant> recentTenants = tenants.stream()
                .sorted(Comparator.comparing(PlatformTenant::createdAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(MAX_RECENT_ITEMS)
                .toList();

        List<PlatformTenant> inactiveTenants = tenants.stream()
                .filter(tenant -> tenant.status() == PlatformTenantStatus.INACTIVE)
                .sorted(Comparator.comparing(PlatformTenant::updatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(MAX_RECENT_ITEMS)
                .toList();

        long totalTenants = tenants.size();
        long activeTenants = tenants.stream().filter(tenant -> tenant.status() == PlatformTenantStatus.ACTIVE).count();
        long inactiveTenantsCount = totalTenants - activeTenants;
        long newTenantsThisPeriod = countCreatedInWindow(tenants, currentMonth);
        long previousNewTenants = countCreatedInWindow(tenants, previousMonth);

        long activeSubscriptions = subscriptions.stream()
                .filter(subscription -> subscription.status() == PlatformSubscriptionStatus.ACTIVE)
                .count();
        long trialSubscriptions = subscriptions.stream()
                .filter(subscription -> subscription.status() == PlatformSubscriptionStatus.TRIAL)
                .count();
        long expiredSubscriptions = subscriptions.stream()
                .filter(subscription -> subscription.status() == PlatformSubscriptionStatus.EXPIRED)
                .count();
        long cancelledSubscriptions = subscriptions.stream()
                .filter(subscription -> subscription.status() == PlatformSubscriptionStatus.CANCELLED)
                .count();
        java.time.Instant nowInstant = generatedAt.toInstant();
        List<PlatformSubscription> expiringSoonSubscriptions = subscriptions.stream()
                .filter(subscription -> subscription.expiresAt() != null)
                .filter(subscription -> !subscription.expiresAt().isBefore(nowInstant))
                .filter(subscription -> !subscription.expiresAt().isAfter(nowInstant.plus(14, ChronoUnit.DAYS)))
                .sorted(Comparator.comparing(PlatformSubscription::expiresAt))
                .limit(MAX_RECENT_ITEMS)
                .toList();
        List<PlatformSubscription> expiredSoonSubscriptions = subscriptions.stream()
                .filter(subscription -> subscription.status() == PlatformSubscriptionStatus.EXPIRED)
                .sorted(Comparator.comparing(PlatformSubscription::updatedAt, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .limit(MAX_RECENT_ITEMS)
                .toList();

        long totalPlans = plans.size();
        long activePlans = plans.stream().filter(PlatformPlan::active).count();
        Map<String, Long> tenantsByPlan = countTenantsByPlan(tenants, plans);

        long totalUsers = platformUsers.size();
        long activeUsers = platformUsers.stream().filter(com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin::active).count();
        long inactiveUsers = totalUsers - activeUsers;

        long auditCount = platformAuditEventRepository.count();
        long failedAuditCount = auditEvents.stream()
                .filter(event -> event.outcome() != null && event.outcome().equalsIgnoreCase("FAILED"))
                .count();

        PeriodRangeSummary comparison = new PeriodRangeSummary(
                countCreatedInWindow(tenants, previousMonth),
                countActiveSubscriptionsInWindow(subscriptions, previousMonth),
                countCreatedInWindow(tenants, previousMonth),
                countExpiredSubscriptionsInWindow(subscriptions, previousMonth)
        );

        List<SuperadminDashboardResponse.AlertItem> alerts = buildAlerts(
                inactiveTenantsCount,
                expiringSoonSubscriptions.size(),
                expiredSubscriptions,
                failedAuditCount,
                activeTenants,
                totalTenants,
                generatedAt
        );

        List<SuperadminDashboardResponse.InsightItem> insights = buildInsights(
                activeTenants,
                inactiveTenantsCount,
                activeSubscriptions,
                trialSubscriptions,
                activePlans,
                totalPlans
        );

        return new SuperadminDashboardResponse(
                new SuperadminDashboardResponse.Metadata(
                        generatedAt,
                        DASHBOARD_ZONE.getId(),
                        "COMPLETE"
                ),
                buildFilters(currentMonth),
                new SuperadminDashboardResponse.Comparisons(
                        new SuperadminDashboardResponse.PeriodRange(previousMonth.from(), previousMonth.to()),
                        new SuperadminDashboardResponse.ComparisonSummary(
                                percentChange(totalTenants, tenants.size()),
                                percentChange(activeSubscriptions, countActiveSubscriptionsInWindow(subscriptions, previousMonth)),
                                percentChange(newTenantsThisPeriod, previousNewTenants),
                                percentChange(expiredSubscriptions, countExpiredSubscriptionsInWindow(subscriptions, previousMonth))
                        )
                ),
                new SuperadminDashboardResponse.Summary(
                        new SuperadminDashboardResponse.TenantsSummary(
                                totalTenants,
                                activeTenants,
                                inactiveTenantsCount,
                                newTenantsThisPeriod
                        ),
                        new SuperadminDashboardResponse.SubscriptionsSummary(
                                activeSubscriptions,
                                trialSubscriptions,
                                expiredSubscriptions,
                                cancelledSubscriptions,
                                expiringSoonSubscriptions.size()
                        ),
                        new SuperadminDashboardResponse.PlansSummary(
                                totalPlans,
                                activePlans
                        ),
                        new SuperadminDashboardResponse.UsersSummary(
                                totalUsers,
                                activeUsers,
                                inactiveUsers,
                                platformAdmins
                        ),
                        new SuperadminDashboardResponse.AuditSummary(
                                auditCount,
                                failedAuditCount
                        ),
                        new SuperadminDashboardResponse.SystemSummary(
                                totalTenants,
                                activeTenants,
                                inactiveTenantsCount
                        )
                ),
                new SuperadminDashboardResponse.TenantsSection(
                        List.of(
                                new SuperadminDashboardResponse.TenantStatusItem("ACTIVE", "Activos", activeTenants),
                                new SuperadminDashboardResponse.TenantStatusItem("INACTIVE", "Inactivos", inactiveTenantsCount)
                        ),
                        recentTenants.stream().map(this::toTenantItem).toList(),
                        inactiveTenants.stream().map(this::toTenantItem).toList()
                ),
                new SuperadminDashboardResponse.SubscriptionsSection(
                        List.of(
                                new SuperadminDashboardResponse.SubscriptionStatusItem("ACTIVE", activeSubscriptions),
                                new SuperadminDashboardResponse.SubscriptionStatusItem("TRIAL", trialSubscriptions),
                                new SuperadminDashboardResponse.SubscriptionStatusItem("EXPIRED", expiredSubscriptions),
                                new SuperadminDashboardResponse.SubscriptionStatusItem("CANCELLED", cancelledSubscriptions)
                        ),
                        plans.stream()
                                .sorted(Comparator.comparing(PlatformPlan::code))
                                .map(plan -> new SuperadminDashboardResponse.SubscriptionPlanItem(
                                        plan.code(),
                                        tenantsByPlan.getOrDefault(plan.code(), 0L)
                                ))
                                .toList(),
                        new SuperadminDashboardResponse.SectionBucket(
                                expiringSoonSubscriptions.size(),
                                expiringSoonSubscriptions.stream().map(this::toSubscriptionItem).toList()
                        ),
                        new SuperadminDashboardResponse.SectionBucket(
                                expiredSoonSubscriptions.size(),
                                expiredSoonSubscriptions.stream().map(this::toSubscriptionItem).toList()
                        )
                ),
                new SuperadminDashboardResponse.PlansSection(
                        plans.stream()
                                .sorted(Comparator.comparing(PlatformPlan::code))
                                .map(plan -> new SuperadminDashboardResponse.PlanItem(
                                        plan.code(),
                                        plan.name(),
                                        plan.planType(),
                                        plan.active(),
                                        plan.maxUsers(),
                                        plan.maxRoles(),
                                        plan.trialDays(),
                                        tenantsByPlan.getOrDefault(plan.code(), 0L)
                                ))
                                .toList()
                ),
                new SuperadminDashboardResponse.UsersSection(
                        totalUsers,
                        activeUsers,
                        inactiveUsers,
                        platformAdmins
                ),
                new SuperadminDashboardResponse.AuditSection(
                        buildAuditEventTypes(auditEvents),
                        new SuperadminDashboardResponse.SectionBucketRecent(
                                recentAudit.size(),
                                recentAudit.stream().map(this::toAuditItem).toList()
                        )
                ),
                alerts,
                insights,
                recentAudit.stream().map(this::toActivityItem).toList()
        );
    }

    private SuperadminDashboardResponse.Filters buildFilters(PeriodWindow currentMonth) {
        return new SuperadminDashboardResponse.Filters(
                "MONTH",
                currentMonth.from().getYear(),
                currentMonth.from().getMonthValue(),
                null,
                currentMonth.from(),
                currentMonth.to()
        );
    }

    private long countCreatedInWindow(List<PlatformTenant> tenants, PeriodWindow window) {
        return tenants.stream()
                .filter(tenant -> isInWindow(tenant.createdAt(), window))
                .count();
    }

    private long countActiveSubscriptionsInWindow(List<PlatformSubscription> subscriptions, PeriodWindow window) {
        return subscriptions.stream()
                .filter(subscription -> subscription.status() == PlatformSubscriptionStatus.ACTIVE)
                .filter(subscription -> isInWindow(subscription.startedAt(), window))
                .count();
    }

    private long countExpiredSubscriptionsInWindow(List<PlatformSubscription> subscriptions, PeriodWindow window) {
        return subscriptions.stream()
                .filter(subscription -> subscription.status() == PlatformSubscriptionStatus.EXPIRED)
                .filter(subscription -> isInWindow(subscription.updatedAt(), window))
                .count();
    }

    private Map<String, Long> countTenantsByPlan(List<PlatformTenant> tenants, List<PlatformPlan> plans) {
        Map<String, String> planCodesById = plans.stream()
                .filter(Objects::nonNull)
                .filter(plan -> plan.id() != null && plan.code() != null)
                .collect(LinkedHashMap::new, (map, plan) -> map.put(plan.id().toString(), plan.code()), Map::putAll);
        Map<String, Long> totals = new LinkedHashMap<>();
        for (PlatformTenant tenant : tenants) {
            String planCode = planCodesById.get(tenant.planId() != null ? tenant.planId().toString() : null);
            if (planCode == null) {
                planCode = "UNKNOWN";
            }
            totals.merge(planCode, 1L, Long::sum);
        }
        return totals;
    }

    private List<SuperadminDashboardResponse.AuditEventItem> buildAuditEventTypes(List<PlatformAuditEvent> auditEvents) {
        Map<String, Long> totals = new LinkedHashMap<>();
        for (PlatformAuditEvent event : auditEvents) {
            String eventType = safeText(event.eventType());
            if (eventType == null) {
                eventType = "UNKNOWN";
            }
            totals.merge(eventType, 1L, Long::sum);
        }
        return totals.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(8)
                .map(entry -> new SuperadminDashboardResponse.AuditEventItem(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<SuperadminDashboardResponse.AlertItem> buildAlerts(
            long inactiveTenants,
            long expiringSoonSubscriptions,
            long expiredSubscriptions,
            long failedAuditEvents,
            long activeTenants,
            long totalTenants,
            OffsetDateTime generatedAt
    ) {
        List<SuperadminDashboardResponse.AlertItem> alerts = new java.util.ArrayList<>();
        if (inactiveTenants > 0) {
            alerts.add(new SuperadminDashboardResponse.AlertItem(
                    "INACTIVE_TENANTS",
                    "WARN",
                    "Tenants inactivos",
                    "Hay " + inactiveTenants + " tenants inactivos que requieren revisión.",
                    "Revisar tenants",
                    generatedAt
            ));
        }
        if (expiringSoonSubscriptions > 0) {
            alerts.add(new SuperadminDashboardResponse.AlertItem(
                    "SUBSCRIPTIONS_EXPIRING_SOON",
                    "MEDIUM",
                    "Suscripciones próximas a vencer",
                    "Hay " + expiringSoonSubscriptions + " suscripciones que vencen en los próximos días.",
                    "Revisar suscripciones",
                    generatedAt
            ));
        }
        if (expiredSubscriptions > 0) {
            alerts.add(new SuperadminDashboardResponse.AlertItem(
                    "EXPIRED_SUBSCRIPTIONS",
                    "HIGH",
                    "Suscripciones vencidas",
                    "Se detectaron " + expiredSubscriptions + " suscripciones vencidas.",
                    "Revisar suscripciones vencidas",
                    generatedAt
            ));
        }
        if (failedAuditEvents > 0) {
            alerts.add(new SuperadminDashboardResponse.AlertItem(
                    "FAILED_AUDIT_EVENTS",
                    "HIGH",
                    "Eventos de auditoría fallidos",
                    "Hay " + failedAuditEvents + " eventos fallidos en auditoría.",
                    "Revisar auditoría",
                    generatedAt
            ));
        }
        if (activeTenants < totalTenants) {
            alerts.add(new SuperadminDashboardResponse.AlertItem(
                    "TENANT_ACTIVATION_GAP",
                    "INFO",
                    "Tenants no activos",
                    "No todos los tenants están activos actualmente.",
                    "Ver estado de tenants",
                    generatedAt
            ));
        }
        return alerts.stream().limit(MAX_ALERT_ITEMS).toList();
    }

    private List<SuperadminDashboardResponse.InsightItem> buildInsights(
            long activeTenants,
            long inactiveTenants,
            long activeSubscriptions,
            long trialSubscriptions,
            long activePlans,
            long totalPlans
    ) {
        List<SuperadminDashboardResponse.InsightItem> insights = new java.util.ArrayList<>();
        if (activeTenants > 0) {
            insights.add(new SuperadminDashboardResponse.InsightItem(
                    "ACTIVE_TENANT_BASE",
                    "INFO",
                    "Base activa",
                    "El sistema mantiene " + activeTenants + " tenants activos."
            ));
        }
        if (inactiveTenants > 0) {
            insights.add(new SuperadminDashboardResponse.InsightItem(
                    "TENANT_INACTIVITY",
                    "WARN",
                    "Tenants inactivos",
                    "Hay " + inactiveTenants + " tenants inactivos que podrían requerir acción."
            ));
        }
        if (trialSubscriptions > 0) {
            insights.add(new SuperadminDashboardResponse.InsightItem(
                    "TRIAL_BASE",
                    "INFO",
                    "Suscripciones de prueba",
                    "Existen " + trialSubscriptions + " suscripciones en trial."
            ));
        }
        if (activePlans == totalPlans) {
            insights.add(new SuperadminDashboardResponse.InsightItem(
                    "PLANS_HEALTHY",
                    "INFO",
                    "Catálogo estable",
                    "Todos los planes registrados están activos."
            ));
        }
        if (activeSubscriptions > 0) {
            insights.add(new SuperadminDashboardResponse.InsightItem(
                    "ACTIVE_SUBSCRIPTION_BASE",
                    "INFO",
                    "Base suscrita",
                    "Hay " + activeSubscriptions + " suscripciones activas en el sistema."
            ));
        }
        return insights;
    }

    private SuperadminDashboardResponse.TenantItem toTenantItem(PlatformTenant tenant) {
        PlatformPlan plan = tenant.planId() != null ? platformPlanRepository.findById(tenant.planId()).orElse(null) : null;
        return new SuperadminDashboardResponse.TenantItem(
                tenant.id(),
                tenant.name(),
                tenant.slug(),
                tenant.schemaName(),
                safeEnumName(tenant.status()),
                plan != null ? plan.code() : null,
                toDashboardTime(tenant.createdAt()),
                toDashboardTime(tenant.updatedAt())
        );
    }

    private SuperadminDashboardResponse.SubscriptionItem toSubscriptionItem(PlatformSubscription subscription) {
        PlatformTenant tenant = platformTenantRepository.findById(subscription.tenantId()).orElse(null);
        PlatformPlan plan = subscription.planId() != null ? platformPlanRepository.findById(subscription.planId()).orElse(null) : null;
        return new SuperadminDashboardResponse.SubscriptionItem(
                subscription.id(),
                subscription.tenantId(),
                tenant != null ? tenant.name() : null,
                tenant != null ? tenant.slug() : null,
                plan != null ? plan.code() : null,
                safeEnumName(subscription.status()),
                toDashboardTime(subscription.expiresAt()),
                subscription.status() == PlatformSubscriptionStatus.EXPIRED ? toDashboardTime(subscription.updatedAt()) : null
        );
    }

    private SuperadminDashboardResponse.AuditItem toAuditItem(PlatformAuditEvent event) {
        return new SuperadminDashboardResponse.AuditItem(
                event.id(),
                safeText(event.actorEmail()),
                safeText(event.eventType()),
                safeText(event.resourceType()),
                safeText(event.resourceId()),
                safeText(event.outcome()),
                toDashboardTime(event.createdAt())
        );
    }

    private SuperadminDashboardResponse.ActivityItem toActivityItem(PlatformAuditEvent event) {
        String actorName = safeText(event.actorEmail());
        if (actorName == null) {
            actorName = safeText(event.actorSubject());
        }
        return new SuperadminDashboardResponse.ActivityItem(
                event.id().toString(),
                safeText(event.resourceType()),
                safeText(event.eventType()),
                safeText(event.eventDetails()),
                actorName,
                toDashboardTime(event.createdAt())
        );
    }

    private boolean isInWindow(OffsetDateTime value, PeriodWindow window) {
        return value != null && !value.isBefore(window.from()) && !value.isAfter(window.to());
    }

    private boolean isInWindow(java.time.Instant instant, PeriodWindow window) {
        return instant != null && isInWindow(instant.atZone(DASHBOARD_ZONE).toOffsetDateTime(), window);
    }

    private PeriodWindow currentMonthWindow(OffsetDateTime now) {
        OffsetDateTime from = now.toLocalDate()
                .with(TemporalAdjusters.firstDayOfMonth())
                .atStartOfDay()
                .atZone(DASHBOARD_ZONE)
                .toOffsetDateTime();
        return new PeriodWindow(from, now);
    }

    private PeriodWindow previousMonthWindow(OffsetDateTime now) {
        LocalDate previousMonth = now.toLocalDate().with(TemporalAdjusters.firstDayOfMonth()).minusMonths(1);
        OffsetDateTime from = previousMonth.atStartOfDay().atZone(DASHBOARD_ZONE).toOffsetDateTime();
        OffsetDateTime to = previousMonth.with(TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 59).atZone(DASHBOARD_ZONE).toOffsetDateTime();
        return new PeriodWindow(from, to);
    }

    private OffsetDateTime toDashboardTime(java.time.Instant instant) {
        return instant != null ? instant.atZone(DASHBOARD_ZONE).toOffsetDateTime() : null;
    }

    private BigDecimal percentChange(long current, long previous) {
        if (previous == 0) {
            return current == 0 ? BigDecimal.ZERO : BigDecimal.valueOf(100);
        }
        return BigDecimal.valueOf((current - previous) * 100.0 / previous).setScale(2, RoundingMode.HALF_UP);
    }

    private String safeEnumName(Enum<?> value) {
        return value != null ? value.name() : null;
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private record PeriodWindow(OffsetDateTime from, OffsetDateTime to) {
    }

    private record PeriodRangeSummary(
            long previousTenants,
            long previousActiveSubscriptions,
            long previousNewTenants,
            long previousExpiredSubscriptions
    ) {
    }
}

```

### `dashboard/application/usecase/GetTenantSummaryUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.dashboard.application.usecase;

import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import com.financesystem.finance_api.modules.platform.dashboard.application.dto.TenantSummaryResponse;
import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.PlatformSubscriptionLifecycleService;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.exception.PlatformSubscriptionNotFoundException;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import com.financesystem.finance_api.modules.platform.tenants.domain.exception.PlatformTenantNotFoundException;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class GetTenantSummaryUseCase {

    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformSubscriptionRepository platformSubscriptionRepository;
    private final PlatformPlanRepository platformPlanRepository;
    private final TenantUserRepository tenantUserRepository;
    private final PlatformSubscriptionLifecycleService lifecycleService;

    public GetTenantSummaryUseCase(
            PlatformTenantRepository platformTenantRepository,
            PlatformSubscriptionRepository platformSubscriptionRepository,
            PlatformPlanRepository platformPlanRepository,
            TenantUserRepository tenantUserRepository,
            PlatformSubscriptionLifecycleService lifecycleService
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.platformSubscriptionRepository = platformSubscriptionRepository;
        this.platformPlanRepository = platformPlanRepository;
        this.tenantUserRepository = tenantUserRepository;
        this.lifecycleService = lifecycleService;
    }

    public TenantSummaryResponse execute(String tenantSlug) {
        lifecycleService.refreshExpiredSubscriptions();

        PlatformTenant tenant = platformTenantRepository.findBySlug(tenantSlug)
                .orElseThrow(() -> new PlatformTenantNotFoundException(
                        "Tenant not found for slug: " + tenantSlug
                ));

        PlatformSubscription subscription = platformSubscriptionRepository.findCurrentByTenantId(tenant.id())
                .orElseThrow(() -> new PlatformSubscriptionNotFoundException(
                        "Current subscription not found for tenant: " + tenantSlug
                ));

        PlatformPlan plan = platformPlanRepository.findById(subscription.planId())
                .orElseThrow(() -> new PlatformSubscriptionNotFoundException(
                        "Plan not found for current subscription"
                ));

        long totalUsers = tenantUserRepository.countActiveUsers();

        long trialDaysLeft = 0;
        if (subscription.trial() && subscription.expiresAt() != null) {
            long days = ChronoUnit.DAYS.between(Instant.now(), subscription.expiresAt());
            trialDaysLeft = Math.max(0, days);
        }

        return new TenantSummaryResponse(
                totalUsers,
                plan.maxUsers(),
                plan.code(),
                trialDaysLeft
        );
    }
}

```

### `dashboard/infrastructure/api/DashboardController.java`

```java
package com.financesystem.finance_api.modules.platform.dashboard.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.platform.dashboard.application.dto.TenantSummaryResponse;
import com.financesystem.finance_api.modules.platform.dashboard.application.usecase.GetTenantSummaryUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final GetTenantSummaryUseCase getTenantSummaryUseCase;

    public DashboardController(GetTenantSummaryUseCase getTenantSummaryUseCase) {
        this.getTenantSummaryUseCase = getTenantSummaryUseCase;
    }

    @GetMapping("/tenant/summary")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<TenantSummaryResponse> getTenantSummary() {
        String tenantSlug = TenantContextHolder.getRequired().tenantSlug();

        return ApiResponse.success(
                "Tenant summary retrieved successfully",
                getTenantSummaryUseCase.execute(tenantSlug)
        );
    }
}

```

### `dashboard/infrastructure/api/PlatformDashboardController.java`

```java
package com.financesystem.finance_api.modules.platform.dashboard.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.dashboard.application.dto.SuperadminDashboardResponse;
import com.financesystem.finance_api.modules.platform.dashboard.application.usecase.GetSuperadminDashboardUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/platform/dashboard")
@SecurityRequirement(name = "bearerAuth")
public class PlatformDashboardController {

    private final GetSuperadminDashboardUseCase getSuperadminDashboardUseCase;

    public PlatformDashboardController(GetSuperadminDashboardUseCase getSuperadminDashboardUseCase) {
        this.getSuperadminDashboardUseCase = getSuperadminDashboardUseCase;
    }

    @GetMapping("/summary")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<SuperadminDashboardResponse> summary() {
        return ApiResponse.success(
                "Superadmin dashboard retrieved successfully",
                getSuperadminDashboardUseCase.execute()
        );
    }
}

```

### `onboarding/application/dto/PublicSignupRequest.java`

```java
package com.financesystem.finance_api.modules.platform.onboarding.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PublicSignupRequest(
        @NotBlank
        @Size(max = 150)
        String companyName,
        @NotBlank
        @Size(max = 100)
        String tenantSlug,
        @NotBlank
        @Email
        @Size(max = 150)
        String adminEmail,
        @NotBlank
        @Size(min = 8, max = 100)
        String password,
        @NotBlank
        @Size(max = 100)
        String firstName,
        @NotBlank
        @Size(max = 100)
        String lastName
        ) {

}

```

### `onboarding/application/dto/PublicSignupResponse.java`

```java
package com.financesystem.finance_api.modules.platform.onboarding.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PublicSignupResponse(
        UUID tenantId,
        String tenantSlug,
        String companyName,
        String adminEmail,
        String initialRole,
        String currentPlanCode,
        String subscriptionStatus,
        Instant trialExpiresAt,
        String loginHint
        ) {

}

```

### `onboarding/application/service/TenantOwnerAdminProvisioningService.java`

```java
package com.financesystem.finance_api.modules.platform.onboarding.application.service;

import com.financesystem.finance_api.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.UUID;

@Service
public class TenantOwnerAdminProvisioningService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public TenantOwnerAdminProvisioningService(
            @Qualifier("targetDataSource") DataSource targetDataSource,
            PasswordEncoder passwordEncoder
    ) {
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
        this.passwordEncoder = passwordEncoder;
    }

    public UUID provisionOwnerAdmin(
            String schemaName,
            String email,
            String rawPassword,
            String firstName,
            String lastName
    ) {
        validateSchemaName(schemaName);

        String normalizedEmail = email.trim().toLowerCase();
        String normalizedFirstName = firstName.trim();
        String normalizedLastName = lastName.trim();

        Integer existingUsers = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM %s.tenant_users
                WHERE email = ?
                """.formatted(schemaName),
                Integer.class,
                normalizedEmail
        );

        if (existingUsers != null && existingUsers > 0) {
            throw new BusinessException("A tenant user with email '" + normalizedEmail + "' already exists");
        }

        jdbcTemplate.execute("""
                INSERT INTO %s.tenant_roles (name, description, active, created_at)
                VALUES ('OWNER_ADMIN', 'Tenant owner administrator role', true, NOW())
                ON CONFLICT (name) DO UPDATE SET
                    active = true,
                    description = EXCLUDED.description
                """.formatted(schemaName));

        UUID userId = UUID.randomUUID();
        String passwordHash = passwordEncoder.encode(rawPassword);

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_users (
                    id, email, password_hash, first_name, last_name, active, status, created_at, updated_at
                )
                VALUES (?, ?, ?, ?, ?, true, 'ACTIVE', NOW(), NOW())
                """.formatted(schemaName),
                userId,
                normalizedEmail,
                passwordHash,
                normalizedFirstName,
                normalizedLastName
        );

        UUID ownerAdminRoleId = jdbcTemplate.queryForObject(
                """
                SELECT id
                FROM %s.tenant_roles
                WHERE name = 'OWNER_ADMIN'
                LIMIT 1
                """.formatted(schemaName),
                (rs, rowNum) -> rs.getObject("id", UUID.class)
        );

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_user_roles (user_id, role_id, assigned_at)
                VALUES (?, ?, NOW())
                ON CONFLICT (user_id, role_id) DO NOTHING
                """.formatted(schemaName),
                userId,
                ownerAdminRoleId
        );

        jdbcTemplate.update(
                """
                INSERT INTO %s.tenant_settings (setting_key, setting_value, created_at, updated_at)
                VALUES ('company.contact_email', ?, NOW(), NOW())
                ON CONFLICT (setting_key) DO UPDATE SET
                    setting_value = EXCLUDED.setting_value,
                    updated_at = NOW()
                """.formatted(schemaName),
                normalizedEmail
        );

        return userId;
    }

    private void validateSchemaName(String schemaName) {
        if (schemaName == null || schemaName.isBlank()) {
            throw new IllegalArgumentException("Schema name must not be blank");
        }

        if (!schemaName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Schema name contains invalid characters: " + schemaName);
        }
    }
}

```

### `onboarding/application/usecase/PublicSignupUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.onboarding.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.onboarding.application.dto.PublicSignupRequest;
import com.financesystem.finance_api.modules.platform.onboarding.application.dto.PublicSignupResponse;
import com.financesystem.finance_api.modules.platform.onboarding.application.service.TenantOwnerAdminProvisioningService;
import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.CreateTenantRequest;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance_api.modules.platform.tenants.application.usecase.CreateTenantUseCase;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicSignupUseCase {

    private final CreateTenantUseCase createTenantUseCase;
    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformSubscriptionRepository platformSubscriptionRepository;
    private final PlatformPlanRepository platformPlanRepository;
    private final TenantOwnerAdminProvisioningService tenantOwnerAdminProvisioningService;
    private final AuditTrailService auditTrailService;

    public PublicSignupUseCase(
            CreateTenantUseCase createTenantUseCase,
            PlatformTenantRepository platformTenantRepository,
            PlatformSubscriptionRepository platformSubscriptionRepository,
            PlatformPlanRepository platformPlanRepository,
            TenantOwnerAdminProvisioningService tenantOwnerAdminProvisioningService,
            AuditTrailService auditTrailService
    ) {
        this.createTenantUseCase = createTenantUseCase;
        this.platformTenantRepository = platformTenantRepository;
        this.platformSubscriptionRepository = platformSubscriptionRepository;
        this.platformPlanRepository = platformPlanRepository;
        this.tenantOwnerAdminProvisioningService = tenantOwnerAdminProvisioningService;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public PublicSignupResponse execute(PublicSignupRequest request) {
        PlatformTenantResponse createdTenantResponse = createTenantUseCase.execute(
                new CreateTenantRequest(
                        request.companyName().trim(),
                        request.tenantSlug().trim(),
                        "DEMO"
                )
        );

        PlatformTenant createdTenant = platformTenantRepository.findById(createdTenantResponse.id())
                .orElseThrow();

        tenantOwnerAdminProvisioningService.provisionOwnerAdmin(
                createdTenant.schemaName(),
                request.adminEmail(),
                request.password(),
                request.firstName(),
                request.lastName()
        );

        PlatformSubscription currentSubscription = platformSubscriptionRepository.findCurrentByTenantId(createdTenant.id())
                .orElseThrow();

        PlatformPlan currentPlan = platformPlanRepository.findById(currentSubscription.planId()).orElseThrow();

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.PUBLIC_SIGNUP_COMPLETED,
                "TENANT",
                createdTenant.id().toString(),
                PlatformAuditPayloads.details(
                        "tenantSlug", createdTenant.slug(),
                        "adminEmail", request.adminEmail().trim().toLowerCase(),
                        "planCode", currentPlan.code(),
                        "subscriptionStatus", currentSubscription.status().name()
                ),
                null,
                PlatformAuditPayloads.tenantState(createdTenant)
        );

        return new PublicSignupResponse(
                createdTenant.id(),
                createdTenant.slug(),
                createdTenant.name(),
                request.adminEmail().trim().toLowerCase(),
                "OWNER_ADMIN",
                currentPlan.code(),
                currentSubscription.status().name(),
                currentSubscription.expiresAt(),
                "Use /api/auth/login with X-Tenant-Slug = " + createdTenant.slug()
        );
    }
}

```

### `onboarding/infrastructure/api/PublicOnboardingController.java`

```java
package com.financesystem.finance_api.modules.platform.onboarding.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.onboarding.application.dto.PublicSignupRequest;
import com.financesystem.finance_api.modules.platform.onboarding.application.dto.PublicSignupResponse;
import com.financesystem.finance_api.modules.platform.onboarding.application.usecase.PublicSignupUseCase;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class PublicOnboardingController {

    private final PublicSignupUseCase publicSignupUseCase;

    public PublicOnboardingController(PublicSignupUseCase publicSignupUseCase) {
        this.publicSignupUseCase = publicSignupUseCase;
    }

    @PostMapping("/signup")
    public ApiResponse<PublicSignupResponse> signup(@Valid @RequestBody PublicSignupRequest request) {
        return ApiResponse.success(
                "Public signup completed successfully",
                publicSignupUseCase.execute(request)
        );
    }
}

```

### `plans/application/dto/CreatePlatformPlanRequest.java`

```java
package com.financesystem.finance_api.modules.platform.plans.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePlatformPlanRequest(
        @NotBlank
        @Size(max = 50)
        String code,

        @NotBlank
        @Size(max = 100)
        String name,

        @Size(max = 255)
        String description,

        @Min(1)
        int maxUsers,

        @Min(1)
        int maxRoles,

        @NotBlank
        @Size(max = 20)
        String planType,

        Integer trialDays
) {
}

```

### `plans/application/dto/PlatformPlanResponse.java`

```java
package com.financesystem.finance_api.modules.platform.plans.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PlatformPlanResponse(
        UUID id,
        String code,
        String name,
        String description,
        int maxUsers,
        int maxRoles,
        String planType,
        Integer trialDays,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `plans/application/mapper/PlatformPlanMapper.java`

```java
package com.financesystem.finance_api.modules.platform.plans.application.mapper;

import com.financesystem.finance_api.modules.platform.plans.application.dto.PlatformPlanResponse;
import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import org.springframework.stereotype.Component;

@Component
public class PlatformPlanMapper {

    public PlatformPlanResponse toResponse(PlatformPlan plan) {
        return new PlatformPlanResponse(
                plan.id(),
                plan.code(),
                plan.name(),
                plan.description(),
                plan.maxUsers(),
                plan.maxRoles(),
                plan.planType(),
                plan.trialDays(),
                plan.active(),
                plan.createdAt(),
                plan.updatedAt()
        );
    }
}

```

### `plans/application/usecase/ActivatePlatformPlanUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.plans.application.usecase;

import com.financesystem.finance_api.modules.platform.plans.application.dto.PlatformPlanResponse;
import com.financesystem.finance_api.modules.platform.plans.application.mapper.PlatformPlanMapper;
import com.financesystem.finance_api.modules.platform.plans.domain.exception.PlatformPlanNotFoundException;
import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActivatePlatformPlanUseCase {

    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformPlanMapper platformPlanMapper;
    private final AuditTrailService auditTrailService;

    public ActivatePlatformPlanUseCase(
            PlatformPlanRepository platformPlanRepository,
            PlatformPlanMapper platformPlanMapper,
            AuditTrailService auditTrailService
    ) {
        this.platformPlanRepository = platformPlanRepository;
        this.platformPlanMapper = platformPlanMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public PlatformPlanResponse execute(UUID id) {
        PlatformPlan plan = platformPlanRepository.findById(id)
                .orElseThrow(() -> new PlatformPlanNotFoundException("Platform plan not found with id: " + id));
        PlatformPlan beforeState = plan;

        PlatformPlan updated = new PlatformPlan(
                plan.id(),
                plan.code(),
                plan.name(),
                plan.description(),
                plan.maxUsers(),
                plan.maxRoles(),
                plan.planType(),
                plan.trialDays(),
                true,
                plan.createdAt(),
                plan.updatedAt()
        );

        PlatformPlan saved = platformPlanRepository.save(updated);

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.PLATFORM_PLAN_ACTIVATED,
                "PLATFORM_PLAN",
                saved.id().toString(),
                PlatformAuditPayloads.details(
                        "code", saved.code(),
                        "active", saved.active()
                ),
                PlatformAuditPayloads.planState(beforeState),
                PlatformAuditPayloads.planState(saved)
        );

        return platformPlanMapper.toResponse(saved);
    }
}

```

### `plans/application/usecase/CreatePlatformPlanUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.plans.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.plans.application.dto.CreatePlatformPlanRequest;
import com.financesystem.finance_api.modules.platform.plans.application.dto.PlatformPlanResponse;
import com.financesystem.finance_api.modules.platform.plans.application.mapper.PlatformPlanMapper;
import com.financesystem.finance_api.modules.platform.plans.domain.exception.PlatformPlanAlreadyExistsException;
import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePlatformPlanUseCase {

    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformPlanMapper platformPlanMapper;
    private final AuditTrailService auditTrailService;

    public CreatePlatformPlanUseCase(
            PlatformPlanRepository platformPlanRepository,
            PlatformPlanMapper platformPlanMapper,
            AuditTrailService auditTrailService
    ) {
        this.platformPlanRepository = platformPlanRepository;
        this.platformPlanMapper = platformPlanMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public PlatformPlanResponse execute(CreatePlatformPlanRequest request) {
        String normalizedCode = request.code().trim().toUpperCase();
        String normalizedPlanType = request.planType().trim().toUpperCase();

        if (platformPlanRepository.existsByCode(normalizedCode)) {
            throw new PlatformPlanAlreadyExistsException(
                    "A platform plan with code '" + normalizedCode + "' already exists"
            );
        }

        validatePlanType(normalizedPlanType, request.trialDays());

        PlatformPlan planToCreate = new PlatformPlan(
                null,
                normalizedCode,
                request.name().trim(),
                request.description() == null ? null : request.description().trim(),
                request.maxUsers(),
                request.maxRoles(),
                normalizedPlanType,
                request.trialDays(),
                true,
                null,
                null
        );

        PlatformPlan createdPlan = platformPlanRepository.save(planToCreate);

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.PLATFORM_PLAN_CREATED,
                "PLATFORM_PLAN",
                createdPlan.id().toString(),
                PlatformAuditPayloads.details(
                        "code", createdPlan.code(),
                        "name", createdPlan.name(),
                        "planType", createdPlan.planType(),
                        "trialDays", createdPlan.trialDays()
                ),
                null,
                PlatformAuditPayloads.planState(createdPlan)
        );

        return platformPlanMapper.toResponse(createdPlan);
    }

    private void validatePlanType(String planType, Integer trialDays) {
        if (!planType.equals("DEMO") && !planType.equals("PAID") && !planType.equals("FREE")) {
            throw new BusinessException("planType must be DEMO, PAID or FREE");
        }

        if (planType.equals("DEMO")) {
            if (trialDays == null || trialDays <= 0) {
                throw new BusinessException("trialDays must be greater than zero for DEMO plans");
            }
        }
    }
}

```

### `plans/application/usecase/DeactivatePlatformPlanUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.plans.application.usecase;

import com.financesystem.finance_api.modules.platform.plans.application.dto.PlatformPlanResponse;
import com.financesystem.finance_api.modules.platform.plans.application.mapper.PlatformPlanMapper;
import com.financesystem.finance_api.modules.platform.plans.domain.exception.PlatformPlanNotFoundException;
import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeactivatePlatformPlanUseCase {

    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformPlanMapper platformPlanMapper;
    private final AuditTrailService auditTrailService;

    public DeactivatePlatformPlanUseCase(
            PlatformPlanRepository platformPlanRepository,
            PlatformPlanMapper platformPlanMapper,
            AuditTrailService auditTrailService
    ) {
        this.platformPlanRepository = platformPlanRepository;
        this.platformPlanMapper = platformPlanMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public PlatformPlanResponse execute(UUID id) {
        PlatformPlan plan = platformPlanRepository.findById(id)
                .orElseThrow(() -> new PlatformPlanNotFoundException("Platform plan not found with id: " + id));
        PlatformPlan beforeState = plan;

        PlatformPlan updated = new PlatformPlan(
                plan.id(),
                plan.code(),
                plan.name(),
                plan.description(),
                plan.maxUsers(),
                plan.maxRoles(),
                plan.planType(),
                plan.trialDays(),
                false,
                plan.createdAt(),
                plan.updatedAt()
        );

        PlatformPlan saved = platformPlanRepository.save(updated);

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.PLATFORM_PLAN_DEACTIVATED,
                "PLATFORM_PLAN",
                saved.id().toString(),
                PlatformAuditPayloads.details(
                        "code", saved.code(),
                        "active", saved.active()
                ),
                PlatformAuditPayloads.planState(beforeState),
                PlatformAuditPayloads.planState(saved)
        );

        return platformPlanMapper.toResponse(saved);
    }
}

```

### `plans/application/usecase/GetPlatformPlanByIdUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.plans.application.usecase;

import com.financesystem.finance_api.modules.platform.plans.application.dto.PlatformPlanResponse;
import com.financesystem.finance_api.modules.platform.plans.application.mapper.PlatformPlanMapper;
import com.financesystem.finance_api.modules.platform.plans.domain.exception.PlatformPlanNotFoundException;
import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetPlatformPlanByIdUseCase {

    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformPlanMapper platformPlanMapper;

    public GetPlatformPlanByIdUseCase(
            PlatformPlanRepository platformPlanRepository,
            PlatformPlanMapper platformPlanMapper
    ) {
        this.platformPlanRepository = platformPlanRepository;
        this.platformPlanMapper = platformPlanMapper;
    }

    public PlatformPlanResponse execute(UUID id) {
        return platformPlanRepository.findById(id)
                .map(platformPlanMapper::toResponse)
                .orElseThrow(() -> new PlatformPlanNotFoundException("Platform plan not found with id: " + id));
    }
}
```

### `plans/application/usecase/ListPlatformPlansUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.plans.application.usecase;

import com.financesystem.finance_api.modules.platform.plans.application.dto.PlatformPlanResponse;
import com.financesystem.finance_api.modules.platform.plans.application.mapper.PlatformPlanMapper;
import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListPlatformPlansUseCase {

    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformPlanMapper platformPlanMapper;

    public ListPlatformPlansUseCase(
            PlatformPlanRepository platformPlanRepository,
            PlatformPlanMapper platformPlanMapper
    ) {
        this.platformPlanRepository = platformPlanRepository;
        this.platformPlanMapper = platformPlanMapper;
    }

    public List<PlatformPlanResponse> execute() {
        return platformPlanRepository.findAll()
                .stream()
                .map(platformPlanMapper::toResponse)
                .toList();
    }
}
```

### `plans/domain/exception/PlatformPlanAlreadyExistsException.java`

```java
package com.financesystem.finance_api.modules.platform.plans.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class PlatformPlanAlreadyExistsException extends BusinessException {

    public PlatformPlanAlreadyExistsException(String message) {
        super(message);
    }
}
```

### `plans/domain/exception/PlatformPlanNotFoundException.java`

```java
package com.financesystem.finance_api.modules.platform.plans.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class PlatformPlanNotFoundException extends ResourceNotFoundException {

    public PlatformPlanNotFoundException(String message) {
        super(message);
    }
}
```

### `plans/domain/model/PlatformPlan.java`

```java
package com.financesystem.finance_api.modules.platform.plans.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PlatformPlan(
        UUID id,
        String code,
        String name,
        String description,
        int maxUsers,
        int maxRoles,
        String planType,
        Integer trialDays,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `plans/domain/repository/PlatformPlanRepository.java`

```java
package com.financesystem.finance_api.modules.platform.plans.domain.repository;

import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlatformPlanRepository {

    PlatformPlan save(PlatformPlan plan);

    Optional<PlatformPlan> findById(UUID id);

    Optional<PlatformPlan> findByCode(String code);

    List<PlatformPlan> findAll();

    boolean existsByCode(String code);
}

```

### `plans/infrastructure/api/PlatformPlanController.java`

```java
package com.financesystem.finance_api.modules.platform.plans.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.pagination.PaginationSupport;
import com.financesystem.finance_api.modules.platform.plans.application.dto.CreatePlatformPlanRequest;
import com.financesystem.finance_api.modules.platform.plans.application.dto.PlatformPlanResponse;
import com.financesystem.finance_api.modules.platform.plans.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/plans")
@SecurityRequirement(name = "bearerAuth")
public class PlatformPlanController {

    private final CreatePlatformPlanUseCase createPlatformPlanUseCase;
    private final ListPlatformPlansUseCase listPlatformPlansUseCase;
    private final GetPlatformPlanByIdUseCase getPlatformPlanByIdUseCase;
    private final ActivatePlatformPlanUseCase activatePlatformPlanUseCase;
    private final DeactivatePlatformPlanUseCase deactivatePlatformPlanUseCase;

    public PlatformPlanController(
            CreatePlatformPlanUseCase createPlatformPlanUseCase,
            ListPlatformPlansUseCase listPlatformPlansUseCase,
            GetPlatformPlanByIdUseCase getPlatformPlanByIdUseCase,
            ActivatePlatformPlanUseCase activatePlatformPlanUseCase,
            DeactivatePlatformPlanUseCase deactivatePlatformPlanUseCase
    ) {
        this.createPlatformPlanUseCase = createPlatformPlanUseCase;
        this.listPlatformPlansUseCase = listPlatformPlansUseCase;
        this.getPlatformPlanByIdUseCase = getPlatformPlanByIdUseCase;
        this.activatePlatformPlanUseCase = activatePlatformPlanUseCase;
        this.deactivatePlatformPlanUseCase = deactivatePlatformPlanUseCase;
    }

    @PostMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<PlatformPlanResponse> createPlan(@Valid @RequestBody CreatePlatformPlanRequest request) {
        return ApiResponse.success(
                "Platform plan created successfully",
                createPlatformPlanUseCase.execute(request)
        );
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<PlatformPlanResponse>> listPlans(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success(
                "Platform plans retrieved successfully",
                PaginationSupport.page(listPlatformPlansUseCase.execute(), pageable)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<PlatformPlanResponse> getPlanById(@PathVariable UUID id) {
        return ApiResponse.success(
                "Platform plan retrieved successfully",
                getPlatformPlanByIdUseCase.execute(id)
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<PlatformPlanResponse> activatePlan(@PathVariable UUID id) {
        return ApiResponse.success(
                "Platform plan activated successfully",
                activatePlatformPlanUseCase.execute(id)
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<PlatformPlanResponse> deactivatePlan(@PathVariable UUID id) {
        return ApiResponse.success(
                "Platform plan deactivated successfully",
                deactivatePlatformPlanUseCase.execute(id)
        );
    }
}

```

### `plans/infrastructure/persistence/PlatformPlanEntity.java`

```java
package com.financesystem.finance_api.modules.platform.plans.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "platform_plans")
public class PlatformPlanEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private int maxUsers;

    @Column(nullable = false)
    private int maxRoles;

    @Column(nullable = false, length = 20)
    private String planType;

    @Column
    private Integer trialDays;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(int maxUsers) {
        this.maxUsers = maxUsers;
    }

    public int getMaxRoles() {
        return maxRoles;
    }

    public void setMaxRoles(int maxRoles) {
        this.maxRoles = maxRoles;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public Integer getTrialDays() {
        return trialDays;
    }

    public void setTrialDays(Integer trialDays) {
        this.trialDays = trialDays;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

```

### `plans/infrastructure/persistence/PlatformPlanJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.plans.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlatformPlanJpaRepository extends JpaRepository<PlatformPlanEntity, UUID> {

    boolean existsByCode(String code);

    Optional<PlatformPlanEntity> findByCode(String code);
}

```

### `plans/infrastructure/persistence/PlatformPlanRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.plans.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PlatformPlanRepositoryAdapter implements PlatformPlanRepository {

    private final PlatformPlanJpaRepository jpaRepository;

    public PlatformPlanRepositoryAdapter(PlatformPlanJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PlatformPlan save(PlatformPlan plan) {
        PlatformPlanEntity entity = toEntity(plan);
        PlatformPlanEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PlatformPlan> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<PlatformPlan> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public List<PlatformPlan> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    private PlatformPlanEntity toEntity(PlatformPlan plan) {
        PlatformPlanEntity entity = new PlatformPlanEntity();
        entity.setId(plan.id());
        entity.setCode(plan.code());
        entity.setName(plan.name());
        entity.setDescription(plan.description());
        entity.setMaxUsers(plan.maxUsers());
        entity.setMaxRoles(plan.maxRoles());
        entity.setPlanType(plan.planType());
        entity.setTrialDays(plan.trialDays());
        entity.setActive(plan.active());
        return entity;
    }

    private PlatformPlan toDomain(PlatformPlanEntity entity) {
        return new PlatformPlan(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getMaxUsers(),
                entity.getMaxRoles(),
                entity.getPlanType(),
                entity.getTrialDays(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

```

### `servicepayments/application/dto/CancelServiceBillRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import jakarta.validation.constraints.Size;

public record CancelServiceBillRequest(
        @Size(max = 255)
        String reason
) {
}

```

### `servicepayments/application/dto/ChangeServiceProviderStatusRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeServiceProviderStatusRequest(
        @NotNull
        ServiceProviderStatus status
) {
}

```

### `servicepayments/application/dto/CreateServiceBillRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateServiceBillRequest(
        @NotNull
        UUID providerId,

        @NotBlank
        @Size(max = 100)
        String serviceCustomerCode,

        @NotBlank
        @Pattern(regexp = "^\\d{4}-\\d{2}$")
        String billingPeriod,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount,

        @NotNull
        CurrencyCode currency,

        @NotNull
        LocalDate dueDate
) {
}

```

### `servicepayments/application/dto/CreateServiceCustomerRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateServiceCustomerRequest(
        @NotNull
        UUID providerId,

        @NotBlank
        @Size(max = 100)
        String serviceCustomerCode,

        @NotBlank
        @Size(max = 150)
        String customerName
) {
}

```

### `servicepayments/application/dto/CreateServiceProviderRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateServiceProviderRequest(
        @NotBlank
        @Size(max = 80)
        String code,

        @NotBlank
        @Size(max = 150)
        String name,

        @NotNull
        ServiceProviderCategory category,

        @Size(max = 100)
        String serviceCustomerCodeLabel
) {
}

```

### `servicepayments/application/dto/PlatformServiceBillFilter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;

import java.time.LocalDate;
import java.util.UUID;

public record PlatformServiceBillFilter(
        UUID providerId,
        String serviceCustomerCode,
        ServiceBillStatus status,
        String billingPeriod,
        LocalDate dueDateFrom,
        LocalDate dueDateTo,
        String paidByTenantSlug,
        String search
) {
}

```

### `servicepayments/application/dto/PlatformServiceBillPaymentFilter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PlatformServiceBillPaymentFilter(
        UUID providerId,
        String tenantSlug,
        UUID userId,
        String accountNumber,
        UUID billId,
        String receiptNumber,
        Instant paidAtFrom,
        Instant paidAtTo
) {
}

```

### `servicepayments/application/dto/PlatformServiceCustomerFilter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;

import java.util.UUID;

public record PlatformServiceCustomerFilter(
        UUID providerId,
        ServiceCustomerStatus status,
        String serviceCustomerCode,
        String search
) {
}

```

### `servicepayments/application/dto/PlatformServiceProviderFilter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;

public record PlatformServiceProviderFilter(
        ServiceProviderCategory category,
        ServiceProviderStatus status,
        String search
) {
}

```

### `servicepayments/application/dto/ServiceBillPaymentResponse.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServiceBillPaymentResponse(
        UUID id,
        UUID billId,
        ServiceProviderSummaryResponse provider,
        String serviceCustomerCode,
        String serviceCustomerName,
        String billingPeriod,
        UUID paidByTenantId,
        String paidByTenantSlug,
        UUID paidByUserId,
        UUID paidByAccountId,
        String paidByAccountNumber,
        UUID paidTransactionId,
        BigDecimal amount,
        String currency,
        String receiptNumber,
        String idempotencyKey,
        ServiceBillPaymentStatus status,
        Instant paidAt,
        Instant createdAt
) {
}

```

### `servicepayments/application/dto/ServiceBillResponse.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ServiceBillResponse(
        UUID id,
        ServiceProviderSummaryResponse provider,
        UUID serviceCustomerId,
        String serviceCustomerCode,
        String customerName,
        String billingPeriod,
        BigDecimal amount,
        String currency,
        LocalDate dueDate,
        ServiceBillStatus status,
        UUID paidByTenantId,
        String paidByTenantSlug,
        UUID paidByUserId,
        UUID paidByAccountId,
        String paidByAccountNumber,
        UUID paidTransactionId,
        Instant paidAt,
        UUID createdBySuperadminId,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `servicepayments/application/dto/ServiceCustomerResponse.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record ServiceCustomerResponse(
        UUID id,
        ServiceProviderSummaryResponse provider,
        String serviceCustomerCode,
        String customerName,
        ServiceCustomerStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `servicepayments/application/dto/ServiceProviderResponse.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;

import java.time.Instant;
import java.util.UUID;

public record ServiceProviderResponse(
        UUID id,
        String code,
        String name,
        ServiceProviderCategory category,
        String serviceCustomerCodeLabel,
        ServiceProviderStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `servicepayments/application/dto/ServiceProviderSummaryResponse.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;

import java.util.UUID;

public record ServiceProviderSummaryResponse(
        UUID id,
        String code,
        String name,
        ServiceProviderCategory category
) {
}

```

### `servicepayments/application/dto/UpdateServiceCustomerRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import jakarta.validation.constraints.Size;

public record UpdateServiceCustomerRequest(
        @Size(max = 150)
        String customerName,

        ServiceCustomerStatus status
) {
}

```

### `servicepayments/application/dto/UpdateServiceProviderRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import jakarta.validation.constraints.Size;

public record UpdateServiceProviderRequest(
        @Size(max = 150)
        String name,

        ServiceProviderCategory category,

        @Size(max = 100)
        String serviceCustomerCodeLabel
) {
}

```

### `servicepayments/application/mapper/ServicePaymentsMapper.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.mapper;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class ServicePaymentsMapper {

    public ServiceProviderResponse toResponse(ServiceProvider serviceProvider) {
        return new ServiceProviderResponse(
                serviceProvider.id(),
                serviceProvider.code(),
                serviceProvider.name(),
                serviceProvider.category(),
                serviceProvider.serviceCustomerCodeLabel(),
                serviceProvider.status(),
                serviceProvider.createdAt(),
                serviceProvider.updatedAt()
        );
    }

    public ServiceProviderSummaryResponse toSummary(ServiceProvider serviceProvider) {
        return new ServiceProviderSummaryResponse(
                serviceProvider.id(),
                serviceProvider.code(),
                serviceProvider.name(),
                serviceProvider.category()
        );
    }

    public ServiceCustomerResponse toResponse(ServiceCustomer serviceCustomer, ServiceProvider provider) {
        return new ServiceCustomerResponse(
                serviceCustomer.id(),
                toSummary(provider),
                serviceCustomer.serviceCustomerCode(),
                serviceCustomer.customerName(),
                serviceCustomer.status(),
                serviceCustomer.createdAt(),
                serviceCustomer.updatedAt()
        );
    }

    public ServiceBillResponse toResponse(ServiceBill serviceBill, ServiceProvider provider) {
        return new ServiceBillResponse(
                serviceBill.id(),
                toSummary(provider),
                serviceBill.serviceCustomerId(),
                serviceBill.serviceCustomerCode(),
                serviceBill.customerName(),
                serviceBill.billingPeriod(),
                serviceBill.amount(),
                serviceBill.currency(),
                serviceBill.dueDate(),
                serviceBill.status(),
                serviceBill.paidByTenantId(),
                serviceBill.paidByTenantSlug(),
                serviceBill.paidByUserId(),
                serviceBill.paidByAccountId(),
                serviceBill.paidByAccountNumber(),
                serviceBill.paidTransactionId(),
                serviceBill.paidAt(),
                serviceBill.createdBySuperadminId(),
                serviceBill.createdAt(),
                serviceBill.updatedAt()
        );
    }

    public ServiceBillPaymentResponse toResponse(ServiceBillPayment payment, ServiceBill bill, ServiceProvider provider) {
        return new ServiceBillPaymentResponse(
                payment.id(),
                payment.billId(),
                toSummary(provider),
                bill.serviceCustomerCode(),
                bill.customerName(),
                bill.billingPeriod(),
                payment.paidByTenantId(),
                payment.paidByTenantSlug(),
                payment.paidByUserId(),
                payment.paidByAccountId(),
                payment.paidByAccountNumber(),
                payment.paidTransactionId(),
                payment.amount(),
                payment.currency(),
                payment.receiptNumber(),
                payment.idempotencyKey(),
                payment.status(),
                payment.paidAt(),
                payment.createdAt()
        );
    }
}

```

### `servicepayments/application/usecase/CancelServiceBillUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.CancelServiceBillRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class CancelServiceBillUseCase {

    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;

    public CancelServiceBillUseCase(
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository
    ) {
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
    }

    @Transactional
    public ServiceBillResponse execute(UUID id, CancelServiceBillRequest request) {
        ServiceBill current = serviceBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service bill not found"));

        if (current.status() != ServiceBillStatus.PENDING) {
            throw new BusinessException("Service bill is not pending");
        }

        ServiceBill updated = serviceBillRepository.save(new ServiceBill(
                current.id(),
                current.providerId(),
                current.serviceCustomerId(),
                current.serviceCustomerCode(),
                current.customerName(),
                current.billingPeriod(),
                current.amount(),
                current.currency(),
                current.dueDate(),
                ServiceBillStatus.CANCELLED,
                current.paidByTenantId(),
                current.paidByTenantSlug(),
                current.paidByUserId(),
                current.paidByAccountId(),
                current.paidByAccountNumber(),
                current.paidTransactionId(),
                current.paidAt(),
                current.createdBySuperadminId(),
                current.createdAt(),
                current.updatedAt()
        ));

        var provider = serviceProviderRepository.findById(current.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_BILL_CANCELLED,
                "SERVICE_BILL",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "reason", StringUtils.hasText(request.reason()) ? request.reason().trim() : null,
                        "cancelledBy", currentSuperadminId()
                ),
                current,
                updated
        );

        return servicePaymentsMapper.toResponse(updated, provider);
    }

    private UUID currentSuperadminId() {
        String email = securityContextFacade.getCurrentEmail();
        if (!StringUtils.hasText(email)) {
            return null;
        }

        return platformSuperadminRepository.findByEmail(email.trim().toLowerCase())
                .map(com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin::id)
                .orElse(null);
    }
}

```

### `servicepayments/application/usecase/ChangeServiceProviderStatusUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ChangeServiceProviderStatusRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChangeServiceProviderStatusUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;

    public ChangeServiceProviderStatusUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public ServiceProviderResponse execute(UUID id, ChangeServiceProviderStatusRequest request) {
        ServiceProvider current = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Service provider not found"));

        if (current.status() == request.status()) {
            return servicePaymentsMapper.toResponse(current);
        }

        ServiceProvider updated = serviceProviderRepository.save(new ServiceProvider(
                current.id(),
                current.code(),
                current.name(),
                current.category(),
                current.serviceCustomerCodeLabel(),
                request.status(),
                current.createdAt(),
                current.updatedAt()
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_PROVIDER_STATUS_CHANGED,
                "SERVICE_PROVIDER",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "status", updated.status()
                ),
                current,
                updated
        );

        return servicePaymentsMapper.toResponse(updated);
    }
}

```

### `servicepayments/application/usecase/CreateServiceBillUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.CreateServiceBillRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Service
public class CreateServiceBillUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;

    public CreateServiceBillUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceBillRepository serviceBillRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceBillRepository = serviceBillRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
    }

    @Transactional
    public ServiceBillResponse execute(CreateServiceBillRequest request) {
        var provider = serviceProviderRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        if (provider.status() != ServiceProviderStatus.ACTIVE) {
            throw new BusinessException("Service provider is inactive");
        }

        String code = normalizeCode(request.serviceCustomerCode());
        var serviceCustomer = serviceCustomerRepository.findByProviderAndCode(provider.id(), code)
                .orElseThrow(() -> new ResourceNotFoundException("Service customer not found"));

        if (serviceCustomer.status() != ServiceCustomerStatus.ACTIVE) {
            throw new BusinessException("Service customer is inactive");
        }

        String billingPeriod = request.billingPeriod().trim();
        if (serviceBillRepository.existsByProviderCodeAndBillingPeriod(provider.id(), code, billingPeriod)) {
            throw new BusinessException("Service bill already exists");
        }

        ServiceBill created = serviceBillRepository.save(new ServiceBill(
                null,
                provider.id(),
                serviceCustomer.id(),
                code,
                serviceCustomer.customerName(),
                billingPeriod,
                request.amount(),
                request.currency().name(),
                request.dueDate(),
                ServiceBillStatus.PENDING,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                currentSuperadminId(),
                null,
                null
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_BILL_CREATED,
                "SERVICE_BILL",
                created.id().toString(),
                PlatformAuditPayloads.details(
                        "providerId", provider.id(),
                        "providerCode", provider.code(),
                        "serviceCustomerCode", created.serviceCustomerCode(),
                        "billingPeriod", created.billingPeriod(),
                        "amount", created.amount(),
                        "currency", created.currency(),
                        "dueDate", created.dueDate(),
                        "status", created.status(),
                        "createdBy", currentSuperadminId()
                ),
                null,
                created
        );

        return servicePaymentsMapper.toResponse(created, provider);
    }

    private String normalizeCode(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("Service customer code must not be blank");
        }

        return value.trim();
    }

    private java.util.UUID currentSuperadminId() {
        String email = securityContextFacade.getCurrentEmail();
        if (!StringUtils.hasText(email)) {
            return null;
        }

        return platformSuperadminRepository.findByEmail(email.trim().toLowerCase())
                .map(com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin::id)
                .orElse(null);
    }
}

```

### `servicepayments/application/usecase/CreateServiceCustomerUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.CreateServiceCustomerRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceCustomerResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CreateServiceCustomerUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;

    public CreateServiceCustomerUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServiceCustomerRepository serviceCustomerRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
    }

    @Transactional
    public ServiceCustomerResponse execute(CreateServiceCustomerRequest request) {
        var provider = serviceProviderRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        if (provider.status() != com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus.ACTIVE) {
            throw new BusinessException("Service provider is inactive");
        }

        String code = normalizeCode(request.serviceCustomerCode());
        if (serviceCustomerRepository.existsByProviderAndCode(provider.id(), code)) {
            throw new BusinessException("Service customer already exists");
        }

        ServiceCustomer created = serviceCustomerRepository.save(new ServiceCustomer(
                null,
                provider.id(),
                code,
                request.customerName().trim(),
                ServiceCustomerStatus.ACTIVE,
                null,
                null
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_CUSTOMER_CREATED,
                "SERVICE_CUSTOMER",
                created.id().toString(),
                PlatformAuditPayloads.details(
                        "providerId", provider.id(),
                        "providerCode", provider.code(),
                        "serviceCustomerCode", created.serviceCustomerCode(),
                        "customerName", created.customerName(),
                        "status", created.status(),
                        "createdBy", currentSuperadminId()
                ),
                null,
                created
        );

        return servicePaymentsMapper.toResponse(created, provider);
    }

    private String normalizeCode(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("Service customer code must not be blank");
        }

        return value.trim();
    }

    private java.util.UUID currentSuperadminId() {
        String email = securityContextFacade.getCurrentEmail();
        if (!StringUtils.hasText(email)) {
            return null;
        }

        return platformSuperadminRepository.findByEmail(email.trim().toLowerCase())
                .map(com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin::id)
                .orElse(null);
    }
}

```

### `servicepayments/application/usecase/CreateServiceProviderUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.CreateServiceProviderRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class CreateServiceProviderUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;

    public CreateServiceProviderUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional
    public ServiceProviderResponse execute(CreateServiceProviderRequest request) {
        String code = normalizeCode(request.code());
        if (serviceProviderRepository.existsByCode(code)) {
            throw new BusinessException("Service provider already exists");
        }

        String customerCodeLabel = StringUtils.hasText(request.serviceCustomerCodeLabel())
                ? request.serviceCustomerCodeLabel().trim()
                : "Código de cliente";

        ServiceProvider created = serviceProviderRepository.save(new ServiceProvider(
                null,
                code,
                request.name().trim(),
                request.category(),
                customerCodeLabel,
                ServiceProviderStatus.ACTIVE,
                null,
                null
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_PROVIDER_CREATED,
                "SERVICE_PROVIDER",
                created.id().toString(),
                PlatformAuditPayloads.details(
                        "code", created.code(),
                        "name", created.name(),
                        "category", created.category(),
                        "serviceCustomerCodeLabel", created.serviceCustomerCodeLabel(),
                        "status", created.status(),
                        "createdBy", securityContextFacade.getCurrentEmail()
                ),
                null,
                created
        );

        return servicePaymentsMapper.toResponse(created);
    }

    private String normalizeCode(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("Service provider code must not be blank");
        }

        return value.trim().toUpperCase();
    }
}

```

### `servicepayments/application/usecase/GetGlobalServiceBillPaymentUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillPaymentResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetGlobalServiceBillPaymentUseCase {

    private final ServiceBillPaymentRepository serviceBillPaymentRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public GetGlobalServiceBillPaymentUseCase(
            ServiceBillPaymentRepository serviceBillPaymentRepository,
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceBillPaymentRepository = serviceBillPaymentRepository;
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public ServiceBillPaymentResponse execute(UUID id) {
        var payment = serviceBillPaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service bill payment not found"));

        var bill = serviceBillRepository.findById(payment.billId())
                .orElseThrow(() -> new ResourceNotFoundException("Service bill not found"));

        var provider = serviceProviderRepository.findById(payment.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        return servicePaymentsMapper.toResponse(payment, bill, provider);
    }
}

```

### `servicepayments/application/usecase/GetServiceBillUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetServiceBillUseCase {

    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public GetServiceBillUseCase(
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public ServiceBillResponse execute(UUID id) {
        var serviceBill = serviceBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service bill not found"));

        var provider = serviceProviderRepository.findById(serviceBill.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        return servicePaymentsMapper.toResponse(serviceBill, provider);
    }
}

```

### `servicepayments/application/usecase/GetServiceCustomerUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceCustomerResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetServiceCustomerUseCase {

    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public GetServiceCustomerUseCase(
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public ServiceCustomerResponse execute(UUID id) {
        var serviceCustomer = serviceCustomerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service customer not found"));

        var provider = serviceProviderRepository.findById(serviceCustomer.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        return servicePaymentsMapper.toResponse(serviceCustomer, provider);
    }
}

```

### `servicepayments/application/usecase/GetServiceProviderUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetServiceProviderUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public GetServiceProviderUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public ServiceProviderResponse execute(UUID id) {
        return serviceProviderRepository.findById(id)
                .map(servicePaymentsMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));
    }
}

```

### `servicepayments/application/usecase/ListGlobalServiceBillPaymentsUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillPaymentFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillPaymentResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListGlobalServiceBillPaymentsUseCase {

    private final ServiceBillPaymentRepository serviceBillPaymentRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public ListGlobalServiceBillPaymentsUseCase(
            ServiceBillPaymentRepository serviceBillPaymentRepository,
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceBillPaymentRepository = serviceBillPaymentRepository;
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public Page<ServiceBillPaymentResponse> execute(PlatformServiceBillPaymentFilter filter, Pageable pageable) {
        return serviceBillPaymentRepository.findAll(filter, pageable)
                .map(payment -> serviceBillRepository.findById(payment.billId())
                        .flatMap(bill -> serviceProviderRepository.findById(payment.providerId())
                                .map(provider -> servicePaymentsMapper.toResponse(payment, bill, provider)))
                        .orElseThrow(() -> new ResourceNotFoundException("Service provider not found")));
    }
}

```

### `servicepayments/application/usecase/ListServiceBillsUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListServiceBillsUseCase {

    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public ListServiceBillsUseCase(
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public Page<ServiceBillResponse> execute(PlatformServiceBillFilter filter, Pageable pageable) {
        return serviceBillRepository.findAll(filter, pageable)
                .map(serviceBill -> serviceProviderRepository.findById(serviceBill.providerId())
                        .map(provider -> servicePaymentsMapper.toResponse(serviceBill, provider))
                        .orElseThrow(() -> new ResourceNotFoundException("Service provider not found")));
    }
}

```

### `servicepayments/application/usecase/ListServiceCustomersUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceCustomerFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceCustomerResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListServiceCustomersUseCase {

    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public ListServiceCustomersUseCase(
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public Page<ServiceCustomerResponse> execute(PlatformServiceCustomerFilter filter, Pageable pageable) {
        return serviceCustomerRepository.findAll(filter, pageable)
                .map(serviceCustomer -> serviceProviderRepository.findById(serviceCustomer.providerId())
                        .map(provider -> servicePaymentsMapper.toResponse(serviceCustomer, provider))
                        .orElseThrow(() -> new ResourceNotFoundException("Service provider not found")));
    }
}

```

### `servicepayments/application/usecase/ListServiceProvidersUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceProviderFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListServiceProvidersUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public ListServiceProvidersUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public Page<ServiceProviderResponse> execute(PlatformServiceProviderFilter filter, Pageable pageable) {
        return serviceProviderRepository.findAll(filter, pageable)
                .map(servicePaymentsMapper::toResponse);
    }
}

```

### `servicepayments/application/usecase/UpdateServiceCustomerUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceCustomerResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.UpdateServiceCustomerRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UpdateServiceCustomerUseCase {

    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;

    public UpdateServiceCustomerUseCase(
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService
    ) {
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public ServiceCustomerResponse execute(UUID id, UpdateServiceCustomerRequest request) {
        ServiceCustomer current = serviceCustomerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service customer not found"));

        if (!StringUtils.hasText(request.customerName()) && request.status() == null) {
            throw new BusinessException("At least one field must be provided");
        }

        String customerName = StringUtils.hasText(request.customerName()) ? request.customerName().trim() : current.customerName();
        var status = request.status() != null ? request.status() : current.status();

        ServiceCustomer updated = serviceCustomerRepository.save(new ServiceCustomer(
                current.id(),
                current.providerId(),
                current.serviceCustomerCode(),
                customerName,
                status,
                current.createdAt(),
                current.updatedAt()
        ));

        var provider = serviceProviderRepository.findById(current.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_CUSTOMER_UPDATED,
                "SERVICE_CUSTOMER",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "customerName", updated.customerName(),
                        "status", updated.status()
                ),
                current,
                updated
        );

        return servicePaymentsMapper.toResponse(updated, provider);
    }
}

```

### `servicepayments/application/usecase/UpdateServiceProviderUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.UpdateServiceProviderRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UpdateServiceProviderUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;

    public UpdateServiceProviderUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public ServiceProviderResponse execute(UUID id, UpdateServiceProviderRequest request) {
        ServiceProvider current = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Service provider not found"));

        String name = StringUtils.hasText(request.name()) ? request.name().trim() : current.name();
        String label = StringUtils.hasText(request.serviceCustomerCodeLabel())
                ? request.serviceCustomerCodeLabel().trim()
                : current.serviceCustomerCodeLabel();
        var category = request.category() != null ? request.category() : current.category();

        ServiceProvider updated = serviceProviderRepository.save(new ServiceProvider(
                current.id(),
                current.code(),
                name,
                category,
                label,
                current.status(),
                current.createdAt(),
                current.updatedAt()
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_PROVIDER_UPDATED,
                "SERVICE_PROVIDER",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "id", updated.id(),
                        "code", updated.code(),
                        "name", updated.name(),
                        "category", updated.category(),
                        "serviceCustomerCodeLabel", updated.serviceCustomerCodeLabel(),
                        "status", updated.status()
                ),
                current,
                updated
        );

        return servicePaymentsMapper.toResponse(updated);
    }
}

```

### `servicepayments/domain/model/ServiceBill.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ServiceBill(
        UUID id,
        UUID providerId,
        UUID serviceCustomerId,
        String serviceCustomerCode,
        String customerName,
        String billingPeriod,
        BigDecimal amount,
        String currency,
        LocalDate dueDate,
        ServiceBillStatus status,
        UUID paidByTenantId,
        String paidByTenantSlug,
        UUID paidByUserId,
        UUID paidByAccountId,
        String paidByAccountNumber,
        UUID paidTransactionId,
        Instant paidAt,
        UUID createdBySuperadminId,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `servicepayments/domain/model/ServiceBillPayment.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServiceBillPayment(
        UUID id,
        UUID billId,
        UUID providerId,
        UUID paidByTenantId,
        String paidByTenantSlug,
        UUID paidByUserId,
        UUID paidByAccountId,
        String paidByAccountNumber,
        UUID paidTransactionId,
        BigDecimal amount,
        String currency,
        String receiptNumber,
        String idempotencyKey,
        ServiceBillPaymentStatus status,
        Instant paidAt,
        Instant createdAt
) {
}

```

### `servicepayments/domain/model/ServiceBillPaymentStatus.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

public enum ServiceBillPaymentStatus {
    PAID,
    REVERSED
}

```

### `servicepayments/domain/model/ServiceBillStatus.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

public enum ServiceBillStatus {
    PENDING,
    PAID,
    EXPIRED,
    CANCELLED,
    REVERSED
}

```

### `servicepayments/domain/model/ServiceCustomer.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ServiceCustomer(
        UUID id,
        UUID providerId,
        String serviceCustomerCode,
        String customerName,
        ServiceCustomerStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `servicepayments/domain/model/ServiceCustomerStatus.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

public enum ServiceCustomerStatus {
    ACTIVE,
    INACTIVE
}

```

### `servicepayments/domain/model/ServiceProviderCategory.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

public enum ServiceProviderCategory {
    ELECTRICITY,
    WATER,
    INTERNET,
    TV_CABLE
}

```

### `servicepayments/domain/model/ServiceProvider.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ServiceProvider(
        UUID id,
        String code,
        String name,
        ServiceProviderCategory category,
        String serviceCustomerCodeLabel,
        ServiceProviderStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `servicepayments/domain/model/ServiceProviderStatus.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

public enum ServiceProviderStatus {
    ACTIVE,
    INACTIVE
}

```

### `servicepayments/domain/repository/ServiceBillPaymentRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillPaymentFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ServiceBillPaymentRepository {

    ServiceBillPayment save(ServiceBillPayment serviceBillPayment);

    Optional<ServiceBillPayment> findById(UUID id);

    Optional<ServiceBillPayment> findByBillId(UUID billId);

    Optional<ServiceBillPayment> findByIdempotencyKey(UUID paidByTenantId, UUID paidByUserId, String idempotencyKey);

    boolean existsByBillId(UUID billId);

    Page<ServiceBillPayment> findAll(PlatformServiceBillPaymentFilter filter, Pageable pageable);
}

```

### `servicepayments/domain/repository/ServiceBillRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceBillRepository {

    ServiceBill save(ServiceBill serviceBill);

    Optional<ServiceBill> findById(UUID id);

    Optional<ServiceBill> findByIdForUpdate(UUID id);

    Optional<ServiceBill> findByProviderCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod);

    boolean existsByProviderCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod);

    List<ServiceBill> findAllByProviderIdAndServiceCustomerCodeAndStatus(
            UUID providerId,
            String serviceCustomerCode,
            ServiceBillStatus status
    );

    Page<ServiceBill> findAll(PlatformServiceBillFilter filter, Pageable pageable);
}

```

### `servicepayments/domain/repository/ServiceCustomerRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceCustomerFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ServiceCustomerRepository {

    ServiceCustomer save(ServiceCustomer serviceCustomer);

    Optional<ServiceCustomer> findById(UUID id);

    Optional<ServiceCustomer> findByProviderAndCode(UUID providerId, String serviceCustomerCode);

    boolean existsByProviderAndCode(UUID providerId, String serviceCustomerCode);

    Page<ServiceCustomer> findAll(PlatformServiceCustomerFilter filter, Pageable pageable);
}

```

### `servicepayments/domain/repository/ServiceProviderRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceProviderFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ServiceProviderRepository {

    ServiceProvider save(ServiceProvider serviceProvider);

    Optional<ServiceProvider> findById(UUID id);

    Optional<ServiceProvider> findByCode(String code);

    boolean existsByCode(String code);

    Page<ServiceProvider> findAll(PlatformServiceProviderFilter filter, Pageable pageable);
}

```

### `servicepayments/infrastructure/api/PlatformServiceBillController.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/service-bills")
@SecurityRequirement(name = "bearerAuth")
public class PlatformServiceBillController {

    private final CreateServiceBillUseCase createServiceBillUseCase;
    private final ListServiceBillsUseCase listServiceBillsUseCase;
    private final GetServiceBillUseCase getServiceBillUseCase;
    private final CancelServiceBillUseCase cancelServiceBillUseCase;

    public PlatformServiceBillController(
            CreateServiceBillUseCase createServiceBillUseCase,
            ListServiceBillsUseCase listServiceBillsUseCase,
            GetServiceBillUseCase getServiceBillUseCase,
            CancelServiceBillUseCase cancelServiceBillUseCase
    ) {
        this.createServiceBillUseCase = createServiceBillUseCase;
        this.listServiceBillsUseCase = listServiceBillsUseCase;
        this.getServiceBillUseCase = getServiceBillUseCase;
        this.cancelServiceBillUseCase = cancelServiceBillUseCase;
    }

    @PostMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceBillResponse> create(@Valid @RequestBody CreateServiceBillRequest request) {
        return ApiResponse.success("Service bill created successfully", createServiceBillUseCase.execute(request));
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<ServiceBillResponse>> list(
            @ParameterObject PlatformServiceBillFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success("Service bills retrieved successfully", listServiceBillsUseCase.execute(filter, pageable));
    }

    @GetMapping("/{billId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceBillResponse> detail(@PathVariable UUID billId) {
        return ApiResponse.success("Service bill retrieved successfully", getServiceBillUseCase.execute(billId));
    }

    @PatchMapping("/{billId}/cancel")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceBillResponse> cancel(
            @PathVariable UUID billId,
            @Valid @RequestBody CancelServiceBillRequest request
    ) {
        return ApiResponse.success("Service bill cancelled successfully", cancelServiceBillUseCase.execute(billId, request));
    }
}

```

### `servicepayments/infrastructure/api/PlatformServiceBillPaymentController.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.GetGlobalServiceBillPaymentUseCase;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.ListGlobalServiceBillPaymentsUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/service-bill-payments")
@SecurityRequirement(name = "bearerAuth")
public class PlatformServiceBillPaymentController {

    private final ListGlobalServiceBillPaymentsUseCase listGlobalServiceBillPaymentsUseCase;
    private final GetGlobalServiceBillPaymentUseCase getGlobalServiceBillPaymentUseCase;

    public PlatformServiceBillPaymentController(
            ListGlobalServiceBillPaymentsUseCase listGlobalServiceBillPaymentsUseCase,
            GetGlobalServiceBillPaymentUseCase getGlobalServiceBillPaymentUseCase
    ) {
        this.listGlobalServiceBillPaymentsUseCase = listGlobalServiceBillPaymentsUseCase;
        this.getGlobalServiceBillPaymentUseCase = getGlobalServiceBillPaymentUseCase;
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<ServiceBillPaymentResponse>> list(
            @ParameterObject PlatformServiceBillPaymentFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success(
                "Service bill payments retrieved successfully",
                listGlobalServiceBillPaymentsUseCase.execute(filter, pageable)
        );
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceBillPaymentResponse> detail(@PathVariable UUID paymentId) {
        return ApiResponse.success(
                "Service bill payment retrieved successfully",
                getGlobalServiceBillPaymentUseCase.execute(paymentId)
        );
    }
}

```

### `servicepayments/infrastructure/api/PlatformServiceCustomerController.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/service-customers")
@SecurityRequirement(name = "bearerAuth")
public class PlatformServiceCustomerController {

    private final CreateServiceCustomerUseCase createServiceCustomerUseCase;
    private final ListServiceCustomersUseCase listServiceCustomersUseCase;
    private final GetServiceCustomerUseCase getServiceCustomerUseCase;
    private final UpdateServiceCustomerUseCase updateServiceCustomerUseCase;

    public PlatformServiceCustomerController(
            CreateServiceCustomerUseCase createServiceCustomerUseCase,
            ListServiceCustomersUseCase listServiceCustomersUseCase,
            GetServiceCustomerUseCase getServiceCustomerUseCase,
            UpdateServiceCustomerUseCase updateServiceCustomerUseCase
    ) {
        this.createServiceCustomerUseCase = createServiceCustomerUseCase;
        this.listServiceCustomersUseCase = listServiceCustomersUseCase;
        this.getServiceCustomerUseCase = getServiceCustomerUseCase;
        this.updateServiceCustomerUseCase = updateServiceCustomerUseCase;
    }

    @PostMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceCustomerResponse> create(@Valid @RequestBody CreateServiceCustomerRequest request) {
        return ApiResponse.success("Service customer created successfully", createServiceCustomerUseCase.execute(request));
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<ServiceCustomerResponse>> list(
            @ParameterObject PlatformServiceCustomerFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success("Service customers retrieved successfully", listServiceCustomersUseCase.execute(filter, pageable));
    }

    @GetMapping("/{serviceCustomerId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceCustomerResponse> detail(@PathVariable UUID serviceCustomerId) {
        return ApiResponse.success("Service customer retrieved successfully", getServiceCustomerUseCase.execute(serviceCustomerId));
    }

    @PatchMapping("/{serviceCustomerId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceCustomerResponse> update(
            @PathVariable UUID serviceCustomerId,
            @Valid @RequestBody UpdateServiceCustomerRequest request
    ) {
        return ApiResponse.success("Service customer updated successfully", updateServiceCustomerUseCase.execute(serviceCustomerId, request));
    }
}

```

### `servicepayments/infrastructure/api/PlatformServiceProviderController.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/service-providers")
@SecurityRequirement(name = "bearerAuth")
public class PlatformServiceProviderController {

    private final CreateServiceProviderUseCase createServiceProviderUseCase;
    private final ListServiceProvidersUseCase listServiceProvidersUseCase;
    private final GetServiceProviderUseCase getServiceProviderUseCase;
    private final UpdateServiceProviderUseCase updateServiceProviderUseCase;
    private final ChangeServiceProviderStatusUseCase changeServiceProviderStatusUseCase;

    public PlatformServiceProviderController(
            CreateServiceProviderUseCase createServiceProviderUseCase,
            ListServiceProvidersUseCase listServiceProvidersUseCase,
            GetServiceProviderUseCase getServiceProviderUseCase,
            UpdateServiceProviderUseCase updateServiceProviderUseCase,
            ChangeServiceProviderStatusUseCase changeServiceProviderStatusUseCase
    ) {
        this.createServiceProviderUseCase = createServiceProviderUseCase;
        this.listServiceProvidersUseCase = listServiceProvidersUseCase;
        this.getServiceProviderUseCase = getServiceProviderUseCase;
        this.updateServiceProviderUseCase = updateServiceProviderUseCase;
        this.changeServiceProviderStatusUseCase = changeServiceProviderStatusUseCase;
    }

    @PostMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceProviderResponse> create(@Valid @RequestBody CreateServiceProviderRequest request) {
        return ApiResponse.success("Service provider created successfully", createServiceProviderUseCase.execute(request));
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<ServiceProviderResponse>> list(
            @ParameterObject PlatformServiceProviderFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success("Service providers retrieved successfully", listServiceProvidersUseCase.execute(filter, pageable));
    }

    @GetMapping("/{providerId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceProviderResponse> detail(@PathVariable UUID providerId) {
        return ApiResponse.success("Service provider retrieved successfully", getServiceProviderUseCase.execute(providerId));
    }

    @PatchMapping("/{providerId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceProviderResponse> update(
            @PathVariable UUID providerId,
            @Valid @RequestBody UpdateServiceProviderRequest request
    ) {
        return ApiResponse.success("Service provider updated successfully", updateServiceProviderUseCase.execute(providerId, request));
    }

    @PatchMapping("/{providerId}/status")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceProviderResponse> changeStatus(
            @PathVariable UUID providerId,
            @Valid @RequestBody ChangeServiceProviderStatusRequest request
    ) {
        return ApiResponse.success(
                "Service provider status changed successfully",
                changeServiceProviderStatusUseCase.execute(providerId, request)
        );
    }
}

```

### `servicepayments/infrastructure/persistence/ServiceBillEntity.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "service_bills")
public class ServiceBillEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "service_customer_id", nullable = false)
    private UUID serviceCustomerId;

    @Column(name = "service_customer_code", nullable = false, length = 100)
    private String serviceCustomerCode;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(name = "billing_period", nullable = false, length = 20)
    private String billingPeriod;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceBillStatus status;

    @Column(name = "paid_by_tenant_id")
    private UUID paidByTenantId;

    @Column(name = "paid_by_tenant_slug", length = 100)
    private String paidByTenantSlug;

    @Column(name = "paid_by_user_id")
    private UUID paidByUserId;

    @Column(name = "paid_by_account_id")
    private UUID paidByAccountId;

    @Column(name = "paid_by_account_number", length = 50)
    private String paidByAccountNumber;

    @Column(name = "paid_transaction_id")
    private UUID paidTransactionId;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "created_by_superadmin_id")
    private UUID createdBySuperadminId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = ServiceBillStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public UUID getServiceCustomerId() {
        return serviceCustomerId;
    }

    public void setServiceCustomerId(UUID serviceCustomerId) {
        this.serviceCustomerId = serviceCustomerId;
    }

    public String getServiceCustomerCode() {
        return serviceCustomerCode;
    }

    public void setServiceCustomerCode(String serviceCustomerCode) {
        this.serviceCustomerCode = serviceCustomerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public ServiceBillStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceBillStatus status) {
        this.status = status;
    }

    public UUID getPaidByTenantId() {
        return paidByTenantId;
    }

    public void setPaidByTenantId(UUID paidByTenantId) {
        this.paidByTenantId = paidByTenantId;
    }

    public String getPaidByTenantSlug() {
        return paidByTenantSlug;
    }

    public void setPaidByTenantSlug(String paidByTenantSlug) {
        this.paidByTenantSlug = paidByTenantSlug;
    }

    public UUID getPaidByUserId() {
        return paidByUserId;
    }

    public void setPaidByUserId(UUID paidByUserId) {
        this.paidByUserId = paidByUserId;
    }

    public UUID getPaidByAccountId() {
        return paidByAccountId;
    }

    public void setPaidByAccountId(UUID paidByAccountId) {
        this.paidByAccountId = paidByAccountId;
    }

    public String getPaidByAccountNumber() {
        return paidByAccountNumber;
    }

    public void setPaidByAccountNumber(String paidByAccountNumber) {
        this.paidByAccountNumber = paidByAccountNumber;
    }

    public UUID getPaidTransactionId() {
        return paidTransactionId;
    }

    public void setPaidTransactionId(UUID paidTransactionId) {
        this.paidTransactionId = paidTransactionId;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public UUID getCreatedBySuperadminId() {
        return createdBySuperadminId;
    }

    public void setCreatedBySuperadminId(UUID createdBySuperadminId) {
        this.createdBySuperadminId = createdBySuperadminId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

```

### `servicepayments/infrastructure/persistence/ServiceBillJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface ServiceBillJpaRepository extends JpaRepository<ServiceBillEntity, UUID>, JpaSpecificationExecutor<ServiceBillEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ServiceBillEntity> findLockedById(UUID id);

    Optional<ServiceBillEntity> findByProviderIdAndServiceCustomerCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod);

    boolean existsByProviderIdAndServiceCustomerCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod);
}

```

### `servicepayments/infrastructure/persistence/ServiceBillPaymentEntity.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_bill_payments")
public class ServiceBillPaymentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "bill_id", nullable = false)
    private UUID billId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "paid_by_tenant_id", nullable = false)
    private UUID paidByTenantId;

    @Column(name = "paid_by_tenant_slug", nullable = false, length = 100)
    private String paidByTenantSlug;

    @Column(name = "paid_by_user_id", nullable = false)
    private UUID paidByUserId;

    @Column(name = "paid_by_account_id", nullable = false)
    private UUID paidByAccountId;

    @Column(name = "paid_by_account_number", nullable = false, length = 50)
    private String paidByAccountNumber;

    @Column(name = "paid_transaction_id", nullable = false)
    private UUID paidTransactionId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(name = "receipt_number", nullable = false, unique = true, length = 100)
    private String receiptNumber;

    @Column(name = "idempotency_key", nullable = false, length = 150)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceBillPaymentStatus status;

    @Column(name = "paid_at", nullable = false)
    private Instant paidAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (paidAt == null) {
            paidAt = now;
        }
        if (status == null) {
            status = ServiceBillPaymentStatus.PAID;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBillId() {
        return billId;
    }

    public void setBillId(UUID billId) {
        this.billId = billId;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public UUID getPaidByTenantId() {
        return paidByTenantId;
    }

    public void setPaidByTenantId(UUID paidByTenantId) {
        this.paidByTenantId = paidByTenantId;
    }

    public String getPaidByTenantSlug() {
        return paidByTenantSlug;
    }

    public void setPaidByTenantSlug(String paidByTenantSlug) {
        this.paidByTenantSlug = paidByTenantSlug;
    }

    public UUID getPaidByUserId() {
        return paidByUserId;
    }

    public void setPaidByUserId(UUID paidByUserId) {
        this.paidByUserId = paidByUserId;
    }

    public UUID getPaidByAccountId() {
        return paidByAccountId;
    }

    public void setPaidByAccountId(UUID paidByAccountId) {
        this.paidByAccountId = paidByAccountId;
    }

    public String getPaidByAccountNumber() {
        return paidByAccountNumber;
    }

    public void setPaidByAccountNumber(String paidByAccountNumber) {
        this.paidByAccountNumber = paidByAccountNumber;
    }

    public UUID getPaidTransactionId() {
        return paidTransactionId;
    }

    public void setPaidTransactionId(UUID paidTransactionId) {
        this.paidTransactionId = paidTransactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public ServiceBillPaymentStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceBillPaymentStatus status) {
        this.status = status;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

```

### `servicepayments/infrastructure/persistence/ServiceBillPaymentJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceBillPaymentJpaRepository extends JpaRepository<ServiceBillPaymentEntity, UUID>, JpaSpecificationExecutor<ServiceBillPaymentEntity> {

    Optional<ServiceBillPaymentEntity> findByBillId(UUID billId);

    Optional<ServiceBillPaymentEntity> findByPaidByTenantIdAndPaidByUserIdAndIdempotencyKey(UUID paidByTenantId, UUID paidByUserId, String idempotencyKey);

    boolean existsByBillId(UUID billId);
}

```

### `servicepayments/infrastructure/persistence/ServiceBillPaymentRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillPaymentFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPayment;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ServiceBillPaymentRepositoryAdapter implements ServiceBillPaymentRepository {

    private final ServiceBillPaymentJpaRepository jpaRepository;

    public ServiceBillPaymentRepositoryAdapter(ServiceBillPaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceBillPayment save(ServiceBillPayment serviceBillPayment) {
        return toDomain(jpaRepository.save(toEntity(serviceBillPayment)));
    }

    @Override
    public Optional<ServiceBillPayment> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceBillPayment> findByBillId(UUID billId) {
        return jpaRepository.findByBillId(billId).map(this::toDomain);
    }

    @Override
    public Optional<ServiceBillPayment> findByIdempotencyKey(UUID paidByTenantId, UUID paidByUserId, String idempotencyKey) {
        return jpaRepository.findByPaidByTenantIdAndPaidByUserIdAndIdempotencyKey(paidByTenantId, paidByUserId, idempotencyKey).map(this::toDomain);
    }

    @Override
    public boolean existsByBillId(UUID billId) {
        return jpaRepository.existsByBillId(billId);
    }

    @Override
    public Page<ServiceBillPayment> findAll(PlatformServiceBillPaymentFilter filter, Pageable pageable) {
        Specification<ServiceBillPaymentEntity> specification = alwaysTrue();

        if (filter != null) {
            if (filter.providerId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("providerId"), filter.providerId()));
            }
            if (StringUtils.hasText(filter.tenantSlug())) {
                String slug = filter.tenantSlug().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("paidByTenantSlug")), "%" + slug + "%"));
            }
            if (filter.userId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("paidByUserId"), filter.userId()));
            }
            if (StringUtils.hasText(filter.accountNumber())) {
                String accountNumber = filter.accountNumber().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("paidByAccountNumber")), "%" + accountNumber + "%"));
            }
            if (filter.billId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("billId"), filter.billId()));
            }
            if (StringUtils.hasText(filter.receiptNumber())) {
                String receipt = filter.receiptNumber().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("receiptNumber")), "%" + receipt + "%"));
            }
            if (filter.paidAtFrom() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.greaterThanOrEqualTo(root.get("paidAt"), filter.paidAtFrom()));
            }
            if (filter.paidAtTo() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.lessThanOrEqualTo(root.get("paidAt"), filter.paidAtTo()));
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private Specification<ServiceBillPaymentEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private ServiceBillPaymentEntity toEntity(ServiceBillPayment payment) {
        ServiceBillPaymentEntity entity = new ServiceBillPaymentEntity();
        entity.setId(payment.id());
        entity.setBillId(payment.billId());
        entity.setProviderId(payment.providerId());
        entity.setPaidByTenantId(payment.paidByTenantId());
        entity.setPaidByTenantSlug(payment.paidByTenantSlug());
        entity.setPaidByUserId(payment.paidByUserId());
        entity.setPaidByAccountId(payment.paidByAccountId());
        entity.setPaidByAccountNumber(payment.paidByAccountNumber());
        entity.setPaidTransactionId(payment.paidTransactionId());
        entity.setAmount(payment.amount());
        entity.setCurrency(payment.currency());
        entity.setReceiptNumber(payment.receiptNumber());
        entity.setIdempotencyKey(payment.idempotencyKey());
        entity.setStatus(payment.status());
        entity.setPaidAt(payment.paidAt());
        return entity;
    }

    private ServiceBillPayment toDomain(ServiceBillPaymentEntity entity) {
        return new ServiceBillPayment(
                entity.getId(),
                entity.getBillId(),
                entity.getProviderId(),
                entity.getPaidByTenantId(),
                entity.getPaidByTenantSlug(),
                entity.getPaidByUserId(),
                entity.getPaidByAccountId(),
                entity.getPaidByAccountNumber(),
                entity.getPaidTransactionId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getReceiptNumber(),
                entity.getIdempotencyKey(),
                entity.getStatus(),
                entity.getPaidAt(),
                entity.getCreatedAt()
        );
    }
}

```

### `servicepayments/infrastructure/persistence/ServiceBillRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ServiceBillRepositoryAdapter implements ServiceBillRepository {

    private final ServiceBillJpaRepository jpaRepository;

    public ServiceBillRepositoryAdapter(ServiceBillJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceBill save(ServiceBill serviceBill) {
        return toDomain(jpaRepository.save(toEntity(serviceBill)));
    }

    @Override
    public Optional<ServiceBill> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceBill> findByIdForUpdate(UUID id) {
        return jpaRepository.findLockedById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceBill> findByProviderCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod) {
        return jpaRepository.findByProviderIdAndServiceCustomerCodeAndBillingPeriod(providerId, serviceCustomerCode, billingPeriod).map(this::toDomain);
    }

    @Override
    public boolean existsByProviderCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod) {
        return jpaRepository.existsByProviderIdAndServiceCustomerCodeAndBillingPeriod(providerId, serviceCustomerCode, billingPeriod);
    }

    @Override
    public List<ServiceBill> findAllByProviderIdAndServiceCustomerCodeAndStatus(
            UUID providerId,
            String serviceCustomerCode,
            ServiceBillStatus status
    ) {
        Specification<ServiceBillEntity> specification = alwaysTrue()
                .and((root, query, cb) -> cb.equal(root.get("providerId"), providerId))
                .and((root, query, cb) -> cb.equal(root.get("serviceCustomerCode"), serviceCustomerCode))
                .and((root, query, cb) -> cb.equal(root.get("status"), status));

        return jpaRepository.findAll(specification).stream()
                .map(this::toDomain)
                .sorted(
                        Comparator.comparing(ServiceBill::dueDate, Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing(ServiceBill::billingPeriod, Comparator.nullsLast(Comparator.naturalOrder()))
                                .thenComparing(ServiceBill::id, Comparator.nullsLast(Comparator.naturalOrder()))
                )
                .toList();
    }

    @Override
    public Page<ServiceBill> findAll(PlatformServiceBillFilter filter, Pageable pageable) {
        Specification<ServiceBillEntity> specification = alwaysTrue();

        if (filter != null) {
            if (filter.providerId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("providerId"), filter.providerId()));
            }
            if (StringUtils.hasText(filter.serviceCustomerCode())) {
                String code = filter.serviceCustomerCode().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("serviceCustomerCode")), "%" + code + "%"));
            }
            if (filter.status() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("status"), filter.status()));
            }
            if (StringUtils.hasText(filter.billingPeriod())) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("billingPeriod"), filter.billingPeriod().trim()));
            }
            if (filter.dueDateFrom() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.greaterThanOrEqualTo(root.get("dueDate"), filter.dueDateFrom()));
            }
            if (filter.dueDateTo() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.lessThanOrEqualTo(root.get("dueDate"), filter.dueDateTo()));
            }
            if (StringUtils.hasText(filter.paidByTenantSlug())) {
                String slug = filter.paidByTenantSlug().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("paidByTenantSlug")), "%" + slug + "%"));
            }
            if (StringUtils.hasText(filter.search())) {
                String like = "%" + filter.search().trim().toLowerCase() + "%";
                specification = specification.and((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.like(cb.lower(root.get("serviceCustomerCode")), like));
                    predicates.add(cb.like(cb.lower(root.get("customerName")), like));
                    predicates.add(cb.like(cb.lower(root.get("billingPeriod")), like));
                    return cb.or(predicates.toArray(Predicate[]::new));
                });
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private Specification<ServiceBillEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private ServiceBillEntity toEntity(ServiceBill serviceBill) {
        ServiceBillEntity entity = new ServiceBillEntity();
        entity.setId(serviceBill.id());
        entity.setProviderId(serviceBill.providerId());
        entity.setServiceCustomerId(serviceBill.serviceCustomerId());
        entity.setServiceCustomerCode(serviceBill.serviceCustomerCode());
        entity.setCustomerName(serviceBill.customerName());
        entity.setBillingPeriod(serviceBill.billingPeriod());
        entity.setAmount(serviceBill.amount());
        entity.setCurrency(serviceBill.currency());
        entity.setDueDate(serviceBill.dueDate());
        entity.setStatus(serviceBill.status());
        entity.setPaidByTenantId(serviceBill.paidByTenantId());
        entity.setPaidByTenantSlug(serviceBill.paidByTenantSlug());
        entity.setPaidByUserId(serviceBill.paidByUserId());
        entity.setPaidByAccountId(serviceBill.paidByAccountId());
        entity.setPaidByAccountNumber(serviceBill.paidByAccountNumber());
        entity.setPaidTransactionId(serviceBill.paidTransactionId());
        entity.setPaidAt(serviceBill.paidAt());
        entity.setCreatedBySuperadminId(serviceBill.createdBySuperadminId());
        return entity;
    }

    private ServiceBill toDomain(ServiceBillEntity entity) {
        return new ServiceBill(
                entity.getId(),
                entity.getProviderId(),
                entity.getServiceCustomerId(),
                entity.getServiceCustomerCode(),
                entity.getCustomerName(),
                entity.getBillingPeriod(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getDueDate(),
                entity.getStatus(),
                entity.getPaidByTenantId(),
                entity.getPaidByTenantSlug(),
                entity.getPaidByUserId(),
                entity.getPaidByAccountId(),
                entity.getPaidByAccountNumber(),
                entity.getPaidTransactionId(),
                entity.getPaidAt(),
                entity.getCreatedBySuperadminId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

```

### `servicepayments/infrastructure/persistence/ServiceCustomerEntity.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_customers")
public class ServiceCustomerEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "service_customer_code", nullable = false, length = 100)
    private String serviceCustomerCode;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceCustomerStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = ServiceCustomerStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public String getServiceCustomerCode() {
        return serviceCustomerCode;
    }

    public void setServiceCustomerCode(String serviceCustomerCode) {
        this.serviceCustomerCode = serviceCustomerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public ServiceCustomerStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceCustomerStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

```

### `servicepayments/infrastructure/persistence/ServiceCustomerJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceCustomerJpaRepository extends JpaRepository<ServiceCustomerEntity, UUID>, JpaSpecificationExecutor<ServiceCustomerEntity> {

    Optional<ServiceCustomerEntity> findByProviderIdAndServiceCustomerCode(UUID providerId, String serviceCustomerCode);

    boolean existsByProviderIdAndServiceCustomerCode(UUID providerId, String serviceCustomerCode);
}

```

### `servicepayments/infrastructure/persistence/ServiceCustomerRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceCustomerFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ServiceCustomerRepositoryAdapter implements ServiceCustomerRepository {

    private final ServiceCustomerJpaRepository jpaRepository;

    public ServiceCustomerRepositoryAdapter(ServiceCustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceCustomer save(ServiceCustomer serviceCustomer) {
        return toDomain(jpaRepository.save(toEntity(serviceCustomer)));
    }

    @Override
    public Optional<ServiceCustomer> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceCustomer> findByProviderAndCode(UUID providerId, String serviceCustomerCode) {
        return jpaRepository.findByProviderIdAndServiceCustomerCode(providerId, serviceCustomerCode).map(this::toDomain);
    }

    @Override
    public boolean existsByProviderAndCode(UUID providerId, String serviceCustomerCode) {
        return jpaRepository.existsByProviderIdAndServiceCustomerCode(providerId, serviceCustomerCode);
    }

    @Override
    public Page<ServiceCustomer> findAll(PlatformServiceCustomerFilter filter, Pageable pageable) {
        Specification<ServiceCustomerEntity> specification = alwaysTrue();

        if (filter != null) {
            if (filter.providerId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("providerId"), filter.providerId()));
            }
            if (filter.status() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("status"), filter.status()));
            }
            if (StringUtils.hasText(filter.serviceCustomerCode())) {
                String code = filter.serviceCustomerCode().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("serviceCustomerCode")), "%" + code + "%"));
            }
            if (StringUtils.hasText(filter.search())) {
                String like = "%" + filter.search().trim().toLowerCase() + "%";
                specification = specification.and((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.like(cb.lower(root.get("serviceCustomerCode")), like));
                    predicates.add(cb.like(cb.lower(root.get("customerName")), like));
                    return cb.or(predicates.toArray(Predicate[]::new));
                });
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private Specification<ServiceCustomerEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private ServiceCustomerEntity toEntity(ServiceCustomer serviceCustomer) {
        ServiceCustomerEntity entity = new ServiceCustomerEntity();
        entity.setId(serviceCustomer.id());
        entity.setProviderId(serviceCustomer.providerId());
        entity.setServiceCustomerCode(serviceCustomer.serviceCustomerCode());
        entity.setCustomerName(serviceCustomer.customerName());
        entity.setStatus(serviceCustomer.status());
        return entity;
    }

    private ServiceCustomer toDomain(ServiceCustomerEntity entity) {
        return new ServiceCustomer(
                entity.getId(),
                entity.getProviderId(),
                entity.getServiceCustomerCode(),
                entity.getCustomerName(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

```

### `servicepayments/infrastructure/persistence/ServiceProviderEntity.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_providers")
public class ServiceProviderEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ServiceProviderCategory category;

    @Column(name = "service_customer_code_label", nullable = false, length = 100)
    private String serviceCustomerCodeLabel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceProviderStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = ServiceProviderStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceProviderCategory getCategory() {
        return category;
    }

    public void setCategory(ServiceProviderCategory category) {
        this.category = category;
    }

    public String getServiceCustomerCodeLabel() {
        return serviceCustomerCodeLabel;
    }

    public void setServiceCustomerCodeLabel(String serviceCustomerCodeLabel) {
        this.serviceCustomerCodeLabel = serviceCustomerCodeLabel;
    }

    public ServiceProviderStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceProviderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

```

### `servicepayments/infrastructure/persistence/ServiceProviderJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceProviderJpaRepository extends JpaRepository<ServiceProviderEntity, UUID>, JpaSpecificationExecutor<ServiceProviderEntity> {

    Optional<ServiceProviderEntity> findByCode(String code);

    boolean existsByCode(String code);
}

```

### `servicepayments/infrastructure/persistence/ServiceProviderRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceProviderFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ServiceProviderRepositoryAdapter implements ServiceProviderRepository {

    private final ServiceProviderJpaRepository jpaRepository;

    public ServiceProviderRepositoryAdapter(ServiceProviderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceProvider save(ServiceProvider serviceProvider) {
        return toDomain(jpaRepository.save(toEntity(serviceProvider)));
    }

    @Override
    public Optional<ServiceProvider> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceProvider> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public Page<ServiceProvider> findAll(PlatformServiceProviderFilter filter, Pageable pageable) {
        Specification<ServiceProviderEntity> specification = alwaysTrue();

        if (filter != null) {
            if (filter.category() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("category"), filter.category()));
            }

            if (filter.status() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("status"), filter.status()));
            }

            if (StringUtils.hasText(filter.search())) {
                String like = "%" + filter.search().trim().toLowerCase() + "%";
                specification = specification.and((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.like(cb.lower(root.get("code")), like));
                    predicates.add(cb.like(cb.lower(root.get("name")), like));
                    predicates.add(cb.like(cb.lower(root.get("serviceCustomerCodeLabel")), like));
                    return cb.or(predicates.toArray(Predicate[]::new));
                });
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private Specification<ServiceProviderEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private ServiceProviderEntity toEntity(ServiceProvider serviceProvider) {
        ServiceProviderEntity entity = new ServiceProviderEntity();
        entity.setId(serviceProvider.id());
        entity.setCode(serviceProvider.code());
        entity.setName(serviceProvider.name());
        entity.setCategory(serviceProvider.category());
        entity.setServiceCustomerCodeLabel(serviceProvider.serviceCustomerCodeLabel());
        entity.setStatus(serviceProvider.status());
        return entity;
    }

    private ServiceProvider toDomain(ServiceProviderEntity entity) {
        return new ServiceProvider(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getCategory(),
                entity.getServiceCustomerCodeLabel(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

```

### `subscriptions/application/dto/AssignPlatformSubscriptionRequest.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignPlatformSubscriptionRequest(
        @NotNull
        UUID tenantId,

        @NotBlank
        String planCode,

        Integer overrideTrialDays
) {
}

```

### `subscriptions/application/dto/PlatformSubscriptionResponse.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PlatformSubscriptionResponse(
        UUID id,
        UUID tenantId,
        String tenantName,
        String tenantSlug,
        UUID planId,
        String planCode,
        String planName,
        String planType,
        int maxUsers,
        int maxRoles,
        String status,
        boolean trial,
        boolean currentSubscription,
        Instant startedAt,
        Instant expiresAt,
        Long remainingDays,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `subscriptions/application/mapper/PlatformSubscriptionMapper.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.application.mapper;

import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.subscriptions.application.dto.PlatformSubscriptionResponse;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class PlatformSubscriptionMapper {

    public PlatformSubscriptionResponse toResponse(
            PlatformSubscription subscription,
            PlatformTenant tenant,
            PlatformPlan plan
    ) {
        Long remainingDays = null;

        if (subscription.expiresAt() != null) {
            long days = Duration.between(Instant.now(), subscription.expiresAt()).toDays();
            remainingDays = Math.max(days, 0);
        }

        return new PlatformSubscriptionResponse(
                subscription.id(),
                subscription.tenantId(),
                tenant.name(),
                tenant.slug(),
                subscription.planId(),
                plan.code(),
                plan.name(),
                plan.planType(),
                plan.maxUsers(),
                plan.maxRoles(),
                subscription.status().name(),
                subscription.trial(),
                subscription.currentSubscription(),
                subscription.startedAt(),
                subscription.expiresAt(),
                remainingDays,
                subscription.createdAt(),
                subscription.updatedAt()
        );
    }
}

```

### `subscriptions/application/service/PlatformSubscriptionLifecycleService.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.application.service;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscriptionStatus;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PlatformSubscriptionLifecycleService {

    private final PlatformSubscriptionRepository platformSubscriptionRepository;
    private final AuditTrailService auditTrailService;

    public PlatformSubscriptionLifecycleService(
            PlatformSubscriptionRepository platformSubscriptionRepository,
            AuditTrailService auditTrailService
    ) {
        this.platformSubscriptionRepository = platformSubscriptionRepository;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public void refreshExpiredSubscriptions() {
        List<PlatformSubscription> subscriptions = platformSubscriptionRepository.findAll();

        for (PlatformSubscription subscription : subscriptions) {
            if (!subscription.currentSubscription()) {
                continue;
            }

            if (subscription.expiresAt() == null) {
                continue;
            }

            if (subscription.status() == PlatformSubscriptionStatus.EXPIRED
                    || subscription.status() == PlatformSubscriptionStatus.CANCELLED) {
                continue;
            }

            if (subscription.expiresAt().isBefore(Instant.now())) {
                PlatformSubscription expired = new PlatformSubscription(
                        subscription.id(),
                        subscription.tenantId(),
                        subscription.planId(),
                        PlatformSubscriptionStatus.EXPIRED,
                        subscription.trial(),
                        subscription.currentSubscription(),
                        subscription.startedAt(),
                        subscription.expiresAt(),
                        subscription.createdAt(),
                        subscription.updatedAt()
                );

                platformSubscriptionRepository.save(expired);

                auditTrailService.recordPlatformEvent(
                        AuditEventTypes.SUBSCRIPTION_EXPIRED,
                        "TENANT_SUBSCRIPTION",
                        expired.id().toString(),
                        PlatformAuditPayloads.details(
                                "tenantId", expired.tenantId(),
                                "planId", expired.planId(),
                                "status", expired.status().name(),
                                "expiresAt", expired.expiresAt()
                        ),
                        PlatformAuditPayloads.subscriptionState(subscription),
                        PlatformAuditPayloads.subscriptionState(expired)
                );
            }
        }
    }
}

```

### `subscriptions/application/service/PlatformSubscriptionProvisioningService.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.application.service;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.plans.domain.exception.PlatformPlanNotFoundException;
import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscriptionStatus;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import com.financesystem.finance_api.modules.platform.tenants.domain.exception.PlatformTenantNotFoundException;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class PlatformSubscriptionProvisioningService {

    private final PlatformSubscriptionRepository platformSubscriptionRepository;
    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformPlanRepository platformPlanRepository;
    private final AuditTrailService auditTrailService;

    public PlatformSubscriptionProvisioningService(
            PlatformSubscriptionRepository platformSubscriptionRepository,
            PlatformTenantRepository platformTenantRepository,
            PlatformPlanRepository platformPlanRepository,
            AuditTrailService auditTrailService
    ) {
        this.platformSubscriptionRepository = platformSubscriptionRepository;
        this.platformTenantRepository = platformTenantRepository;
        this.platformPlanRepository = platformPlanRepository;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public PlatformSubscription assignCurrentSubscription(
            UUID tenantId,
            String requestedPlanCode,
            Integer overrideTrialDays,
            boolean recordAudit
    ) {
        PlatformTenant tenant = platformTenantRepository.findById(tenantId)
                .orElseThrow(() -> new PlatformTenantNotFoundException("Tenant not found with id: " + tenantId));

        String normalizedPlanCode = requestedPlanCode == null || requestedPlanCode.isBlank()
                ? "DEMO"
                : requestedPlanCode.trim().toUpperCase();

        PlatformPlan plan = platformPlanRepository.findByCode(normalizedPlanCode)
                .filter(PlatformPlan::active)
                .orElseThrow(() -> new PlatformPlanNotFoundException(
                        "Active platform plan not found for code: " + normalizedPlanCode
                ));

        Instant startedAt = Instant.now();
        PlatformSubscriptionStatus status;
        boolean isTrial;
        Instant expiresAt = null;
        PlatformSubscription previousCurrentSubscription = platformSubscriptionRepository.findCurrentByTenantId(tenant.id()).orElse(null);

        if ("DEMO".equalsIgnoreCase(plan.planType())) {
            int effectiveTrialDays = overrideTrialDays != null
                    ? overrideTrialDays
                    : (plan.trialDays() != null ? plan.trialDays() : 10);

            if (effectiveTrialDays <= 0) {
                throw new BusinessException("Trial days must be greater than zero");
            }

            status = PlatformSubscriptionStatus.TRIAL;
            isTrial = true;
            expiresAt = startedAt.plus(effectiveTrialDays, ChronoUnit.DAYS);
        } else {
            status = PlatformSubscriptionStatus.ACTIVE;
            isTrial = false;
        }

        platformSubscriptionRepository.clearCurrentSubscriptions(tenant.id());

        PlatformSubscription subscriptionToCreate = new PlatformSubscription(
                null,
                tenant.id(),
                plan.id(),
                status,
                isTrial,
                true,
                startedAt,
                expiresAt,
                null,
                null
        );

        PlatformSubscription created = platformSubscriptionRepository.save(subscriptionToCreate);

        PlatformTenant updatedTenant = new PlatformTenant(
                tenant.id(),
                tenant.name(),
                tenant.slug(),
                tenant.schemaName(),
                tenant.status(),
                plan.id(),
                tenant.active(),
                tenant.createdAt(),
                tenant.updatedAt()
        );

        platformTenantRepository.save(updatedTenant);

        if (recordAudit) {
            auditTrailService.recordPlatformEvent(
                    AuditEventTypes.SUBSCRIPTION_ASSIGNED,
                    "TENANT_SUBSCRIPTION",
                    created.id().toString(),
                    PlatformAuditPayloads.details(
                            "tenantId", tenant.id().toString(),
                            "tenantSlug", tenant.slug(),
                            "planCode", plan.code(),
                            "planType", plan.planType(),
                            "status", created.status().name(),
                            "trial", created.trial(),
                            "currentSubscription", created.currentSubscription(),
                            "expiresAt", created.expiresAt()
                    ),
                    previousCurrentSubscription,
                    created
            );
        }

        return created;
    }
}

```

### `subscriptions/application/service/TenantPlanEnforcementService.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.application.service;

import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.exception.PlatformSubscriptionNotFoundException;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.exception.TenantSubscriptionAccessDeniedException;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscriptionStatus;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.TenantSubscriptionPolicySnapshot;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import com.financesystem.finance_api.modules.platform.tenants.domain.exception.PlatformTenantNotFoundException;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;

@Service
public class TenantPlanEnforcementService {

    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformSubscriptionRepository platformSubscriptionRepository;
    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformSubscriptionLifecycleService platformSubscriptionLifecycleService;

    public TenantPlanEnforcementService(
            PlatformTenantRepository platformTenantRepository,
            PlatformSubscriptionRepository platformSubscriptionRepository,
            PlatformPlanRepository platformPlanRepository,
            PlatformSubscriptionLifecycleService platformSubscriptionLifecycleService
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.platformSubscriptionRepository = platformSubscriptionRepository;
        this.platformPlanRepository = platformPlanRepository;
        this.platformSubscriptionLifecycleService = platformSubscriptionLifecycleService;
    }

    public TenantSubscriptionPolicySnapshot resolveCurrentTenantPolicy() {
        platformSubscriptionLifecycleService.refreshExpiredSubscriptions();

        String tenantSlug = TenantContextHolder.getRequired().tenantSlug();

        PlatformTenant tenant = platformTenantRepository.findBySlug(tenantSlug)
                .orElseThrow(() -> new PlatformTenantNotFoundException(
                "Tenant not found for slug: " + tenantSlug
        ));

        PlatformSubscription subscription = platformSubscriptionRepository.findCurrentByTenantId(tenant.id())
                .orElseThrow(() -> new PlatformSubscriptionNotFoundException(
                "Current subscription not found for tenant: " + tenantSlug
        ));

        PlatformPlan plan = platformPlanRepository.findById(subscription.planId())
                .orElseThrow(() -> new PlatformSubscriptionNotFoundException(
                "Plan not found for current subscription"
        ));

        return new TenantSubscriptionPolicySnapshot(
                tenant.id(),
                tenant.slug(),
                tenant.active(),
                tenant.status().name(),
                subscription.id(),
                subscription.status(),
                subscription.trial(),
                subscription.expiresAt(),
                plan.id(),
                plan.code(),
                plan.planType(),
                plan.maxUsers(),
                plan.maxRoles()
        );
    }

    public void assertCanCreateUser(long currentActiveUsers) {
        TenantSubscriptionPolicySnapshot policy = resolveCurrentTenantPolicy();
        assertTenantOperational(policy);

        if (currentActiveUsers >= policy.maxUsers()) {
            throw new TenantSubscriptionAccessDeniedException(
                    "Your current plan '" + policy.planCode() + "' allows a maximum of "
                    + policy.maxUsers() + " active users. Upgrade is required to create more users."
            );
        }
    }

    public void assertCanActivateUser(long currentActiveUsers) {
        TenantSubscriptionPolicySnapshot policy = resolveCurrentTenantPolicy();
        assertTenantOperational(policy);

        if (currentActiveUsers >= policy.maxUsers()) {
            throw new TenantSubscriptionAccessDeniedException(
                    "Your current plan '" + policy.planCode() + "' allows a maximum of "
                    + policy.maxUsers() + " active users. Upgrade is required to activate more users."
            );
        }
    }

    public void assertCanCreateRole(long currentActiveCustomRoles) {
        TenantSubscriptionPolicySnapshot policy = resolveCurrentTenantPolicy();
        assertTenantOperational(policy);

        if (currentActiveCustomRoles >= policy.maxRoles()) {
            throw new TenantSubscriptionAccessDeniedException(
                    "Your current plan '" + policy.planCode() + "' allows a maximum of "
                    + policy.maxRoles() + " active custom roles. Upgrade is required to create more roles."
            );
        }
    }

    public void assertCanActivateRole(long currentActiveCustomRoles) {
        TenantSubscriptionPolicySnapshot policy = resolveCurrentTenantPolicy();
        assertTenantOperational(policy);

        if (currentActiveCustomRoles >= policy.maxRoles()) {
            throw new TenantSubscriptionAccessDeniedException(
                    "Your current plan '" + policy.planCode() + "' allows a maximum of "
                    + policy.maxRoles() + " active custom roles. Upgrade is required to activate more roles."
            );
        }
    }

    private void assertTenantOperational(TenantSubscriptionPolicySnapshot policy) {
        if (!policy.tenantActive()) {
            throw new TenantSubscriptionAccessDeniedException(
                    "The tenant is inactive and cannot perform this operation"
            );
        }

        if (policy.subscriptionStatus() != PlatformSubscriptionStatus.ACTIVE
                && policy.subscriptionStatus() != PlatformSubscriptionStatus.TRIAL) {
            throw new TenantSubscriptionAccessDeniedException(
                    "The current subscription status is '" + policy.subscriptionStatus().name()
                    + "'. Upgrade or reactivate the subscription to continue."
            );
        }
    }
}

```

### `subscriptions/application/usecase/AssignPlatformSubscriptionUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.application.usecase;

import com.financesystem.finance_api.modules.platform.plans.domain.model.PlatformPlan;
import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.dto.AssignPlatformSubscriptionRequest;
import com.financesystem.finance_api.modules.platform.subscriptions.application.dto.PlatformSubscriptionResponse;
import com.financesystem.finance_api.modules.platform.subscriptions.application.mapper.PlatformSubscriptionMapper;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.PlatformSubscriptionLifecycleService;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.PlatformSubscriptionProvisioningService;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignPlatformSubscriptionUseCase {

    private final PlatformSubscriptionProvisioningService provisioningService;
    private final PlatformSubscriptionLifecycleService lifecycleService;
    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformSubscriptionMapper platformSubscriptionMapper;

    public AssignPlatformSubscriptionUseCase(
            PlatformSubscriptionProvisioningService provisioningService,
            PlatformSubscriptionLifecycleService lifecycleService,
            PlatformTenantRepository platformTenantRepository,
            PlatformPlanRepository platformPlanRepository,
            PlatformSubscriptionMapper platformSubscriptionMapper
    ) {
        this.provisioningService = provisioningService;
        this.lifecycleService = lifecycleService;
        this.platformTenantRepository = platformTenantRepository;
        this.platformPlanRepository = platformPlanRepository;
        this.platformSubscriptionMapper = platformSubscriptionMapper;
    }

    @Transactional
    public PlatformSubscriptionResponse execute(AssignPlatformSubscriptionRequest request) {
        lifecycleService.refreshExpiredSubscriptions();

        PlatformSubscription subscription = provisioningService.assignCurrentSubscription(
                request.tenantId(),
                request.planCode(),
                request.overrideTrialDays(),
                true
        );

        PlatformTenant tenant = platformTenantRepository.findById(subscription.tenantId()).orElseThrow();
        PlatformPlan plan = platformPlanRepository.findById(subscription.planId()).orElseThrow();

        return platformSubscriptionMapper.toResponse(subscription, tenant, plan);
    }
}

```

### `subscriptions/application/usecase/GetCurrentTenantSubscriptionUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.application.usecase;

import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.dto.PlatformSubscriptionResponse;
import com.financesystem.finance_api.modules.platform.subscriptions.application.mapper.PlatformSubscriptionMapper;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.PlatformSubscriptionLifecycleService;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.exception.PlatformSubscriptionNotFoundException;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import com.financesystem.finance_api.modules.platform.tenants.domain.exception.PlatformTenantNotFoundException;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentTenantSubscriptionUseCase {

    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformSubscriptionRepository platformSubscriptionRepository;
    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformSubscriptionMapper platformSubscriptionMapper;
    private final PlatformSubscriptionLifecycleService lifecycleService;

    public GetCurrentTenantSubscriptionUseCase(
            PlatformTenantRepository platformTenantRepository,
            PlatformSubscriptionRepository platformSubscriptionRepository,
            PlatformPlanRepository platformPlanRepository,
            PlatformSubscriptionMapper platformSubscriptionMapper,
            PlatformSubscriptionLifecycleService lifecycleService
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.platformSubscriptionRepository = platformSubscriptionRepository;
        this.platformPlanRepository = platformPlanRepository;
        this.platformSubscriptionMapper = platformSubscriptionMapper;
        this.lifecycleService = lifecycleService;
    }

    public PlatformSubscriptionResponse execute(String tenantSlug) {
        lifecycleService.refreshExpiredSubscriptions();

        PlatformTenant tenant = platformTenantRepository.findBySlug(tenantSlug)
                .orElseThrow(() -> new PlatformTenantNotFoundException(
                        "Tenant not found for slug: " + tenantSlug
                ));

        PlatformSubscription subscription = platformSubscriptionRepository.findCurrentByTenantId(tenant.id())
                .orElseThrow(() -> new PlatformSubscriptionNotFoundException(
                        "Current subscription not found for tenant: " + tenantSlug
                ));

        return platformSubscriptionMapper.toResponse(
                subscription,
                tenant,
                platformPlanRepository.findById(subscription.planId()).orElseThrow()
        );
    }
}

```

### `subscriptions/application/usecase/GetPlatformSubscriptionByIdUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.application.usecase;

import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.dto.PlatformSubscriptionResponse;
import com.financesystem.finance_api.modules.platform.subscriptions.application.mapper.PlatformSubscriptionMapper;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.PlatformSubscriptionLifecycleService;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.exception.PlatformSubscriptionNotFoundException;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetPlatformSubscriptionByIdUseCase {

    private final PlatformSubscriptionRepository platformSubscriptionRepository;
    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformSubscriptionMapper platformSubscriptionMapper;
    private final PlatformSubscriptionLifecycleService lifecycleService;

    public GetPlatformSubscriptionByIdUseCase(
            PlatformSubscriptionRepository platformSubscriptionRepository,
            PlatformTenantRepository platformTenantRepository,
            PlatformPlanRepository platformPlanRepository,
            PlatformSubscriptionMapper platformSubscriptionMapper,
            PlatformSubscriptionLifecycleService lifecycleService
    ) {
        this.platformSubscriptionRepository = platformSubscriptionRepository;
        this.platformTenantRepository = platformTenantRepository;
        this.platformPlanRepository = platformPlanRepository;
        this.platformSubscriptionMapper = platformSubscriptionMapper;
        this.lifecycleService = lifecycleService;
    }

    public PlatformSubscriptionResponse execute(UUID id) {
        lifecycleService.refreshExpiredSubscriptions();

        PlatformSubscription subscription = platformSubscriptionRepository.findById(id)
                .orElseThrow(() -> new PlatformSubscriptionNotFoundException(
                        "Platform subscription not found with id: " + id
                ));

        return platformSubscriptionMapper.toResponse(
                subscription,
                platformTenantRepository.findById(subscription.tenantId()).orElseThrow(),
                platformPlanRepository.findById(subscription.planId()).orElseThrow()
        );
    }
}

```

### `subscriptions/application/usecase/ListPlatformSubscriptionsUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.application.usecase;

import com.financesystem.finance_api.modules.platform.plans.domain.repository.PlatformPlanRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.dto.PlatformSubscriptionResponse;
import com.financesystem.finance_api.modules.platform.subscriptions.application.mapper.PlatformSubscriptionMapper;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.PlatformSubscriptionLifecycleService;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListPlatformSubscriptionsUseCase {

    private final PlatformSubscriptionRepository platformSubscriptionRepository;
    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformPlanRepository platformPlanRepository;
    private final PlatformSubscriptionMapper platformSubscriptionMapper;
    private final PlatformSubscriptionLifecycleService lifecycleService;

    public ListPlatformSubscriptionsUseCase(
            PlatformSubscriptionRepository platformSubscriptionRepository,
            PlatformTenantRepository platformTenantRepository,
            PlatformPlanRepository platformPlanRepository,
            PlatformSubscriptionMapper platformSubscriptionMapper,
            PlatformSubscriptionLifecycleService lifecycleService
    ) {
        this.platformSubscriptionRepository = platformSubscriptionRepository;
        this.platformTenantRepository = platformTenantRepository;
        this.platformPlanRepository = platformPlanRepository;
        this.platformSubscriptionMapper = platformSubscriptionMapper;
        this.lifecycleService = lifecycleService;
    }

    public List<PlatformSubscriptionResponse> execute() {
        lifecycleService.refreshExpiredSubscriptions();

        return platformSubscriptionRepository.findAll()
                .stream()
                .map(subscription -> platformSubscriptionMapper.toResponse(
                        subscription,
                        platformTenantRepository.findById(subscription.tenantId()).orElseThrow(),
                        platformPlanRepository.findById(subscription.planId()).orElseThrow()
                ))
                .toList();
    }
}

```

### `subscriptions/domain/exception/PlatformSubscriptionNotFoundException.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class PlatformSubscriptionNotFoundException extends ResourceNotFoundException {

    public PlatformSubscriptionNotFoundException(String message) {
        super(message);
    }
}

```

### `subscriptions/domain/exception/TenantSubscriptionAccessDeniedException.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class TenantSubscriptionAccessDeniedException extends BusinessException {

    public TenantSubscriptionAccessDeniedException(String message) {
        super(message);
    }
}

```

### `subscriptions/domain/model/PlatformSubscription.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PlatformSubscription(
        UUID id,
        UUID tenantId,
        UUID planId,
        PlatformSubscriptionStatus status,
        boolean trial,
        boolean currentSubscription,
        Instant startedAt,
        Instant expiresAt,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `subscriptions/domain/model/PlatformSubscriptionStatus.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.domain.model;

public enum PlatformSubscriptionStatus {
    TRIAL,
    ACTIVE,
    EXPIRED,
    CANCELLED
}

```

### `subscriptions/domain/model/TenantSubscriptionPolicySnapshot.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.domain.model;

import java.time.Instant;
import java.util.UUID;

public record TenantSubscriptionPolicySnapshot(
        UUID tenantId,
        String tenantSlug,
        boolean tenantActive,
        String tenantStatus,
        UUID subscriptionId,
        PlatformSubscriptionStatus subscriptionStatus,
        boolean trial,
        Instant expiresAt,
        UUID planId,
        String planCode,
        String planType,
        int maxUsers,
        int maxRoles
        ) {

}

```

### `subscriptions/domain/repository/PlatformSubscriptionRepository.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.domain.repository;

import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlatformSubscriptionRepository {

    PlatformSubscription save(PlatformSubscription subscription);

    Optional<PlatformSubscription> findById(UUID id);

    Optional<PlatformSubscription> findCurrentByTenantId(UUID tenantId);

    List<PlatformSubscription> findAll();

    void clearCurrentSubscriptions(UUID tenantId);
}

```

### `subscriptions/infrastructure/api/PlatformSubscriptionController.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.pagination.PaginationSupport;
import com.financesystem.finance_api.modules.platform.subscriptions.application.dto.AssignPlatformSubscriptionRequest;
import com.financesystem.finance_api.modules.platform.subscriptions.application.dto.PlatformSubscriptionResponse;
import com.financesystem.finance_api.modules.platform.subscriptions.application.usecase.AssignPlatformSubscriptionUseCase;
import com.financesystem.finance_api.modules.platform.subscriptions.application.usecase.GetPlatformSubscriptionByIdUseCase;
import com.financesystem.finance_api.modules.platform.subscriptions.application.usecase.ListPlatformSubscriptionsUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/subscriptions")
@SecurityRequirement(name = "bearerAuth")
public class PlatformSubscriptionController {

    private final AssignPlatformSubscriptionUseCase assignPlatformSubscriptionUseCase;
    private final ListPlatformSubscriptionsUseCase listPlatformSubscriptionsUseCase;
    private final GetPlatformSubscriptionByIdUseCase getPlatformSubscriptionByIdUseCase;

    public PlatformSubscriptionController(
            AssignPlatformSubscriptionUseCase assignPlatformSubscriptionUseCase,
            ListPlatformSubscriptionsUseCase listPlatformSubscriptionsUseCase,
            GetPlatformSubscriptionByIdUseCase getPlatformSubscriptionByIdUseCase
    ) {
        this.assignPlatformSubscriptionUseCase = assignPlatformSubscriptionUseCase;
        this.listPlatformSubscriptionsUseCase = listPlatformSubscriptionsUseCase;
        this.getPlatformSubscriptionByIdUseCase = getPlatformSubscriptionByIdUseCase;
    }

    @PostMapping("/assign")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<PlatformSubscriptionResponse> assignSubscription(
            @Valid @RequestBody AssignPlatformSubscriptionRequest request
    ) {
        return ApiResponse.success(
                "Platform subscription assigned successfully",
                assignPlatformSubscriptionUseCase.execute(request)
        );
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<PlatformSubscriptionResponse>> listSubscriptions(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success(
                "Platform subscriptions retrieved successfully",
                PaginationSupport.page(listPlatformSubscriptionsUseCase.execute(), pageable)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<PlatformSubscriptionResponse> getSubscriptionById(@PathVariable UUID id) {
        return ApiResponse.success(
                "Platform subscription retrieved successfully",
                getPlatformSubscriptionByIdUseCase.execute(id)
        );
    }
}

```

### `subscriptions/infrastructure/api/TenantSubscriptionController.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.platform.subscriptions.application.dto.PlatformSubscriptionResponse;
import com.financesystem.finance_api.modules.platform.subscriptions.application.usecase.GetCurrentTenantSubscriptionUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subscription")
@SecurityRequirement(name = "bearerAuth")
public class TenantSubscriptionController {

    private final GetCurrentTenantSubscriptionUseCase getCurrentTenantSubscriptionUseCase;

    public TenantSubscriptionController(
            GetCurrentTenantSubscriptionUseCase getCurrentTenantSubscriptionUseCase
    ) {
        this.getCurrentTenantSubscriptionUseCase = getCurrentTenantSubscriptionUseCase;
    }

    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PlatformSubscriptionResponse> getCurrentSubscription() {
        String tenantSlug = TenantContextHolder.getRequired().tenantSlug();

        return ApiResponse.success(
                "Current tenant subscription retrieved successfully",
                getCurrentTenantSubscriptionUseCase.execute(tenantSlug)
        );
    }
}

```

### `subscriptions/infrastructure/persistence/PlatformSubscriptionEntity.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscriptionStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "platform_subscriptions")
public class PlatformSubscriptionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private UUID planId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PlatformSubscriptionStatus status;

    @Column(nullable = false)
    private boolean isTrial;

    @Column(nullable = false)
    private boolean currentSubscription;

    @Column(nullable = false)
    private Instant startedAt;

    @Column
    private Instant expiresAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getPlanId() {
        return planId;
    }

    public void setPlanId(UUID planId) {
        this.planId = planId;
    }

    public PlatformSubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(PlatformSubscriptionStatus status) {
        this.status = status;
    }

    public boolean isTrial() {
        return isTrial;
    }

    public void setTrial(boolean trial) {
        isTrial = trial;
    }

    public boolean isCurrentSubscription() {
        return currentSubscription;
    }

    public void setCurrentSubscription(boolean currentSubscription) {
        this.currentSubscription = currentSubscription;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

```

### `subscriptions/infrastructure/persistence/PlatformSubscriptionJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.infrastructure.persistence;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface PlatformSubscriptionJpaRepository extends JpaRepository<PlatformSubscriptionEntity, UUID> {

    Optional<PlatformSubscriptionEntity> findByTenantIdAndCurrentSubscriptionTrue(UUID tenantId);

    @Modifying
    @Transactional
    @Query("""
            update PlatformSubscriptionEntity s
               set s.currentSubscription = false,
                   s.updatedAt = CURRENT_TIMESTAMP
             where s.tenantId = :tenantId
               and s.currentSubscription = true
            """)
    void clearCurrentSubscriptions(UUID tenantId);
}

```

### `subscriptions/infrastructure/persistence/PlatformSubscriptionRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.subscriptions.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.subscriptions.domain.model.PlatformSubscription;
import com.financesystem.finance_api.modules.platform.subscriptions.domain.repository.PlatformSubscriptionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PlatformSubscriptionRepositoryAdapter implements PlatformSubscriptionRepository {

    private final PlatformSubscriptionJpaRepository jpaRepository;

    public PlatformSubscriptionRepositoryAdapter(PlatformSubscriptionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PlatformSubscription save(PlatformSubscription subscription) {
        PlatformSubscriptionEntity entity = toEntity(subscription);
        PlatformSubscriptionEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PlatformSubscription> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<PlatformSubscription> findCurrentByTenantId(UUID tenantId) {
        return jpaRepository.findByTenantIdAndCurrentSubscriptionTrue(tenantId).map(this::toDomain);
    }

    @Override
    public List<PlatformSubscription> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void clearCurrentSubscriptions(UUID tenantId) {
        jpaRepository.clearCurrentSubscriptions(tenantId);
    }

    private PlatformSubscriptionEntity toEntity(PlatformSubscription subscription) {
        PlatformSubscriptionEntity entity = new PlatformSubscriptionEntity();
        entity.setId(subscription.id());
        entity.setTenantId(subscription.tenantId());
        entity.setPlanId(subscription.planId());
        entity.setStatus(subscription.status());
        entity.setTrial(subscription.trial());
        entity.setCurrentSubscription(subscription.currentSubscription());
        entity.setStartedAt(subscription.startedAt());
        entity.setExpiresAt(subscription.expiresAt());
        return entity;
    }

    private PlatformSubscription toDomain(PlatformSubscriptionEntity entity) {
        return new PlatformSubscription(
                entity.getId(),
                entity.getTenantId(),
                entity.getPlanId(),
                entity.getStatus(),
                entity.isTrial(),
                entity.isCurrentSubscription(),
                entity.getStartedAt(),
                entity.getExpiresAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

```

### `superadmin/application/dto/PlatformSuperadminResponse.java`

```java
package com.financesystem.finance_api.modules.platform.superadmin.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PlatformSuperadminResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
```

### `superadmin/application/mapper/PlatformSuperadminMapper.java`

```java
package com.financesystem.finance_api.modules.platform.superadmin.application.mapper;

import com.financesystem.finance_api.modules.platform.superadmin.application.dto.PlatformSuperadminResponse;
import com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin;
import org.springframework.stereotype.Component;

@Component
public class PlatformSuperadminMapper {

    public PlatformSuperadminResponse toResponse(PlatformSuperadmin superadmin) {
        return new PlatformSuperadminResponse(
                superadmin.id(),
                superadmin.email(),
                superadmin.firstName(),
                superadmin.lastName(),
                superadmin.active(),
                superadmin.createdAt(),
                superadmin.updatedAt()
        );
    }
}
```

### `superadmin/application/usecase/GetCurrentPlatformSuperadminUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.superadmin.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.platform.superadmin.application.dto.PlatformSuperadminResponse;
import com.financesystem.finance_api.modules.platform.superadmin.application.mapper.PlatformSuperadminMapper;
import com.financesystem.finance_api.modules.platform.superadmin.domain.exception.PlatformSuperadminNotFoundException;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.stereotype.Service;

@Service
public class GetCurrentPlatformSuperadminUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;
    private final PlatformSuperadminMapper platformSuperadminMapper;

    public GetCurrentPlatformSuperadminUseCase(
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository,
            PlatformSuperadminMapper platformSuperadminMapper
    ) {
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
        this.platformSuperadminMapper = platformSuperadminMapper;
    }

    public PlatformSuperadminResponse execute() {
        String currentSubject = securityContextFacade.getCurrentSubject();

        return platformSuperadminRepository.findByEmail(currentSubject)
                .map(platformSuperadminMapper::toResponse)
                .orElseThrow(() -> new PlatformSuperadminNotFoundException(
                        "Platform superadmin not found for current subject: " + currentSubject
                ));
    }
}
```

### `superadmin/domain/exception/PlatformSuperadminNotFoundException.java`

```java
package com.financesystem.finance_api.modules.platform.superadmin.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class PlatformSuperadminNotFoundException extends ResourceNotFoundException {

    public PlatformSuperadminNotFoundException(String message) {
        super(message);
    }
}
```

### `superadmin/domain/model/PlatformSuperadmin.java`

```java
package com.financesystem.finance_api.modules.platform.superadmin.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PlatformSuperadmin(
        UUID id,
        String email,
        String passwordHash,
        String firstName,
        String lastName,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
```

### `superadmin/domain/repository/PlatformSuperadminRepository.java`

```java
package com.financesystem.finance_api.modules.platform.superadmin.domain.repository;

import com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin;

import java.util.List;
import java.util.Optional;

public interface PlatformSuperadminRepository {

    PlatformSuperadmin save(PlatformSuperadmin superadmin);

    Optional<PlatformSuperadmin> findByEmail(String email);

    List<PlatformSuperadmin> findAll();

    boolean existsByEmail(String email);
}

```

### `superadmin/infrastructure/api/PlatformSuperadminController.java`

```java
package com.financesystem.finance_api.modules.platform.superadmin.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.superadmin.application.dto.PlatformSuperadminResponse;
import com.financesystem.finance_api.modules.platform.superadmin.application.usecase.GetCurrentPlatformSuperadminUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/platform/superadmins")
@SecurityRequirement(name = "bearerAuth")
public class PlatformSuperadminController {

    private final GetCurrentPlatformSuperadminUseCase getCurrentPlatformSuperadminUseCase;

    public PlatformSuperadminController(
            GetCurrentPlatformSuperadminUseCase getCurrentPlatformSuperadminUseCase
    ) {
        this.getCurrentPlatformSuperadminUseCase = getCurrentPlatformSuperadminUseCase;
    }

    @GetMapping("/me")
    @PreAuthorize("@authorizationGuards.isPlatformAuthenticated()")
    public ApiResponse<PlatformSuperadminResponse> me() {
        return ApiResponse.success(
                "Platform superadmin retrieved successfully",
                getCurrentPlatformSuperadminUseCase.execute()
        );
    }
}

```

### `superadmin/infrastructure/persistence/PlatformSuperadminEntity.java`

```java
package com.financesystem.finance_api.modules.platform.superadmin.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "platform_superadmins")
public class PlatformSuperadminEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
```

### `superadmin/infrastructure/persistence/PlatformSuperadminJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.superadmin.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlatformSuperadminJpaRepository extends JpaRepository<PlatformSuperadminEntity, UUID> {

    Optional<PlatformSuperadminEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
```

### `superadmin/infrastructure/persistence/PlatformSuperadminRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.superadmin.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PlatformSuperadminRepositoryAdapter implements PlatformSuperadminRepository {

    private final PlatformSuperadminJpaRepository jpaRepository;

    public PlatformSuperadminRepositoryAdapter(PlatformSuperadminJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PlatformSuperadmin save(PlatformSuperadmin superadmin) {
        PlatformSuperadminEntity entity = toEntity(superadmin);
        PlatformSuperadminEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PlatformSuperadmin> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<PlatformSuperadmin> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    private PlatformSuperadminEntity toEntity(PlatformSuperadmin superadmin) {
        PlatformSuperadminEntity entity = new PlatformSuperadminEntity();
        entity.setId(superadmin.id());
        entity.setEmail(superadmin.email());
        entity.setPasswordHash(superadmin.passwordHash());
        entity.setFirstName(superadmin.firstName());
        entity.setLastName(superadmin.lastName());
        entity.setActive(superadmin.active());
        return entity;
    }

    private PlatformSuperadmin toDomain(PlatformSuperadminEntity entity) {
        return new PlatformSuperadmin(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

```

### `tenants/application/dto/CreatePlatformTenantRequest.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePlatformTenantRequest(
        @NotBlank
        @Size(max = 150)
        String name,

        @NotBlank
        @Size(max = 100)
        String slug,

        @Size(max = 50)
        String planCode,

        @NotBlank
        @Email
        @Size(max = 150)
        String adminEmail,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotBlank
        @Size(max = 100)
        String lastName
) {
}

```

### `tenants/application/dto/CreateTenantRequest.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTenantRequest(
        @NotBlank
        @Size(max = 150)
        String name,

        @NotBlank
        @Size(max = 100)
        String slug,

        @Size(max = 50)
        String planCode
) {
}

```

### `tenants/application/dto/PlatformTenantResponse.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PlatformTenantResponse(
        UUID id,
        String name,
        String slug,
        String schemaName,
        String status,
        UUID planId,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
```

### `tenants/application/mapper/PlatformTenantMapper.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.application.mapper;

import com.financesystem.finance_api.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import org.springframework.stereotype.Component;

@Component
public class PlatformTenantMapper {

    public PlatformTenantResponse toResponse(PlatformTenant tenant) {
        return new PlatformTenantResponse(
                tenant.id(),
                tenant.name(),
                tenant.slug(),
                tenant.schemaName(),
                tenant.status().name(),
                tenant.planId(),
                tenant.active(),
                tenant.createdAt(),
                tenant.updatedAt()
        );
    }
}
```

### `tenants/application/usecase/ActivateTenantUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance_api.modules.platform.tenants.application.mapper.PlatformTenantMapper;
import com.financesystem.finance_api.modules.platform.tenants.domain.exception.PlatformTenantNotFoundException;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenantStatus;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActivateTenantUseCase {

    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformTenantMapper platformTenantMapper;
    private final AuditTrailService auditTrailService;

    public ActivateTenantUseCase(
            PlatformTenantRepository platformTenantRepository,
            PlatformTenantMapper platformTenantMapper,
            AuditTrailService auditTrailService
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.platformTenantMapper = platformTenantMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public PlatformTenantResponse execute(UUID id) {
        PlatformTenant tenant = platformTenantRepository.findById(id)
                .orElseThrow(() -> new PlatformTenantNotFoundException("Tenant not found with id: " + id));
        PlatformTenant beforeState = tenant;

        PlatformTenant updated = new PlatformTenant(
                tenant.id(),
                tenant.name(),
                tenant.slug(),
                tenant.schemaName(),
                PlatformTenantStatus.ACTIVE,
                tenant.planId(),
                true,
                tenant.createdAt(),
                tenant.updatedAt()
        );

        PlatformTenant saved = platformTenantRepository.save(updated);

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.TENANT_ACTIVATED,
                "TENANT",
                saved.id().toString(),
                PlatformAuditPayloads.details(
                        "slug", saved.slug(),
                        "status", saved.status().name()
                ),
                PlatformAuditPayloads.tenantState(beforeState),
                PlatformAuditPayloads.tenantState(saved)
        );

        return platformTenantMapper.toResponse(saved);
    }
}

```

### `tenants/application/usecase/CreatePlatformTenantUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.application.usecase;

import com.financesystem.finance_api.modules.platform.onboarding.application.service.TenantOwnerAdminProvisioningService;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.CreatePlatformTenantRequest;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.CreateTenantRequest;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.PlatformTenantResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePlatformTenantUseCase {

    private final CreateTenantUseCase createTenantUseCase;
    private final TenantOwnerAdminProvisioningService tenantOwnerAdminProvisioningService;

    public CreatePlatformTenantUseCase(
            CreateTenantUseCase createTenantUseCase,
            TenantOwnerAdminProvisioningService tenantOwnerAdminProvisioningService
    ) {
        this.createTenantUseCase = createTenantUseCase;
        this.tenantOwnerAdminProvisioningService = tenantOwnerAdminProvisioningService;
    }

    @Transactional
    public PlatformTenantResponse execute(CreatePlatformTenantRequest request) {
        PlatformTenantResponse tenantResponse = createTenantUseCase.execute(
                new CreateTenantRequest(request.name(), request.slug(), request.planCode())
        );

        tenantOwnerAdminProvisioningService.provisionOwnerAdmin(
                tenantResponse.schemaName(),
                request.adminEmail(),
                request.password(),
                request.firstName(),
                request.lastName()
        );

        return tenantResponse;
    }
}

```

### `tenants/application/usecase/CreateTenantUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.application.usecase;

import com.financesystem.finance_api.bootstrap.tenant.TenantBootstrapService;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.common.tenancy.migration.TenantSchemaMigrationService;
import com.financesystem.finance_api.common.tenancy.reporting.ReportingSecurityService;
import com.financesystem.finance_api.common.tenancy.schema.TenantSchemaNamingStrategy;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.PlatformSubscriptionProvisioningService;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.CreateTenantRequest;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance_api.modules.platform.tenants.application.mapper.PlatformTenantMapper;
import com.financesystem.finance_api.modules.platform.tenants.domain.exception.PlatformTenantAlreadyExistsException;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenantStatus;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTenantUseCase {

    private final PlatformTenantRepository platformTenantRepository;
    private final TenantSchemaNamingStrategy tenantSchemaNamingStrategy;
    private final TenantSchemaMigrationService tenantSchemaMigrationService;
    private final TenantBootstrapService tenantBootstrapService;
    private final PlatformTenantMapper platformTenantMapper;
    private final PlatformSubscriptionProvisioningService platformSubscriptionProvisioningService;
    private final ReportingSecurityService reportingSecurityService;
    private final AuditTrailService auditTrailService;

    public CreateTenantUseCase(
            PlatformTenantRepository platformTenantRepository,
            TenantSchemaNamingStrategy tenantSchemaNamingStrategy,
            TenantSchemaMigrationService tenantSchemaMigrationService,
            TenantBootstrapService tenantBootstrapService,
            PlatformTenantMapper platformTenantMapper,
            PlatformSubscriptionProvisioningService platformSubscriptionProvisioningService,
            ReportingSecurityService reportingSecurityService,
            AuditTrailService auditTrailService
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.tenantSchemaNamingStrategy = tenantSchemaNamingStrategy;
        this.tenantSchemaMigrationService = tenantSchemaMigrationService;
        this.tenantBootstrapService = tenantBootstrapService;
        this.platformTenantMapper = platformTenantMapper;
        this.platformSubscriptionProvisioningService = platformSubscriptionProvisioningService;
        this.reportingSecurityService = reportingSecurityService;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public PlatformTenantResponse execute(CreateTenantRequest request) {
        String normalizedSlug = tenantSchemaNamingStrategy.normalizeSlug(request.slug());
        String schemaName = tenantSchemaNamingStrategy.toSchemaName(normalizedSlug);

        if (platformTenantRepository.existsBySlug(normalizedSlug)) {
            throw new PlatformTenantAlreadyExistsException("A tenant with slug '" + normalizedSlug + "' already exists");
        }

        if (platformTenantRepository.existsBySchemaName(schemaName)) {
            throw new PlatformTenantAlreadyExistsException("A tenant with schema '" + schemaName + "' already exists");
        }

        PlatformTenant tenantToCreate = new PlatformTenant(
                null,
                request.name().trim(),
                normalizedSlug,
                schemaName,
                PlatformTenantStatus.ACTIVE,
                null,
                true,
                null,
                null
        );

        PlatformTenant createdTenant = platformTenantRepository.save(tenantToCreate);

        tenantSchemaMigrationService.migrateSchema(createdTenant.schemaName());
        tenantBootstrapService.initializeTenantData(createdTenant.schemaName(), createdTenant.name());

        // Reporting security: grant the read-only role SELECT on this tenant's
        // reporting_* views and refresh the cross-tenant platform views.
        reportingSecurityService.applyTenantSecurity(createdTenant.schemaName());

        platformSubscriptionProvisioningService.assignCurrentSubscription(
                createdTenant.id(),
                request.planCode(),
                null,
                true
        );

        PlatformTenant updatedTenant = platformTenantRepository.findById(createdTenant.id()).orElseThrow();

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.TENANT_CREATED,
                "TENANT",
                updatedTenant.id().toString(),
                PlatformAuditPayloads.details(
                        "name", updatedTenant.name(),
                        "slug", updatedTenant.slug(),
                        "schemaName", updatedTenant.schemaName(),
                        "planCode", request.planCode()
                ),
                null,
                PlatformAuditPayloads.tenantState(updatedTenant)
        );

        return platformTenantMapper.toResponse(updatedTenant);
    }
}

```

### `tenants/application/usecase/DeactivateTenantUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance_api.modules.platform.tenants.application.mapper.PlatformTenantMapper;
import com.financesystem.finance_api.modules.platform.tenants.domain.exception.PlatformTenantNotFoundException;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenantStatus;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeactivateTenantUseCase {

    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformTenantMapper platformTenantMapper;
    private final AuditTrailService auditTrailService;

    public DeactivateTenantUseCase(
            PlatformTenantRepository platformTenantRepository,
            PlatformTenantMapper platformTenantMapper,
            AuditTrailService auditTrailService
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.platformTenantMapper = platformTenantMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public PlatformTenantResponse execute(UUID id) {
        PlatformTenant tenant = platformTenantRepository.findById(id)
                .orElseThrow(() -> new PlatformTenantNotFoundException("Tenant not found with id: " + id));
        PlatformTenant beforeState = tenant;

        PlatformTenant updated = new PlatformTenant(
                tenant.id(),
                tenant.name(),
                tenant.slug(),
                tenant.schemaName(),
                PlatformTenantStatus.INACTIVE,
                tenant.planId(),
                false,
                tenant.createdAt(),
                tenant.updatedAt()
        );

        PlatformTenant saved = platformTenantRepository.save(updated);

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.TENANT_DEACTIVATED,
                "TENANT",
                saved.id().toString(),
                PlatformAuditPayloads.details(
                        "slug", saved.slug(),
                        "status", saved.status().name()
                ),
                PlatformAuditPayloads.tenantState(beforeState),
                PlatformAuditPayloads.tenantState(saved)
        );

        return platformTenantMapper.toResponse(saved);
    }
}

```

### `tenants/application/usecase/GetTenantByIdUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.application.usecase;

import com.financesystem.finance_api.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance_api.modules.platform.tenants.application.mapper.PlatformTenantMapper;
import com.financesystem.finance_api.modules.platform.tenants.domain.exception.PlatformTenantNotFoundException;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetTenantByIdUseCase {

    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformTenantMapper platformTenantMapper;

    public GetTenantByIdUseCase(
            PlatformTenantRepository platformTenantRepository,
            PlatformTenantMapper platformTenantMapper
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.platformTenantMapper = platformTenantMapper;
    }

    public PlatformTenantResponse execute(UUID id) {
        return platformTenantRepository.findById(id)
                .map(platformTenantMapper::toResponse)
                .orElseThrow(() -> new PlatformTenantNotFoundException("Tenant not found with id: " + id));
    }
}
```

### `tenants/application/usecase/ListTenantsUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.application.usecase;

import com.financesystem.finance_api.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance_api.modules.platform.tenants.application.mapper.PlatformTenantMapper;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTenantsUseCase {

    private final PlatformTenantRepository platformTenantRepository;
    private final PlatformTenantMapper platformTenantMapper;

    public ListTenantsUseCase(
            PlatformTenantRepository platformTenantRepository,
            PlatformTenantMapper platformTenantMapper
    ) {
        this.platformTenantRepository = platformTenantRepository;
        this.platformTenantMapper = platformTenantMapper;
    }

    public List<PlatformTenantResponse> execute() {
        return platformTenantRepository.findAll()
                .stream()
                .map(platformTenantMapper::toResponse)
                .toList();
    }
}
```

### `tenants/domain/exception/PlatformPlanNotFoundException.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class PlatformPlanNotFoundException extends ResourceNotFoundException {

    public PlatformPlanNotFoundException(String message) {
        super(message);
    }
}
```

### `tenants/domain/exception/PlatformTenantAlreadyExistsException.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class PlatformTenantAlreadyExistsException extends BusinessException {

    public PlatformTenantAlreadyExistsException(String message) {
        super(message);
    }
}
```

### `tenants/domain/exception/PlatformTenantNotFoundException.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class PlatformTenantNotFoundException extends ResourceNotFoundException {

    public PlatformTenantNotFoundException(String message) {
        super(message);
    }
}
```

### `tenants/domain/model/PlatformTenant.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.domain.model;

import java.time.Instant;
import java.util.UUID;

public record PlatformTenant(
        UUID id,
        String name,
        String slug,
        String schemaName,
        PlatformTenantStatus status,
        UUID planId,
        boolean active,
        Instant createdAt,
        Instant updatedAt
) {
}
```

### `tenants/domain/model/PlatformTenantStatus.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.domain.model;

public enum PlatformTenantStatus {
    ACTIVE,
    INACTIVE
}
```

### `tenants/domain/repository/PlatformTenantRepository.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.domain.repository;

import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlatformTenantRepository {

    PlatformTenant save(PlatformTenant tenant);

    Optional<PlatformTenant> findById(UUID id);

    Optional<PlatformTenant> findBySlug(String slug);

    List<PlatformTenant> findAll();

    boolean existsBySlug(String slug);

    boolean existsBySchemaName(String schemaName);
}

```

### `tenantsettings/application/dto/TenantSettingsResponse.java`

```java
package com.financesystem.finance_api.modules.platform.tenantsettings.application.dto;

public record TenantSettingsResponse(
        String companyName,
        String legalName,
        String timezone,
        String currency,
        String contactEmail,
        String contactPhone
) {
}
```

### `tenantsettings/application/dto/UpdateTenantSettingsRequest.java`

```java
package com.financesystem.finance_api.modules.platform.tenantsettings.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTenantSettingsRequest(
        @NotBlank
        @Size(max = 150)
        String companyName,

        @Size(max = 200)
        String legalName,

        @NotBlank
        @Size(max = 100)
        String timezone,

        @NotBlank
        @Size(max = 10)
        String currency,

        @Email
        @Size(max = 150)
        String contactEmail,

        @Size(max = 50)
        String contactPhone
) {
}
```

### `tenantsettings/application/mapper/TenantSettingsMapper.java`

```java
package com.financesystem.finance_api.modules.platform.tenantsettings.application.mapper;

import com.financesystem.finance_api.modules.platform.tenantsettings.application.dto.TenantSettingsResponse;
import com.financesystem.finance_api.modules.platform.tenantsettings.domain.model.TenantSetting;
import com.financesystem.finance_api.modules.platform.tenantsettings.domain.model.TenantSettingKeys;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TenantSettingsMapper {

    public TenantSettingsResponse toResponse(List<TenantSetting> settings) {
        Map<String, String> settingsByKey = settings.stream()
                .collect(Collectors.toMap(
                        TenantSetting::settingKey,
                        TenantSetting::settingValue,
                        (first, second) -> second
                ));

        return new TenantSettingsResponse(
                settingsByKey.get(TenantSettingKeys.COMPANY_NAME),
                settingsByKey.get(TenantSettingKeys.COMPANY_LEGAL_NAME),
                settingsByKey.get(TenantSettingKeys.COMPANY_TIMEZONE),
                settingsByKey.get(TenantSettingKeys.COMPANY_CURRENCY),
                settingsByKey.get(TenantSettingKeys.COMPANY_CONTACT_EMAIL),
                settingsByKey.get(TenantSettingKeys.COMPANY_CONTACT_PHONE)
        );
    }
}
```

### `tenantsettings/application/usecase/GetTenantSettingsUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.tenantsettings.application.usecase;

import com.financesystem.finance_api.modules.platform.tenantsettings.application.dto.TenantSettingsResponse;
import com.financesystem.finance_api.modules.platform.tenantsettings.application.mapper.TenantSettingsMapper;
import com.financesystem.finance_api.modules.platform.tenantsettings.domain.repository.TenantSettingRepository;
import org.springframework.stereotype.Service;

@Service
public class GetTenantSettingsUseCase {

    private final TenantSettingRepository tenantSettingRepository;
    private final TenantSettingsMapper tenantSettingsMapper;

    public GetTenantSettingsUseCase(
            TenantSettingRepository tenantSettingRepository,
            TenantSettingsMapper tenantSettingsMapper
    ) {
        this.tenantSettingRepository = tenantSettingRepository;
        this.tenantSettingsMapper = tenantSettingsMapper;
    }

    public TenantSettingsResponse execute() {
        return tenantSettingsMapper.toResponse(tenantSettingRepository.findAll());
    }
}
```

### `tenantsettings/application/usecase/UpdateTenantSettingsUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.tenantsettings.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.tenantsettings.application.dto.TenantSettingsResponse;
import com.financesystem.finance_api.modules.platform.tenantsettings.application.dto.UpdateTenantSettingsRequest;
import com.financesystem.finance_api.modules.platform.tenantsettings.application.mapper.TenantSettingsMapper;
import com.financesystem.finance_api.modules.platform.tenantsettings.domain.model.TenantSetting;
import com.financesystem.finance_api.modules.platform.tenantsettings.domain.model.TenantSettingKeys;
import com.financesystem.finance_api.modules.platform.tenantsettings.domain.repository.TenantSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UpdateTenantSettingsUseCase {

    private final TenantSettingRepository tenantSettingRepository;
    private final TenantSettingsMapper tenantSettingsMapper;
    private final AuditTrailService auditTrailService;

    public UpdateTenantSettingsUseCase(
            TenantSettingRepository tenantSettingRepository,
            TenantSettingsMapper tenantSettingsMapper,
            AuditTrailService auditTrailService
    ) {
        this.tenantSettingRepository = tenantSettingRepository;
        this.tenantSettingsMapper = tenantSettingsMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public TenantSettingsResponse execute(UpdateTenantSettingsRequest request) {
        List<TenantSetting> beforeState = tenantSettingRepository.findAll();

        saveOrUpdate(TenantSettingKeys.COMPANY_NAME, normalizeRequired(request.companyName()));
        saveOrUpdate(TenantSettingKeys.COMPANY_LEGAL_NAME, normalizeNullable(request.legalName()));
        saveOrUpdate(TenantSettingKeys.COMPANY_TIMEZONE, normalizeRequired(request.timezone()));
        saveOrUpdate(TenantSettingKeys.COMPANY_CURRENCY, normalizeRequired(request.currency()).toUpperCase());
        saveOrUpdate(TenantSettingKeys.COMPANY_CONTACT_EMAIL, normalizeNullable(request.contactEmail()));
        saveOrUpdate(TenantSettingKeys.COMPANY_CONTACT_PHONE, normalizeNullable(request.contactPhone()));

        List<TenantSetting> afterState = tenantSettingRepository.findAll();

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.TENANT_SETTINGS_UPDATED,
                "TENANT_SETTINGS",
                "GLOBAL",
                PlatformAuditPayloads.details(
                        "updatedKeys", List.of(
                                TenantSettingKeys.COMPANY_NAME,
                                TenantSettingKeys.COMPANY_LEGAL_NAME,
                                TenantSettingKeys.COMPANY_TIMEZONE,
                                TenantSettingKeys.COMPANY_CURRENCY,
                                TenantSettingKeys.COMPANY_CONTACT_EMAIL,
                                TenantSettingKeys.COMPANY_CONTACT_PHONE
                        )
                ),
                PlatformAuditPayloads.settingsState(beforeState),
                PlatformAuditPayloads.settingsState(afterState)
        );

        return tenantSettingsMapper.toResponse(afterState);
    }

    private void saveOrUpdate(String key, String value) {
        TenantSetting existing = tenantSettingRepository.findByKey(key).orElse(null);

        TenantSetting settingToSave = new TenantSetting(
                existing != null ? existing.id() : null,
                key,
                value,
                existing != null ? existing.createdAt() : null,
                existing != null ? existing.updatedAt() : null
        );

        tenantSettingRepository.save(settingToSave);
    }

    private String normalizeRequired(String value) {
        return value == null ? null : value.trim();
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

```

### `tenantsettings/domain/model/TenantSetting.java`

```java
package com.financesystem.finance_api.modules.platform.tenantsettings.domain.model;

import java.time.Instant;
import java.util.UUID;

public record TenantSetting(
        UUID id,
        String settingKey,
        String settingValue,
        Instant createdAt,
        Instant updatedAt
) {
}
```

### `tenantsettings/domain/model/TenantSettingKeys.java`

```java
package com.financesystem.finance_api.modules.platform.tenantsettings.domain.model;

public final class TenantSettingKeys {

    public static final String COMPANY_NAME = "company.name";
    public static final String COMPANY_LEGAL_NAME = "company.legal_name";
    public static final String COMPANY_TIMEZONE = "company.timezone";
    public static final String COMPANY_CURRENCY = "company.currency";
    public static final String COMPANY_CONTACT_EMAIL = "company.contact_email";
    public static final String COMPANY_CONTACT_PHONE = "company.contact_phone";

    private TenantSettingKeys() {
    }
}
```

### `tenantsettings/domain/repository/TenantSettingRepository.java`

```java
package com.financesystem.finance_api.modules.platform.tenantsettings.domain.repository;

import com.financesystem.finance_api.modules.platform.tenantsettings.domain.model.TenantSetting;

import java.util.List;
import java.util.Optional;

public interface TenantSettingRepository {

    List<TenantSetting> findAll();

    Optional<TenantSetting> findByKey(String settingKey);

    TenantSetting save(TenantSetting tenantSetting);
}
```

### `tenantsettings/infrastructure/api/TenantSettingsController.java`

```java
package com.financesystem.finance_api.modules.platform.tenantsettings.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.tenantsettings.application.dto.TenantSettingsResponse;
import com.financesystem.finance_api.modules.platform.tenantsettings.application.dto.UpdateTenantSettingsRequest;
import com.financesystem.finance_api.modules.platform.tenantsettings.application.usecase.GetTenantSettingsUseCase;
import com.financesystem.finance_api.modules.platform.tenantsettings.application.usecase.UpdateTenantSettingsUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings/tenant")
@SecurityRequirement(name = "bearerAuth")
public class TenantSettingsController {

    private final GetTenantSettingsUseCase getTenantSettingsUseCase;
    private final UpdateTenantSettingsUseCase updateTenantSettingsUseCase;

    public TenantSettingsController(
            GetTenantSettingsUseCase getTenantSettingsUseCase,
            UpdateTenantSettingsUseCase updateTenantSettingsUseCase
    ) {
        this.getTenantSettingsUseCase = getTenantSettingsUseCase;
        this.updateTenantSettingsUseCase = updateTenantSettingsUseCase;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<TenantSettingsResponse> getTenantSettings() {
        return ApiResponse.success(
                "Tenant settings retrieved successfully",
                getTenantSettingsUseCase.execute()
        );
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<TenantSettingsResponse> updateTenantSettings(
            @Valid @RequestBody UpdateTenantSettingsRequest request
    ) {
        return ApiResponse.success(
                "Tenant settings updated successfully",
                updateTenantSettingsUseCase.execute(request)
        );
    }
}

```

### `tenantsettings/infrastructure/persistence/TenantSettingEntity.java`

```java
package com.financesystem.finance_api.modules.platform.tenantsettings.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant_settings")
public class TenantSettingEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "setting_key", nullable = false, unique = true, length = 100)
    private String settingKey;

    @Column(name = "setting_value", columnDefinition = "TEXT")
    private String settingValue;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
```

### `tenantsettings/infrastructure/persistence/TenantSettingJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.tenantsettings.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantSettingJpaRepository extends JpaRepository<TenantSettingEntity, UUID> {

    Optional<TenantSettingEntity> findBySettingKey(String settingKey);
}
```

### `tenantsettings/infrastructure/persistence/TenantSettingRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.tenantsettings.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.tenantsettings.domain.model.TenantSetting;
import com.financesystem.finance_api.modules.platform.tenantsettings.domain.repository.TenantSettingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TenantSettingRepositoryAdapter implements TenantSettingRepository {

    private final TenantSettingJpaRepository jpaRepository;

    public TenantSettingRepositoryAdapter(TenantSettingJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<TenantSetting> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<TenantSetting> findByKey(String settingKey) {
        return jpaRepository.findBySettingKey(settingKey).map(this::toDomain);
    }

    @Override
    public TenantSetting save(TenantSetting tenantSetting) {
        TenantSettingEntity entity = toEntity(tenantSetting);
        TenantSettingEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private TenantSettingEntity toEntity(TenantSetting tenantSetting) {
        TenantSettingEntity entity = new TenantSettingEntity();
        entity.setId(tenantSetting.id());
        entity.setSettingKey(tenantSetting.settingKey());
        entity.setSettingValue(tenantSetting.settingValue());
        return entity;
    }

    private TenantSetting toDomain(TenantSettingEntity entity) {
        return new TenantSetting(
                entity.getId(),
                entity.getSettingKey(),
                entity.getSettingValue(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
```

### `tenants/infrastructure/api/PlatformTenantController.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.common.pagination.PaginationSupport;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.CreatePlatformTenantRequest;
import com.financesystem.finance_api.modules.platform.tenants.application.dto.PlatformTenantResponse;
import com.financesystem.finance_api.modules.platform.tenants.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/tenants")
@SecurityRequirement(name = "bearerAuth")
public class PlatformTenantController {

    private final CreateTenantUseCase createTenantUseCase;
    private final ListTenantsUseCase listTenantsUseCase;
    private final GetTenantByIdUseCase getTenantByIdUseCase;
    private final ActivateTenantUseCase activateTenantUseCase;
    private final DeactivateTenantUseCase deactivateTenantUseCase;
    private final CreatePlatformTenantUseCase createPlatformTenantUseCase;

    public PlatformTenantController(
            CreateTenantUseCase createTenantUseCase,
            ListTenantsUseCase listTenantsUseCase,
            GetTenantByIdUseCase getTenantByIdUseCase,
            ActivateTenantUseCase activateTenantUseCase,
            DeactivateTenantUseCase deactivateTenantUseCase,
            CreatePlatformTenantUseCase createPlatformTenantUseCase
    ) {
        this.createTenantUseCase = createTenantUseCase;
        this.listTenantsUseCase = listTenantsUseCase;
        this.getTenantByIdUseCase = getTenantByIdUseCase;
        this.activateTenantUseCase = activateTenantUseCase;
        this.deactivateTenantUseCase = deactivateTenantUseCase;
        this.createPlatformTenantUseCase = createPlatformTenantUseCase;
    }

    @PostMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<PlatformTenantResponse> createTenant(@Valid @RequestBody CreatePlatformTenantRequest request) {
        return ApiResponse.success(
                "Tenant created successfully",
                createPlatformTenantUseCase.execute(request)
        );
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<PlatformTenantResponse>> listTenants(@ParameterObject @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.success(
                "Tenants retrieved successfully",
                PaginationSupport.page(listTenantsUseCase.execute(), pageable)
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<PlatformTenantResponse> getTenantById(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant retrieved successfully",
                getTenantByIdUseCase.execute(id)
        );
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<PlatformTenantResponse> activateTenant(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant activated successfully",
                activateTenantUseCase.execute(id)
        );
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<PlatformTenantResponse> deactivateTenant(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant deactivated successfully",
                deactivateTenantUseCase.execute(id)
        );
    }
}

```

### `tenants/infrastructure/persistence/PlatformPlanLookupService.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.tenants.domain.exception.PlatformPlanNotFoundException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

@Service
public class PlatformPlanLookupService {

    private final JdbcTemplate jdbcTemplate;

    public PlatformPlanLookupService(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public UUID findPlanIdByCodeOrDefault(String rawPlanCode) {
        String planCode = (rawPlanCode == null || rawPlanCode.isBlank())
                ? "BASIC"
                : rawPlanCode.trim().toUpperCase();

        List<UUID> ids = jdbcTemplate.query(
                """
                SELECT id
                FROM platform_plans
                WHERE code = ?
                  AND active = true
                LIMIT 1
                """,
                (rs, rowNum) -> rs.getObject("id", UUID.class),
                planCode
        );

        if (ids.isEmpty()) {
            throw new PlatformPlanNotFoundException("Platform plan not found for code: " + planCode);
        }

        return ids.getFirst();
    }
}
```

### `tenants/infrastructure/persistence/PlatformTenantEntity.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenantStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "platform_tenants")
public class PlatformTenantEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name = "schema_name", nullable = false, unique = true, length = 128)
    private String schemaName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PlatformTenantStatus status;

    @Column(name = "plan_id")
    private UUID planId;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public PlatformTenantStatus getStatus() {
        return status;
    }

    public void setStatus(PlatformTenantStatus status) {
        this.status = status;
    }

    public UUID getPlanId() {
        return planId;
    }

    public void setPlanId(UUID planId) {
        this.planId = planId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
```

### `tenants/infrastructure/persistence/PlatformTenantJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PlatformTenantJpaRepository extends JpaRepository<PlatformTenantEntity, UUID> {

    boolean existsBySlug(String slug);

    boolean existsBySchemaName(String schemaName);

    Optional<PlatformTenantEntity> findBySlug(String slug);
}

```

### `tenants/infrastructure/persistence/PlatformTenantRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.tenants.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.tenants.domain.model.PlatformTenant;
import com.financesystem.finance_api.modules.platform.tenants.domain.repository.PlatformTenantRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class PlatformTenantRepositoryAdapter implements PlatformTenantRepository {

    private final PlatformTenantJpaRepository jpaRepository;

    public PlatformTenantRepositoryAdapter(PlatformTenantJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public PlatformTenant save(PlatformTenant tenant) {
        PlatformTenantEntity entity = toEntity(tenant);
        PlatformTenantEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<PlatformTenant> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<PlatformTenant> findBySlug(String slug) {
        return jpaRepository.findBySlug(slug).map(this::toDomain);
    }

    @Override
    public List<PlatformTenant> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsBySlug(String slug) {
        return jpaRepository.existsBySlug(slug);
    }

    @Override
    public boolean existsBySchemaName(String schemaName) {
        return jpaRepository.existsBySchemaName(schemaName);
    }

    private PlatformTenantEntity toEntity(PlatformTenant tenant) {
        PlatformTenantEntity entity = new PlatformTenantEntity();
        entity.setId(tenant.id());
        entity.setName(tenant.name());
        entity.setSlug(tenant.slug());
        entity.setSchemaName(tenant.schemaName());
        entity.setStatus(tenant.status());
        entity.setPlanId(tenant.planId());
        entity.setActive(tenant.active());
        return entity;
    }

    private PlatformTenant toDomain(PlatformTenantEntity entity) {
        return new PlatformTenant(
                entity.getId(),
                entity.getName(),
                entity.getSlug(),
                entity.getSchemaName(),
                entity.getStatus(),
                entity.getPlanId(),
                entity.isActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

```

