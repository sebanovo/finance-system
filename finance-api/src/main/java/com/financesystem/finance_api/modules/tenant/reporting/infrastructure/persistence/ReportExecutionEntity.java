package com.financesystem.finance_api.modules.tenant.reporting.infrastructure.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenant_report_executions")
public class ReportExecutionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "report_type", nullable = false, length = 80)
    private String reportType;

    @Column(name = "report_title", nullable = false, length = 150)
    private String reportTitle;

    @Column(name = "execution_name", nullable = false, length = 180)
    private String executionName;

    @Column(nullable = false, length = 30)
    private String mode;

    @Column(name = "requested_by_subject", length = 150)
    private String requestedBySubject;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_payload", nullable = false, columnDefinition = "jsonb")
    private JsonNode requestPayload;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private JsonNode outputs;

    @Column(name = "row_count", nullable = false)
    private int rowCount;

    @Column(name = "source_execution_id")
    private UUID sourceExecutionId;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public String getExecutionName() {
        return executionName;
    }

    public void setExecutionName(String executionName) {
        this.executionName = executionName;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getRequestedBySubject() {
        return requestedBySubject;
    }

    public void setRequestedBySubject(String requestedBySubject) {
        this.requestedBySubject = requestedBySubject;
    }

    public JsonNode getRequestPayload() {
        return requestPayload;
    }

    public void setRequestPayload(JsonNode requestPayload) {
        this.requestPayload = requestPayload;
    }

    public JsonNode getOutputs() {
        return outputs;
    }

    public void setOutputs(JsonNode outputs) {
        this.outputs = outputs;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public UUID getSourceExecutionId() {
        return sourceExecutionId;
    }

    public void setSourceExecutionId(UUID sourceExecutionId) {
        this.sourceExecutionId = sourceExecutionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
