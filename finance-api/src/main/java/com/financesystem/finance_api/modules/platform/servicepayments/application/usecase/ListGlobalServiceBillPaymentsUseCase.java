package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillPaymentFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillPaymentResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListGlobalServiceBillPaymentsUseCase {

    private final ServiceBillPaymentRepository serviceBillPaymentRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public ListGlobalServiceBillPaymentsUseCase(
            ServiceBillPaymentRepository serviceBillPaymentRepository,
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceBillPaymentRepository = serviceBillPaymentRepository;
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public Page<ServiceBillPaymentResponse> execute(PlatformServiceBillPaymentFilter filter, Pageable pageable) {
        return serviceBillPaymentRepository.findAll(filter, pageable)
                .map(payment -> serviceBillRepository.findById(payment.billId())
                        .flatMap(bill -> serviceProviderRepository.findById(payment.providerId())
                                .map(provider -> servicePaymentsMapper.toResponse(payment, bill, provider)))
                        .orElseThrow(() -> new ResourceNotFoundException("Service provider not found")));
    }
}
