/**
 * localStorage utility for preserving form data during session expiry.
 *
 * Purpose:
 * - Save unsaved profile edits when session expires
 * - Restore form data after user re-authenticates
 * - Persist data even if browser closes
 *
 * Use Case:
 * 1. User editing profile
 * 2. Session expires (7 days)
 * 3. User redirected to login
 * 4. After login, form data restored from localStorage
 *
 * Security Note:
 * - Only stores non-sensitive form data (not passwords)
 * - Cleared after successful save
 */

const PROFILE_DRAFT_KEY = 'profile_draft';
const REDIRECT_AFTER_LOGIN_KEY = 'redirect_after_login';

/**
 * Save profile form data to localStorage.
 *
 * @param data Profile form data
 */
export function saveProfileDraft(data: Record<string, any>): void {
  try {
    const draft = {
      data,
      timestamp: new Date().toISOString(),
    };

    localStorage.setItem(PROFILE_DRAFT_KEY, JSON.stringify(draft));
  } catch (error) {
    console.error('Failed to save profile draft:', error);
  }
}

/**
 * Restore profile form data from localStorage.
 *
 * @returns Saved profile data or null
 */
export function restoreProfileDraft(): Record<string, any> | null {
  try {
    const draftJson = localStorage.getItem(PROFILE_DRAFT_KEY);

    if (!draftJson) {
      return null;
    }

    const draft = JSON.parse(draftJson);

    // Check if draft is recent (within 24 hours)
    const timestamp = new Date(draft.timestamp);
    const hoursSinceSave = (Date.now() - timestamp.getTime()) / (1000 * 60 * 60);

    if (hoursSinceSave > 24) {
      // Draft too old, clear it
      clearProfileDraft();
      return null;
    }

    return draft.data;
  } catch (error) {
    console.error('Failed to restore profile draft:', error);
    return null;
  }
}

/**
 * Clear saved profile draft after successful save.
 */
export function clearProfileDraft(): void {
  try {
    localStorage.removeItem(PROFILE_DRAFT_KEY);
  } catch (error) {
    console.error('Failed to clear profile draft:', error);
  }
}

/**
 * Save current page URL for redirect after login.
 *
 * @param url Current page URL
 */
export function saveRedirectUrl(url: string): void {
  try {
    localStorage.setItem(REDIRECT_AFTER_LOGIN_KEY, url);
  } catch (error) {
    console.error('Failed to save redirect URL:', error);
  }
}

/**
 * Get saved redirect URL and clear it.
 *
 * @returns Saved URL or null
 */
export function getAndClearRedirectUrl(): string | null {
  try {
    const url = localStorage.getItem(REDIRECT_AFTER_LOGIN_KEY);

    if (url) {
      localStorage.removeItem(REDIRECT_AFTER_LOGIN_KEY);
    }

    return url;
  } catch (error) {
    console.error('Failed to get redirect URL:', error);
    return null;
  }
}

/**
 * Save JWT tokens to localStorage.
 *
 * @param accessToken Access token (15-minute expiry)
 * @param refreshToken Refresh token (7-day expiry)
 */
export function saveTokens(accessToken: string, refreshToken: string): void {
  try {
    localStorage.setItem('access_token', accessToken);
    localStorage.setItem('refresh_token', refreshToken);
  } catch (error) {
    console.error('Failed to save tokens:', error);
  }
}

/**
 * Clear JWT tokens from localStorage.
 */
export function clearTokens(): void {
  try {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
  } catch (error) {
    console.error('Failed to clear tokens:', error);
  }
}

/**
 * Get access token from localStorage.
 *
 * @returns Access token or null
 */
export function getAccessToken(): string | null {
  try {
    return localStorage.getItem('access_token');
  } catch (error) {
    console.error('Failed to get access token:', error);
    return null;
  }
}

/**
 * Get refresh token from localStorage.
 *
 * @returns Refresh token or null
 */
export function getRefreshToken(): string | null {
  try {
    return localStorage.getItem('refresh_token');
  } catch (error) {
    console.error('Failed to get refresh token:', error);
    return null;
  }
}
