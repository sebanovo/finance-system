package com.financesystem.finance_api.modules.reporting.infrastructure.persistence;

import com.fasterxml.jackson.databind.JsonNode;
import com.financesystem.finance_api.modules.reporting.domain.ReportActorScope;
import com.financesystem.finance_api.modules.reporting.domain.ReportExecutionKind;
import com.financesystem.finance_api.modules.reporting.domain.ReportExecutionStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Audited report execution. Lives in {@code public} with a denormalized
 * snapshot of the actor — no FKs to tenant schemas.
 */
@Entity
@Table(schema = "public", name = "report_executions")
public class ReportExecutionEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "requested_by_user_id")
    private UUID requestedByUserId;

    @Column(name = "requested_by_email", length = 255)
    private String requestedByEmail;

    @Column(name = "requested_by_display_name", length = 255)
    private String requestedByDisplayName;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_scope", nullable = false, length = 20)
    private ReportActorScope actorScope;

    @Column(name = "tenant_slug", length = 100)
    private String tenantSlug;

    @Column(name = "tenant_schema", length = 128)
    private String tenantSchema;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false, length = 20)
    private ReportExecutionKind kind;

    @Column(name = "definition_key", length = 120)
    private String definitionKey;

    @Column(name = "request_params", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode requestParams;

    @Column(name = "prompt", columnDefinition = "TEXT")
    private String prompt;

    @Column(name = "transcript", columnDefinition = "TEXT")
    private String transcript;

    @Column(name = "sql", columnDefinition = "TEXT")
    private String sql;

    @Column(name = "referenced_views", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode referencedViews;

    @Column(name = "schema_used", length = 128)
    private String schemaUsed;

    @Column(name = "result_json", columnDefinition = "JSONB")
    @JdbcTypeCode(SqlTypes.JSON)
    private JsonNode resultJson;

    @Column(name = "row_count")
    private Integer rowCount;

    @Column(name = "truncated", nullable = false)
    private boolean truncated;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ReportExecutionStatus status;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "executed_at")
    private Instant executedAt;

    @PrePersist
    void onCreate() {
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

    public UUID getRequestedByUserId() {
        return requestedByUserId;
    }

    public void setRequestedByUserId(UUID requestedByUserId) {
        this.requestedByUserId = requestedByUserId;
    }

    public String getRequestedByEmail() {
        return requestedByEmail;
    }

    public void setRequestedByEmail(String requestedByEmail) {
        this.requestedByEmail = requestedByEmail;
    }

    public String getRequestedByDisplayName() {
        return requestedByDisplayName;
    }

    public void setRequestedByDisplayName(String requestedByDisplayName) {
        this.requestedByDisplayName = requestedByDisplayName;
    }

    public ReportActorScope getActorScope() {
        return actorScope;
    }

    public void setActorScope(ReportActorScope actorScope) {
        this.actorScope = actorScope;
    }

    public String getTenantSlug() {
        return tenantSlug;
    }

    public void setTenantSlug(String tenantSlug) {
        this.tenantSlug = tenantSlug;
    }

    public String getTenantSchema() {
        return tenantSchema;
    }

    public void setTenantSchema(String tenantSchema) {
        this.tenantSchema = tenantSchema;
    }

    public ReportExecutionKind getKind() {
        return kind;
    }

    public void setKind(ReportExecutionKind kind) {
        this.kind = kind;
    }

    public String getDefinitionKey() {
        return definitionKey;
    }

    public void setDefinitionKey(String definitionKey) {
        this.definitionKey = definitionKey;
    }

    public JsonNode getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(JsonNode requestParams) {
        this.requestParams = requestParams;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getTranscript() {
        return transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public JsonNode getReferencedViews() {
        return referencedViews;
    }

    public void setReferencedViews(JsonNode referencedViews) {
        this.referencedViews = referencedViews;
    }

    public String getSchemaUsed() {
        return schemaUsed;
    }

    public void setSchemaUsed(String schemaUsed) {
        this.schemaUsed = schemaUsed;
    }

    public JsonNode getResultJson() {
        return resultJson;
    }

    public void setResultJson(JsonNode resultJson) {
        this.resultJson = resultJson;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public boolean isTruncated() {
        return truncated;
    }

    public void setTruncated(boolean truncated) {
        this.truncated = truncated;
    }

    public ReportExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ReportExecutionStatus status) {
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

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExecutedAt() {
        return executedAt;
    }

    public void setExecutedAt(Instant executedAt) {
        this.executedAt = executedAt;
    }
}
