/**
 * User type representing authenticated user data.
 * Matches the backend User entity structure.
 */
export interface User {
  user_id: number;
  nickname: string;
  email: string;
  gender?: string;
  birthday?: string; // ISO date string (YYYY-MM-DD)
  location?: string;
  occupation?: string;
  github_link?: string;
  level: number;
  oauth_providers: string[]; // e.g., ["google", "facebook"]
  created_at: string; // ISO datetime string
  updated_at: string; // ISO datetime string
}

/**
 * OAuth provider types supported by the platform.
 */
export type OAuthProvider = 'google' | 'facebook';

/**
 * OAuth provider link information.
 */
export interface OAuthProviderLink {
  provider_type: OAuthProvider;
  provider_email: string;
  linked_at: string; // ISO datetime string
}
