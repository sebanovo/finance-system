package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.CreateAssistedServicePaymentRequest;
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

class CreateAssistedServicePaymentUseCaseTest {

    @Test
    void shouldDelegateAssistedPaymentWithCashboxChannel() {
        ServicePaymentProcessingService processingService = mock(ServicePaymentProcessingService.class);
        SecurityContextFacade securityContextFacade = mock(SecurityContextFacade.class);

        CreateAssistedServicePaymentUseCase useCase = new CreateAssistedServicePaymentUseCase(
                processingService,
                securityContextFacade
        );

        UUID actorUserId = UUID.randomUUID();
        UUID targetUserId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        UUID billId = UUID.randomUUID();

        when(securityContextFacade.getCurrentSubject()).thenReturn(actorUserId.toString());
        when(securityContextFacade.getCurrentTenantSlug()).thenReturn("tenant_financruz");

        ServicePaymentResponse expected = mock(ServicePaymentResponse.class);
        when(processingService.process(any())).thenReturn(expected);

        ServicePaymentResponse actual = useCase.execute(new CreateAssistedServicePaymentRequest(
                targetUserId,
                "100000000456",
                providerId,
                "100002",
                billId,
                "counter-pay-001"
        ));

        assertSame(expected, actual);
        verify(processingService).process(new ServicePaymentProcessingRequest(
                actorUserId,
                targetUserId,
                "tenant_financruz",
                providerId,
                "100002",
                billId,
                null,
                "100000000456",
                "counter-pay-001",
                TransactionChannel.CASHBOX,
                AuditEventTypes.SERVICE_PAYMENT_ASSISTED_CREATED,
                "ASSISTED"
        ));
    }
}
