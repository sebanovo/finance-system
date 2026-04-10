package com.financesystem.finance_api.modules.governance.audit.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import com.financesystem.finance_api.modules.governance.audit.domain.model.PlatformAuditEvent;
import com.financesystem.finance_api.modules.governance.audit.domain.model.TenantAuditEvent;
import com.financesystem.finance_api.modules.governance.audit.domain.repository.PlatformAuditEventRepository;
import com.financesystem.finance_api.modules.governance.audit.domain.repository.TenantAuditEventRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditTrailService {

    private final PlatformAuditEventRepository platformAuditEventRepository;
    private final TenantAuditEventRepository tenantAuditEventRepository;
    private final SecurityContextFacade securityContextFacade;
    private final ObjectMapper objectMapper;

    public AuditTrailService(
            PlatformAuditEventRepository platformAuditEventRepository,
            TenantAuditEventRepository tenantAuditEventRepository,
            SecurityContextFacade securityContextFacade,
            ObjectMapper objectMapper
    ) {
        this.platformAuditEventRepository = platformAuditEventRepository;
        this.tenantAuditEventRepository = tenantAuditEventRepository;
        this.securityContextFacade = securityContextFacade;
        this.objectMapper = objectMapper;
    }

    public void recordPlatformEvent(
            String eventType,
            String resourceType,
            String resourceId,
            Object details
    ) {
        platformAuditEventRepository.save(
                new PlatformAuditEvent(
                        null,
                        resolveActorSubject(),
                        eventType,
                        resourceType,
                        resourceId,
                        serializeDetails(details),
                        null
                )
        );
    }

    public void recordTenantEvent(
            String eventType,
            String resourceType,
            String resourceId,
            Object details
    ) {
        tenantAuditEventRepository.save(
                new TenantAuditEvent(
                        null,
                        resolveActorSubject(),
                        eventType,
                        resourceType,
                        resourceId,
                        serializeDetails(details),
                        null
                )
        );
    }

    private String resolveActorSubject() {
        String subject = securityContextFacade.getCurrentSubject();
        return (subject == null || subject.isBlank()) ? "SYSTEM" : subject;
    }

    private String serializeDetails(Object details) {
        if (details == null) {
            return null;
        }

        try {
            return objectMapper.writeValueAsString(details);
        } catch (JsonProcessingException ex) {
            return String.valueOf(details);
        }
    }
}