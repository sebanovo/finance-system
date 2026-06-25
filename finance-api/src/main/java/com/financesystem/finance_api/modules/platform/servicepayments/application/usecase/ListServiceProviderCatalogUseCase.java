package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceCustomerFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceProviderFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceCustomerCatalogResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderCatalogResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListServiceProviderCatalogUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public ListServiceProviderCatalogUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServiceCustomerRepository serviceCustomerRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public List<ServiceProviderCatalogResponse> execute() {
        var providerPage = serviceProviderRepository.findAll(
                new PlatformServiceProviderFilter(null, ServiceProviderStatus.ACTIVE, null),
                Pageable.unpaged()
        );

        var customerPage = serviceCustomerRepository.findAll(
                new PlatformServiceCustomerFilter(null, ServiceCustomerStatus.ACTIVE, null, null),
                Pageable.unpaged()
        );

        Map<UUID, List<ServiceCustomerCatalogResponse>> customersByProvider = customerPage.getContent().stream()
                .sorted(Comparator.comparing(
                        (com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer customer) -> customer.serviceCustomerCode(),
                        String.CASE_INSENSITIVE_ORDER
                ).thenComparing(customer -> customer.customerName(), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.groupingBy(
                        com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer::providerId,
                        Collectors.mapping(servicePaymentsMapper::toCatalogResponse, Collectors.toList())
                ));

        return providerPage.getContent().stream()
                .sorted(Comparator.comparing(
                        (com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider provider) -> provider.name(),
                        String.CASE_INSENSITIVE_ORDER
                ).thenComparing(provider -> provider.code(), String.CASE_INSENSITIVE_ORDER))
                .map(provider -> servicePaymentsMapper.toCatalogResponse(
                        provider,
                        customersByProvider.getOrDefault(provider.id(), List.of())
                ))
                .toList();
    }
}
