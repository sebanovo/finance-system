package com.financesystem.finance_api.modules.platform.servicepayments.application.mapper;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class ServicePaymentsMapper {

    public ServiceProviderResponse toResponse(ServiceProvider serviceProvider) {
        return new ServiceProviderResponse(
                serviceProvider.id(),
                serviceProvider.code(),
                serviceProvider.name(),
                serviceProvider.category(),
                serviceProvider.serviceCustomerCodeLabel(),
                serviceProvider.status(),
                serviceProvider.createdAt(),
                serviceProvider.updatedAt()
        );
    }

    public ServiceProviderSummaryResponse toSummary(ServiceProvider serviceProvider) {
        return new ServiceProviderSummaryResponse(
                serviceProvider.id(),
                serviceProvider.code(),
                serviceProvider.name(),
                serviceProvider.category()
        );
    }

    public ServiceCustomerResponse toResponse(ServiceCustomer serviceCustomer, ServiceProvider provider) {
        return new ServiceCustomerResponse(
                serviceCustomer.id(),
                toSummary(provider),
                serviceCustomer.serviceCustomerCode(),
                serviceCustomer.customerName(),
                serviceCustomer.status(),
                serviceCustomer.createdAt(),
                serviceCustomer.updatedAt()
        );
    }

    public ServiceBillResponse toResponse(ServiceBill serviceBill, ServiceProvider provider) {
        return new ServiceBillResponse(
                serviceBill.id(),
                toSummary(provider),
                serviceBill.serviceCustomerId(),
                serviceBill.serviceCustomerCode(),
                serviceBill.customerName(),
                serviceBill.billingPeriod(),
                serviceBill.amount(),
                serviceBill.currency(),
                serviceBill.dueDate(),
                serviceBill.status(),
                serviceBill.paidByTenantId(),
                serviceBill.paidByTenantSlug(),
                serviceBill.paidByUserId(),
                serviceBill.paidByAccountId(),
                serviceBill.paidByAccountNumber(),
                serviceBill.paidTransactionId(),
                serviceBill.paidAt(),
                serviceBill.createdBySuperadminId(),
                serviceBill.createdAt(),
                serviceBill.updatedAt()
        );
    }

    public ServiceBillPaymentResponse toResponse(ServiceBillPayment payment, ServiceBill bill, ServiceProvider provider) {
        return new ServiceBillPaymentResponse(
                payment.id(),
                payment.billId(),
                toSummary(provider),
                bill.serviceCustomerCode(),
                bill.customerName(),
                bill.billingPeriod(),
                payment.paidByTenantId(),
                payment.paidByTenantSlug(),
                payment.paidByUserId(),
                payment.paidByAccountId(),
                payment.paidByAccountNumber(),
                payment.paidTransactionId(),
                payment.amount(),
                payment.currency(),
                payment.receiptNumber(),
                payment.idempotencyKey(),
                payment.status(),
                payment.paidAt(),
                payment.createdAt()
        );
    }
}
