package com.financesystem.finance_api.modules.governance.audit.application.usecase;

import com.financesystem.finance_api.modules.governance.audit.application.dto.AuditEventResponse;
import com.financesystem.finance_api.modules.governance.audit.application.mapper.AuditEventMapper;
import com.financesystem.finance_api.modules.governance.audit.domain.repository.PlatformAuditEventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListPlatformAuditEventsUseCase {

    private final PlatformAuditEventRepository platformAuditEventRepository;
    private final AuditEventMapper auditEventMapper;

    public ListPlatformAuditEventsUseCase(
            PlatformAuditEventRepository platformAuditEventRepository,
            AuditEventMapper auditEventMapper
    ) {
        this.platformAuditEventRepository = platformAuditEventRepository;
        this.auditEventMapper = auditEventMapper;
    }

    public List<AuditEventResponse> execute(int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 200);

        return platformAuditEventRepository.findRecent(safeLimit)
                .stream()
                .map(auditEventMapper::toResponse)
                .toList();
    }
}