package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.*;
import com.financesystem.finance_api.modules.tenant.accounts.domain.repository.AccountRepository;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.CreateBankServicePaymentRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentProcessingRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.service.ServicePaymentProcessingService;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreateBankServicePaymentUseCaseTest {

    @Test
    void shouldResolvePayerFromSourceAccountAndDelegateToProcessingService() {
        ServicePaymentProcessingService processingService = mock(ServicePaymentProcessingService.class);
        SecurityContextFacade securityContextFacade = mock(SecurityContextFacade.class);
        AccountRepository accountRepository = mock(AccountRepository.class);

        CreateBankServicePaymentUseCase useCase = new CreateBankServicePaymentUseCase(
                processingService,
                securityContextFacade,
                accountRepository
        );

        UUID actorUserId = UUID.randomUUID();
        UUID payerUserId = UUID.randomUUID();
        UUID providerId = UUID.randomUUID();
        UUID billId = UUID.randomUUID();
        UUID accountId = UUID.randomUUID();
        Instant now = Instant.now();

        Account account = new Account(
                accountId,
                payerUserId,
                "100000000456",
                AccountName.CHECKING_ACCOUNT,
                null,
                AccountType.CHECKING,
                CurrencyCode.BOB,
                new BigDecimal("250.00"),
                BigDecimal.ZERO,
                AccountStatus.ACTIVE,
                null,
                true,
                true,
                now,
                null,
                now,
                now
        );

        when(securityContextFacade.getCurrentSubject()).thenReturn(actorUserId.toString());
        when(securityContextFacade.getCurrentTenantSlug()).thenReturn("tenant_financruz");
        when(accountRepository.findByAccountNumber("100000000456")).thenReturn(Optional.of(account));

        ServicePaymentResponse expected = mock(ServicePaymentResponse.class);
        when(processingService.process(any())).thenReturn(expected);

        ServicePaymentResponse actual = useCase.execute(new CreateBankServicePaymentRequest(
                "100000000456",
                providerId,
                "100002",
                billId,
                "bank-pay-001"
        ));

        assertSame(expected, actual);
        verify(processingService).process(new ServicePaymentProcessingRequest(
                actorUserId,
                payerUserId,
                "tenant_financruz",
                providerId,
                "100002",
                billId,
                null,
                "100000000456",
                "bank-pay-001",
                TransactionChannel.CASHBOX,
                AuditEventTypes.SERVICE_PAYMENT_ASSISTED_CREATED,
                "ASSISTED"
        ));
    }
}
