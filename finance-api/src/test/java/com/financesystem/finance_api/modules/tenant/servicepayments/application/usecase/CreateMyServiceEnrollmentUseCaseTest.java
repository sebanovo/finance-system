package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderSummaryResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.identity.users.domain.repository.TenantUserRepository;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUser;
import com.financesystem.finance_api.modules.identity.users.domain.model.TenantUserStatus;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.CreateServiceEnrollmentRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollment;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollmentStatus;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.repository.TenantServiceEnrollmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateMyServiceEnrollmentUseCaseTest {

    @AfterEach
    void clearTenantContext() {
        TenantContextHolder.clear();
    }

    @Test
    void shouldReactivateInactiveEnrollmentAndReplaceAliasWhenProvided() {
        ServiceProviderRepository providerRepository = mock(ServiceProviderRepository.class);
        ServiceCustomerRepository customerRepository = mock(ServiceCustomerRepository.class);
        TenantServiceEnrollmentRepository enrollmentRepository = mock(TenantServiceEnrollmentRepository.class);
        TenantUserRepository tenantUserRepository = mock(TenantUserRepository.class);
        TenantServicePaymentsMapper mapper = new TenantServicePaymentsMapper();
        AuditTrailService auditTrailService = mock(AuditTrailService.class);
        SecurityContextFacade securityContextFacade = mock(SecurityContextFacade.class);

        UUID userId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID enrollmentId = UUID.randomUUID();

        TenantContextHolder.set(new TenantContext("tenant-a", "tenant_a", false));
        when(securityContextFacade.getCurrentSubject()).thenReturn(userId.toString());
        when(tenantUserRepository.findById(userId)).thenReturn(Optional.of(new TenantUser(
                userId,
                "user@test.com",
                "hash",
                "Test",
                "User",
                true,
                TenantUserStatus.ACTIVE,
                null,
                null
        )));
        when(providerRepository.findById(providerId)).thenReturn(Optional.of(new ServiceProvider(
                providerId,
                "ENER-001",
                "Energia Sur",
                ServiceProviderCategory.ELECTRICITY,
                null,
                ServiceProviderStatus.ACTIVE,
                null,
                null
        )));
        when(customerRepository.findByProviderAndCode(providerId, "100001")).thenReturn(Optional.of(new ServiceCustomer(
                customerId,
                providerId,
                "100001",
                "Cliente Energia",
                ServiceCustomerStatus.ACTIVE,
                null,
                null
        )));

        TenantServiceEnrollment inactiveEnrollment = new TenantServiceEnrollment(
                enrollmentId,
                userId,
                providerId,
                "ENER-001",
                "Energia Sur",
                ServiceProviderCategory.ELECTRICITY.name(),
                "100001",
                "Cliente Energia Antiguo",
                "Alias viejo",
                TenantServiceEnrollmentStatus.INACTIVE,
                null,
                null
        );

        when(enrollmentRepository.findByUserIdAndProviderIdAndServiceCustomerCode(userId, providerId, "100001"))
                .thenReturn(Optional.of(inactiveEnrollment));
        when(enrollmentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CreateMyServiceEnrollmentUseCase useCase = new CreateMyServiceEnrollmentUseCase(
                providerRepository,
                customerRepository,
                enrollmentRepository,
                tenantUserRepository,
                mapper,
                auditTrailService,
                securityContextFacade
        );

        var response = useCase.execute(new CreateServiceEnrollmentRequest(providerId, " 100001 ", "Alias nuevo"));

        assertNotNull(response);
        assertEquals(enrollmentId, response.enrollmentId());
        assertEquals(TenantServiceEnrollmentStatus.ACTIVE, response.status());
        assertEquals("Alias nuevo", response.alias());
        verify(enrollmentRepository).save(any(TenantServiceEnrollment.class));
    }
}
