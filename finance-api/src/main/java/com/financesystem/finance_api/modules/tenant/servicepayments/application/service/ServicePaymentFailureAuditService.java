package com.financesystem.finance_api.modules.tenant.servicepayments.application.service;

import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentProcessingRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ServicePaymentFailureAuditService {

    private final AuditTrailService auditTrailService;

    public ServicePaymentFailureAuditService(AuditTrailService auditTrailService) {
        this.auditTrailService = auditTrailService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(
            ServicePaymentProcessingRequest request,
            String tenantSlug,
            UUID actorUserId,
            UUID payerUserId,
            RuntimeException exception
    ) {
        auditTrailService.recordTenantEvent(
                AuditEventTypes.SERVICE_PAYMENT_FAILED,
                "SERVICE_PAYMENT",
                request.billId().toString(),
                PlatformAuditPayloads.details(
                        "tenantSlug", tenantSlug,
                        "billId", request.billId(),
                        "providerId", request.providerId(),
                        "serviceCustomerCode", request.serviceCustomerCode(),
                        "sourceAccountNumber", request.sourceAccountNumber(),
                        "actorUserId", actorUserId,
                        "payerUserId", payerUserId,
                        "reason", exception.getMessage(),
                        "mode", request.paymentMode(),
                        "idempotencyKey", request.idempotencyKey()
                )
        );
    }
}
