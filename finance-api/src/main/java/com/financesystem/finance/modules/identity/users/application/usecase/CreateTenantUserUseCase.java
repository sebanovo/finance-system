package com.financesystem.finance.modules.identity.users.application.usecase;

import com.financesystem.finance.modules.identity.users.application.dto.CreateTenantUserRequest;
import com.financesystem.finance.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance.modules.identity.users.domain.exception.TenantUserAlreadyExistsException;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateTenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserMapper tenantUserMapper;
    private final PasswordEncoder passwordEncoder;

    public CreateTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserMapper tenantUserMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserMapper = tenantUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public TenantUserResponse execute(CreateTenantUserRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        if (tenantUserRepository.existsByEmail(normalizedEmail)) {
            throw new TenantUserAlreadyExistsException(
                    "A tenant user with email '" + normalizedEmail + "' already exists"
            );
        }

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