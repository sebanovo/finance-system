# Directory Export: /home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/modules/platform/servicepayments

_Generated on 2026-06-20 03:22:41Z_

## Summary

- Source directory: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/modules/platform/servicepayments`
- Output file: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/modules/platform/servicepayments.md`

## Files

### `application/dto/CancelServiceBillRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import jakarta.validation.constraints.Size;

public record CancelServiceBillRequest(
        @Size(max = 255)
        String reason
) {
}

```

### `application/dto/ChangeServiceProviderStatusRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import jakarta.validation.constraints.NotNull;

public record ChangeServiceProviderStatusRequest(
        @NotNull
        ServiceProviderStatus status
) {
}

```

### `application/dto/CreateServiceBillRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.tenant.accounts.domain.model.CurrencyCode;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record CreateServiceBillRequest(
        @NotNull
        UUID providerId,

        @NotBlank
        @Size(max = 100)
        String serviceCustomerCode,

        @NotBlank
        @Pattern(regexp = "^\\d{4}-\\d{2}$")
        String billingPeriod,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount,

        @NotNull
        CurrencyCode currency,

        @NotNull
        LocalDate dueDate
) {
}

```

### `application/dto/CreateServiceCustomerRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateServiceCustomerRequest(
        @NotNull
        UUID providerId,

        @NotBlank
        @Size(max = 100)
        String serviceCustomerCode,

        @NotBlank
        @Size(max = 150)
        String customerName
) {
}

```

### `application/dto/CreateServiceProviderRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateServiceProviderRequest(
        @NotBlank
        @Size(max = 80)
        String code,

        @NotBlank
        @Size(max = 150)
        String name,

        @NotNull
        ServiceProviderCategory category,

        @Size(max = 100)
        String serviceCustomerCodeLabel
) {
}

```

### `application/dto/PlatformServiceBillFilter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;

import java.time.LocalDate;
import java.util.UUID;

public record PlatformServiceBillFilter(
        UUID providerId,
        String serviceCustomerCode,
        ServiceBillStatus status,
        String billingPeriod,
        LocalDate dueDateFrom,
        LocalDate dueDateTo,
        String paidByTenantSlug,
        String search
) {
}

```

### `application/dto/PlatformServiceBillPaymentFilter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import java.time.Instant;
import java.util.UUID;

public record PlatformServiceBillPaymentFilter(
        UUID providerId,
        String tenantSlug,
        UUID userId,
        String accountNumber,
        UUID billId,
        String receiptNumber,
        Instant paidAtFrom,
        Instant paidAtTo
) {
}

```

### `application/dto/PlatformServiceCustomerFilter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;

import java.util.UUID;

public record PlatformServiceCustomerFilter(
        UUID providerId,
        ServiceCustomerStatus status,
        String serviceCustomerCode,
        String search
) {
}

```

### `application/dto/PlatformServiceProviderFilter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;

public record PlatformServiceProviderFilter(
        ServiceProviderCategory category,
        ServiceProviderStatus status,
        String search
) {
}

```

### `application/dto/ServiceBillPaymentResponse.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServiceBillPaymentResponse(
        UUID id,
        UUID billId,
        ServiceProviderSummaryResponse provider,
        String serviceCustomerCode,
        String serviceCustomerName,
        String billingPeriod,
        UUID paidByTenantId,
        String paidByTenantSlug,
        UUID paidByUserId,
        UUID paidByAccountId,
        String paidByAccountNumber,
        UUID paidTransactionId,
        BigDecimal amount,
        String currency,
        String receiptNumber,
        String idempotencyKey,
        ServiceBillPaymentStatus status,
        Instant paidAt,
        Instant createdAt
) {
}

```

### `application/dto/ServiceBillResponse.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ServiceBillResponse(
        UUID id,
        ServiceProviderSummaryResponse provider,
        UUID serviceCustomerId,
        String serviceCustomerCode,
        String customerName,
        String billingPeriod,
        BigDecimal amount,
        String currency,
        LocalDate dueDate,
        ServiceBillStatus status,
        UUID paidByTenantId,
        String paidByTenantSlug,
        UUID paidByUserId,
        UUID paidByAccountId,
        String paidByAccountNumber,
        UUID paidTransactionId,
        Instant paidAt,
        UUID createdBySuperadminId,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `application/dto/ServiceCustomerResponse.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record ServiceCustomerResponse(
        UUID id,
        ServiceProviderSummaryResponse provider,
        String serviceCustomerCode,
        String customerName,
        ServiceCustomerStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `application/dto/ServiceProviderResponse.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;

import java.time.Instant;
import java.util.UUID;

public record ServiceProviderResponse(
        UUID id,
        String code,
        String name,
        ServiceProviderCategory category,
        String serviceCustomerCodeLabel,
        ServiceProviderStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `application/dto/ServiceProviderSummaryResponse.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;

import java.util.UUID;

public record ServiceProviderSummaryResponse(
        UUID id,
        String code,
        String name,
        ServiceProviderCategory category
) {
}

```

### `application/dto/UpdateServiceCustomerRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import jakarta.validation.constraints.Size;

public record UpdateServiceCustomerRequest(
        @Size(max = 150)
        String customerName,

        ServiceCustomerStatus status
) {
}

```

### `application/dto/UpdateServiceProviderRequest.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.dto;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import jakarta.validation.constraints.Size;

public record UpdateServiceProviderRequest(
        @Size(max = 150)
        String name,

        ServiceProviderCategory category,

        @Size(max = 100)
        String serviceCustomerCodeLabel
) {
}

```

### `application/mapper/ServicePaymentsMapper.java`

```java
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

```

### `application/usecase/CancelServiceBillUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.CancelServiceBillRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class CancelServiceBillUseCase {

    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;

    public CancelServiceBillUseCase(
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository
    ) {
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
    }

    @Transactional
    public ServiceBillResponse execute(UUID id, CancelServiceBillRequest request) {
        ServiceBill current = serviceBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service bill not found"));

        if (current.status() != ServiceBillStatus.PENDING) {
            throw new BusinessException("Service bill is not pending");
        }

        ServiceBill updated = serviceBillRepository.save(new ServiceBill(
                current.id(),
                current.providerId(),
                current.serviceCustomerId(),
                current.serviceCustomerCode(),
                current.customerName(),
                current.billingPeriod(),
                current.amount(),
                current.currency(),
                current.dueDate(),
                ServiceBillStatus.CANCELLED,
                current.paidByTenantId(),
                current.paidByTenantSlug(),
                current.paidByUserId(),
                current.paidByAccountId(),
                current.paidByAccountNumber(),
                current.paidTransactionId(),
                current.paidAt(),
                current.createdBySuperadminId(),
                current.createdAt(),
                current.updatedAt()
        ));

        var provider = serviceProviderRepository.findById(current.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_BILL_CANCELLED,
                "SERVICE_BILL",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "reason", StringUtils.hasText(request.reason()) ? request.reason().trim() : null,
                        "cancelledBy", currentSuperadminId()
                ),
                current,
                updated
        );

        return servicePaymentsMapper.toResponse(updated, provider);
    }

    private UUID currentSuperadminId() {
        String email = securityContextFacade.getCurrentEmail();
        if (!StringUtils.hasText(email)) {
            return null;
        }

        return platformSuperadminRepository.findByEmail(email.trim().toLowerCase())
                .map(com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin::id)
                .orElse(null);
    }
}

```

### `application/usecase/ChangeServiceProviderStatusUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ChangeServiceProviderStatusRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChangeServiceProviderStatusUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;

    public ChangeServiceProviderStatusUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public ServiceProviderResponse execute(UUID id, ChangeServiceProviderStatusRequest request) {
        ServiceProvider current = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Service provider not found"));

        if (current.status() == request.status()) {
            return servicePaymentsMapper.toResponse(current);
        }

        ServiceProvider updated = serviceProviderRepository.save(new ServiceProvider(
                current.id(),
                current.code(),
                current.name(),
                current.category(),
                current.serviceCustomerCodeLabel(),
                request.status(),
                current.createdAt(),
                current.updatedAt()
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_PROVIDER_STATUS_CHANGED,
                "SERVICE_PROVIDER",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "status", updated.status()
                ),
                current,
                updated
        );

        return servicePaymentsMapper.toResponse(updated);
    }
}

```

### `application/usecase/CreateServiceBillUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.CreateServiceBillRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;

@Service
public class CreateServiceBillUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;

    public CreateServiceBillUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceBillRepository serviceBillRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceBillRepository = serviceBillRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
    }

    @Transactional
    public ServiceBillResponse execute(CreateServiceBillRequest request) {
        var provider = serviceProviderRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        if (provider.status() != ServiceProviderStatus.ACTIVE) {
            throw new BusinessException("Service provider is inactive");
        }

        String code = normalizeCode(request.serviceCustomerCode());
        var serviceCustomer = serviceCustomerRepository.findByProviderAndCode(provider.id(), code)
                .orElseThrow(() -> new ResourceNotFoundException("Service customer not found"));

        if (serviceCustomer.status() != ServiceCustomerStatus.ACTIVE) {
            throw new BusinessException("Service customer is inactive");
        }

        String billingPeriod = request.billingPeriod().trim();
        if (serviceBillRepository.existsByProviderCodeAndBillingPeriod(provider.id(), code, billingPeriod)) {
            throw new BusinessException("Service bill already exists");
        }

        ServiceBill created = serviceBillRepository.save(new ServiceBill(
                null,
                provider.id(),
                serviceCustomer.id(),
                code,
                serviceCustomer.customerName(),
                billingPeriod,
                request.amount(),
                request.currency().name(),
                request.dueDate(),
                ServiceBillStatus.PENDING,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                currentSuperadminId(),
                null,
                null
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_BILL_CREATED,
                "SERVICE_BILL",
                created.id().toString(),
                PlatformAuditPayloads.details(
                        "providerId", provider.id(),
                        "providerCode", provider.code(),
                        "serviceCustomerCode", created.serviceCustomerCode(),
                        "billingPeriod", created.billingPeriod(),
                        "amount", created.amount(),
                        "currency", created.currency(),
                        "dueDate", created.dueDate(),
                        "status", created.status(),
                        "createdBy", currentSuperadminId()
                ),
                null,
                created
        );

        return servicePaymentsMapper.toResponse(created, provider);
    }

    private String normalizeCode(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("Service customer code must not be blank");
        }

        return value.trim();
    }

    private java.util.UUID currentSuperadminId() {
        String email = securityContextFacade.getCurrentEmail();
        if (!StringUtils.hasText(email)) {
            return null;
        }

        return platformSuperadminRepository.findByEmail(email.trim().toLowerCase())
                .map(com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin::id)
                .orElse(null);
    }
}

```

### `application/usecase/CreateServiceCustomerUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.CreateServiceCustomerRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceCustomerResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.modules.platform.superadmin.domain.repository.PlatformSuperadminRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CreateServiceCustomerUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;
    private final PlatformSuperadminRepository platformSuperadminRepository;

    public CreateServiceCustomerUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServiceCustomerRepository serviceCustomerRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade,
            PlatformSuperadminRepository platformSuperadminRepository
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
        this.platformSuperadminRepository = platformSuperadminRepository;
    }

    @Transactional
    public ServiceCustomerResponse execute(CreateServiceCustomerRequest request) {
        var provider = serviceProviderRepository.findById(request.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        if (provider.status() != com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus.ACTIVE) {
            throw new BusinessException("Service provider is inactive");
        }

        String code = normalizeCode(request.serviceCustomerCode());
        if (serviceCustomerRepository.existsByProviderAndCode(provider.id(), code)) {
            throw new BusinessException("Service customer already exists");
        }

        ServiceCustomer created = serviceCustomerRepository.save(new ServiceCustomer(
                null,
                provider.id(),
                code,
                request.customerName().trim(),
                ServiceCustomerStatus.ACTIVE,
                null,
                null
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_CUSTOMER_CREATED,
                "SERVICE_CUSTOMER",
                created.id().toString(),
                PlatformAuditPayloads.details(
                        "providerId", provider.id(),
                        "providerCode", provider.code(),
                        "serviceCustomerCode", created.serviceCustomerCode(),
                        "customerName", created.customerName(),
                        "status", created.status(),
                        "createdBy", currentSuperadminId()
                ),
                null,
                created
        );

        return servicePaymentsMapper.toResponse(created, provider);
    }

    private String normalizeCode(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("Service customer code must not be blank");
        }

        return value.trim();
    }

    private java.util.UUID currentSuperadminId() {
        String email = securityContextFacade.getCurrentEmail();
        if (!StringUtils.hasText(email)) {
            return null;
        }

        return platformSuperadminRepository.findByEmail(email.trim().toLowerCase())
                .map(com.financesystem.finance_api.modules.platform.superadmin.domain.model.PlatformSuperadmin::id)
                .orElse(null);
    }
}

```

### `application/usecase/CreateServiceProviderUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.CreateServiceProviderRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class CreateServiceProviderUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;
    private final SecurityContextFacade securityContextFacade;

    public CreateServiceProviderUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService,
            SecurityContextFacade securityContextFacade
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
        this.securityContextFacade = securityContextFacade;
    }

    @Transactional
    public ServiceProviderResponse execute(CreateServiceProviderRequest request) {
        String code = normalizeCode(request.code());
        if (serviceProviderRepository.existsByCode(code)) {
            throw new BusinessException("Service provider already exists");
        }

        String customerCodeLabel = StringUtils.hasText(request.serviceCustomerCodeLabel())
                ? request.serviceCustomerCodeLabel().trim()
                : "Código de cliente";

        ServiceProvider created = serviceProviderRepository.save(new ServiceProvider(
                null,
                code,
                request.name().trim(),
                request.category(),
                customerCodeLabel,
                ServiceProviderStatus.ACTIVE,
                null,
                null
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_PROVIDER_CREATED,
                "SERVICE_PROVIDER",
                created.id().toString(),
                PlatformAuditPayloads.details(
                        "code", created.code(),
                        "name", created.name(),
                        "category", created.category(),
                        "serviceCustomerCodeLabel", created.serviceCustomerCodeLabel(),
                        "status", created.status(),
                        "createdBy", securityContextFacade.getCurrentEmail()
                ),
                null,
                created
        );

        return servicePaymentsMapper.toResponse(created);
    }

    private String normalizeCode(String value) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException("Service provider code must not be blank");
        }

        return value.trim().toUpperCase();
    }
}

```

### `application/usecase/GetGlobalServiceBillPaymentUseCase.java`

```java
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

```

### `application/usecase/GetServiceBillUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GetServiceBillUseCase {

    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public GetServiceBillUseCase(
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public ServiceBillResponse execute(UUID id) {
        var serviceBill = serviceBillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service bill not found"));

        var provider = serviceProviderRepository.findById(serviceBill.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        return servicePaymentsMapper.toResponse(serviceBill, provider);
    }
}

```

### `application/usecase/GetServiceCustomerUseCase.java`

```java
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

```

### `application/usecase/GetServiceProviderUseCase.java`

```java
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

```

### `application/usecase/ListGlobalServiceBillPaymentsUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillPaymentFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillPaymentResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListGlobalServiceBillPaymentsUseCase {

    private final ServiceBillPaymentRepository serviceBillPaymentRepository;
    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public ListGlobalServiceBillPaymentsUseCase(
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

    public Page<ServiceBillPaymentResponse> execute(PlatformServiceBillPaymentFilter filter, Pageable pageable) {
        return serviceBillPaymentRepository.findAll(filter, pageable)
                .map(payment -> serviceBillRepository.findById(payment.billId())
                        .flatMap(bill -> serviceProviderRepository.findById(payment.providerId())
                                .map(provider -> servicePaymentsMapper.toResponse(payment, bill, provider)))
                        .orElseThrow(() -> new ResourceNotFoundException("Service provider not found")));
    }
}

```

### `application/usecase/ListServiceBillsUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceBillResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListServiceBillsUseCase {

    private final ServiceBillRepository serviceBillRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;

    public ListServiceBillsUseCase(
            ServiceBillRepository serviceBillRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper
    ) {
        this.serviceBillRepository = serviceBillRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
    }

    public Page<ServiceBillResponse> execute(PlatformServiceBillFilter filter, Pageable pageable) {
        return serviceBillRepository.findAll(filter, pageable)
                .map(serviceBill -> serviceProviderRepository.findById(serviceBill.providerId())
                        .map(provider -> servicePaymentsMapper.toResponse(serviceBill, provider))
                        .orElseThrow(() -> new ResourceNotFoundException("Service provider not found")));
    }
}

```

### `application/usecase/ListServiceCustomersUseCase.java`

```java
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

```

### `application/usecase/ListServiceProvidersUseCase.java`

```java
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

```

### `application/usecase/UpdateServiceCustomerUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.exception.ResourceNotFoundException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceCustomerResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.UpdateServiceCustomerRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UpdateServiceCustomerUseCase {

    private final ServiceCustomerRepository serviceCustomerRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;

    public UpdateServiceCustomerUseCase(
            ServiceCustomerRepository serviceCustomerRepository,
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService
    ) {
        this.serviceCustomerRepository = serviceCustomerRepository;
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public ServiceCustomerResponse execute(UUID id, UpdateServiceCustomerRequest request) {
        ServiceCustomer current = serviceCustomerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service customer not found"));

        if (!StringUtils.hasText(request.customerName()) && request.status() == null) {
            throw new BusinessException("At least one field must be provided");
        }

        String customerName = StringUtils.hasText(request.customerName()) ? request.customerName().trim() : current.customerName();
        var status = request.status() != null ? request.status() : current.status();

        ServiceCustomer updated = serviceCustomerRepository.save(new ServiceCustomer(
                current.id(),
                current.providerId(),
                current.serviceCustomerCode(),
                customerName,
                status,
                current.createdAt(),
                current.updatedAt()
        ));

        var provider = serviceProviderRepository.findById(current.providerId())
                .orElseThrow(() -> new ResourceNotFoundException("Service provider not found"));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_CUSTOMER_UPDATED,
                "SERVICE_CUSTOMER",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "customerName", updated.customerName(),
                        "status", updated.status()
                ),
                current,
                updated
        );

        return servicePaymentsMapper.toResponse(updated, provider);
    }
}

```

### `application/usecase/UpdateServiceProviderUseCase.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.modules.governance.audit.application.service.AuditTrailService;
import com.financesystem.finance_api.modules.governance.audit.domain.model.AuditEventTypes;
import com.financesystem.finance_api.modules.platform.audit.PlatformAuditPayloads;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.ServiceProviderResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.UpdateServiceProviderRequest;
import com.financesystem.finance_api.modules.platform.servicepayments.application.mapper.ServicePaymentsMapper;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
public class UpdateServiceProviderUseCase {

    private final ServiceProviderRepository serviceProviderRepository;
    private final ServicePaymentsMapper servicePaymentsMapper;
    private final AuditTrailService auditTrailService;

    public UpdateServiceProviderUseCase(
            ServiceProviderRepository serviceProviderRepository,
            ServicePaymentsMapper servicePaymentsMapper,
            AuditTrailService auditTrailService
    ) {
        this.serviceProviderRepository = serviceProviderRepository;
        this.servicePaymentsMapper = servicePaymentsMapper;
        this.auditTrailService = auditTrailService;
    }

    @Transactional
    public ServiceProviderResponse execute(UUID id, UpdateServiceProviderRequest request) {
        ServiceProvider current = serviceProviderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Service provider not found"));

        String name = StringUtils.hasText(request.name()) ? request.name().trim() : current.name();
        String label = StringUtils.hasText(request.serviceCustomerCodeLabel())
                ? request.serviceCustomerCodeLabel().trim()
                : current.serviceCustomerCodeLabel();
        var category = request.category() != null ? request.category() : current.category();

        ServiceProvider updated = serviceProviderRepository.save(new ServiceProvider(
                current.id(),
                current.code(),
                name,
                category,
                label,
                current.status(),
                current.createdAt(),
                current.updatedAt()
        ));

        auditTrailService.recordPlatformEvent(
                AuditEventTypes.SERVICE_PROVIDER_UPDATED,
                "SERVICE_PROVIDER",
                updated.id().toString(),
                PlatformAuditPayloads.details(
                        "id", updated.id(),
                        "code", updated.code(),
                        "name", updated.name(),
                        "category", updated.category(),
                        "serviceCustomerCodeLabel", updated.serviceCustomerCodeLabel(),
                        "status", updated.status()
                ),
                current,
                updated
        );

        return servicePaymentsMapper.toResponse(updated);
    }
}

```

### `domain/model/ServiceBill.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ServiceBill(
        UUID id,
        UUID providerId,
        UUID serviceCustomerId,
        String serviceCustomerCode,
        String customerName,
        String billingPeriod,
        BigDecimal amount,
        String currency,
        LocalDate dueDate,
        ServiceBillStatus status,
        UUID paidByTenantId,
        String paidByTenantSlug,
        UUID paidByUserId,
        UUID paidByAccountId,
        String paidByAccountNumber,
        UUID paidTransactionId,
        Instant paidAt,
        UUID createdBySuperadminId,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `domain/model/ServiceBillPayment.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ServiceBillPayment(
        UUID id,
        UUID billId,
        UUID providerId,
        UUID paidByTenantId,
        String paidByTenantSlug,
        UUID paidByUserId,
        UUID paidByAccountId,
        String paidByAccountNumber,
        UUID paidTransactionId,
        BigDecimal amount,
        String currency,
        String receiptNumber,
        String idempotencyKey,
        ServiceBillPaymentStatus status,
        Instant paidAt,
        Instant createdAt
) {
}

```

### `domain/model/ServiceBillPaymentStatus.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

public enum ServiceBillPaymentStatus {
    PAID,
    REVERSED
}

```

### `domain/model/ServiceBillStatus.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

public enum ServiceBillStatus {
    PENDING,
    PAID,
    EXPIRED,
    CANCELLED,
    REVERSED
}

```

### `domain/model/ServiceCustomer.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ServiceCustomer(
        UUID id,
        UUID providerId,
        String serviceCustomerCode,
        String customerName,
        ServiceCustomerStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `domain/model/ServiceCustomerStatus.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

public enum ServiceCustomerStatus {
    ACTIVE,
    INACTIVE
}

```

### `domain/model/ServiceProviderCategory.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

public enum ServiceProviderCategory {
    ELECTRICITY,
    WATER,
    INTERNET,
    TV_CABLE
}

```

### `domain/model/ServiceProvider.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ServiceProvider(
        UUID id,
        String code,
        String name,
        ServiceProviderCategory category,
        String serviceCustomerCodeLabel,
        ServiceProviderStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}

```

### `domain/model/ServiceProviderStatus.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.model;

public enum ServiceProviderStatus {
    ACTIVE,
    INACTIVE
}

```

### `domain/repository/ServiceBillPaymentRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillPaymentFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ServiceBillPaymentRepository {

    ServiceBillPayment save(ServiceBillPayment serviceBillPayment);

    Optional<ServiceBillPayment> findById(UUID id);

    Optional<ServiceBillPayment> findByBillId(UUID billId);

    Optional<ServiceBillPayment> findByIdempotencyKey(UUID paidByTenantId, UUID paidByUserId, String idempotencyKey);

    boolean existsByBillId(UUID billId);

    Page<ServiceBillPayment> findAll(PlatformServiceBillPaymentFilter filter, Pageable pageable);
}

```

### `domain/repository/ServiceBillRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceBillRepository {

    ServiceBill save(ServiceBill serviceBill);

    Optional<ServiceBill> findById(UUID id);

    Optional<ServiceBill> findByIdForUpdate(UUID id);

    Optional<ServiceBill> findByProviderCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod);

    boolean existsByProviderCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod);

    List<ServiceBill> findAllByProviderIdAndServiceCustomerCodeAndStatus(
            UUID providerId,
            String serviceCustomerCode,
            ServiceBillStatus status
    );

    Page<ServiceBill> findAll(PlatformServiceBillFilter filter, Pageable pageable);
}

```

### `domain/repository/ServiceCustomerRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceCustomerFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ServiceCustomerRepository {

    ServiceCustomer save(ServiceCustomer serviceCustomer);

    Optional<ServiceCustomer> findById(UUID id);

    Optional<ServiceCustomer> findByProviderAndCode(UUID providerId, String serviceCustomerCode);

    boolean existsByProviderAndCode(UUID providerId, String serviceCustomerCode);

    Page<ServiceCustomer> findAll(PlatformServiceCustomerFilter filter, Pageable pageable);
}

```

### `domain/repository/ServiceProviderRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.domain.repository;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceProviderFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ServiceProviderRepository {

    ServiceProvider save(ServiceProvider serviceProvider);

    Optional<ServiceProvider> findById(UUID id);

    Optional<ServiceProvider> findByCode(String code);

    boolean existsByCode(String code);

    Page<ServiceProvider> findAll(PlatformServiceProviderFilter filter, Pageable pageable);
}

```

### `infrastructure/api/PlatformServiceBillController.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/service-bills")
@SecurityRequirement(name = "bearerAuth")
public class PlatformServiceBillController {

    private final CreateServiceBillUseCase createServiceBillUseCase;
    private final ListServiceBillsUseCase listServiceBillsUseCase;
    private final GetServiceBillUseCase getServiceBillUseCase;
    private final CancelServiceBillUseCase cancelServiceBillUseCase;

    public PlatformServiceBillController(
            CreateServiceBillUseCase createServiceBillUseCase,
            ListServiceBillsUseCase listServiceBillsUseCase,
            GetServiceBillUseCase getServiceBillUseCase,
            CancelServiceBillUseCase cancelServiceBillUseCase
    ) {
        this.createServiceBillUseCase = createServiceBillUseCase;
        this.listServiceBillsUseCase = listServiceBillsUseCase;
        this.getServiceBillUseCase = getServiceBillUseCase;
        this.cancelServiceBillUseCase = cancelServiceBillUseCase;
    }

    @PostMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceBillResponse> create(@Valid @RequestBody CreateServiceBillRequest request) {
        return ApiResponse.success("Service bill created successfully", createServiceBillUseCase.execute(request));
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<ServiceBillResponse>> list(
            @ParameterObject PlatformServiceBillFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success("Service bills retrieved successfully", listServiceBillsUseCase.execute(filter, pageable));
    }

    @GetMapping("/{billId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceBillResponse> detail(@PathVariable UUID billId) {
        return ApiResponse.success("Service bill retrieved successfully", getServiceBillUseCase.execute(billId));
    }

    @PatchMapping("/{billId}/cancel")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceBillResponse> cancel(
            @PathVariable UUID billId,
            @Valid @RequestBody CancelServiceBillRequest request
    ) {
        return ApiResponse.success("Service bill cancelled successfully", cancelServiceBillUseCase.execute(billId, request));
    }
}

```

### `infrastructure/api/PlatformServiceBillPaymentController.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.GetGlobalServiceBillPaymentUseCase;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.ListGlobalServiceBillPaymentsUseCase;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/service-bill-payments")
@SecurityRequirement(name = "bearerAuth")
public class PlatformServiceBillPaymentController {

    private final ListGlobalServiceBillPaymentsUseCase listGlobalServiceBillPaymentsUseCase;
    private final GetGlobalServiceBillPaymentUseCase getGlobalServiceBillPaymentUseCase;

    public PlatformServiceBillPaymentController(
            ListGlobalServiceBillPaymentsUseCase listGlobalServiceBillPaymentsUseCase,
            GetGlobalServiceBillPaymentUseCase getGlobalServiceBillPaymentUseCase
    ) {
        this.listGlobalServiceBillPaymentsUseCase = listGlobalServiceBillPaymentsUseCase;
        this.getGlobalServiceBillPaymentUseCase = getGlobalServiceBillPaymentUseCase;
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<ServiceBillPaymentResponse>> list(
            @ParameterObject PlatformServiceBillPaymentFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success(
                "Service bill payments retrieved successfully",
                listGlobalServiceBillPaymentsUseCase.execute(filter, pageable)
        );
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceBillPaymentResponse> detail(@PathVariable UUID paymentId) {
        return ApiResponse.success(
                "Service bill payment retrieved successfully",
                getGlobalServiceBillPaymentUseCase.execute(paymentId)
        );
    }
}

```

### `infrastructure/api/PlatformServiceCustomerController.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/service-customers")
@SecurityRequirement(name = "bearerAuth")
public class PlatformServiceCustomerController {

    private final CreateServiceCustomerUseCase createServiceCustomerUseCase;
    private final ListServiceCustomersUseCase listServiceCustomersUseCase;
    private final GetServiceCustomerUseCase getServiceCustomerUseCase;
    private final UpdateServiceCustomerUseCase updateServiceCustomerUseCase;

    public PlatformServiceCustomerController(
            CreateServiceCustomerUseCase createServiceCustomerUseCase,
            ListServiceCustomersUseCase listServiceCustomersUseCase,
            GetServiceCustomerUseCase getServiceCustomerUseCase,
            UpdateServiceCustomerUseCase updateServiceCustomerUseCase
    ) {
        this.createServiceCustomerUseCase = createServiceCustomerUseCase;
        this.listServiceCustomersUseCase = listServiceCustomersUseCase;
        this.getServiceCustomerUseCase = getServiceCustomerUseCase;
        this.updateServiceCustomerUseCase = updateServiceCustomerUseCase;
    }

    @PostMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceCustomerResponse> create(@Valid @RequestBody CreateServiceCustomerRequest request) {
        return ApiResponse.success("Service customer created successfully", createServiceCustomerUseCase.execute(request));
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<ServiceCustomerResponse>> list(
            @ParameterObject PlatformServiceCustomerFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success("Service customers retrieved successfully", listServiceCustomersUseCase.execute(filter, pageable));
    }

    @GetMapping("/{serviceCustomerId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceCustomerResponse> detail(@PathVariable UUID serviceCustomerId) {
        return ApiResponse.success("Service customer retrieved successfully", getServiceCustomerUseCase.execute(serviceCustomerId));
    }

    @PatchMapping("/{serviceCustomerId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceCustomerResponse> update(
            @PathVariable UUID serviceCustomerId,
            @Valid @RequestBody UpdateServiceCustomerRequest request
    ) {
        return ApiResponse.success("Service customer updated successfully", updateServiceCustomerUseCase.execute(serviceCustomerId, request));
    }
}

```

### `infrastructure/api/PlatformServiceProviderController.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiResponse;
import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.*;
import com.financesystem.finance_api.modules.platform.servicepayments.application.usecase.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/platform/service-providers")
@SecurityRequirement(name = "bearerAuth")
public class PlatformServiceProviderController {

    private final CreateServiceProviderUseCase createServiceProviderUseCase;
    private final ListServiceProvidersUseCase listServiceProvidersUseCase;
    private final GetServiceProviderUseCase getServiceProviderUseCase;
    private final UpdateServiceProviderUseCase updateServiceProviderUseCase;
    private final ChangeServiceProviderStatusUseCase changeServiceProviderStatusUseCase;

    public PlatformServiceProviderController(
            CreateServiceProviderUseCase createServiceProviderUseCase,
            ListServiceProvidersUseCase listServiceProvidersUseCase,
            GetServiceProviderUseCase getServiceProviderUseCase,
            UpdateServiceProviderUseCase updateServiceProviderUseCase,
            ChangeServiceProviderStatusUseCase changeServiceProviderStatusUseCase
    ) {
        this.createServiceProviderUseCase = createServiceProviderUseCase;
        this.listServiceProvidersUseCase = listServiceProvidersUseCase;
        this.getServiceProviderUseCase = getServiceProviderUseCase;
        this.updateServiceProviderUseCase = updateServiceProviderUseCase;
        this.changeServiceProviderStatusUseCase = changeServiceProviderStatusUseCase;
    }

    @PostMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceProviderResponse> create(@Valid @RequestBody CreateServiceProviderRequest request) {
        return ApiResponse.success("Service provider created successfully", createServiceProviderUseCase.execute(request));
    }

    @GetMapping
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<Page<ServiceProviderResponse>> list(
            @ParameterObject PlatformServiceProviderFilter filter,
            @ParameterObject @PageableDefault(size = 50) Pageable pageable
    ) {
        return ApiResponse.success("Service providers retrieved successfully", listServiceProvidersUseCase.execute(filter, pageable));
    }

    @GetMapping("/{providerId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceProviderResponse> detail(@PathVariable UUID providerId) {
        return ApiResponse.success("Service provider retrieved successfully", getServiceProviderUseCase.execute(providerId));
    }

    @PatchMapping("/{providerId}")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceProviderResponse> update(
            @PathVariable UUID providerId,
            @Valid @RequestBody UpdateServiceProviderRequest request
    ) {
        return ApiResponse.success("Service provider updated successfully", updateServiceProviderUseCase.execute(providerId, request));
    }

    @PatchMapping("/{providerId}/status")
    @PreAuthorize("@authorizationGuards.isPlatformAdmin()")
    public ApiResponse<ServiceProviderResponse> changeStatus(
            @PathVariable UUID providerId,
            @Valid @RequestBody ChangeServiceProviderStatusRequest request
    ) {
        return ApiResponse.success(
                "Service provider status changed successfully",
                changeServiceProviderStatusUseCase.execute(providerId, request)
        );
    }
}

```

### `infrastructure/persistence/ServiceBillEntity.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "service_bills")
public class ServiceBillEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "service_customer_id", nullable = false)
    private UUID serviceCustomerId;

    @Column(name = "service_customer_code", nullable = false, length = 100)
    private String serviceCustomerCode;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(name = "billing_period", nullable = false, length = 20)
    private String billingPeriod;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceBillStatus status;

    @Column(name = "paid_by_tenant_id")
    private UUID paidByTenantId;

    @Column(name = "paid_by_tenant_slug", length = 100)
    private String paidByTenantSlug;

    @Column(name = "paid_by_user_id")
    private UUID paidByUserId;

    @Column(name = "paid_by_account_id")
    private UUID paidByAccountId;

    @Column(name = "paid_by_account_number", length = 50)
    private String paidByAccountNumber;

    @Column(name = "paid_transaction_id")
    private UUID paidTransactionId;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "created_by_superadmin_id")
    private UUID createdBySuperadminId;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = ServiceBillStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public UUID getServiceCustomerId() {
        return serviceCustomerId;
    }

    public void setServiceCustomerId(UUID serviceCustomerId) {
        this.serviceCustomerId = serviceCustomerId;
    }

    public String getServiceCustomerCode() {
        return serviceCustomerCode;
    }

    public void setServiceCustomerCode(String serviceCustomerCode) {
        this.serviceCustomerCode = serviceCustomerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBillingPeriod() {
        return billingPeriod;
    }

    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public ServiceBillStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceBillStatus status) {
        this.status = status;
    }

    public UUID getPaidByTenantId() {
        return paidByTenantId;
    }

    public void setPaidByTenantId(UUID paidByTenantId) {
        this.paidByTenantId = paidByTenantId;
    }

    public String getPaidByTenantSlug() {
        return paidByTenantSlug;
    }

    public void setPaidByTenantSlug(String paidByTenantSlug) {
        this.paidByTenantSlug = paidByTenantSlug;
    }

    public UUID getPaidByUserId() {
        return paidByUserId;
    }

    public void setPaidByUserId(UUID paidByUserId) {
        this.paidByUserId = paidByUserId;
    }

    public UUID getPaidByAccountId() {
        return paidByAccountId;
    }

    public void setPaidByAccountId(UUID paidByAccountId) {
        this.paidByAccountId = paidByAccountId;
    }

    public String getPaidByAccountNumber() {
        return paidByAccountNumber;
    }

    public void setPaidByAccountNumber(String paidByAccountNumber) {
        this.paidByAccountNumber = paidByAccountNumber;
    }

    public UUID getPaidTransactionId() {
        return paidTransactionId;
    }

    public void setPaidTransactionId(UUID paidTransactionId) {
        this.paidTransactionId = paidTransactionId;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public UUID getCreatedBySuperadminId() {
        return createdBySuperadminId;
    }

    public void setCreatedBySuperadminId(UUID createdBySuperadminId) {
        this.createdBySuperadminId = createdBySuperadminId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

```

### `infrastructure/persistence/ServiceBillJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface ServiceBillJpaRepository extends JpaRepository<ServiceBillEntity, UUID>, JpaSpecificationExecutor<ServiceBillEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ServiceBillEntity> findLockedById(UUID id);

    Optional<ServiceBillEntity> findByProviderIdAndServiceCustomerCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod);

    boolean existsByProviderIdAndServiceCustomerCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod);
}

```

### `infrastructure/persistence/ServiceBillPaymentEntity.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPaymentStatus;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_bill_payments")
public class ServiceBillPaymentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "bill_id", nullable = false)
    private UUID billId;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "paid_by_tenant_id", nullable = false)
    private UUID paidByTenantId;

    @Column(name = "paid_by_tenant_slug", nullable = false, length = 100)
    private String paidByTenantSlug;

    @Column(name = "paid_by_user_id", nullable = false)
    private UUID paidByUserId;

    @Column(name = "paid_by_account_id", nullable = false)
    private UUID paidByAccountId;

    @Column(name = "paid_by_account_number", nullable = false, length = 50)
    private String paidByAccountNumber;

    @Column(name = "paid_transaction_id", nullable = false)
    private UUID paidTransactionId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(name = "receipt_number", nullable = false, unique = true, length = 100)
    private String receiptNumber;

    @Column(name = "idempotency_key", nullable = false, length = 150)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceBillPaymentStatus status;

    @Column(name = "paid_at", nullable = false)
    private Instant paidAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (paidAt == null) {
            paidAt = now;
        }
        if (status == null) {
            status = ServiceBillPaymentStatus.PAID;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBillId() {
        return billId;
    }

    public void setBillId(UUID billId) {
        this.billId = billId;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public UUID getPaidByTenantId() {
        return paidByTenantId;
    }

    public void setPaidByTenantId(UUID paidByTenantId) {
        this.paidByTenantId = paidByTenantId;
    }

    public String getPaidByTenantSlug() {
        return paidByTenantSlug;
    }

    public void setPaidByTenantSlug(String paidByTenantSlug) {
        this.paidByTenantSlug = paidByTenantSlug;
    }

    public UUID getPaidByUserId() {
        return paidByUserId;
    }

    public void setPaidByUserId(UUID paidByUserId) {
        this.paidByUserId = paidByUserId;
    }

    public UUID getPaidByAccountId() {
        return paidByAccountId;
    }

    public void setPaidByAccountId(UUID paidByAccountId) {
        this.paidByAccountId = paidByAccountId;
    }

    public String getPaidByAccountNumber() {
        return paidByAccountNumber;
    }

    public void setPaidByAccountNumber(String paidByAccountNumber) {
        this.paidByAccountNumber = paidByAccountNumber;
    }

    public UUID getPaidTransactionId() {
        return paidTransactionId;
    }

    public void setPaidTransactionId(UUID paidTransactionId) {
        this.paidTransactionId = paidTransactionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public ServiceBillPaymentStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceBillPaymentStatus status) {
        this.status = status;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public void setPaidAt(Instant paidAt) {
        this.paidAt = paidAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}

```

### `infrastructure/persistence/ServiceBillPaymentJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceBillPaymentJpaRepository extends JpaRepository<ServiceBillPaymentEntity, UUID>, JpaSpecificationExecutor<ServiceBillPaymentEntity> {

    Optional<ServiceBillPaymentEntity> findByBillId(UUID billId);

    Optional<ServiceBillPaymentEntity> findByPaidByTenantIdAndPaidByUserIdAndIdempotencyKey(UUID paidByTenantId, UUID paidByUserId, String idempotencyKey);

    boolean existsByBillId(UUID billId);
}

```

### `infrastructure/persistence/ServiceBillPaymentRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillPaymentFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillPayment;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillPaymentRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ServiceBillPaymentRepositoryAdapter implements ServiceBillPaymentRepository {

    private final ServiceBillPaymentJpaRepository jpaRepository;

    public ServiceBillPaymentRepositoryAdapter(ServiceBillPaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceBillPayment save(ServiceBillPayment serviceBillPayment) {
        return toDomain(jpaRepository.save(toEntity(serviceBillPayment)));
    }

    @Override
    public Optional<ServiceBillPayment> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceBillPayment> findByBillId(UUID billId) {
        return jpaRepository.findByBillId(billId).map(this::toDomain);
    }

    @Override
    public Optional<ServiceBillPayment> findByIdempotencyKey(UUID paidByTenantId, UUID paidByUserId, String idempotencyKey) {
        return jpaRepository.findByPaidByTenantIdAndPaidByUserIdAndIdempotencyKey(paidByTenantId, paidByUserId, idempotencyKey).map(this::toDomain);
    }

    @Override
    public boolean existsByBillId(UUID billId) {
        return jpaRepository.existsByBillId(billId);
    }

    @Override
    public Page<ServiceBillPayment> findAll(PlatformServiceBillPaymentFilter filter, Pageable pageable) {
        Specification<ServiceBillPaymentEntity> specification = alwaysTrue();

        if (filter != null) {
            if (filter.providerId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("providerId"), filter.providerId()));
            }
            if (StringUtils.hasText(filter.tenantSlug())) {
                String slug = filter.tenantSlug().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("paidByTenantSlug")), "%" + slug + "%"));
            }
            if (filter.userId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("paidByUserId"), filter.userId()));
            }
            if (StringUtils.hasText(filter.accountNumber())) {
                String accountNumber = filter.accountNumber().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("paidByAccountNumber")), "%" + accountNumber + "%"));
            }
            if (filter.billId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("billId"), filter.billId()));
            }
            if (StringUtils.hasText(filter.receiptNumber())) {
                String receipt = filter.receiptNumber().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("receiptNumber")), "%" + receipt + "%"));
            }
            if (filter.paidAtFrom() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.greaterThanOrEqualTo(root.get("paidAt"), filter.paidAtFrom()));
            }
            if (filter.paidAtTo() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.lessThanOrEqualTo(root.get("paidAt"), filter.paidAtTo()));
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private Specification<ServiceBillPaymentEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private ServiceBillPaymentEntity toEntity(ServiceBillPayment payment) {
        ServiceBillPaymentEntity entity = new ServiceBillPaymentEntity();
        entity.setId(payment.id());
        entity.setBillId(payment.billId());
        entity.setProviderId(payment.providerId());
        entity.setPaidByTenantId(payment.paidByTenantId());
        entity.setPaidByTenantSlug(payment.paidByTenantSlug());
        entity.setPaidByUserId(payment.paidByUserId());
        entity.setPaidByAccountId(payment.paidByAccountId());
        entity.setPaidByAccountNumber(payment.paidByAccountNumber());
        entity.setPaidTransactionId(payment.paidTransactionId());
        entity.setAmount(payment.amount());
        entity.setCurrency(payment.currency());
        entity.setReceiptNumber(payment.receiptNumber());
        entity.setIdempotencyKey(payment.idempotencyKey());
        entity.setStatus(payment.status());
        entity.setPaidAt(payment.paidAt());
        return entity;
    }

    private ServiceBillPayment toDomain(ServiceBillPaymentEntity entity) {
        return new ServiceBillPayment(
                entity.getId(),
                entity.getBillId(),
                entity.getProviderId(),
                entity.getPaidByTenantId(),
                entity.getPaidByTenantSlug(),
                entity.getPaidByUserId(),
                entity.getPaidByAccountId(),
                entity.getPaidByAccountNumber(),
                entity.getPaidTransactionId(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getReceiptNumber(),
                entity.getIdempotencyKey(),
                entity.getStatus(),
                entity.getPaidAt(),
                entity.getCreatedAt()
        );
    }
}

```

### `infrastructure/persistence/ServiceBillRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceBillFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBill;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceBillStatus;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceBillRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ServiceBillRepositoryAdapter implements ServiceBillRepository {

    private final ServiceBillJpaRepository jpaRepository;

    public ServiceBillRepositoryAdapter(ServiceBillJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceBill save(ServiceBill serviceBill) {
        return toDomain(jpaRepository.save(toEntity(serviceBill)));
    }

    @Override
    public Optional<ServiceBill> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceBill> findByIdForUpdate(UUID id) {
        return jpaRepository.findLockedById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceBill> findByProviderCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod) {
        return jpaRepository.findByProviderIdAndServiceCustomerCodeAndBillingPeriod(providerId, serviceCustomerCode, billingPeriod).map(this::toDomain);
    }

    @Override
    public boolean existsByProviderCodeAndBillingPeriod(UUID providerId, String serviceCustomerCode, String billingPeriod) {
        return jpaRepository.existsByProviderIdAndServiceCustomerCodeAndBillingPeriod(providerId, serviceCustomerCode, billingPeriod);
    }

    @Override
    public List<ServiceBill> findAllByProviderIdAndServiceCustomerCodeAndStatus(
            UUID providerId,
            String serviceCustomerCode,
            ServiceBillStatus status
    ) {
        Specification<ServiceBillEntity> specification = alwaysTrue()
                .and((root, query, cb) -> cb.equal(root.get("providerId"), providerId))
                .and((root, query, cb) -> cb.equal(root.get("serviceCustomerCode"), serviceCustomerCode))
                .and((root, query, cb) -> cb.equal(root.get("status"), status));

        return jpaRepository.findAll(specification).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Page<ServiceBill> findAll(PlatformServiceBillFilter filter, Pageable pageable) {
        Specification<ServiceBillEntity> specification = alwaysTrue();

        if (filter != null) {
            if (filter.providerId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("providerId"), filter.providerId()));
            }
            if (StringUtils.hasText(filter.serviceCustomerCode())) {
                String code = filter.serviceCustomerCode().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("serviceCustomerCode")), "%" + code + "%"));
            }
            if (filter.status() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("status"), filter.status()));
            }
            if (StringUtils.hasText(filter.billingPeriod())) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("billingPeriod"), filter.billingPeriod().trim()));
            }
            if (filter.dueDateFrom() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.greaterThanOrEqualTo(root.get("dueDate"), filter.dueDateFrom()));
            }
            if (filter.dueDateTo() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.lessThanOrEqualTo(root.get("dueDate"), filter.dueDateTo()));
            }
            if (StringUtils.hasText(filter.paidByTenantSlug())) {
                String slug = filter.paidByTenantSlug().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("paidByTenantSlug")), "%" + slug + "%"));
            }
            if (StringUtils.hasText(filter.search())) {
                String like = "%" + filter.search().trim().toLowerCase() + "%";
                specification = specification.and((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.like(cb.lower(root.get("serviceCustomerCode")), like));
                    predicates.add(cb.like(cb.lower(root.get("customerName")), like));
                    predicates.add(cb.like(cb.lower(root.get("billingPeriod")), like));
                    return cb.or(predicates.toArray(Predicate[]::new));
                });
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private Specification<ServiceBillEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private ServiceBillEntity toEntity(ServiceBill serviceBill) {
        ServiceBillEntity entity = new ServiceBillEntity();
        entity.setId(serviceBill.id());
        entity.setProviderId(serviceBill.providerId());
        entity.setServiceCustomerId(serviceBill.serviceCustomerId());
        entity.setServiceCustomerCode(serviceBill.serviceCustomerCode());
        entity.setCustomerName(serviceBill.customerName());
        entity.setBillingPeriod(serviceBill.billingPeriod());
        entity.setAmount(serviceBill.amount());
        entity.setCurrency(serviceBill.currency());
        entity.setDueDate(serviceBill.dueDate());
        entity.setStatus(serviceBill.status());
        entity.setPaidByTenantId(serviceBill.paidByTenantId());
        entity.setPaidByTenantSlug(serviceBill.paidByTenantSlug());
        entity.setPaidByUserId(serviceBill.paidByUserId());
        entity.setPaidByAccountId(serviceBill.paidByAccountId());
        entity.setPaidByAccountNumber(serviceBill.paidByAccountNumber());
        entity.setPaidTransactionId(serviceBill.paidTransactionId());
        entity.setPaidAt(serviceBill.paidAt());
        entity.setCreatedBySuperadminId(serviceBill.createdBySuperadminId());
        return entity;
    }

    private ServiceBill toDomain(ServiceBillEntity entity) {
        return new ServiceBill(
                entity.getId(),
                entity.getProviderId(),
                entity.getServiceCustomerId(),
                entity.getServiceCustomerCode(),
                entity.getCustomerName(),
                entity.getBillingPeriod(),
                entity.getAmount(),
                entity.getCurrency(),
                entity.getDueDate(),
                entity.getStatus(),
                entity.getPaidByTenantId(),
                entity.getPaidByTenantSlug(),
                entity.getPaidByUserId(),
                entity.getPaidByAccountId(),
                entity.getPaidByAccountNumber(),
                entity.getPaidTransactionId(),
                entity.getPaidAt(),
                entity.getCreatedBySuperadminId(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

```

### `infrastructure/persistence/ServiceCustomerEntity.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomerStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_customers")
public class ServiceCustomerEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "provider_id", nullable = false)
    private UUID providerId;

    @Column(name = "service_customer_code", nullable = false, length = 100)
    private String serviceCustomerCode;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceCustomerStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = ServiceCustomerStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public String getServiceCustomerCode() {
        return serviceCustomerCode;
    }

    public void setServiceCustomerCode(String serviceCustomerCode) {
        this.serviceCustomerCode = serviceCustomerCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public ServiceCustomerStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceCustomerStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

```

### `infrastructure/persistence/ServiceCustomerJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceCustomerJpaRepository extends JpaRepository<ServiceCustomerEntity, UUID>, JpaSpecificationExecutor<ServiceCustomerEntity> {

    Optional<ServiceCustomerEntity> findByProviderIdAndServiceCustomerCode(UUID providerId, String serviceCustomerCode);

    boolean existsByProviderIdAndServiceCustomerCode(UUID providerId, String serviceCustomerCode);
}

```

### `infrastructure/persistence/ServiceCustomerRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceCustomerFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceCustomer;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceCustomerRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ServiceCustomerRepositoryAdapter implements ServiceCustomerRepository {

    private final ServiceCustomerJpaRepository jpaRepository;

    public ServiceCustomerRepositoryAdapter(ServiceCustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceCustomer save(ServiceCustomer serviceCustomer) {
        return toDomain(jpaRepository.save(toEntity(serviceCustomer)));
    }

    @Override
    public Optional<ServiceCustomer> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceCustomer> findByProviderAndCode(UUID providerId, String serviceCustomerCode) {
        return jpaRepository.findByProviderIdAndServiceCustomerCode(providerId, serviceCustomerCode).map(this::toDomain);
    }

    @Override
    public boolean existsByProviderAndCode(UUID providerId, String serviceCustomerCode) {
        return jpaRepository.existsByProviderIdAndServiceCustomerCode(providerId, serviceCustomerCode);
    }

    @Override
    public Page<ServiceCustomer> findAll(PlatformServiceCustomerFilter filter, Pageable pageable) {
        Specification<ServiceCustomerEntity> specification = alwaysTrue();

        if (filter != null) {
            if (filter.providerId() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("providerId"), filter.providerId()));
            }
            if (filter.status() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("status"), filter.status()));
            }
            if (StringUtils.hasText(filter.serviceCustomerCode())) {
                String code = filter.serviceCustomerCode().trim().toLowerCase();
                specification = specification.and((root, query, cb) ->
                        cb.like(cb.lower(root.get("serviceCustomerCode")), "%" + code + "%"));
            }
            if (StringUtils.hasText(filter.search())) {
                String like = "%" + filter.search().trim().toLowerCase() + "%";
                specification = specification.and((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.like(cb.lower(root.get("serviceCustomerCode")), like));
                    predicates.add(cb.like(cb.lower(root.get("customerName")), like));
                    return cb.or(predicates.toArray(Predicate[]::new));
                });
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private Specification<ServiceCustomerEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private ServiceCustomerEntity toEntity(ServiceCustomer serviceCustomer) {
        ServiceCustomerEntity entity = new ServiceCustomerEntity();
        entity.setId(serviceCustomer.id());
        entity.setProviderId(serviceCustomer.providerId());
        entity.setServiceCustomerCode(serviceCustomer.serviceCustomerCode());
        entity.setCustomerName(serviceCustomer.customerName());
        entity.setStatus(serviceCustomer.status());
        return entity;
    }

    private ServiceCustomer toDomain(ServiceCustomerEntity entity) {
        return new ServiceCustomer(
                entity.getId(),
                entity.getProviderId(),
                entity.getServiceCustomerCode(),
                entity.getCustomerName(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

```

### `infrastructure/persistence/ServiceProviderEntity.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderCategory;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProviderStatus;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "service_providers")
public class ServiceProviderEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private ServiceProviderCategory category;

    @Column(name = "service_customer_code_label", nullable = false, length = 100)
    private String serviceCustomerCodeLabel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ServiceProviderStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = ServiceProviderStatus.ACTIVE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ServiceProviderCategory getCategory() {
        return category;
    }

    public void setCategory(ServiceProviderCategory category) {
        this.category = category;
    }

    public String getServiceCustomerCodeLabel() {
        return serviceCustomerCodeLabel;
    }

    public void setServiceCustomerCodeLabel(String serviceCustomerCodeLabel) {
        this.serviceCustomerCodeLabel = serviceCustomerCodeLabel;
    }

    public ServiceProviderStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceProviderStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}

```

### `infrastructure/persistence/ServiceProviderJpaRepository.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface ServiceProviderJpaRepository extends JpaRepository<ServiceProviderEntity, UUID>, JpaSpecificationExecutor<ServiceProviderEntity> {

    Optional<ServiceProviderEntity> findByCode(String code);

    boolean existsByCode(String code);
}

```

### `infrastructure/persistence/ServiceProviderRepositoryAdapter.java`

```java
package com.financesystem.finance_api.modules.platform.servicepayments.infrastructure.persistence;

import com.financesystem.finance_api.modules.platform.servicepayments.application.dto.PlatformServiceProviderFilter;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.model.ServiceProvider;
import com.financesystem.finance_api.modules.platform.servicepayments.domain.repository.ServiceProviderRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ServiceProviderRepositoryAdapter implements ServiceProviderRepository {

    private final ServiceProviderJpaRepository jpaRepository;

    public ServiceProviderRepositoryAdapter(ServiceProviderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ServiceProvider save(ServiceProvider serviceProvider) {
        return toDomain(jpaRepository.save(toEntity(serviceProvider)));
    }

    @Override
    public Optional<ServiceProvider> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ServiceProvider> findByCode(String code) {
        return jpaRepository.findByCode(code).map(this::toDomain);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaRepository.existsByCode(code);
    }

    @Override
    public Page<ServiceProvider> findAll(PlatformServiceProviderFilter filter, Pageable pageable) {
        Specification<ServiceProviderEntity> specification = alwaysTrue();

        if (filter != null) {
            if (filter.category() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("category"), filter.category()));
            }

            if (filter.status() != null) {
                specification = specification.and((root, query, cb) ->
                        cb.equal(root.get("status"), filter.status()));
            }

            if (StringUtils.hasText(filter.search())) {
                String like = "%" + filter.search().trim().toLowerCase() + "%";
                specification = specification.and((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();
                    predicates.add(cb.like(cb.lower(root.get("code")), like));
                    predicates.add(cb.like(cb.lower(root.get("name")), like));
                    predicates.add(cb.like(cb.lower(root.get("serviceCustomerCodeLabel")), like));
                    return cb.or(predicates.toArray(Predicate[]::new));
                });
            }
        }

        return jpaRepository.findAll(specification, pageable).map(this::toDomain);
    }

    private Specification<ServiceProviderEntity> alwaysTrue() {
        return (root, query, cb) -> cb.conjunction();
    }

    private ServiceProviderEntity toEntity(ServiceProvider serviceProvider) {
        ServiceProviderEntity entity = new ServiceProviderEntity();
        entity.setId(serviceProvider.id());
        entity.setCode(serviceProvider.code());
        entity.setName(serviceProvider.name());
        entity.setCategory(serviceProvider.category());
        entity.setServiceCustomerCodeLabel(serviceProvider.serviceCustomerCodeLabel());
        entity.setStatus(serviceProvider.status());
        return entity;
    }

    private ServiceProvider toDomain(ServiceProviderEntity entity) {
        return new ServiceProvider(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getCategory(),
                entity.getServiceCustomerCodeLabel(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

```

