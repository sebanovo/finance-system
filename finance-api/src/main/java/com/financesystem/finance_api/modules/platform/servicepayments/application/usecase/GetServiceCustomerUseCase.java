package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceCustomerResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetServiceCustomerUseCase {

    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public GetServiceCustomerUseCase(
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public ServiceCustomerResponse execute(UUID id) {
        var serviceCustomer = serviceCustomerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service customer not found"));

        var provider = serviceProviderRepository.findById(serviceCustomer.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        return servicePaymentsMapper.toResponse(serviceCustomer, provider);
    }
}
