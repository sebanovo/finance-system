# Directory Export: /home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/common

_Generated on 2026-06-25 01:03:40Z_

## Summary

- Source directory: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/common`
- Output file: `/home/walter/Documents/misDatos/repositorios/finance-system/finance-api/src/main/java/com/financesystem/finance_api/common.md`

## Files

### `audit/AuditContextFacade.java`

```java
package com.financesystem.finance_api.common.audit;

import com.financesystem.finance_api.common.security.context.SecurityContextFacade;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

@Component
public class AuditContextFacade {

    private static final String HEADER_REQUEST_ID = "X-Request-Id";
    private static final String HEADER_CORRELATION_ID = "X-Correlation-Id";
    private static final String HEADER_FORWARDED_FOR = "X-Forwarded-For";

    private final SecurityContextFacade securityContextFacade;

    public AuditContextFacade(SecurityContextFacade securityContextFacade) {
        this.securityContextFacade = securityContextFacade;
    }

    public AuditContext resolve() {
        String actorSubject = resolveActorSubject();
        String actorEmail = resolveActorEmail();
        String tenantSlug = securityContextFacade.getCurrentTenantSlug();
        String requestId = resolveRequestId();
        String correlationId = resolveCorrelationId(requestId);

        return new AuditContext(
                resolveActorId(actorSubject),
                actorSubject,
                actorEmail,
                tenantSlug,
                resolveIpAddress(),
                resolveUserAgent(),
                requestId,
                correlationId,
                "APPLICATION",
                "SUCCESS"
        );
    }

    private UUID resolveActorId(String actorSubject) {
        if (actorSubject == null || actorSubject.isBlank()) {
            return null;
        }

        try {
            return UUID.fromString(actorSubject.trim());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String resolveActorSubject() {
        String subject = securityContextFacade.getCurrentSubject();
        return (subject == null || subject.isBlank()) ? "SYSTEM" : subject.trim();
    }

    private String resolveActorEmail() {
        String email = securityContextFacade.getCurrentEmail();
        return (email == null || email.isBlank()) ? null : email.trim();
    }

    private String resolveRequestId() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return null;
        }

        return normalize(request.getHeader(HEADER_REQUEST_ID), request.getHeader(HEADER_CORRELATION_ID));
    }

    private String resolveCorrelationId(String requestId) {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return requestId;
        }

        return normalize(request.getHeader(HEADER_CORRELATION_ID), requestId);
    }

    private InetAddress resolveIpAddress() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return null;
        }

        String forwardedFor = normalize(request.getHeader(HEADER_FORWARDED_FOR), null);
        if (forwardedFor != null) {
            int separatorIndex = forwardedFor.indexOf(',');
            return toInetAddress(separatorIndex >= 0 ? forwardedFor.substring(0, separatorIndex).trim() : forwardedFor);
        }

        String remoteAddress = normalize(request.getRemoteAddr(), null);
        return toInetAddress(remoteAddress);
    }

    private String resolveUserAgent() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return null;
        }

        return normalize(request.getHeader("User-Agent"), null);
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }

        return servletRequestAttributes.getRequest();
    }

    private String normalize(String value, String fallback) {
        if (value == null) {
            return fallback;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }

    private InetAddress toInetAddress(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        try {
            return InetAddress.getByName(value.trim());
        } catch (UnknownHostException ex) {
            return null;
        }
    }

    public record AuditContext(
            UUID actorId,
            String actorSubject,
            String actorEmail,
            String tenantSlug,
            InetAddress ipAddress,
            String userAgent,
            String requestId,
            String correlationId,
            String source,
            String outcome
    ) {
    }
}

```

### `config/AsyncConfig.java`

```java
package com.financesystem.finance_api.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "backupTaskExecutor")
    public Executor backupTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("backup-job-");
        executor.initialize();
        return executor;
    }
}

```

### `config/CorsConfig.java`

```java
package com.financesystem.finance_api.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:4200,http://localhost:8080,http://localhost:5200}")
    private String allowedOrigins;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();

        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### `config/OpenApiConfig.java`

```java
package com.financesystem.finance_api.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI financeSystemOpenApi() {
        String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Finance System API")
                        .description("Base API for the Finance System SaaS platform")
                        .version("v1")
                        .contact(new Contact().name("Finance System Team"))
                        .license(new License().name("Internal Academic Use")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(
                                securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }
}
```

### `config/SecurityConfig.java`

```java
package com.financesystem.finance_api.common.config;

import com.financesystem.finance_api.common.security.http.RestAccessDeniedHandler;
import com.financesystem.finance_api.common.security.http.RestAuthenticationEntryPoint;
import com.financesystem.finance_api.common.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            RestAccessDeniedHandler restAccessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/api-docs",
                                "/api-docs/**",
                                "/actuator/health",
                                "/actuator/info",
                                "/api/public/**",
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password",
                                "/api/auth/face/login",
                                "/api/platform/auth/login",
                                "/api/platform/auth/refresh"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

```

### `config/WebMvcConfig.java`

```java
package com.financesystem.finance_api.common.config;

import com.financesystem.finance_api.common.tenancy.interceptor.TenantContextInterceptor;
import com.financesystem.finance_api.common.tenancy.maintenance.TenantMaintenanceInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TenantContextInterceptor tenantContextInterceptor;
    private final TenantMaintenanceInterceptor tenantMaintenanceInterceptor;

    public WebMvcConfig(
            TenantContextInterceptor tenantContextInterceptor,
            TenantMaintenanceInterceptor tenantMaintenanceInterceptor
    ) {
        this.tenantContextInterceptor = tenantContextInterceptor;
        this.tenantMaintenanceInterceptor = tenantMaintenanceInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantContextInterceptor)
                .addPathPatterns("/**")
                .order(0);

        registry.addInterceptor(tenantMaintenanceInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/platform/**",
                        "/api/public/**",
                        "/api/auth/login",
                        "/api/auth/refresh",
                        "/api/auth/forgot-password",
                        "/api/auth/reset-password",
                        "/api/auth/face/login",
                        "/api-docs/**",
                        "/swagger-ui/**",
                        "/actuator/**"
                )
                .order(1);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(0, new com.financesystem.finance_api.common.pagination.LegacyPageableArgumentResolver());
    }
}

```

### `exception/BusinessException.java`

```java
package com.financesystem.finance_api.common.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

```

### `exception/GlobalExceptionHandler.java`

```java
package com.financesystem.finance_api.common.exception;

import com.financesystem.finance_api.common.response.ApiErrorResponse;
import com.financesystem.finance_api.common.tenancy.exception.TenantResolutionException;
import com.financesystem.finance_api.modules.identity.auth.application.exception.FaceRecognitionUnavailableException;
import com.financesystem.finance_api.modules.identity.auth.domain.exception.AuthenticationFailedException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(exception.getMessage(), List.of(exception.getMessage())));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(exception.getMessage(), List.of(exception.getMessage())));
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthenticationFailed(AuthenticationFailedException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiErrorResponse.of(exception.getMessage(), List.of(exception.getMessage())));
    }

    @ExceptionHandler(FaceRecognitionUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> handleFaceRecognitionUnavailable(FaceRecognitionUnavailableException exception) {
        String reason = exception.getMessage();
        if (reason == null || reason.isBlank()) {
            reason = "Face recognition service is unavailable";
        }

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiErrorResponse.of(
                        "Face recognition service is unavailable",
                        List.of(reason)
                ));
    }

    @ExceptionHandler(TenantResolutionException.class)
    public ResponseEntity<ApiErrorResponse> handleTenantResolutionException(TenantResolutionException exception) {
        String message = exception.getMessage();
        String userMessage = "Invalid tenant configuration";
        String guidance = "Verify the X-Tenant-Slug header matches an existing tenant and try again.";

        if (message != null && !message.isBlank()) {
            if (message.contains("missing")) {
                userMessage = "Missing tenant header";
            } else if (message.contains("was not found")) {
                userMessage = "Unknown tenant";
            } else if (message.contains("not ready")) {
                userMessage = "Tenant schema is not ready";
            } else if (message.contains("does not match any registered tenant")) {
                userMessage = "Unknown tenant";
            } else if (message.contains("invalid")) {
                userMessage = "Invalid tenant value";
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(
                        userMessage,
                        List.of(
                                message != null && !message.isBlank() ? message : guidance,
                                "Verify the X-Tenant-Slug header matches an existing tenant and that the tenant schema has been migrated."
                        )
                ));
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(RuntimeException exception) {
        String reason = exception.getMessage();
        if (reason == null || reason.isBlank()) {
            reason = "You do not have the required role or permission to access this resource";
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiErrorResponse.of(
                        "You do not have the required role or permission to access this resource",
                        List.of(reason)
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        List<String> errors = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(error -> {
                    if (error instanceof FieldError fieldError) {
                        return fieldError.getField() + ": " + fieldError.getDefaultMessage();
                    }
                    return error.getDefaultMessage();
                })
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of("Validation failed", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of("Constraint validation failed", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadableMessage(HttpMessageNotReadableException exception) {
        String reason = exception.getMostSpecificCause() != null && exception.getMostSpecificCause().getMessage() != null
                ? exception.getMostSpecificCause().getMessage()
                : "Request body is malformed or contains invalid field values";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of("Invalid request body", List.of(reason)));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException exception) {
        String reason = exception.getMessage() != null && !exception.getMessage().isBlank()
                ? exception.getMessage()
                : "The uploaded file exceeds the maximum allowed size";

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(ApiErrorResponse.of(
                        "Uploaded file too large",
                        List.of(reason, "Reduce the photo size and try again")
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String field = exception.getName() != null ? exception.getName() : "parameter";
        String expected = exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName() : "expected type";
        String value = exception.getValue() != null ? String.valueOf(exception.getValue()) : "null";

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.of(
                        "Invalid request parameter",
                        List.of(field + " value '" + value + "' is not a valid " + expected)
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        String reason = extractDataIntegrityReason(exception);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.of("Database constraint violation", List.of(reason)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGenericException(Exception exception) {
        logger.error("Unexpected internal error", exception);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of("Unexpected internal error", List.of(
                        exception.getMessage() != null && !exception.getMessage().isBlank()
                                ? exception.getMessage()
                                : "An unexpected internal error occurred"
                )));
    }

    private String extractDataIntegrityReason(DataIntegrityViolationException exception) {
        Throwable current = exception;
        while (current != null) {
            String message = current.getMessage();
            if (message != null && !message.isBlank()) {
                String normalized = message.trim();
                if (normalized.contains("duplicate key value")) {
                    return "Duplicate value violates a unique constraint";
                }
                if (normalized.contains("violates foreign key constraint")) {
                    return "Referenced entity does not exist";
                }
                if (normalized.contains("violates not-null constraint")) {
                    return "A required field was missing";
                }
                if (normalized.contains("violates check constraint")) {
                    return "A field value violates a business constraint";
                }
                return normalized;
            }
            current = current.getCause();
        }

        return "Database constraint violation";
    }
}

```

### `exception/ResourceNotFoundException.java`

```java
package com.financesystem.finance_api.common.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

### `mail/AppMailProperties.java`

```java
package com.financesystem.finance_api.common.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.mail")
public class AppMailProperties {

    private String from = "no-reply@finance.local";

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
```

### `mail/MailDeliveryService.java`

```java
package com.financesystem.finance_api.common.mail;

public interface MailDeliveryService {

    void send(MailMessage message);
}
```

### `mail/MailMessage.java`

```java
package com.financesystem.finance_api.common.mail;

public record MailMessage(
        String to,
        String subject,
        String body
) {
}
```

### `mail/SmtpMailDeliveryService.java`

```java
package com.financesystem.finance_api.common.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpMailDeliveryService implements MailDeliveryService {

    private static final Logger logger = LoggerFactory.getLogger(SmtpMailDeliveryService.class);

    private final JavaMailSender javaMailSender;
    private final AppMailProperties appMailProperties;

    public SmtpMailDeliveryService(
            JavaMailSender javaMailSender,
            AppMailProperties appMailProperties
    ) {
        this.javaMailSender = javaMailSender;
        this.appMailProperties = appMailProperties;
    }

    @Override
    public void send(MailMessage message) {
        logger.info("Sending email notification to '{}'.", message.to());

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(appMailProperties.getFrom());
        simpleMailMessage.setTo(message.to());
        simpleMailMessage.setSubject(message.subject());
        simpleMailMessage.setText(message.body());

        javaMailSender.send(simpleMailMessage);

        logger.info("Email notification sent successfully to '{}'.", message.to());
    }
}
```

### `pagination/LegacyPageableArgumentResolver.java`

```java
package com.financesystem.finance_api.common.pagination;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

@Component
public class LegacyPageableArgumentResolver implements HandlerMethodArgumentResolver {

    private final PageableHandlerMethodArgumentResolver delegate =
            new PageableHandlerMethodArgumentResolver(new SortHandlerMethodArgumentResolver());

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) throws Exception {
        Pageable pageable = delegate.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return pageable;
        }

        Map<String, String[]> params = request.getParameterMap();
        if (PaginationCompatibilitySupport.hasExplicitSpringPagingParameters(request)) {
            return pageable;
        }

        Integer limit = readPositiveInt(params.get("limit"));
        Integer offset = readNonNegativeInt(params.get("offset"));

        if (limit == null && offset == null) {
            return pageable;
        }

        int size = limit != null ? limit : pageable.getPageSize();
        if (size <= 0) {
            size = pageable.getPageSize() > 0 ? pageable.getPageSize() : 50;
        }

        int safeOffset = offset != null ? offset : 0;
        int page = size == 0 ? 0 : Math.max(safeOffset / size, 0);

        return PageRequest.of(page, size, pageable.getSort());
    }

    private Integer readPositiveInt(@Nullable String[] values) {
        Integer value = readInt(values);
        return value != null && value > 0 ? value : null;
    }

    private Integer readNonNegativeInt(@Nullable String[] values) {
        Integer value = readInt(values);
        return value != null && value >= 0 ? value : null;
    }

    private Integer readInt(@Nullable String[] values) {
        if (values == null || values.length == 0 || values[0] == null || values[0].isBlank()) {
            return null;
        }

        try {
            return Integer.parseInt(values[0]);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}

```

### `pagination/PaginationCompatibilityResponseAdvice.java`

```java
package com.financesystem.finance_api.common.pagination;

import com.financesystem.finance_api.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class PaginationCompatibilityResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(
            MethodParameter returnType,
            Class<? extends HttpMessageConverter<?>> converterType
    ) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (!(body instanceof ApiResponse<?> apiResponse)) {
            return body;
        }

        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return body;
        }

        HttpServletRequest servletHttpRequest = servletRequest.getServletRequest();
        Object adaptedData = PaginationCompatibilitySupport.adaptResponseData(
                apiResponse.data(),
                servletHttpRequest
        );

        if (adaptedData == apiResponse.data()) {
            return body;
        }

        return new ApiResponse<>(
                apiResponse.success(),
                apiResponse.message(),
                adaptedData,
                apiResponse.timestamp()
        );
    }
}

```

### `pagination/PaginationCompatibilitySupport.java`

```java
package com.financesystem.finance_api.common.pagination;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;

import java.util.Map;

public final class PaginationCompatibilitySupport {

    private PaginationCompatibilitySupport() {
    }

    public static boolean hasExplicitSpringPagingParameters(HttpServletRequest request) {
        if (request == null) {
            return false;
        }

        Map<String, String[]> params = request.getParameterMap();
        return params.containsKey("page")
                || params.containsKey("size")
                || params.containsKey("sort");
    }

    public static Object adaptResponseData(Object data, HttpServletRequest request) {
        if (data instanceof Page<?> page && !hasExplicitSpringPagingParameters(request)) {
            return page.getContent();
        }

        return data;
    }
}

```

### `pagination/PaginationSupport.java`

```java
package com.financesystem.finance_api.common.pagination;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

public final class PaginationSupport {

    private static final int DEFAULT_PAGE_SIZE = 50;

    private PaginationSupport() {
    }

    public static <T> Page<T> page(List<T> items, Pageable pageable) {
        List<T> safeItems = items == null ? Collections.emptyList() : items;
        Pageable safePageable = normalize(pageable);

        int page = safePageable.getPageNumber();
        int size = safePageable.getPageSize();
        int fromIndex = Math.min(page * size, safeItems.size());
        int toIndex = Math.min(fromIndex + size, safeItems.size());

        return new PageImpl<>(
                safeItems.subList(fromIndex, toIndex),
                PageRequest.of(page, size, safePageable.getSort()),
                safeItems.size()
        );
    }

    private static Pageable normalize(Pageable pageable) {
        if (pageable == null) {
            return PageRequest.of(0, DEFAULT_PAGE_SIZE);
        }

        int page = Math.max(pageable.getPageNumber(), 0);
        int size = pageable.getPageSize() > 0 ? pageable.getPageSize() : DEFAULT_PAGE_SIZE;
        return PageRequest.of(page, size, pageable.getSort());
    }
}

```

### `response/ApiErrorResponse.java`

```java
package com.financesystem.finance_api.common.response;

import java.time.Instant;
import java.util.List;

public record ApiErrorResponse(
        boolean success,
        String message,
        List<String> errors,
        Instant timestamp
) {
    public static ApiErrorResponse of(String message, List<String> errors) {
        return new ApiErrorResponse(false, message, errors, Instant.now());
    }
}
```

### `response/ApiResponse.java`

```java
package com.financesystem.finance_api.common.response;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data,
        Instant timestamp
) {
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, Instant.now());
    }
}
```

### `security/authorization/AuthorizationGuards.java`

```java
package com.financesystem.finance_api.common.security.authorization;

import com.financesystem.finance_api.common.security.principal.AuthenticatedUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component("authorizationGuards")
public class AuthorizationGuards {

    private static final String PLATFORM_TENANT = "platform";

    public boolean isPlatformAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(authentication)) {
            return false;
        }

        AuthenticatedUserPrincipal principal = getPrincipal(authentication);
        if (principal == null) {
            return false;
        }

        return PLATFORM_TENANT.equalsIgnoreCase(principal.tenantSlug())
                && hasAnyAuthority(authentication, "ROLE_ADMIN", "ROLE_SUPERADMIN");
    }

    public boolean isPlatformAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!isAuthenticated(authentication)) {
            return false;
        }

        AuthenticatedUserPrincipal principal = getPrincipal(authentication);
        return principal != null && PLATFORM_TENANT.equalsIgnoreCase(principal.tenantSlug());
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated();
    }

    private AuthenticatedUserPrincipal getPrincipal(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUserPrincipal authenticatedUserPrincipal) {
            return authenticatedUserPrincipal;
        }
        return null;
    }

    private boolean hasAnyAuthority(Authentication authentication, String... authorities) {
        Set<String> currentAuthorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        for (String authority : authorities) {
            if (currentAuthorities.contains(authority)) {
                return true;
            }
        }

        return false;
    }
}

```

### `security/context/SecurityContextFacade.java`

```java
package com.financesystem.finance_api.common.security.context;

import com.financesystem.finance_api.common.security.principal.AuthenticatedUserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SecurityContextFacade {

    public AuthenticatedUserPrincipal getCurrentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUserPrincipal authenticatedUserPrincipal) {
            return authenticatedUserPrincipal;
        }

        return null;
    }

    public String getCurrentSubject() {
        AuthenticatedUserPrincipal principal = getCurrentPrincipal();
        return principal != null ? principal.subject() : null;
    }

    public String getCurrentDisplayName() {
        AuthenticatedUserPrincipal principal = getCurrentPrincipal();
        if (principal == null) {
            return null;
        }

        String displayName = principal.displayName();
        return (displayName == null || displayName.isBlank()) ? null : displayName;
    }

    public String getCurrentEmail() {
        AuthenticatedUserPrincipal principal = getCurrentPrincipal();
        if (principal == null) {
            return null;
        }

        String email = principal.email();
        return (email == null || email.isBlank()) ? null : email;
    }

    public String getCurrentTenantSlug() {
        AuthenticatedUserPrincipal principal = getCurrentPrincipal();
        return principal != null ? principal.tenantSlug() : null;
    }

    public boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authority == null || authority.isBlank()) {
            return false;
        }

        Set<String> currentAuthorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return currentAuthorities.contains(authority);
    }
}

```

### `security/http/RestAccessDeniedHandler.java`

```java
package com.financesystem.finance_api.common.security.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.common.response.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public RestAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorResponse body = ApiErrorResponse.of(
                "You do not have permission to access this resource",
                List.of(accessDeniedException.getMessage())
        );

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
```

### `security/http/RestAuthenticationEntryPoint.java`

```java
package com.financesystem.finance_api.common.security.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.common.response.ApiErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorResponse body = ApiErrorResponse.of(
                "Authentication is required to access this resource",
                List.of(authException.getMessage())
        );

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
```

### `security/jwt/JwtAuthenticationFilter.java`

```java
package com.financesystem.finance_api.common.security.jwt;

import com.financesystem.finance_api.common.security.principal.AuthenticatedUserPrincipal;
import com.financesystem.finance_api.common.tenancy.TenancyProperties;
import com.financesystem.finance_api.modules.platform.auth.domain.model.PlatformAuthConstants;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenService jwtTokenService;
    private final TenancyProperties tenancyProperties;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(
            JwtTokenService jwtTokenService,
            TenancyProperties tenancyProperties,
            AuthenticationEntryPoint authenticationEntryPoint
    ) {
        this.jwtTokenService = jwtTokenService;
        this.tenancyProperties = tenancyProperties;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();

        if (tenancyProperties.isPublicPath(requestUri)) {
            return true;
        }

        return antPathMatcher.match("/api/auth/login", requestUri)
                || antPathMatcher.match("/api/auth/refresh", requestUri)
                || antPathMatcher.match("/api/auth/forgot-password", requestUri)
                || antPathMatcher.match("/api/auth/reset-password", requestUri)
                || antPathMatcher.match("/api/auth/face/login", requestUri)
                || antPathMatcher.match("/api/platform/auth/login", requestUri)
                || antPathMatcher.match("/api/platform/auth/refresh", requestUri);
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");

            if (!StringUtils.hasText(authorizationHeader) || !authorizationHeader.startsWith(BEARER_PREFIX)) {
                throw new InsufficientAuthenticationException("Missing or invalid Authorization header");
            }

            String token = authorizationHeader.substring(BEARER_PREFIX.length());
            AuthenticatedUserPrincipal principal = jwtTokenService.parseAccessToken(token);

            String requestUri = request.getRequestURI();
            if (isPlatformToken(principal)) {
                if (!isPlatformRequest(requestUri)) {
                    throw new BadCredentialsException("Platform token cannot be used for tenant resources");
                }
            } else {
                String tenantHeaderValue = request.getHeader(tenancyProperties.getHeaderName());
                if (StringUtils.hasText(tenantHeaderValue)
                        && !tenantHeaderValue.equalsIgnoreCase(principal.tenantSlug())) {
                    throw new BadCredentialsException("Token tenant does not match request tenant header");
                }
            }

            List<SimpleGrantedAuthority> authorities = buildAuthorities(principal);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            filterChain.doFilter(request, response);
        } catch (JwtException | InsufficientAuthenticationException | BadCredentialsException ex) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(
                    request,
                    response,
                    new BadCredentialsException(ex.getMessage(), ex)
            );
        }
    }

    private List<SimpleGrantedAuthority> buildAuthorities(AuthenticatedUserPrincipal principal) {
        LinkedHashSet<String> authorityValues = new LinkedHashSet<>();

        for (String role : principal.roles()) {
            if (!StringUtils.hasText(role)) {
                throw new JwtException("Token roles contain blank values");
            }

            String normalizedRole = role.trim();
            if (!normalizedRole.startsWith("ROLE_")) {
                normalizedRole = "ROLE_" + normalizedRole.toUpperCase();
            }

            authorityValues.add(normalizedRole);
        }

        for (String permission : principal.permissions()) {
            if (!StringUtils.hasText(permission)) {
                throw new JwtException("Token permissions contain blank values");
            }

            authorityValues.add(permission.trim());
        }

        return authorityValues.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    private boolean isPlatformToken(AuthenticatedUserPrincipal principal) {
        return principal != null
                && principal.tenantSlug() != null
                && PlatformAuthConstants.PLATFORM_TENANT_SLUG.equalsIgnoreCase(principal.tenantSlug());
    }

    private boolean isPlatformRequest(String requestUri) {
        return antPathMatcher.match("/api/platform/**", requestUri)
                || antPathMatcher.match("/api/dashboard/superadmin/**", requestUri);
    }
}

```

### `security/jwt/JwtClaimNames.java`

```java
package com.financesystem.finance_api.common.security.jwt;

public final class JwtClaimNames {

    public static final String TENANT = "tenant";
    public static final String EMAIL = "email";
    public static final String DISPLAY_NAME = "displayName";
    public static final String ROLES = "roles";
    public static final String PERMISSIONS = "permissions";
    public static final String TOKEN_TYPE = "type";

    public static final String ACCESS = "access";
    public static final String REFRESH = "refresh";

    private JwtClaimNames() {
    }
}

```

### `security/jwt/JwtTokenService.java`

```java
package com.financesystem.finance_api.common.security.jwt;

import com.financesystem.finance_api.common.security.JwtProperties;
import com.financesystem.finance_api.common.security.principal.AuthenticatedUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class JwtTokenService {

    private static final Pattern ROLE_NAME_PATTERN = Pattern.compile("^[A-Z][A-Z0-9_]*$");
    private static final Pattern PERMISSION_CODE_PATTERN = Pattern.compile("^[a-z][a-z0-9-]*(?:[.][a-z0-9-]+)*$");
    private static final String ROLE_PREFIX = "ROLE_";

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(String subject, String tenantSlug, List<String> roles) {
        return generateAccessToken(subject, null, null, tenantSlug, roles, List.of());
    }

    public String generateAccessToken(String subject, String email, String tenantSlug, List<String> roles) {
        return generateAccessToken(subject, email, null, tenantSlug, roles, List.of());
    }

    public String generateAccessToken(
            String subject,
            String email,
            String displayName,
            String tenantSlug,
            List<String> roles,
            List<String> permissions
    ) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtProperties.getAccessExpirationMs());

        return Jwts.builder()
                .setSubject(requireText(subject, "subject"))
                .addClaims(buildAccessClaims(email, displayName, tenantSlug, roles, permissions))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String subject, String tenantSlug) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(jwtProperties.getRefreshExpirationMs());

        return Jwts.builder()
                .setSubject(requireText(subject, "subject"))
                .addClaims(Map.of(
                        JwtClaimNames.TENANT, requireText(tenantSlug, "tenantSlug"),
                        JwtClaimNames.TOKEN_TYPE, JwtClaimNames.REFRESH
                ))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public AuthenticatedUserPrincipal parseAccessToken(String token) {
        Claims claims = parseClaims(token);

        String tokenType = claims.get(JwtClaimNames.TOKEN_TYPE, String.class);
        if (!JwtClaimNames.ACCESS.equals(tokenType)) {
            throw new JwtException("Invalid token type");
        }

        String subject = claims.getSubject();
        String email = claims.get(JwtClaimNames.EMAIL, String.class);
        String displayName = claims.get(JwtClaimNames.DISPLAY_NAME, String.class);
        String tenantSlug = claims.get(JwtClaimNames.TENANT, String.class);

        List<String> roles = normalizeRoleNames(readStringListClaim(claims, JwtClaimNames.ROLES));
        List<String> permissions = normalizePermissionCodes(readStringListClaim(claims, JwtClaimNames.PERMISSIONS));

        if (!StringUtils.hasText(subject)) {
            throw new JwtException("Token subject is missing");
        }

        if (!StringUtils.hasText(tenantSlug)) {
            throw new JwtException("Token tenant is missing");
        }

        return new AuthenticatedUserPrincipal(
                subject,
                normalizeOptionalText(email),
                displayName,
                tenantSlug,
                roles,
                permissions
        );
    }

    public Claims parseRefreshToken(String token) {
        Claims claims = parseClaims(token);

        String tokenType = claims.get(JwtClaimNames.TOKEN_TYPE, String.class);
        if (!JwtClaimNames.REFRESH.equals(tokenType)) {
            throw new JwtException("Invalid refresh token type");
        }

        return claims;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private String requireText(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new JwtException("Token " + fieldName + " is missing");
        }

        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }

        return value.trim();
    }

    private Map<String, Object> buildAccessClaims(
            String email,
            String displayName,
            String tenantSlug,
            List<String> roles,
            List<String> permissions
    ) {
        Map<String, Object> claims = new LinkedHashMap<>();
        String normalizedEmail = normalizeOptionalText(email);
        if (normalizedEmail != null) {
            claims.put(JwtClaimNames.EMAIL, normalizedEmail);
        }
        String normalizedDisplayName = normalizeOptionalText(displayName);
        if (normalizedDisplayName != null) {
            claims.put(JwtClaimNames.DISPLAY_NAME, normalizedDisplayName);
        }
        claims.put(JwtClaimNames.TENANT, requireText(tenantSlug, "tenantSlug"));
        claims.put(JwtClaimNames.ROLES, normalizeRoleNames(roles));
        claims.put(JwtClaimNames.PERMISSIONS, normalizePermissionCodes(permissions));
        claims.put(JwtClaimNames.TOKEN_TYPE, JwtClaimNames.ACCESS);
        return claims;
    }

    private List<String> readStringListClaim(Claims claims, String claimName) {
        Object claimValue = claims.get(claimName);
        if (claimValue == null) {
            return List.of();
        }

        if (!(claimValue instanceof List<?> rawList)) {
            throw new JwtException("Token claim '" + claimName + "' must be an array");
        }

        List<String> values = rawList.stream()
                .map(item -> {
                    if (!(item instanceof String stringValue)) {
                        throw new JwtException("Token claim '" + claimName + "' must contain only strings");
                    }
                    return stringValue;
                })
                .toList();

        return values;
    }

    private List<String> normalizeRoleNames(List<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return List.of();
        }

        LinkedHashSet<String> normalizedRoles = new LinkedHashSet<>();
        for (String roleName : roleNames) {
            String normalizedRole = normalizeRoleName(roleName);
            if (!ROLE_NAME_PATTERN.matcher(normalizedRole).matches()) {
                throw new JwtException("Invalid role name in token: " + roleName);
            }
            normalizedRoles.add(normalizedRole);
        }

        return List.copyOf(normalizedRoles);
    }

    private String normalizeRoleName(String roleName) {
        if (!StringUtils.hasText(roleName)) {
            throw new JwtException("Token roles contain blank values");
        }

        String normalizedRole = roleName.trim();
        if (normalizedRole.startsWith(ROLE_PREFIX)) {
            normalizedRole = normalizedRole.substring(ROLE_PREFIX.length());
        }

        if (!StringUtils.hasText(normalizedRole)) {
            throw new JwtException("Token roles contain blank values");
        }

        return normalizedRole;
    }

    private List<String> normalizePermissionCodes(List<String> permissionCodes) {
        if (permissionCodes == null || permissionCodes.isEmpty()) {
            return List.of();
        }

        LinkedHashSet<String> normalizedPermissions = new LinkedHashSet<>();
        for (String permissionCode : permissionCodes) {
            if (!StringUtils.hasText(permissionCode)) {
                throw new JwtException("Token permissions contain blank values");
            }

            String normalizedPermission = permissionCode.trim();
            if (!PERMISSION_CODE_PATTERN.matcher(normalizedPermission).matches()) {
                throw new JwtException("Invalid permission code in token: " + permissionCode);
            }

            normalizedPermissions.add(normalizedPermission);
        }

        return List.copyOf(normalizedPermissions);
    }
}

```

### `security/JwtProperties.java`

```java
package com.financesystem.finance_api.common.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {

    private String secret;
    private long accessExpirationMs;
    private long refreshExpirationMs;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    public void setAccessExpirationMs(long accessExpirationMs) {
        this.accessExpirationMs = accessExpirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    public void setRefreshExpirationMs(long refreshExpirationMs) {
        this.refreshExpirationMs = refreshExpirationMs;
    }
}
```

### `security/principal/AuthenticatedUserPrincipal.java`

```java
package com.financesystem.finance_api.common.security.principal;

import java.util.List;

public record AuthenticatedUserPrincipal(
        String subject,
        String email,
        String displayName,
        String tenantSlug,
        List<String> roles,
        List<String> permissions
) {
}

```

### `tenancy/context/TenantContextHolder.java`

```java
package com.financesystem.finance_api.common.tenancy.context;

import com.financesystem.finance_api.common.tenancy.exception.TenantResolutionException;

public final class TenantContextHolder {

    private static final ThreadLocal<TenantContext> CONTEXT = new ThreadLocal<>();

    private TenantContextHolder() {
    }

    public static void set(TenantContext tenantContext) {
        CONTEXT.set(tenantContext);
    }

    public static TenantContext get() {
        return CONTEXT.get();
    }

    public static TenantContext getRequired() {
        TenantContext context = CONTEXT.get();
        if (context == null) {
            throw new TenantResolutionException("Tenant context is not available for the current request");
        }
        return context;
    }

    public static String getCurrentSchemaOrDefault(String defaultSchema) {
        TenantContext context = CONTEXT.get();
        if (context == null || context.schemaName() == null || context.schemaName().isBlank()) {
            return defaultSchema;
        }
        return context.schemaName();
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
```

### `tenancy/context/TenantContext.java`

```java
package com.financesystem.finance_api.common.tenancy.context;

public record TenantContext(
        String tenantSlug,
        String schemaName,
        boolean publicRequest
) {
    /**
     * Backward-compatible alias for older compiled code that still expects a publicSchema accessor.
     * The current model uses schemaName for both public and tenant contexts.
     */
    public String publicSchema() {
        return schemaName;
    }
}

```

### `tenancy/datasource/ReadOnlyDataSourceConfig.java`

```java
package com.financesystem.finance_api.common.tenancy.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Read-only datasources used exclusively by the reporting executors.
 *
 * <p>Two pools, two roles (defense in depth):
 * <ul>
 *   <li>{@code tenantReadOnlyDataSource} → role {@code finance_tenant_readonly}
 *       (SELECT only on tenant {@code reporting_*} views).</li>
 *   <li>{@code platformReadOnlyDataSource} → role {@code finance_platform_readonly}
 *       (SELECT only on {@code reporting.*} cross-tenant views).</li>
 * </ul>
 *
 * <p>The {@code search_path} is NOT set here; each execution fixes it per
 * transaction with {@code SET LOCAL} (Fase 2). Pools are lazy
 * ({@code initializationFailTimeout = -1}, {@code minimumIdle = 0}) so the
 * application still boots if the read-only roles do not exist yet (e.g. before
 * the V9 migration runs on a fresh database).
 */
@Configuration
public class ReadOnlyDataSourceConfig {

    @Bean(name = "tenantReadOnlyDataSource")
    public DataSource tenantReadOnlyDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${TENANT_READONLY_DB_USER:finance_tenant_readonly}") String username,
            @Value("${TENANT_READONLY_DB_PASSWORD:tenant_ro_pass}") String password
    ) {
        return buildReadOnlyPool(url, username, password, "reporting-tenant-readonly-pool");
    }

    @Bean(name = "platformReadOnlyDataSource")
    public DataSource platformReadOnlyDataSource(
            @Value("${spring.datasource.url}") String url,
            @Value("${PLATFORM_READONLY_DB_USER:finance_platform_readonly}") String username,
            @Value("${PLATFORM_READONLY_DB_PASSWORD:platform_ro_pass}") String password
    ) {
        return buildReadOnlyPool(url, username, password, "reporting-platform-readonly-pool");
    }

    private HikariDataSource buildReadOnlyPool(String url, String username, String password, String poolName) {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl(url);
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setPoolName(poolName);
        ds.setReadOnly(true);
        ds.setMaximumPoolSize(5);
        ds.setMinimumIdle(0);
        // Lazy: do not fail application startup if the role is not provisioned yet.
        ds.setInitializationFailTimeout(-1);
        return ds;
    }
}

```

### `tenancy/datasource/TenantAwareDataSource.java`

```java
package com.financesystem.finance_api.common.tenancy.datasource;

import com.financesystem.finance_api.common.tenancy.TenancyProperties;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.common.tenancy.exception.TenantResolutionException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.jdbc.datasource.AbstractDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TenantAwareDataSource extends AbstractDataSource {

    private final DataSource delegate;
    private final TenancyProperties tenancyProperties;

    public TenantAwareDataSource(DataSource delegate, TenancyProperties tenancyProperties) {
        this.delegate = delegate;
        this.tenancyProperties = tenancyProperties;
    }

    @Override
    public Connection getConnection() throws SQLException {
        String schemaName = resolveSchemaName();
        Connection connection = delegate.getConnection();
        applySchema(connection, schemaName);
        return connection;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        String schemaName = resolveSchemaName();
        Connection connection = delegate.getConnection(username, password);
        applySchema(connection, schemaName);
        return connection;
    }

    private void applySchema(Connection connection, String schemaName) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("SET search_path TO " + schemaName + ", " + tenancyProperties.getPublicSchema());
        }
    }

    private String resolveSchemaName() {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return TenantContextHolder.getCurrentSchemaOrDefault(tenancyProperties.getPublicSchema());
        }

        String requestPath = request.getRequestURI();
        if (tenancyProperties.usesPublicSchema(requestPath)) {
            return tenancyProperties.getPublicSchema();
        }

        TenantContext tenantContext = TenantContextHolder.get();
        if (tenantContext == null
                || tenantContext.publicRequest()
                || tenantContext.schemaName() == null
                || tenantContext.schemaName().isBlank()
                || tenancyProperties.getPublicSchema().equalsIgnoreCase(tenantContext.schemaName())) {
            throw new TenantResolutionException(
                    "Tenant header '" + tenancyProperties.getHeaderName() + "' is missing or invalid for request path '" +
                            requestPath + "'. Verify the tenant slug matches an existing tenant before retrying."
            );
        }

        return tenantContext.schemaName();
    }

    private HttpServletRequest currentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }

        return servletRequestAttributes.getRequest();
    }
}

```

### `tenancy/datasource/TenantDataSourceConfig.java`

```java
package com.financesystem.finance_api.common.tenancy.datasource;

import com.financesystem.finance_api.common.tenancy.TenancyProperties;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class TenantDataSourceConfig {

    @Bean(name = "targetDataSource")
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource targetDataSource(DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Primary
    @Bean(name = "dataSource")
    public DataSource tenantAwareDataSource(
            @Qualifier("targetDataSource") DataSource targetDataSource,
            TenancyProperties tenancyProperties
    ) {
        return new TenantAwareDataSource(targetDataSource, tenancyProperties);
    }
}
```

### `tenancy/exception/TenantResolutionException.java`

```java
package com.financesystem.finance_api.common.tenancy.exception;

public class TenantResolutionException extends RuntimeException {

    public TenantResolutionException(String message) {
        super(message);
    }
}
```

### `tenancy/interceptor/TenantContextInterceptor.java`

```java
package com.financesystem.finance_api.common.tenancy.interceptor;

import com.financesystem.finance_api.common.tenancy.TenancyProperties;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import com.financesystem.finance_api.common.tenancy.resolver.TenantResolver;
import com.financesystem.finance_api.common.tenancy.validation.TenantSchemaReadinessService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantContextInterceptor implements HandlerInterceptor {

    private final TenancyProperties tenancyProperties;
    private final TenantResolver tenantResolver;
    private final TenantSchemaReadinessService tenantSchemaReadinessService;

    public TenantContextInterceptor(
            TenancyProperties tenancyProperties,
            TenantResolver tenantResolver,
            TenantSchemaReadinessService tenantSchemaReadinessService
    ) {
        this.tenancyProperties = tenancyProperties;
        this.tenantResolver = tenantResolver;
        this.tenantSchemaReadinessService = tenantSchemaReadinessService;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) {
        String requestPath = request.getRequestURI();

        if (tenancyProperties.usesPublicSchema(requestPath)) {
            TenantContextHolder.set(new TenantContext(
                    null,
                    tenancyProperties.getPublicSchema(),
                    true
            ));
            return true;
        }

        TenantContext resolvedTenant = tenantResolver.resolve(request);
        tenantSchemaReadinessService.assertTenantSchemaReady(
                resolvedTenant.schemaName(),
                tenancyProperties.getHeaderName(),
                resolvedTenant.tenantSlug()
        );
        TenantContextHolder.set(resolvedTenant);
        return true;
    }

    @Override
    public void afterCompletion(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler,
            Exception ex
    ) {
        TenantContextHolder.clear();
    }
}

```

### `tenancy/maintenance/TenantMaintenanceInterceptor.java`

```java
package com.financesystem.finance_api.common.tenancy.maintenance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financesystem.finance_api.common.response.ApiErrorResponse;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.context.TenantContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
public class TenantMaintenanceInterceptor implements HandlerInterceptor {

    private final TenantMaintenanceService tenantMaintenanceService;
    private final ObjectMapper objectMapper;

    public TenantMaintenanceInterceptor(
            TenantMaintenanceService tenantMaintenanceService,
            ObjectMapper objectMapper
    ) {
        this.tenantMaintenanceService = tenantMaintenanceService;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull Object handler
    ) throws Exception {
        TenantContext context;
        try {
            context = TenantContextHolder.getRequired();
        } catch (Exception exception) {
            return true;
        }

        if (context.publicRequest() || context.tenantSlug() == null) {
            return true;
        }

        if (!tenantMaintenanceService.isTenantInMaintenance(context.tenantSlug())) {
            return true;
        }

        if (request.getRequestURI().startsWith("/api/backups")) {
            return true;
        }

        String reason = tenantMaintenanceService.getMaintenanceReason(context.tenantSlug())
                .orElse("Tenant is temporarily unavailable due to maintenance");

        response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiErrorResponse body = ApiErrorResponse.of(
                "Tenant is temporarily unavailable due to maintenance",
                List.of(reason)
        );

        objectMapper.writeValue(response.getOutputStream(), body);
        return false;
    }
}

```

### `tenancy/maintenance/TenantMaintenanceService.java`

```java
package com.financesystem.finance_api.common.tenancy.maintenance;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Optional;

@Service
public class TenantMaintenanceService {

    private final JdbcTemplate jdbcTemplate;

    public TenantMaintenanceService(@Qualifier("targetDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public boolean isTenantInMaintenance(String tenantSlug) {
        Boolean result = jdbcTemplate.queryForObject(
                """
                SELECT maintenance_mode
                FROM public.platform_tenants
                WHERE slug = ?
                """,
                Boolean.class,
                tenantSlug
        );
        return Boolean.TRUE.equals(result);
    }

    public Optional<String> getMaintenanceReason(String tenantSlug) {
        return jdbcTemplate.query(
                """
                SELECT maintenance_reason
                FROM public.platform_tenants
                WHERE slug = ?
                """,
                rs -> rs.next() ? Optional.ofNullable(rs.getString("maintenance_reason")) : Optional.empty(),
                tenantSlug
        );
    }

    public void enableMaintenance(String tenantSlug, String reason) {
        jdbcTemplate.update(
                """
                UPDATE public.platform_tenants
                SET maintenance_mode = true,
                    maintenance_reason = ?,
                    maintenance_started_at = NOW(),
                    updated_at = NOW()
                WHERE slug = ?
                """,
                reason,
                tenantSlug
        );
    }

    public void disableMaintenance(String tenantSlug) {
        jdbcTemplate.update(
                """
                UPDATE public.platform_tenants
                SET maintenance_mode = false,
                    maintenance_reason = NULL,
                    maintenance_started_at = NULL,
                    updated_at = NOW()
                WHERE slug = ?
                """,
                tenantSlug
        );
    }
}

```

### `tenancy/migration/TenantSchemaMigrationService.java`

```java
package com.financesystem.finance_api.common.tenancy.migration;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
public class TenantSchemaMigrationService {

    private static final Logger logger = LoggerFactory.getLogger(TenantSchemaMigrationService.class);

    private final DataSource targetDataSource;
    private final JdbcTemplate jdbcTemplate;

    public TenantSchemaMigrationService(@Qualifier("targetDataSource") DataSource targetDataSource) {
        this.targetDataSource = targetDataSource;
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
    }

    public void migrateRegisteredTenantSchemas() {
        List<String> schemaNames = jdbcTemplate.queryForList(
                """
                SELECT schema_name
                FROM public.platform_tenants
                WHERE active = true
                ORDER BY created_at ASC
                """,
                String.class
        );

        if (schemaNames.isEmpty()) {
            logger.info("No registered tenant schemas found to migrate.");
            return;
        }

        for (String schemaName : schemaNames) {
            migrateSchema(schemaName);
        }
    }

    public void migrateSchema(String schemaName) {
        validateSchemaName(schemaName);

        logger.info("Running tenant Flyway migrations for schema '{}'.", schemaName);

        Flyway flyway = Flyway.configure()
                .dataSource(targetDataSource)
                .locations("classpath:db/migration/tenant")
                .schemas(schemaName)
                .defaultSchema(schemaName)
                .baselineOnMigrate(true)
                .createSchemas(true)
                .load();

        flyway.migrate();

        logger.info("Tenant Flyway migrations completed for schema '{}'.", schemaName);
    }

    private void validateSchemaName(String schemaName) {
        if (schemaName == null || schemaName.isBlank()) {
            throw new IllegalArgumentException("Schema name must not be blank");
        }

        if (!schemaName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Schema name contains invalid characters: " + schemaName);
        }
    }
}
```

### `tenancy/reporting/ReportingSecurityService.java`

```java
package com.financesystem.finance_api.common.tenancy.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

/**
 * Keeps the reporting security layer in sync as tenants are provisioned,
 * backfilled or restored.
 *
 * <p>Two responsibilities:
 * <ul>
 *   <li>Grant the read-only role {@code finance_tenant_readonly} SELECT on a
 *       tenant's {@code reporting_*} views (USAGE on the schema, SELECT on the
 *       views only — never on raw tables).</li>
 *   <li>Re-run {@code reporting.regenerate_views()} so the cross-tenant
 *       platform views (read by {@code finance_platform_readonly}) reflect the
 *       current set of tenants.</li>
 * </ul>
 *
 * <p>All statements run on the privileged {@code targetDataSource} (the app
 * user owns the schemas and can GRANT); never on a read-only datasource.
 */
@Service
public class ReportingSecurityService {

    private static final Logger logger = LoggerFactory.getLogger(ReportingSecurityService.class);

    private static final String TENANT_READONLY_ROLE = "finance_tenant_readonly";

    private final JdbcTemplate jdbcTemplate;

    public ReportingSecurityService(@Qualifier("targetDataSource") DataSource targetDataSource) {
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
    }

    /**
     * Apply per-view grants for one tenant schema and refresh the platform views.
     * Safe to call repeatedly (grants are idempotent).
     */
    public void applyTenantSecurity(String schemaName) {
        applyTenantGrants(schemaName);
        regeneratePlatformViews();
    }

    /**
     * Grant USAGE on the schema and SELECT on each existing {@code reporting_*}
     * view to the tenant read-only role. Only views that actually exist are
     * granted, so a tenant with an incomplete migration does not abort the run.
     */
    public void applyTenantGrants(String schemaName) {
        String schema = requireSafeSchema(schemaName);

        List<String> views = jdbcTemplate.queryForList(
                """
                SELECT table_name
                FROM information_schema.views
                WHERE table_schema = ?
                  AND table_name LIKE 'reporting\\_%'
                """,
                String.class,
                schema
        );

        if (views.isEmpty()) {
            logger.warn("No reporting_* views found in schema '{}'; skipping grants.", schema);
            return;
        }

        jdbcTemplate.execute("GRANT USAGE ON SCHEMA \"" + schema + "\" TO " + TENANT_READONLY_ROLE);
        for (String view : views) {
            // view names come from information_schema and match reporting_[a-z_]+; quoted for safety.
            jdbcTemplate.execute(
                    "GRANT SELECT ON \"" + schema + "\".\"" + view + "\" TO " + TENANT_READONLY_ROLE);
        }
        logger.info("Applied reporting read-only grants on schema '{}' ({} views).", schema, views.size());
    }

    /** Rebuild the cross-tenant {@code reporting.*} views (UNION ALL + tenant_slug). */
    public void regeneratePlatformViews() {
        jdbcTemplate.execute("SELECT reporting.regenerate_views()");
        logger.info("Regenerated cross-tenant reporting views.");
    }

    /**
     * Re-apply grants for every active tenant and regenerate the platform views.
     * Used at startup (after tenant migrations) and after a full-database restore.
     */
    public void backfillRegisteredTenants() {
        List<String> schemas = jdbcTemplate.queryForList(
                "SELECT schema_name FROM public.platform_tenants WHERE active = true ORDER BY created_at ASC",
                String.class
        );

        for (String schema : schemas) {
            try {
                applyTenantGrants(schema);
            } catch (Exception e) {
                logger.warn("Failed to apply reporting grants for schema '{}': {}", schema, e.getMessage());
            }
        }

        regeneratePlatformViews();
        logger.info("Reporting security backfill completed for {} tenant schema(s).", schemas.size());
    }

    private String requireSafeSchema(String schemaName) {
        if (schemaName == null || !schemaName.matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("Invalid schema name for reporting grants: " + schemaName);
        }
        return schemaName;
    }
}

```

### `tenancy/resolver/HeaderTenantResolver.java`

```java
package com.financesystem.finance_api.common.tenancy.resolver;

import com.financesystem.finance_api.common.tenancy.TenancyProperties;
import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import com.financesystem.finance_api.common.tenancy.exception.TenantResolutionException;
import com.financesystem.finance_api.common.tenancy.schema.TenantSchemaNamingStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class HeaderTenantResolver implements TenantResolver {

    private final TenancyProperties tenancyProperties;
    private final TenantSchemaNamingStrategy tenantSchemaNamingStrategy;

    public HeaderTenantResolver(
            TenancyProperties tenancyProperties,
            TenantSchemaNamingStrategy tenantSchemaNamingStrategy
    ) {
        this.tenancyProperties = tenancyProperties;
        this.tenantSchemaNamingStrategy = tenantSchemaNamingStrategy;
    }

    @Override
    public TenantContext resolve(HttpServletRequest request) {
        String rawTenantSlug = request.getHeader(tenancyProperties.getHeaderName());

        if (!StringUtils.hasText(rawTenantSlug)) {
            throw new TenantResolutionException(
                    "Required tenant header '" + tenancyProperties.getHeaderName() + "' is missing"
            );
        }

        String normalizedSlug = tenantSchemaNamingStrategy.normalizeSlug(rawTenantSlug);

        String schemaName = tenantSchemaNamingStrategy.toSchemaName(normalizedSlug);

        return new TenantContext(normalizedSlug, schemaName, false);
    }
}

```

### `tenancy/resolver/TenantResolver.java`

```java
package com.financesystem.finance_api.common.tenancy.resolver;

import com.financesystem.finance_api.common.tenancy.context.TenantContext;
import jakarta.servlet.http.HttpServletRequest;

public interface TenantResolver {

    TenantContext resolve(HttpServletRequest request);
}
```

### `tenancy/schema/TenantSchemaNamingStrategy.java`

```java
package com.financesystem.finance_api.common.tenancy.schema;

import com.financesystem.finance_api.common.tenancy.TenancyProperties;
import com.financesystem.finance_api.common.tenancy.exception.TenantResolutionException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TenantSchemaNamingStrategy {

    private final TenancyProperties tenancyProperties;

    public TenantSchemaNamingStrategy(TenancyProperties tenancyProperties) {
        this.tenancyProperties = tenancyProperties;
    }

    public String normalizeSlug(String rawSlug) {
        if (!StringUtils.hasText(rawSlug)) {
            throw new TenantResolutionException("Tenant slug header is required");
        }

        String normalized = rawSlug.trim()
                .toLowerCase()
                .replace(" ", "-")
                .replaceAll("[^a-z0-9\\-_]", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("_{2,}", "_")
                .replaceAll("^-+", "")
                .replaceAll("-+$", "");

        if (!StringUtils.hasText(normalized)) {
            throw new TenantResolutionException("Tenant slug is invalid");
        }

        if (!normalized.matches("^[a-z0-9][a-z0-9\\-_]{1,62}$")) {
            throw new TenantResolutionException("Tenant slug format is invalid");
        }

        return normalized;
    }

    public String toSchemaName(String tenantSlug) {
        String normalizedSlug = normalizeSlug(tenantSlug);
        String safeSchemaSuffix = normalizedSlug.replace("-", "_");
        return tenancyProperties.getTenantSchemaPrefix() + safeSchemaSuffix;
    }
}
```

### `tenancy/TenancyProperties.java`

```java
package com.financesystem.finance_api.common.tenancy;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.tenancy")
public class TenancyProperties {

    private String headerName = "X-Tenant-Slug";
    private String publicSchema = "public";
    private String tenantSchemaPrefix = "tenant_";
    private List<String> publicPaths = new ArrayList<>();
    private List<String> globalPaths = new ArrayList<>();

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getPublicSchema() {
        return publicSchema;
    }

    public void setPublicSchema(String publicSchema) {
        this.publicSchema = publicSchema;
    }

    public String getTenantSchemaPrefix() {
        return tenantSchemaPrefix;
    }

    public void setTenantSchemaPrefix(String tenantSchemaPrefix) {
        this.tenantSchemaPrefix = tenantSchemaPrefix;
    }

    public List<String> getPublicPaths() {
        return publicPaths;
    }

    public void setPublicPaths(List<String> publicPaths) {
        this.publicPaths = publicPaths;
    }

    public List<String> getGlobalPaths() {
        return globalPaths;
    }

    public void setGlobalPaths(List<String> globalPaths) {
        this.globalPaths = globalPaths;
    }

    public boolean isPublicPath(String requestPath) {
        return matches(requestPath, publicPaths);
    }

    public boolean isGlobalPath(String requestPath) {
        return matches(requestPath, globalPaths);
    }

    public boolean usesPublicSchema(String requestPath) {
        return isPublicPath(requestPath) || isGlobalPath(requestPath);
    }

    private boolean matches(String requestPath, List<String> patterns) {
        if (!StringUtils.hasText(requestPath)) {
            return false;
        }

        AntPathMatcher matcher = new AntPathMatcher();
        return patterns.stream().anyMatch(pattern -> matcher.match(pattern, requestPath));
    }
}
```

### `tenancy/validation/TenantSchemaReadinessService.java`

```java
package com.financesystem.finance_api.common.tenancy.validation;

import com.financesystem.finance_api.common.tenancy.exception.TenantResolutionException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;

@Service
public class TenantSchemaReadinessService {

    private static final List<String> REQUIRED_TABLES = List.of(
            "tenant_users",
            "tenant_roles",
            "tenant_user_roles",
            "tenant_role_permissions"
    );

    private final JdbcTemplate jdbcTemplate;

    public TenantSchemaReadinessService(@Qualifier("targetDataSource") DataSource targetDataSource) {
        this.jdbcTemplate = new JdbcTemplate(targetDataSource);
    }

    public void assertTenantSchemaReady(String schemaName, String tenantHeaderName, String tenantSlug) {
        if (!StringUtils.hasText(schemaName)) {
            throw new TenantResolutionException("Tenant schema is not available");
        }

        if (!schemaExists(schemaName)) {
            throw new TenantResolutionException(
                    "Tenant schema '" + schemaName + "' was not found for tenant slug '" + tenantSlug + "'. " +
                            "Check the '" + tenantHeaderName + "' header for typos and verify the tenant was created."
            );
        }

        List<String> missingTables = REQUIRED_TABLES.stream()
                .filter(tableName -> !tableExists(schemaName, tableName))
                .toList();

        if (!missingTables.isEmpty()) {
            throw new TenantResolutionException(
                    "Tenant schema '" + schemaName + "' is not ready for tenant slug '" + tenantSlug + "'. " +
                            "Missing required tables: " + String.join(", ", missingTables) + ". " +
                            "Run tenant migrations before retrying."
            );
        }
    }

    private boolean schemaExists(String schemaName) {
        Integer schemaCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.schemata
                WHERE schema_name = ?
                """,
                Integer.class,
                schemaName
        );

        return schemaCount != null && schemaCount > 0;
    }

    private boolean tableExists(String schemaName, String tableName) {
        Integer tableCount = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = ? AND table_name = ?
                """,
                Integer.class,
                schemaName,
                tableName
        );

        return tableCount != null && tableCount > 0;
    }
}

```

