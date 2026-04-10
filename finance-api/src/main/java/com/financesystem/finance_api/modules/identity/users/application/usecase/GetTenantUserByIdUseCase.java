package com.financesystem.finance_api.modules.identity.users.application.usecase;

import com.financesystem.finance_api.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance_api.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance_api.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetTenantUserByIdUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserMapper tenantUserMapper;

    public GetTenantUserByIdUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserMapper tenantUserMapper
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserMapper = tenantUserMapper;
    }

    public TenantUserResponse execute(UUID id) {
        return tenantUserRepository.findById(id)
                .map(tenantUserMapper::toResponse)
                .orElseThrow(() -> new TenantUserNotFoundException("Tenant user not found with id: " + id));
    }
}