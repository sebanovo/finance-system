package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListServiceBillsUseCase {

    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public ListServiceBillsUseCase(
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public Page<ServiceBillResponse> execute(PlatformServiceBillFilter filter, Pageable pageable) {
        return serviceBillRepository.findAll(filter, pageable)
                .map(serviceBill -> serviceProviderRepository.findById(serviceBill.providerId())
                        .map(provider -> servicePaymentsMapper.toResponse(serviceBill, provider))
                        .orElseThrow(() -> new ResourceNotFoundException("Service provider not found")));
    }
}
