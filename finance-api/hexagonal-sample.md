# APPLICATION

---

# application/dto

## CreateTenantUserRequest

```java
package com.financesystem.finance_api.modules.identity.users.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTenantUserRequest(
        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @NotBlank
        @Size(min = 8, max = 100)
        String password,

        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName
) {
}
```

---

## TenantUserResponse

```java
package com.financesystem.finance_api.modules.identity.users.application.dto;

import java.time.Instant;
import java.util.UUID;

public record TenantUserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        boolean active,
        String status,
        Instant createdAt,
        Instant updatedAt
) {
}
```

---

## UpdateTenantUserRequest

```java
package com.financesystem.finance_api.modules.identity.users.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateTenantUserRequest(
        @NotBlank
        @Email
        @Size(max = 150)
        String email,

        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName
) {
}
```

---

# application/mapper

## TenantUserMapper

```java
package com.financesystem.finance_api.modules.identity.users.application.mapper;

import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import org.springframework.stereotype.Component;

@Component
public class TenantUserMapper {

    public TenantUserResponse toResponse(TenantUser tenantUser) {
        return new TenantUserResponse(
                tenantUser.id(),
                tenantUser.email(),
                tenantUser.firstName(),
                tenantUser.lastName(),
                tenantUser.active(),
                tenantUser.status().name(),
                tenantUser.createdAt(),
                tenantUser.updatedAt()
        );
    }
}
```

---

# application/usecase

## ActivateTenantUserUseCase

```java
package com.financesystem.finance_api.modules.identity.users.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.TenantPlanEnforcementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class ActivateTenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserMapper tenantUserMapper;
    private final AuditTrailService auditTrailService;
    private final TenantPlanEnforcementService tenantPlanEnforcementService;

    public ActivateTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserMapper tenantUserMapper,
            AuditTrailService auditTrailService,
            TenantPlanEnforcementService tenantPlanEnforcementService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserMapper = tenantUserMapper;
        this.auditTrailService = auditTrailService;
        this.tenantPlanEnforcementService = tenantPlanEnforcementService;
    }

    @Transactional
    public TenantUserResponse execute(UUID id) {
        TenantUser existingUser = tenantUserRepository.findById(id)
                .orElseThrow(() -> new TenantUserNotFoundException("Tenant user not found with id: " + id));

        if (!existingUser.active()) {
            tenantPlanEnforcementService.assertCanActivateUser(
                    tenantUserRepository.countActiveUsers()
            );
        }

        TenantUser updatedUser = new TenantUser(
                existingUser.id(),
                existingUser.email(),
                existingUser.passwordHash(),
                existingUser.firstName(),
                existingUser.lastName(),
                true,
                TenantUserStatus.ACTIVE,
                existingUser.createdAt(),
                existingUser.updatedAt()
        );

        TenantUser savedUser = tenantUserRepository.save(updatedUser);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.USER_ACTIVATED,
                "USER",
                savedUser.id().toString(),
                Map.of("email", savedUser.email())
        );

        return tenantUserMapper.toResponse(savedUser);
    }
}
```

---

## CreateTenantUserUseCase

```java
package com.financesystem.finance_api.modules.identity.users.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.users.application.dto.CreateTenantUserRequest;
import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserAlreadyExistsException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.TenantPlanEnforcementService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class CreateTenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserMapper tenantUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditTrailService auditTrailService;
    private final TenantPlanEnforcementService tenantPlanEnforcementService;

    public CreateTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserMapper tenantUserMapper,
            PasswordEncoder passwordEncoder,
            AuditTrailService auditTrailService,
            TenantPlanEnforcementService tenantPlanEnforcementService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserMapper = tenantUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.auditTrailService = auditTrailService;
        this.tenantPlanEnforcementService = tenantPlanEnforcementService;
    }

    @Transactional
    public TenantUserResponse execute(CreateTenantUserRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (tenantUserRepository.existsByEmail(normalizedEmail)) {
            throw new TenantUserAlreadyExistsException(
                    "A tenant user with email '" + normalizedEmail + "' already exists"
            );
        }

        tenantPlanEnforcementService.assertCanCreateUser(
                tenantUserRepository.countActiveUsers()
        );

        TenantUser tenantUserToCreate = new TenantUser(
                null,
                normalizedEmail,
                passwordEncoder.encode(request.password()),
                normalizeNullable(request.firstName()),
                normalizeNullable(request.lastName()),
                true,
                TenantUserStatus.ACTIVE,
                null,
                null
        );

        TenantUser createdTenantUser = tenantUserRepository.save(tenantUserToCreate);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.USER_CREATED,
                "USER",
                createdTenantUser.id().toString(),
                Map.of("email", createdTenantUser.email())
        );

        return tenantUserMapper.toResponse(createdTenantUser);
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

---

## DeactivateTenantUserUseCase

```java
package com.financesystem.finance_api.modules.identity.users.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
public class DeactivateTenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserMapper tenantUserMapper;
    private final AuditTrailService auditTrailService;

    public DeactivateTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserMapper tenantUserMapper,
            AuditTrailService auditTrailService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserMapper = tenantUserMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public TenantUserResponse execute(UUID id) {
        TenantUser existingUser = tenantUserRepository.findById(id)
                .orElseThrow(() -> new TenantUserNotFoundException("Tenant user not found with id: " + id));

        TenantUser updatedUser = new TenantUser(
                existingUser.id(),
                existingUser.email(),
                existingUser.passwordHash(),
                existingUser.firstName(),
                existingUser.lastName(),
                false,
                TenantUserStatus.INACTIVE,
                existingUser.createdAt(),
                existingUser.updatedAt()
        );

        TenantUser savedUser = tenantUserRepository.save(updatedUser);

        auditTrailService.recordTenantEvent(
                AuditEventTypes.USER_DEACTIVATED,
                "USER",
                savedUser.id().toString(),
                Map.of("email", savedUser.email())
        );

        return tenantUserMapper.toResponse(savedUser);
    }
}
```


# DOMAIN

---

# domain/exception

## TenantUserAlreadyExistsException

```java
package com.financesystem.finance_api.modules.identity.users.domain.exception;

import com.financesystem.finance_api.common.exception.BusinessException;

public class TenantUserAlreadyExistsException extends BusinessException {

    public TenantUserAlreadyExistsException(String message) {
        super(message);
    }
}
```

---

## TenantUserNotFoundException

```java
package com.financesystem.finance_api.modules.identity.users.domain.exception;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;

public class TenantUserNotFoundException extends ResourceNotFoundException {

    public TenantUserNotFoundException(String message) {
        super(message);
    }
}
```

---

# domain/model

## TenantUser

```java
package com.financesystem.finance_api.modules.identity.users.domain.model;

import java.time.Instant;
import java.util.UUID;

public record TenantUser(
        UUID id,
        String email,
        String passwordHash,
        String firstName,
        String lastName,
        boolean active,
        TenantUserStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
```

---

## TenantUserStatus

```java
package com.financesystem.finance_api.modules.identity.users.domain.model;

public enum TenantUserStatus {
    ACTIVE,
    INACTIVE
}
```

---

# domain/repository

## TenantUserRepository

```java
package com.financesystem.finance_api.modules.identity.users.domain.repository;

import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TenantUserRepository {

    TenantUser save(TenantUser tenantUser);

    Optional<TenantUser> findById(UUID id);

    Optional<TenantUser> findByEmail(String email);

    List<TenantUser> findAll();

    boolean existsByEmail(String email);

    long countActiveUsers();
}
```

---

# infrastructure/api

## TenantUserController

```java
package com.financesystem.finance_api.modules.identity.users.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.identity.users.application.dto.CreateTenantUserRequest;
import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.dto.UpdateTenantUserRequest;
import com.financesystem.finance_api.modules.identity.users.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@SecurityRequirement(name = "bearerAuth")
public class TenantUserController {

    private final CreateTenantUserUseCase createTenantUserUseCase;
    private final ListTenantUsersUseCase listTenantUsersUseCase;
    private final GetTenantUserByIdUseCase getTenantUserByIdUseCase;
    private final UpdateTenantUserUseCase updateTenantUserUseCase;
    private final ActivateTenantUserUseCase activateTenantUserUseCase;
    private final DeactivateTenantUserUseCase deactivateTenantUserUseCase;

    public TenantUserController(
            CreateTenantUserUseCase createTenantUserUseCase,
            ListTenantUsersUseCase listTenantUsersUseCase,
            GetTenantUserByIdUseCase getTenantUserByIdUseCase,
            UpdateTenantUserUseCase updateTenantUserUseCase,
            ActivateTenantUserUseCase activateTenantUserUseCase,
            DeactivateTenantUserUseCase deactivateTenantUserUseCase
    ) {
        this.createTenantUserUseCase = createTenantUserUseCase;
        this.listTenantUsersUseCase = listTenantUsersUseCase;
        this.getTenantUserByIdUseCase = getTenantUserByIdUseCase;
        this.updateTenantUserUseCase = updateTenantUserUseCase;
        this.activateTenantUserUseCase = activateTenantUserUseCase;
        this.deactivateTenantUserUseCase = deactivateTenantUserUseCase;
    }

    // @PostMapping
    // @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER_ADMIN')")
    public ApiResponse<TenantUserResponse> createUser(@Valid @RequestBody CreateTenantUserRequest request) {
        return ApiResponse.success(
                "Tenant user created successfully",
                createTenantUserUseCase.execute(request)
        );
    }

    // @GetMapping
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER_ADMIN')")
    public ApiResponse<List<TenantUserResponse>> listUsers() {
        return ApiResponse.success(
                "Tenant users retrieved successfully",
                listTenantUsersUseCase.execute()
        );
    }

    // @GetMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER_ADMIN')")
    public ApiResponse<TenantUserResponse> getUserById(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant user retrieved successfully",
                getTenantUserByIdUseCase.execute(id)
        );
    }

    // @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER_ADMIN')")
    public ApiResponse<TenantUserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateTenantUserRequest request
    ) {
        return ApiResponse.success(
                "Tenant user updated successfully",
                updateTenantUserUseCase.execute(id, request)
        );
    }

    // @PatchMapping("/{id}/activate")
    // @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER_ADMIN')")
    public ApiResponse<TenantUserResponse> activateUser(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant user activated successfully",
                activateTenantUserUseCase.execute(id)
        );
    }

    // @PatchMapping("/{id}/deactivate")
    // @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER_ADMIN')")
    public ApiResponse<TenantUserResponse> deactivateUser(@PathVariable UUID id) {
        return ApiResponse.success(
                "Tenant user deactivated successfully",
                deactivateTenantUserUseCase.execute(id)
        );
    }
}
```

---

# infrastructure/persistence

## TenantUserEntity

```java
package com.financesystem.finance_api.modules.identity.users.infrastructure.persistence;

import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant_users")
public class TenantUserEntity {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TenantUserStatus status;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public TenantUserStatus getStatus() {
        return status;
    }

    public void setStatus(TenantUserStatus status) {
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

---

## TenantUserJpaRepository

```java
package com.financesystem.finance_api.modules.identity.users.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantUserJpaRepository extends JpaRepository<TenantUserEntity, UUID> {

    Optional<TenantUserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByActiveTrue();
}
```

---

## TenantUserRepositoryAdapter

```java
package com.financesystem.finance_api.modules.identity.users.infrastructure.persistence;

import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class TenantUserRepositoryAdapter implements TenantUserRepository {

    private final TenantUserJpaRepository jpaRepository;

    public TenantUserRepositoryAdapter(TenantUserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TenantUser save(TenantUser tenantUser) {
        TenantUserEntity entity = toEntity(tenantUser);
        TenantUserEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<TenantUser> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<TenantUser> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public List<TenantUser> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public long countActiveUsers() {
        return jpaRepository.countByActiveTrue();
    }

    private TenantUserEntity toEntity(TenantUser tenantUser) {
        TenantUserEntity entity = new TenantUserEntity();
        entity.setId(tenantUser.id());
        entity.setEmail(tenantUser.email());
        entity.setPasswordHash(tenantUser.passwordHash());
        entity.setFirstName(tenantUser.firstName());
        entity.setLastName(tenantUser.lastName());
        entity.setActive(tenantUser.active());
        entity.setStatus(tenantUser.status());
        return entity;
    }

    private TenantUser toDomain(TenantUserEntity entity) {
        return new TenantUser(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.isActive(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
```

---

# resources/db/migration/tenant

---

## V1_create_tenant_users.sql

```sql
CREATE TABLE IF NOT EXISTS tenant_users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

---

## V2_create_tenant_roles.sql

```sql
CREATE TABLE IF NOT EXISTS tenant_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    description VARCHAR(255),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS tenant_user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_tenant_user_roles_user
        FOREIGN KEY (user_id) REFERENCES tenant_users(id) ON DELETE CASCADE,
    CONSTRAINT fk_tenant_user_roles_role
        FOREIGN KEY (role_id) REFERENCES tenant_roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS tenant_role_permissions (
    role_id UUID NOT NULL,
    permission_code VARCHAR(100) NOT NULL,
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (role_id, permission_code),
    CONSTRAINT fk_tenant_role_permissions_role
        FOREIGN KEY (role_id) REFERENCES tenant_roles(id) ON DELETE CASCADE
);
```

---

## V3_create_tenant_settings.sql

```sql
CREATE TABLE IF NOT EXISTS tenant_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

---

## V4_create_tenant_audit_events.sql

```sql
CREATE TABLE IF NOT EXISTS tenant_audit_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_subject VARCHAR(150),
    event_type VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100),
    resource_id VARCHAR(100),
    event_details TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

---

## V5_create_tenant_password_reset_tokens.sql

```sql
CREATE TABLE IF NOT EXISTS tenant_password_reset_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(150) NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    used_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tenant_password_reset_tokens_email
    ON tenant_password_reset_tokens(email);

CREATE INDEX IF NOT EXISTS idx_tenant_password_reset_tokens_expires_at
    ON tenant_password_reset_tokens(expires_at);
```



# AUDIT TRAIL SERVICE

---

# audit/application/service

## AuditTrailService

```java
package com.financesystem.finance_api.modules.governance.audit.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.domain.model.PlatformAuditEvent;
import com.financesystem.finance_api.modules.governance.audit.domain.model.TenantAuditEvent;
import com.financesystem.finance_api.modules.governance.audit.domain.repository.PlatformAuditEventRepository;
import com.financesystem.finance_api.modules.governance.audit.domain.repository.TenantAuditEventRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditTrailService {

    private final PlatformAuditEventRepository platformAuditEventRepository;
    private final TenantAuditEventRepository tenantAuditEventRepository;
    private final SecurityContextFacade securityContextFacade;
    private final ObjectMapper objectMapper;

    public AuditTrailService(
            PlatformAuditEventRepository platformAuditEventRepository,
            TenantAuditEventRepository tenantAuditEventRepository,
            SecurityContextFacade securityContextFacade,
            ObjectMapper objectMapper
    ) {
        this.platformAuditEventRepository = platformAuditEventRepository;
        this.tenantAuditEventRepository = tenantAuditEventRepository;
        this.securityContextFacade = securityContextFacade;
        this.objectMapper = objectMapper;
    }

    public void recordPlatformEvent(
            String eventType,
            String resourceType,
            String resourceId,
            Object details
    ) {
        platformAuditEventRepository.save(
                new PlatformAuditEvent(
                        null,
                        resolveActorSubject(),
                        eventType,
                        resourceType,
                        resourceId,
                        serializeDetails(details),
                        null
                )
        );
    }

    public void recordTenantEvent(
            String eventType,
            String resourceType,
            String resourceId,
            Object details
    ) {
        tenantAuditEventRepository.save(
                new TenantAuditEvent(
                        null,
                        resolveActorSubject(),
                        eventType,
                        resourceType,
                        resourceId,
                        serializeDetails(details),
                        null
                )
        );
    }

    private String resolveActorSubject() {
        String subject = securityContextFacade.getCurrentSubject();
        return (subject == null || subject.isBlank()) ? "SYSTEM" : subject;
    }

    private String serializeDetails(Object details) {
        if (details == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(details);
        } catch (JsonProcessingException ex) {
            return String.valueOf(details);
        }
    }
}
```

---

# audit/domain/model

## AuditEventTypes

```java
package com.financesystem.finance_api.modules.governance.audit.domain.model;

public final class AuditEventTypes {

    public static final String TENANT_CREATED = "TENANT_CREATED";
    public static final String TENANT_ACTIVATED = "TENANT_ACTIVATED";
    public static final String TENANT_DEACTIVATED = "TENANT_DEACTIVATED";

    public static final String SUBSCRIPTION_ASSIGNED = "SUBSCRIPTION_ASSIGNED";
    public static final String PUBLIC_SIGNUP_COMPLETED = "PUBLIC_SIGNUP_COMPLETED";

    public static final String USER_CREATED = "USER_CREATED";
    public static final String USER_UPDATED = "USER_UPDATED";
    public static final String USER_ACTIVATED = "USER_ACTIVATED";
    public static final String USER_DEACTIVATED = "USER_DEACTIVATED";

    public static final String ROLE_CREATED = "ROLE_CREATED";
    public static final String ROLE_UPDATED = "ROLE_UPDATED";
    public static final String ROLE_ACTIVATED = "ROLE_ACTIVATED";
    public static final String ROLE_DEACTIVATED = "ROLE_DEACTIVATED";
    public static final String USER_ROLES_ASSIGNED = "USER_ROLES_ASSIGNED";

    public static final String LOGIN = "LOGIN";
    public static final String TOKEN_REFRESHED = "TOKEN_REFRESHED";
    public static final String LOGOUT = "LOGOUT";
    public static final String PASSWORD_CHANGED = "PASSWORD_CHANGED";
    public static final String PASSWORD_RESET_REQUESTED = "PASSWORD_RESET_REQUESTED";
    public static final String PASSWORD_RESET_COMPLETED = "PASSWORD_RESET_COMPLETED";

    private AuditEventTypes() {
    }
}
```