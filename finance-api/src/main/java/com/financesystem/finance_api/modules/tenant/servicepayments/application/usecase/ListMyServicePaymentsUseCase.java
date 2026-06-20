package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillPaymentFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.MyServicePaymentFilter;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ListMyServicePaymentsUseCase {

    private final ServiceBillPaymentRepository serviceBillPaymentRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper tenantServicePaymentsMapper;
    private final SecurityContextFacade securityContextFacade;

    public ListMyServicePaymentsUseCase(
            ServiceBillPaymentRepository serviceBillPaymentRepository,
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper tenantServicePaymentsMapper,
            SecurityContextFacade securityContextFacade
    ) {
        this.serviceBillPaymentRepository = serviceBillPaymentRepository;
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.tenantServicePaymentsMapper = tenantServicePaymentsMapper;
        this.securityContextFacade = securityContextFacade;
    }

    public Page<ServicePaymentResponse> execute(MyServicePaymentFilter filter, Pageable pageable) {
        UUID userId = currentUserId();
        String tenantSlug = currentTenantSlug();
        PlatformServiceBillPaymentFilter effectiveFilter = new PlatformServiceBillPaymentFilter(
                filter != null ? filter.providerId() : null,
                tenantSlug,
                userId,
                filter != null ? filter.receiptNumber() : null,
                filter != null ? filter.billId() : null,
                null,
                filter != null ? filter.paidAtFrom() : null,
                filter != null ? filter.paidAtTo() : null
        );

        return serviceBillPaymentRepository.findAll(effectiveFilter, pageable)
                .map(payment -> serviceBillRepository.findById(payment.billId())
                        .flatMap(bill -> serviceProviderRepository.findById(payment.providerId())
                                .map(provider -> tenantServicePaymentsMapper.toPaymentResponse(payment, bill, provider)))
                        .orElseThrow(() -> new BusinessException("Service payment data is incomplete")));
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
