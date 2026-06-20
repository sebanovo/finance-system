package com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderSummaryResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPayment;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.QueryServiceBillsResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServiceBillQueryItemResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServiceEnrollmentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServicePaymentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.model.TenantServiceEnrollment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantServicePaymentsMapper {

    public ServiceEnrollmentResponse toEnrollmentResponse(TenantServiceEnrollment enrollment) {
        return new ServiceEnrollmentResponse(
                enrollment.id(),
                new ServiceProviderSummaryResponse(
                        enrollment.providerId(),
                        enrollment.providerCode(),
                        enrollment.providerName(),
                        com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory.valueOf(enrollment.providerCategory())
                ),
                enrollment.serviceCustomerCode(),
                enrollment.serviceCustomerName(),
                enrollment.alias(),
                enrollment.status(),
                enrollment.createdAt(),
                enrollment.updatedAt()
        );
    }

    public QueryServiceBillsResponse toBillsResponse(ServiceProvider provider, String customerCode, String customerName, List<ServiceBill> bills) {
        return new QueryServiceBillsResponse(
                new ServiceProviderSummaryResponse(
                        provider.id(),
                        provider.code(),
                        provider.name(),
                        provider.category()
                ),
                customerCode,
                customerName,
                bills.stream()
                        .map(this::toBillItemResponse)
                        .toList()
        );
    }

    public ServiceBillQueryItemResponse toBillItemResponse(ServiceBill bill) {
        return new ServiceBillQueryItemResponse(
                bill.id(),
                bill.billingPeriod(),
                bill.amount(),
                bill.currency(),
                bill.dueDate(),
                bill.status()
        );
    }

    public ServicePaymentResponse toPaymentResponse(ServiceBillPayment payment, ServiceBill bill, ServiceProvider provider) {
        return new ServicePaymentResponse(
                payment.id(),
                bill.id(),
                payment.paidTransactionId(),
                payment.receiptNumber(),
                new ServiceProviderSummaryResponse(
                        provider.id(),
                        provider.code(),
                        provider.name(),
                        provider.category()
                ),
                bill.serviceCustomerCode(),
                bill.customerName(),
                bill.billingPeriod(),
                payment.amount(),
                payment.currency(),
                payment.paidByAccountNumber(),
                bill.status(),
                payment.paidAt()
        );
    }
}
