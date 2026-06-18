package com.financesystem.finance_api.modules.reporting.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Tunables for the reporting module (bound from {@code reports.*}). */
@Component
@ConfigurationProperties(prefix = "reports")
public class ReportingProperties {

    private String schema = "reporting";
    private final Ai ai = new Ai();
    private final Sql sql = new Sql();
    private final Snapshot snapshot = new Snapshot();
    private final Export export = new Export();

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public Ai getAi() {
        return ai;
    }

    public Sql getSql() {
        return sql;
    }

    public Snapshot getSnapshot() {
        return snapshot;
    }

    public Export getExport() {
        return export;
    }

    /** NL→SQL microservice client settings. */
    public static class Ai {
        private String mode = "mock";          // mock | http
        private String baseUrl = "http://reports-ai:8000";
        private String internalToken = "";
        private int timeoutMs = 15000;
        private int rateLimitPerMinute = 20;   // per-user AI request cap
        private long audioMaxBytes = 10 * 1024 * 1024;
        private String audioAllowedMimes =
                "audio/webm,audio/wav,audio/x-wav,audio/mpeg,audio/mp3,audio/mp4,audio/m4a,audio/x-m4a,application/octet-stream";

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public int getRateLimitPerMinute() {
            return rateLimitPerMinute;
        }

        public void setRateLimitPerMinute(int rateLimitPerMinute) {
            this.rateLimitPerMinute = rateLimitPerMinute;
        }

        public long getAudioMaxBytes() {
            return audioMaxBytes;
        }

        public void setAudioMaxBytes(long audioMaxBytes) {
            this.audioMaxBytes = audioMaxBytes;
        }

        public String getAudioAllowedMimes() {
            return audioAllowedMimes;
        }

        public void setAudioAllowedMimes(String audioAllowedMimes) {
            this.audioAllowedMimes = audioAllowedMimes;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getInternalToken() {
            return internalToken;
        }

        public void setInternalToken(String internalToken) {
            this.internalToken = internalToken;
        }

        public int getTimeoutMs() {
            return timeoutMs;
        }

        public void setTimeoutMs(int timeoutMs) {
            this.timeoutMs = timeoutMs;
        }
    }

    /** Guard / execution limits. */
    public static class Sql {
        private int maxRows = 500;             // systemLimit injected by the guard
        private int statementTimeoutMs = 5000;

        public int getMaxRows() {
            return maxRows;
        }

        public void setMaxRows(int maxRows) {
            this.maxRows = maxRows;
        }

        public int getStatementTimeoutMs() {
            return statementTimeoutMs;
        }

        public void setStatementTimeoutMs(int statementTimeoutMs) {
            this.statementTimeoutMs = statementTimeoutMs;
        }
    }

    /** Persisted snapshot caps for result_json. */
    public static class Snapshot {
        private int maxRows = 500;
        private long maxBytes = 5_242_880L;

        public int getMaxRows() {
            return maxRows;
        }

        public void setMaxRows(int maxRows) {
            this.maxRows = maxRows;
        }

        public long getMaxBytes() {
            return maxBytes;
        }

        public void setMaxBytes(long maxBytes) {
            this.maxBytes = maxBytes;
        }
    }

    /** Full-export settings. */
    public static class Export {
        private int maxRows = 10000;
        private String storagePath = "/app/generated-reports";

        public int getMaxRows() {
            return maxRows;
        }

        public void setMaxRows(int maxRows) {
            this.maxRows = maxRows;
        }

        public String getStoragePath() {
            return storagePath;
        }

        public void setStoragePath(String storagePath) {
            this.storagePath = storagePath;
        }
    }
}
