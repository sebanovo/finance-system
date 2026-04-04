package com.financesystem.finance.modules.identity.auth.application.usecase;

import com.financesystem.finance.common.exception.BusinessException;
import com.financesystem.finance.common.security.context.SecurityContextFacade;
import com.financesystem.finance.modules.identity.auth.application.dto.ChangePasswordRequest;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance.modules.identity.users.domain.repository.TenantUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangePasswordUseCase {

    private final SecurityContextFacade securityContextFacade;
    private final TenantUserRepository tenantUserRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordUseCase(
            SecurityContextFacade securityContextFacade,
            TenantUserRepository tenantUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.securityContextFacade = securityContextFacade;
        this.tenantUserRepository = tenantUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void execute(ChangePasswordRequest request) {
        String currentSubject = securityContextFacade.getCurrentSubject();

        TenantUser tenantUser = tenantUserRepository.findByEmail(currentSubject)
                .orElseThrow(() -> new BusinessException("Authenticated user was not found"));

        if (!passwordEncoder.matches(request.currentPassword(), tenantUser.passwordHash())) {
            throw new BusinessException("Current password is incorrect");
        }

        if (request.currentPassword().equals(request.newPassword())) {
            throw new BusinessException("New password must be different from current password");
        }

        TenantUser updatedUser = new TenantUser(
                tenantUser.id(),
                tenantUser.email(),
                passwordEncoder.encode(request.newPassword()),
                tenantUser.firstName(),
                tenantUser.lastName(),
                tenantUser.active(),
                tenantUser.status(),
                tenantUser.createdAt(),
                tenantUser.updatedAt()
        );

        tenantUserRepository.save(updatedUser);
    }
}