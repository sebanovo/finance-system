package com.financesystem.finance_api.modules.tenant.transactions.application.service;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.CreatePaymentTransactionRequest;
import com.financesystem.finance_api.modules.tenant.transactions.application.dto.TransactionResponse;
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

class PaymentProcessingServiceTest {

    @Test
    void shouldDelegatePaymentProcessingWithExplicitRequester() {
        TransactionProcessorService transactionProcessorService = mock(TransactionProcessorService.class);
        PaymentProcessingService paymentProcessingService = new PaymentProcessingService(transactionProcessorService);

        UUID requestedByUserId = UUID.randomUUID();
        CreatePaymentTransactionRequest request = new CreatePaymentTransactionRequest(
                UUID.randomUUID(),
                new BigDecimal("120.00"),
                CurrencyCode.BOB,
                TransactionChannel.API,
                "INV-1",
                "Service payment",
                "idem-1"
        );
        TransactionResponse expected = mock(TransactionResponse.class);

        when(transactionProcessorService.createPayment(any(CreatePaymentTransactionRequest.class), eq(requestedByUserId)))
                .thenReturn(expected);

        TransactionResponse actual = paymentProcessingService.createPayment(request, requestedByUserId);

        assertSame(expected, actual);
        verify(transactionProcessorService).createPayment(request, requestedByUserId);
    }
}
