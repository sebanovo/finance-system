package com.financesystem.finance.modules.identity.auth.application.usecase;

import com.financesystem.finance.common.tenancy.context.TenantContext;
import com.financesystem.finance.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance.modules.governance.notifications.application.config.PasswordResetNotificationProperties;
import com.financesystem.finance.modules.governance.notifications.application.usecase.SendPasswordResetNotificationUseCase;
import com.financesystem.finance.modules.identity.auth.application.dto.ForgotPasswordRequest;
import com.financesystem.finance.modules.identity.auth.domain.repository.PasswordResetTokenRepository;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance.modules.identity.users.domain.repository.TenantUserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ForgotPasswordUseCaseTest {

    @AfterEach
    void cleanTenantContext() {
        TenantContextHolder.clear();
    }

    @Test
    void shouldCreateResetTokenAndSendNotificationWhenUserExists() {
        TenantUserRepository tenantUserRepository = mock(TenantUserRepository.class);
        PasswordResetTokenRepository passwordResetTokenRepository = mock(PasswordResetTokenRepository.class);
        SendPasswordResetNotificationUseCase sendPasswordResetNotificationUseCase = mock(SendPasswordResetNotificationUseCase.class);
        AuditTrailService auditTrailService = mock(AuditTrailService.class);

        PasswordResetNotificationProperties properties = new PasswordResetNotificationProperties();
        properties.setExpirationMinutes(30);
        properties.setResetUrlBase("http://localhost:4200/reset-password");

        ForgotPasswordUseCase useCase = new ForgotPasswordUseCase(
                tenantUserRepository,
                passwordResetTokenRepository,
                properties,
                sendPasswordResetNotificationUseCase,
                auditTrailService
        );

        TenantContextHolder.set(new TenantContext("financruz", "tenant_financruz", false));

        TenantUser tenantUser = new TenantUser(
                UUID.randomUUID(),
                "admin@financruz.com",
                "hash",
                "Admin",
                "Tenant",
                true,
                TenantUserStatus.ACTIVE,
                Instant.now(),
                Instant.now()
        );

        when(tenantUserRepository.findByEmail("admin@financruz.com")).thenReturn(Optional.of(tenantUser));

        useCase.execute(new ForgotPasswordRequest("admin@financruz.com"));

        verify(passwordResetTokenRepository).invalidateAllByEmail(eq("admin@financruz.com"), any());
        verify(passwordResetTokenRepository).save(any());
        verify(sendPasswordResetNotificationUseCase).execute(any());
        verify(auditTrailService).recordTenantEvent(anyString(), eq("USER"), eq(tenantUser.id().toString()), any());
    }
}