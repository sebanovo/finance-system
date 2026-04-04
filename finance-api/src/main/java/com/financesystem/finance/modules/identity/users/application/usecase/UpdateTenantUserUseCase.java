package com.financesystem.finance.modules.identity.users.application.usecase;

import com.financesystem.finance.modules.identity.users.application.dto.TenantUserResponse;
import com.financesystem.finance.modules.identity.users.application.dto.UpdateTenantUserRequest;
import com.financesystem.finance.modules.identity.users.application.mapper.TenantUserMapper;
import com.financesystem.finance.modules.identity.users.domain.exception.TenantUserAlreadyExistsException;
import com.financesystem.finance.modules.identity.users.domain.exception.TenantUserNotFoundException;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UpdateTenantUserUseCase {

    private final TenantUserRepository tenantUserRepository;
    private final TenantUserMapper tenantUserMapper;

    public UpdateTenantUserUseCase(
            TenantUserRepository tenantUserRepository,
            TenantUserMapper tenantUserMapper
    ) {
        this.tenantUserRepository = tenantUserRepository;
        this.tenantUserMapper = tenantUserMapper;
    }

    @Transactional
    public TenantUserResponse execute(UUID id, UpdateTenantUserRequest request) {
        TenantUser existingUser = tenantUserRepository.findById(id)
                .orElseThrow(() -> new TenantUserNotFoundException("Tenant user not found with id: " + id));

        String normalizedEmail = request.email().trim().toLowerCase();

        tenantUserRepository.findByEmail(normalizedEmail)
                .filter(foundUser -> !foundUser.id().equals(id))
                .ifPresent(foundUser -> {
                    throw new TenantUserAlreadyExistsException(
                            "A tenant user with email '" + normalizedEmail + "' already exists"
                    );
                });

        TenantUser updatedUser = new TenantUser(
                existingUser.id(),
                normalizedEmail,
                existingUser.passwordHash(),
                normalizeNullable(request.firstName()),
                normalizeNullable(request.lastName()),
                existingUser.active(),
                existingUser.status(),
                existingUser.createdAt(),
                existingUser.updatedAt()
        );

        TenantUser savedUser = tenantUserRepository.save(updatedUser);
        return tenantUserMapper.toResponse(savedUser);
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}