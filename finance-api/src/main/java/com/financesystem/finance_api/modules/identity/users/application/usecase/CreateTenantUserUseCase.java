package com.financesystem.finance_api.modules.identity.users.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.identity.audit.IdentityAuditPayloads;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantRoleRepository;
import com.financesystem.finance_api.modules.identity.access.domain.repository.TenantUserRoleRepository;
import com.financesystem.finance_api.modules.identity.users.application.dto.CreateTenantUserRequest;
import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserAlreadyExistsException;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import com.financesystem.finance_api.modules.platform.subscriptions.application.service.TenantPlanEnforcementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class CreateTenantUserUseCase {

    private static final Logger logger = LoggerFactory.getLogger(CreateTenantUserUseCase.class);

    private final TenantUserRepository tenantUserRepository;
    private final TenantRoleRepository tenantRoleRepository;
    private final TenantUserRoleRepository tenantUserRoleRepository;
    private final TenantUserMapper tenantUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuditTrailService auditTrailService;
    private final TenantPlanEnforcementService tenantPlanEnforcementService;

    public CreateTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantRoleRepository tenantRoleRepository,
            TenantUserRoleRepository tenantUserRoleRepository,
            TenantUserMapper tenantUserMapper,
            PasswordEncoder passwordEncoder,
            AuditTrailService auditTrailService,
            TenantPlanEnforcementService tenantPlanEnforcementService
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantRoleRepository = tenantRoleRepository;
        this.tenantUserRoleRepository = tenantUserRoleRepository;
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

        boolean defaultRoleAssigned = tenantRoleRepository.findByName("USER")
                .map(role -> role.id())
                .map(userRoleId -> {
                    tenantUserRoleRepository.replaceUserRoles(createdTenantUser.id(), List.of(userRoleId));
                    return true;
                })
                .orElseGet(() -> {
                    logger.warn(
                            "Default tenant role 'USER' was not found for user '{}'; user will be created without roles",
                            createdTenantUser.email()
                    );
                    return false;
                });

        auditTrailService.recordTenantEvent(
                AuditEventTypes.USER_CREATED,
                "USER",
                createdTenantUser.id().toString(),
                IdentityAuditPayloads.of(
                        "operation", "CREATE_USER",
                        "email", createdTenantUser.email(),
                        "firstName", createdTenantUser.firstName(),
                        "lastName", createdTenantUser.lastName(),
                        "defaultRole", "USER",
                        "defaultRoleAssigned", defaultRoleAssigned
                ),
                null,
                IdentityAuditPayloads.userState(
                        createdTenantUser.email(),
                        createdTenantUser.firstName(),
                        createdTenantUser.lastName(),
                        createdTenantUser.active(),
                        createdTenantUser.status().name()
                )
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
