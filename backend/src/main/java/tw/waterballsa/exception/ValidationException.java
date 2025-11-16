package tw.waterballsa.exception;

/**
 * Exception thrown when input validation fails.
 *
 * Examples:
 * - Invalid GitHub URL format
 * - Invalid gender value
 * - Required field missing
 * - Invalid email format
 *
 * HTTP Status: 400 Bad Request
 */
public class ValidationException extends RuntimeException {

    private final String field;
    private final Object invalidValue;

    public ValidationException(String message) {
        super(message);
        this.field = null;
        this.invalidValue = null;
    }

    public ValidationException(String message, String field) {
        super(message);
        this.field = field;
        this.invalidValue = null;
    }

    public ValidationException(String message, String field, Object invalidValue) {
        super(message);
        this.field = field;
        this.invalidValue = invalidValue;
    }

    public String getField() {
        return field;
    }

    public Object getInvalidValue() {
        return invalidValue;
    }
}
