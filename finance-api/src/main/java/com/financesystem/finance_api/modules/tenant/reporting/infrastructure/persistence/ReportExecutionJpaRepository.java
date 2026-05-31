package com.financesystem.finance_api.modules.tenant.reporting.infrastructure.persistence;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReportExecutionJpaRepository extends JpaRepository<ReportExecutionEntity, UUID> {

    List<ReportExecutionEntity> findByReportTypeIgnoreCaseAndModeIgnoreCaseOrderByCreatedAtDesc(String reportType, String mode, Pageable pageable);

    List<ReportExecutionEntity> findByReportTypeIgnoreCaseOrderByCreatedAtDesc(String reportType, Pageable pageable);

    List<ReportExecutionEntity> findByModeIgnoreCaseOrderByCreatedAtDesc(String mode, Pageable pageable);

    List<ReportExecutionEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    List<ReportExecutionEntity> findBySourceExecutionIdOrderByCreatedAtDesc(UUID sourceExecutionId);

    long countByReportTypeIgnoreCaseAndModeIgnoreCase(String reportType, String mode);

    long countByReportTypeIgnoreCase(String reportType);

    long countByModeIgnoreCase(String mode);
}
