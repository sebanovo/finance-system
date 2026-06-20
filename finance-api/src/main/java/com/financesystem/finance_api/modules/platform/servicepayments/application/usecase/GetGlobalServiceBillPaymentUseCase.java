package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillPaymentResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetGlobalServiceBillPaymentUseCase {

    private final ServiceBillPaymentRepository serviceBillPaymentRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public GetGlobalServiceBillPaymentUseCase(
            ServiceBillPaymentRepository serviceBillPaymentRepository,
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceBillPaymentRepository = serviceBillPaymentRepository;
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public ServiceBillPaymentResponse execute(UUID id) {
        var payment = serviceBillPaymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service bill payment not found"));

        var bill = serviceBillRepository.findById(payment.billId())
                .orElseThrow(() -> new ResourceNotFoundException("Service bill not found"));

        var provider = serviceProviderRepository.findById(payment.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        return servicePaymentsMapper.toResponse(payment, bill, provider);
    }
}
