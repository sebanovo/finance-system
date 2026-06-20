package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceProviderFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListServiceProvidersUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public ListServiceProvidersUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public Page<ServiceProviderResponse> execute(PlatformServiceProviderFilter filter, Pageable pageable) {
        return serviceProviderRepository.findAll(filter, pageable)
                .map(servicePaymentsMapper::toResponse);
    }
}
