package com.financesystem.finance.modules.identity.users.application.mapper;

import com.financesystem.finance.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUser;
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