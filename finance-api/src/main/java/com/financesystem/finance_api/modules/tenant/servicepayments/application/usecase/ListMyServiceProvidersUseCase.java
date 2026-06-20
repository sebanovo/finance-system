package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceProviderFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListMyServiceProvidersUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public ListMyServiceProvidersUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public Page<ServiceProviderResponse> execute(PlatformServiceProviderFilter filter, Pageable pageable) {
        PlatformServiceProviderFilter effectiveFilter = filter == null
                ? new PlatformServiceProviderFilter(null, ServiceProviderStatus.ACTIVE, null)
                : new PlatformServiceProviderFilter(filter.category(), ServiceProviderStatus.ACTIVE, filter.search());

        return serviceProviderRepository.findAll(effectiveFilter, pageable)
                .map(servicePaymentsMapper::toResponse);
    }
}
