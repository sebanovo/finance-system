package com.financesystem.finance.modules.identity.users.application.usecase;

import com.financesystem.finance.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActivateTenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserMapper tenantUserMapper;

    public ActivateTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserMapper tenantUserMapper
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserMapper = tenantUserMapper;
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
                true,
                TenantUserStatus.ACTIVE,
                existingUser.createdAt(),
                existingUser.updatedAt()
        );

        TenantUser savedUser = tenantUserRepository.save(updatedUser);
        return tenantUserMapper.toResponse(savedUser);
    }
}