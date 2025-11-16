/**
 * API response types matching backend DTOs.
 * These types represent the structure of responses from the backend API.
 */

/**
 * User role enum.
 * Matches the backend UserRole enum.
 */
export type UserRole = 'GUEST' | 'STUDENT' | 'TEACHER' | 'ADMIN';

/**
 * Role metadata for Discord-style UI display.
 */
export interface RoleMetadata {
  displayName: string;
  color: string;
}

/**
 * Session information response from GET /auth/session.
 * Contains authenticated user's profile and linked OAuth providers.
 */
export interface SessionInfoResponse {
  user_id: number;
  nickname: string;
  email: string;
  level: number;
  role: UserRole; // User's role: GUEST, STUDENT, TEACHER, or ADMIN
  oauth_providers: string[]; // e.g., ["google", "facebook"]
}

/**
 * Token response from POST /auth/refresh.
 * Contains the new access token and its metadata.
 */
export interface TokenResponse {
  access_token: string;
  token_type: string; // "Bearer"
  expires_in: number; // Seconds until expiration
}

/**
 * Standard error response from the API.
 * Returned for all error conditions (4xx, 5xx).
 */
export interface ErrorResponse {
  code: string; // Error code (e.g., "UNAUTHORIZED", "VALIDATION_ERROR")
  message: string; // User-friendly error message in Chinese
  details?: Record<string, any>; // Optional additional context
}

/**
 * Refresh token request for POST /auth/refresh.
 */
export interface RefreshTokenRequest {
  refresh_token: string;
}
