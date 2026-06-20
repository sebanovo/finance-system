package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.CreateServicePaymentRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentProcessingRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.service.ServicePaymentProcessingService;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateMyServicePaymentUseCaseTest {

    @Test
    void shouldDelegateSelfPaymentWithCurrentUserAndApiChannel() {
        ServicePaymentProcessingService processingService = mock(ServicePaymentProcessingService.class);
        SecurityContextFacade securityContextFacade = mock(SecurityContextFacade.class);

        CreateMyServicePaymentUseCase useCase = new CreateMyServicePaymentUseCase(processingService, securityContextFacade);

        UUID userId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        UUID billId = UUID.randomUUID();
        UUID enrollmentId = UUID.randomUUID();

        when(securityContextFacade.getCurrentSubject()).thenReturn(userId.toString());
        when(securityContextFacade.getCurrentTenantSlug()).thenReturn("tenant_financruz");

        ServicePaymentResponse expected = mock(ServicePaymentResponse.class);
        when(processingService.process(any())).thenReturn(expected);

        ServicePaymentResponse actual = useCase.execute(new CreateServicePaymentRequest(
                "100000000123",
                providerId,
                "100001",
                billId,
                enrollmentId,
                "self-service-001"
        ));

        assertSame(expected, actual);
        verify(processingService).process(new ServicePaymentProcessingRequest(
                userId,
                userId,
                "tenant_financruz",
                providerId,
                "100001",
                billId,
                enrollmentId,
                "100000000123",
                "self-service-001",
                TransactionChannel.API,
                AuditEventTypes.SERVICE_PAYMENT_CREATED,
                "SELF_SERVICE"
        ));
    }
}
