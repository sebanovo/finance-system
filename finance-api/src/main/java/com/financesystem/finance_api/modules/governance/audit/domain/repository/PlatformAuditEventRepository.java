package com.financesystem.finance_api.modules.governance.audit.domain.repository;

import com.financesystem.finance_api.modules.governance.audit.domain.model.PlatformAuditEvent;

import java.util.List;

public interface PlatformAuditEventRepository {

    PlatformAuditEvent save(PlatformAuditEvent event);

    List<PlatformAuditEvent> findRecent(int limit);
}