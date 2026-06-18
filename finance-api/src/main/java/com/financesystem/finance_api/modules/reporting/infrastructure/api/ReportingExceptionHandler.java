package com.financesystem.finance_api.modules.reporting.infrastructure.api;

import com.financesystem.finance_api.common.response.ApiErrorResponse;
import com.financesystem.finance_api.modules.reporting.application.ai.RateLimitExceededException;
import com.financesystem.finance_api.modules.reporting.application.ai.ReportsAiUnavailableException;
import com.financesystem.finance_api.modules.reporting.application.exception.ReportAccessDeniedException;
import com.financesystem.finance_api.modules.reporting.application.exception.ReportNotFoundException;
import com.financesystem.finance_api.modules.reporting.application.executor.ReportExecutionException;
import com.financesystem.finance_api.modules.reporting.application.guard.SqlGuardException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/** Maps reporting exceptions to HTTP statuses. Scoped to the reporting controllers. */
@RestControllerAdvice(assignableTypes = {TenantReportController.class, PlatformReportController.class})
public class ReportingExceptionHandler {

    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> notFound(ReportNotFoundException e) {
        return status(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ReportAccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> accessDenied(ReportAccessDeniedException e) {
        return status(HttpStatus.FORBIDDEN, e.getMessage());
    }

    @ExceptionHandler({SqlGuardException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponse> badRequest(RuntimeException e) {
        return status(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ReportExecutionException.class)
    public ResponseEntity<ApiErrorResponse> executionFailed(ReportExecutionException e) {
        return status(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage());
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> rateLimited(RateLimitExceededException e) {
        return status(HttpStatus.TOO_MANY_REQUESTS, e.getMessage());
    }

    @ExceptionHandler(ReportsAiUnavailableException.class)
    public ResponseEntity<ApiErrorResponse> aiUnavailable(ReportsAiUnavailableException e) {
        return status(HttpStatus.SERVICE_UNAVAILABLE,
                "El servicio de IA no está disponible. Probá un reporte controlado o reintentá más tarde.");
    }

    private ResponseEntity<ApiErrorResponse> status(HttpStatus status, String message) {
        String safe = (message == null || message.isBlank()) ? status.getReasonPhrase() : message;
        return ResponseEntity.status(status).body(ApiErrorResponse.of(safe, List.of(safe)));
    }
}
