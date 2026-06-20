package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.QueryServiceBillsResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.QueryTenantServiceBillsRequest;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class QueryTenantServiceBillsUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final TenantServicePaymentsMapper tenantServicePaymentsMapper;
    private final AuditTrailService auditTrailService;

    public QueryTenantServiceBillsUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceBillRepository serviceBillRepository,
            TenantServicePaymentsMapper tenantServicePaymentsMapper,
            AuditTrailService auditTrailService
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceBillRepository = serviceBillRepository;
        this.tenantServicePaymentsMapper = tenantServicePaymentsMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional(readOnly = true)
    public QueryServiceBillsResponse execute(QueryTenantServiceBillsRequest request) {
        if (request == null || request.providerId() == null || !StringUtils.hasText(request.serviceCustomerCode())) {
            throw new BusinessException("Provider and service customer code are required");
        }

        ServiceProvider provider = serviceProviderRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));
        if (provider.status() != ServiceProviderStatus.ACTIVE) {
            throw new BusinessException("Service provider is inactive");
        }

        var serviceCustomer = serviceCustomerRepository.findByProviderAndCode(provider.id(), request.serviceCustomerCode().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Service customer not found"));
        if (serviceCustomer.status() != ServiceCustomerStatus.ACTIVE) {
            throw new BusinessException("Service customer is inactive");
        }

        var bills = serviceBillRepository.findAllByProviderIdAndServiceCustomerCodeAndStatus(
                provider.id(),
                request.serviceCustomerCode().trim(),
                ServiceBillStatus.PENDING
        );

        auditTrailService.recordTenantEvent(
                AuditEventTypes.SERVICE_BILL_QUERIED,
                "SERVICE_BILL",
                serviceCustomer.id().toString(),
                PlatformAuditPayloads.details(
                        "tenantSlug", TenantContextHolder.getRequired().tenantSlug(),
                        "providerId", provider.id(),
                        "serviceCustomerCode", request.serviceCustomerCode().trim(),
                        "mode", "TENANT_ADMIN"
                )
        );

        return tenantServicePaymentsMapper.toBillsResponse(
                provider,
                request.serviceCustomerCode().trim(),
                serviceCustomer.customerName(),
                bills
        );
    }
}
