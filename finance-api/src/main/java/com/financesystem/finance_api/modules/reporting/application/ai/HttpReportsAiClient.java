package com.financesystem.finance_api.modules.reporting.application.ai;

import com.financesystem.finance_api.modules.reporting.application.config.ReportingProperties;
import com.financesystem.finance_api.modules.reporting.domain.ReportScope;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Locale;
import java.util.Map;

/**
 * Talks to the {@code reports-ai} FastAPI microservice (Fase 3). Active when
 * {@code reports.ai.mode=http}. Short timeout, no long retries; on failure
 * raises {@link ReportsAiUnavailableException} → 503 with a friendly message.
 */
@Component
@ConditionalOnProperty(name = "reports.ai.mode", havingValue = "http")
public class HttpReportsAiClient implements ReportsAiGateway {

    private final RestClient restClient;
    private final String internalToken;

    public HttpReportsAiClient(ReportingProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getAi().getTimeoutMs());
        factory.setReadTimeout(properties.getAi().getTimeoutMs());
        this.restClient = RestClient.builder()
                .baseUrl(properties.getAi().getBaseUrl())
                .requestFactory(factory)
                .build();
        this.internalToken = properties.getAi().getInternalToken();
    }

    @Override
    public AiSqlResponse generateFromText(String prompt, ReportScope scope, String schemaDescription) {
        try {
            AiHttpResponse response = restClient.post()
                    .uri("/generate-sql")
                    .header("X-Internal-Token", internalToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "prompt", prompt == null ? "" : prompt,
                            "schema_description", schemaDescription,
                            "scope", scope.name().toLowerCase(Locale.ROOT)
                    ))
                    .retrieve()
                    .body(AiHttpResponse.class);
            return toResponse(response);
        } catch (Exception e) {
            throw new ReportsAiUnavailableException("El servicio de IA no está disponible.", e);
        }
    }

    @Override
    public AiSqlResponse transcribeAndGenerate(byte[] audio, String mimeType, ReportScope scope,
                                               String schemaDescription) {
        try {
            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("audio", new ByteArrayResource(audio) {
                @Override
                public String getFilename() {
                    return "audio";
                }
            });
            form.add("schema_description", schemaDescription);
            form.add("scope", scope.name().toLowerCase(Locale.ROOT));

            AiHttpResponse response = restClient.post()
                    .uri("/transcribe-and-generate")
                    .header("X-Internal-Token", internalToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.MULTIPART_FORM_DATA_VALUE)
                    .body(form)
                    .retrieve()
                    .body(AiHttpResponse.class);
            return toResponse(response);
        } catch (Exception e) {
            throw new ReportsAiUnavailableException("El servicio de IA no está disponible.", e);
        }
    }

    private AiSqlResponse toResponse(AiHttpResponse response) {
        if (response == null || response.sql() == null || response.sql().isBlank()) {
            throw new ReportsAiUnavailableException("El servicio de IA devolvió una respuesta vacía.", null);
        }
        return new AiSqlResponse(response.transcript(), response.sql(), response.explanation());
    }

    private record AiHttpResponse(String transcript, String sql, String explanation) {
    }
}
