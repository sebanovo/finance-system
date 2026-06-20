package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetServiceProviderUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public GetServiceProviderUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public ServiceProviderResponse execute(UUID id) {
        return serviceProviderRepository.findById(id)
                .map(servicePaymentsMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));
    }
}
