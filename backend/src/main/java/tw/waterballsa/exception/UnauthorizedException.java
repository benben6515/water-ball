package tw.waterballsa.exception;

/**
 * Exception thrown when authentication fails or is missing.
 *
 * Examples:
 * - Invalid JWT token
 * - Expired JWT token
 * - Missing Authorization header
 * - OAuth authentication failed
 * - Invalid refresh token
 *
 * HTTP Status: 401 Unauthorized
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
