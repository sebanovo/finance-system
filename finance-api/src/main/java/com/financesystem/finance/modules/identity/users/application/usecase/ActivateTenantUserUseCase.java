package com.financesystem.finance.modules.identity.users.application.usecase;

import com.financesystem.finance.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance.modules.identity.users.domain.repository.TenantUserRepository;
import com.financesystem.finance.modules.platform.subscriptions.application.service.TenantPlanEnforcementService;
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
