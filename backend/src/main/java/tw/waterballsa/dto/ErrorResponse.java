package tw.waterballsa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

/**
 * Standard error response DTO.
 *
 * Format:
 * {
 *   "code": "ERROR_CODE",
 *   "message": "User-friendly error message in Chinese",
 *   "details": { ... } // Optional additional context
 * }
 *
 * Examples:
 * - VALIDATION_ERROR: Input validation failed
 * - UNAUTHORIZED: Authentication required
 * - FORBIDDEN: Permission denied
 * - NOT_FOUND: Resource not found
 * - INTERNAL_ERROR: Server error
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String code;
    private String message;
    private Map<String, Object> details;

    public ErrorResponse() {
    }

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorResponse(String code, String message, Map<String, Object> details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    // Getters and Setters
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}
