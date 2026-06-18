package com.financesystem.finance_api.modules.reporting.infrastructure.persistence;

import com.financesystem.finance_api.modules.reporting.domain.ReportExportMode;
import com.financesystem.finance_api.modules.reporting.domain.ReportFormat;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Metadata of a generated export. The file itself lives on disk
 * ({@code storage_path}); only metadata is stored here.
 */
@Entity
@Table(schema = "public", name = "report_exports")
public class ReportExportEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "execution_id", nullable = false)
    private UUID executionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false, length = 10)
    private ReportFormat format;

    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 20)
    private ReportExportMode mode;

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;

    @Column(name = "content_type", nullable = false, length = 120)
    private String contentType;

    @Column(name = "file_size_bytes")
    private Long fileSizeBytes;

    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

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

    public UUID getExecutionId() {
        return executionId;
    }

    public void setExecutionId(UUID executionId) {
        this.executionId = executionId;
    }

    public ReportFormat getFormat() {
        return format;
    }

    public void setFormat(ReportFormat format) {
        this.format = format;
    }

    public ReportExportMode getMode() {
        return mode;
    }

    public void setMode(ReportExportMode mode) {
        this.mode = mode;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public void setFileSizeBytes(Long fileSizeBytes) {
        this.fileSizeBytes = fileSizeBytes;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(UUID createdByUserId) {
        this.createdByUserId = createdByUserId;
    }
}
