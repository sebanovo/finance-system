package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetMyServicePaymentUseCase {

    private final ServiceBillPaymentRepository serviceBillPaymentRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final TenantServicePaymentsMapper tenantServicePaymentsMapper;
    private final SecurityContextFacade securityContextFacade;

    public GetMyServicePaymentUseCase(
            ServiceBillPaymentRepository serviceBillPaymentRepository,
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            TenantServicePaymentsMapper tenantServicePaymentsMapper,
            SecurityContextFacade securityContextFacade
    ) {
        this.serviceBillPaymentRepository = serviceBillPaymentRepository;
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.tenantServicePaymentsMapper = tenantServicePaymentsMapper;
        this.securityContextFacade = securityContextFacade;
    }

    public ServicePaymentResponse execute(UUID paymentId) {
        UUID userId = currentUserId();
        String tenantSlug = currentTenantSlug();

        var payment = serviceBillPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Service payment not found"));
        if (!tenantSlug.equalsIgnoreCase(payment.paidByTenantSlug()) || !payment.paidByUserId().equals(userId)) {
            throw new BusinessException("Service payment does not belong to the current user");
        }

        var bill = serviceBillRepository.findById(payment.billId())
                .orElseThrow(() -> new ResourceNotFoundException("Service bill not found"));
        var provider = serviceProviderRepository.findById(payment.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        return tenantServicePaymentsMapper.toPaymentResponse(payment, bill, provider);
    }

    private UUID currentUserId() {
        String subject = securityContextFacade.getCurrentSubject();
        if (subject == null || subject.isBlank()) {
            throw new BusinessException("Authenticated user is required");
        }

        try {
            return UUID.fromString(subject.trim());
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Authenticated user subject is invalid");
        }
    }

    private String currentTenantSlug() {
        String tenantSlug = securityContextFacade.getCurrentTenantSlug();
        if (tenantSlug == null || tenantSlug.isBlank()) {
            throw new BusinessException("Tenant is required");
        }

        return tenantSlug.trim();
    }
}
