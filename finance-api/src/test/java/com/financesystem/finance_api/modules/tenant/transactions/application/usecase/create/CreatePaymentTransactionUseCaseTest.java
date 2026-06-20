package com.financesystem.finance_api.modules.tenant.transactions.application.usecase.create;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreatePaymentTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
import com.financesystem.finance_api.modules.tenant.transactions.application.service.PaymentProcessingService;
import com.financesystem.finance_api.modules.tenant.transactions.domain.model.TransactionChannel;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreatePaymentTransactionUseCaseTest {

    @Test
    void shouldForwardAuthenticatedUserAsRequester() {
        PaymentProcessingService paymentProcessingService = mock(PaymentProcessingService.class);
        SecurityContextFacade securityContextFacade = mock(SecurityContextFacade.class);
        CreatePaymentTransactionUseCase useCase = new CreatePaymentTransactionUseCase(paymentProcessingService, securityContextFacade);

        UUID userId = UUID.randomUUID();
        CreatePaymentTransactionRequest request = new CreatePaymentTransactionRequest(
                UUID.randomUUID(),
                new BigDecimal("200.00"),
                CurrencyCode.BOB,
                TransactionChannel.API,
                "EXT-1",
                "Payment",
                "idem-2"
        );
        TransactionResponse expected = mock(TransactionResponse.class);

        when(securityContextFacade.getCurrentSubject()).thenReturn(userId.toString());
        when(paymentProcessingService.createPayment(any(CreatePaymentTransactionRequest.class), eq(userId)))
                .thenReturn(expected);

        TransactionResponse actual = useCase.execute(request);

        assertSame(expected, actual);
        verify(paymentProcessingService).createPayment(request, userId);
    }
}
