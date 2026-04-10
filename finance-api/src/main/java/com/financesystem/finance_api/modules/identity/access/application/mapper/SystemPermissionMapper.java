package com.financesystem.finance_api.modules.identity.access.application.mapper;

import com.financesystem.finance_api.modules.identity.access.application.dto.SystemPermissionResponse;
import com.financesystem.finance_api.modules.identity.access.domain.model.SystemPermission;
import org.springframework.stereotype.Component;

@Component
public class SystemPermissionMapper {

    public SystemPermissionResponse toResponse(SystemPermission systemPermission) {
        return new SystemPermissionResponse(
                systemPermission.code(),
                systemPermission.module(),
                systemPermission.description()
        );
    }
}