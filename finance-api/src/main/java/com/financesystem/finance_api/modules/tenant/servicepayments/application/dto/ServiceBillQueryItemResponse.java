package com.financesystem.finance_api.modules.tenant.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ServiceBillQueryItemResponse(
        UUID billId,
        String billingPeriod,
        BigDecimal amount,
        String currency,
        LocalDate dueDate,
        ServiceBillStatus status
) {
}
