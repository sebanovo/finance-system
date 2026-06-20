package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillPaymentFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.TenantServicePaymentFilter;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ListTenantServicePaymentsUseCase {

    private final ServiceBillPaymentRepository serviceBillPaymentRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final TenantServicePaymentsMapper tenantServicePaymentsMapper;
    private final SecurityContextFacade securityContextFacade;

    public ListTenantServicePaymentsUseCase(
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

    public Page<ServicePaymentResponse> execute(TenantServicePaymentFilter filter, Pageable pageable) {
        UUID userId = filter != null ? filter.userId() : null;
        PlatformServiceBillPaymentFilter effectiveFilter = new PlatformServiceBillPaymentFilter(
                filter != null ? filter.providerId() : null,
                currentTenantSlug(),
                userId,
                filter != null ? filter.accountNumber() : null,
                filter != null ? filter.billId() : null,
                filter != null ? filter.receiptNumber() : null,
                filter != null ? filter.paidAtFrom() : null,
                filter != null ? filter.paidAtTo() : null
        );

        return serviceBillPaymentRepository.findAll(effectiveFilter, pageable)
                .map(payment -> serviceBillRepository.findById(payment.billId())
                        .flatMap(bill -> serviceProviderRepository.findById(payment.providerId())
                                .map(provider -> tenantServicePaymentsMapper.toPaymentResponse(payment, bill, provider)))
                        .orElseThrow(() -> new BusinessException("Service payment data is incomplete")));
    }

    private String currentTenantSlug() {
        String tenantSlug = securityContextFacade.getCurrentTenantSlug();
        if (tenantSlug == null || tenantSlug.isBlank()) {
            throw new BusinessException("Tenant is required");
        }
        return tenantSlug.trim();
    }
}
