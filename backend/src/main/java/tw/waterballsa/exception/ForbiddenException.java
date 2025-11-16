package tw.waterballsa.exception;

/**
 * Exception thrown when user lacks permission for requested resource.
 *
 * Examples:
 * - User tries to access another user's order
 * - User tries to view another user's profile
 * - User tries to unlink account that belongs to another user
 *
 * HTTP Status: 403 Forbidden
 */
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(String message, Throwable cause) {
        super(message, cause);
    }
}
