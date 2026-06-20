package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetServiceBillUseCase {

    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public GetServiceBillUseCase(
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public ServiceBillResponse execute(UUID id) {
        var serviceBill = serviceBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service bill not found"));

        var provider = serviceProviderRepository.findById(serviceBill.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        return servicePaymentsMapper.toResponse(serviceBill, provider);
    }
}
