package com.financesystem.finance_api.modules.tenant.servicepayments.application.usecase;

import com.financesystem.finance_api.common.exception.BusinessException;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.MyServiceEnrollmentFilter;
import com.financesystem.finance_api.modules.tenant.servicepayments.application.dto.ServiceEnrollmentResponse;
import com.financesystem.finance_api.modules.tenant.servicepayments.domain.repository.TenantServiceEnrollmentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ListMyServiceEnrollmentsUseCase {

    private final TenantServiceEnrollmentRepository tenantServiceEnrollmentRepository;
    private final com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper tenantServicePaymentsMapper;
    private final SecurityContextFacade securityContextFacade;

    public ListMyServiceEnrollmentsUseCase(
            TenantServiceEnrollmentRepository tenantServiceEnrollmentRepository,
            com.financesystem.finance_api.modules.tenant.servicepayments.application.mapper.TenantServicePaymentsMapper tenantServicePaymentsMapper,
            SecurityContextFacade securityContextFacade
    ) {
        this.tenantServiceEnrollmentRepository = tenantServiceEnrollmentRepository;
        this.tenantServicePaymentsMapper = tenantServicePaymentsMapper;
        this.securityContextFacade = securityContextFacade;
    }

    public Page<ServiceEnrollmentResponse> execute(MyServiceEnrollmentFilter filter, Pageable pageable) {
        UUID userId = currentUserId();
        MyServiceEnrollmentFilter effectiveFilter = filter == null
                ? new MyServiceEnrollmentFilter(null, null, null, null)
                : filter;

        return tenantServiceEnrollmentRepository.findAll(effectiveFilter, userId, pageable)
                .map(tenantServicePaymentsMapper::toEnrollmentResponse);
    }

    private UUID currentUserId() {
        String subject = securityContextFacade.getCurrentSubject();
        if (subject == null || subject.isBlank()) {
            throw new BusinessException("Authenticated user is required");
        }

        try {
            return UUID.fromString(subject.trim());
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Authenticated user subject is invalid");
        }
    }
}
