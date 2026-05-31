package com.financesystem.finance_api.modules.tenant.reporting.infrastructure.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExecution;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExecutionStatus;
import com.financesystem.finance_api.modules.tenant.reporting.domain.model.ReportExport;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportExecutionNotFoundException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.exception.ReportException;
import com.financesystem.finance_api.modules.tenant.reporting.domain.repository.ReportExecutionRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ReportExecutionRepositoryAdapter implements ReportExecutionRepository {

    private final ReportExecutionJpaRepository executionJpaRepository;
    private final ReportExportJpaRepository exportJpaRepository;
    private final ObjectMapper objectMapper;

    public ReportExecutionRepositoryAdapter(
            ReportExecutionJpaRepository executionJpaRepository,
            ReportExportJpaRepository exportJpaRepository,
            ObjectMapper objectMapper
    ) {
        this.executionJpaRepository = executionJpaRepository;
        this.exportJpaRepository = exportJpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public ReportExecution save(ReportExecution execution) {
        ReportExecutionEntity entity = toEntity(execution);
        ReportExecutionEntity saved = executionJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ReportExecution> findById(UUID id) {
        return executionJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<ReportExecution> findRecent(String reportType, String mode, int limit, int offset) {
        PageRequest pageRequest = PageRequest.of(Math.max(offset, 0) / Math.max(limit, 1), Math.max(limit, 1));
        if (reportType != null && !reportType.isBlank() && mode != null && !mode.isBlank()) {
            return executionJpaRepository.findByReportTypeIgnoreCaseAndModeIgnoreCaseOrderByCreatedAtDesc(reportType, mode, pageRequest)
                    .stream().map(this::toDomain).toList();
        }
        if (reportType != null && !reportType.isBlank()) {
            return executionJpaRepository.findByReportTypeIgnoreCaseOrderByCreatedAtDesc(reportType, pageRequest)
                    .stream().map(this::toDomain).toList();
        }
        if (mode != null && !mode.isBlank()) {
            return executionJpaRepository.findByModeIgnoreCaseOrderByCreatedAtDesc(mode, pageRequest)
                    .stream().map(this::toDomain).toList();
        }
        return executionJpaRepository.findAllByOrderByCreatedAtDesc(pageRequest)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public long count(String reportType, String mode) {
        if (reportType != null && !reportType.isBlank() && mode != null && !mode.isBlank()) {
            return executionJpaRepository.countByReportTypeIgnoreCaseAndModeIgnoreCase(reportType, mode);
        }
        if (reportType != null && !reportType.isBlank()) {
            return executionJpaRepository.countByReportTypeIgnoreCase(reportType);
        }
        if (mode != null && !mode.isBlank()) {
            return executionJpaRepository.countByModeIgnoreCase(mode);
        }
        return executionJpaRepository.count();
    }

    @Override
    public ReportExport saveExport(ReportExport export) {
        ReportExportEntity entity = toEntity(export);
        ReportExportEntity saved = exportJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public List<ReportExport> findExportsByExecutionId(UUID executionId) {
        return exportJpaRepository.findAllByExecutionIdOrderByCreatedAtDesc(executionId)
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<ReportExecution> findRerunsBySourceExecutionId(UUID sourceExecutionId) {
        if (sourceExecutionId == null) {
            return List.of();
        }

        return executionJpaRepository.findBySourceExecutionIdOrderByCreatedAtDesc(sourceExecutionId)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public ReportExecution updateStatus(UUID id, ReportExecutionStatus status, String errorMessage, int rowCount) {
        ReportExecutionEntity entity = executionJpaRepository.findById(id)
                .orElseThrow(() -> new ReportExecutionNotFoundException(id));
        entity.setStatus(status.name());
        entity.setErrorMessage(errorMessage);
        entity.setRowCount(rowCount);
        return toDomain(executionJpaRepository.save(entity));
    }

    private ReportExecutionEntity toEntity(ReportExecution execution) {
        ReportExecutionEntity entity = new ReportExecutionEntity();
        entity.setId(execution.id());
        entity.setReportType(execution.reportType());
        entity.setReportTitle(execution.reportTitle());
        entity.setExecutionName(execution.executionName());
        entity.setMode(execution.mode());
        entity.setRequestedBySubject(execution.requestedBySubject());
        entity.setRequestPayload(toJsonNode(execution.requestPayload()));
        entity.setOutputs(toJsonNode(execution.outputs()));
        entity.setRowCount(execution.rowCount());
        entity.setSourceExecutionId(execution.sourceExecutionId());
        entity.setStatus(execution.status().name());
        entity.setErrorMessage(execution.errorMessage());
        return entity;
    }

    private ReportExecution toDomain(ReportExecutionEntity entity) {
        return new ReportExecution(
                entity.getId(),
                entity.getReportType(),
                entity.getReportTitle(),
                entity.getExecutionName(),
                entity.getMode(),
                entity.getRequestedBySubject(),
                toJsonString(entity.getRequestPayload()),
                toJsonString(entity.getOutputs()),
                entity.getRowCount(),
                entity.getSourceExecutionId(),
                ReportExecutionStatus.valueOf(entity.getStatus()),
                entity.getErrorMessage(),
                entity.getCreatedAt()
        );
    }

    private ReportExportEntity toEntity(ReportExport export) {
        ReportExportEntity entity = new ReportExportEntity();
        entity.setId(export.id());
        entity.setExecutionId(export.executionId());
        entity.setOutput(export.output());
        entity.setFileName(export.fileName());
        entity.setContentType(export.contentType());
        entity.setFileSizeBytes(export.fileSizeBytes());
        return entity;
    }

    private ReportExport toDomain(ReportExportEntity entity) {
        return new ReportExport(
                entity.getId(),
                entity.getExecutionId(),
                entity.getOutput(),
                entity.getFileName(),
                entity.getContentType(),
                entity.getFileSizeBytes(),
                entity.getCreatedAt()
        );
    }

    private JsonNode toJsonNode(String value) {
        try {
            return value == null ? null : objectMapper.readTree(value);
        } catch (Exception ex) {
            throw new ReportException("Failed to parse JSON payload", ex);
        }
    }

    private String toJsonString(JsonNode value) {
        return value == null ? null : value.toString();
    }
}
