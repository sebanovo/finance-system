package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceCustomerFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceCustomerResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListServiceCustomersUseCase {

    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public ListServiceCustomersUseCase(
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public Page<ServiceCustomerResponse> execute(PlatformServiceCustomerFilter filter, Pageable pageable) {
        return serviceCustomerRepository.findAll(filter, pageable)
                .map(serviceCustomer -> serviceProviderRepository.findById(serviceCustomer.providerId())
                        .map(provider -> servicePaymentsMapper.toResponse(serviceCustomer, provider))
                        .orElseThrow(() -> new ResourceNotFoundException("Service provider not found")));
    }
}
