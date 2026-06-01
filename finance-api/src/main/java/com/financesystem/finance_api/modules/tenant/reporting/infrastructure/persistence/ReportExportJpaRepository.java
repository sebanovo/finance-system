package com.financesystem.finance_api.modules.tenant.reporting.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportExportJpaRepository extends JpaRepository<ReportExportEntity, UUID> {

    java.util.Optional<ReportExportEntity> findById(UUID id);

    List<ReportExportEntity> findAllByExecutionIdOrderByCreatedAtDesc(UUID executionId);
}
