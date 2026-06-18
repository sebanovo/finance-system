package com.financesystem.finance_api.modules.reporting.infrastructure.persistence;

import com.financesystem.finance_api.modules.reporting.domain.ReportActorScope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReportExecutionJpaRepository extends JpaRepository<ReportExecutionEntity, UUID> {

    Page<ReportExecutionEntity> findByActorScopeOrderByCreatedAtDesc(ReportActorScope actorScope, Pageable pageable);

    Page<ReportExecutionEntity> findByActorScopeAndTenantSlugOrderByCreatedAtDesc(
            ReportActorScope actorScope, String tenantSlug, Pageable pageable);
}
