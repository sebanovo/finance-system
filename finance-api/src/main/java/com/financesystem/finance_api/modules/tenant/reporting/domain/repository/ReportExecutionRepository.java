package com.financesystem.finance_api.modules.tenant.reporting.domain.repository;

import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExecution;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExecutionStatus;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExport;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportExecutionRepository {

    ReportExecution save(ReportExecution execution);

    Optional<ReportExecution> findById(UUID id);

    List<ReportExecution> findRecent(String reportType, String mode, int limit, int offset);

    long count(String reportType, String mode);

    ReportExport saveExport(ReportExport export);

    List<ReportExport> findExportsByExecutionId(UUID executionId);

    List<ReportExecution> findRerunsBySourceExecutionId(UUID sourceExecutionId);

    ReportExecution updateStatus(UUID id, ReportExecutionStatus status, String errorMessage, int rowCount);
}
