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
public class GetTenantServicePaymentUseCase {

    private final ServiceBillPaymentRepository serviceBillPaymentRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final TenantServicePaymentsMapper tenantServicePaymentsMapper;
    private final SecurityContextFacade securityContextFacade;

    public GetTenantServicePaymentUseCase(
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
        var payment = serviceBillPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Service payment not found"));
        if (!currentTenantSlug().equalsIgnoreCase(payment.paidByTenantSlug())) {
            throw new BusinessException("Service payment does not belong to the current tenant");
        }

        var bill = serviceBillRepository.findById(payment.billId())
                .orElseThrow(() -> new ResourceNotFoundException("Service bill not found"));
        var provider = serviceProviderRepository.findById(payment.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        return tenantServicePaymentsMapper.toPaymentResponse(payment, bill, provider);
    }

    private String currentTenantSlug() {
        String tenantSlug = securityContextFacade.getCurrentTenantSlug();
        if (tenantSlug == null || tenantSlug.isBlank()) {
            throw new BusinessException("Tenant is required");
        }
        return tenantSlug.trim();
    }
}
