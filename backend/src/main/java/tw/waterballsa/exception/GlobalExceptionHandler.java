package tw.waterballsa.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tw.waterballsa.dto.ErrorResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for REST API.
 *
 * Handles all exceptions and returns consistent error responses in Chinese.
 *
 * Error Response Format:
 * {
 *   "code": "ERROR_CODE",
 *   "message": "錯誤訊息 (Chinese error message)",
 *   "details": { ... }
 * }
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle validation errors (400 Bad Request).
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        logger.warn("Validation error: {}", ex.getMessage());

        Map<String, Object> details = new HashMap<>();
        if (ex.getField() != null) {
            details.put("field", ex.getField());
        }
        if (ex.getInvalidValue() != null) {
            details.put("invalid_value", ex.getInvalidValue());
        }

        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
                ex.getMessage(),
                details.isEmpty() ? null : details
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle Spring Validation errors (400 Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        logger.warn("Method argument validation failed: {}", ex.getMessage());

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorResponse error = new ErrorResponse(
                "VALIDATION_ERROR",
                "請求參數驗證失敗",
                Map.of("field_errors", fieldErrors)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle authentication errors (401 Unauthorized).
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        logger.warn("Unauthorized access attempt: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "UNAUTHORIZED",
                ex.getMessage() != null ? ex.getMessage() : "請先登入",
                null
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle permission errors (403 Forbidden).
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(ForbiddenException ex) {
        logger.warn("Forbidden access attempt: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                "FORBIDDEN",
                ex.getMessage() != null ? ex.getMessage() : "您無權訪問此資源",
                null
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Handle resource not found errors (404 Not Found).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        logger.warn("Resource not found: {}", ex.getMessage());

        Map<String, Object> details = new HashMap<>();
        if (ex.getResourceType() != null) {
            details.put("resource_type", ex.getResourceType());
        }
        if (ex.getResourceId() != null) {
            details.put("resource_id", ex.getResourceId());
        }

        ErrorResponse error = new ErrorResponse(
                "NOT_FOUND",
                ex.getMessage() != null ? ex.getMessage() : "找不到請求的資源",
                details.isEmpty() ? null : details
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle all other unhandled exceptions (500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        logger.error("Unhandled exception", ex);

        ErrorResponse error = new ErrorResponse(
                "INTERNAL_ERROR",
                "伺服器內部錯誤，請稍後再試",
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
