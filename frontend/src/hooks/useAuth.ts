'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import api, { getErrorMessage } from '@/lib/api';
import { clearTokens, saveTokens, getAccessToken } from '@/lib/storage';

/**
 * User session information.
 */
export interface User {
  user_id: string;
  nickname: string;
  email: string;
  level: number;
  oauth_providers: string[];
}

/**
 * Auth hook return type.
 */
export interface UseAuthReturn {
  user: User | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  login: (accessToken: string, refreshToken: string) => Promise<void>;
  logout: () => Promise<void>;
  refreshSession: () => Promise<void>;
}

/**
 * Custom hook for authentication and session management.
 *
 * Features:
 * - Get current user session
 * - Login with tokens
 * - Logout and clear session
 * - Refresh session data
 * - Auto-redirect on 401 errors
 *
 * Usage:
 * ```typescript
 * const { user, isAuthenticated, logout } = useAuth();
 *
 * if (!isAuthenticated) {
 *   return <div>請先登入</div>;
 * }
 *
 * return <div>歡迎, {user.nickname}</div>;
 * ```
 */
export function useAuth(): UseAuthReturn {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const router = useRouter();

  /**
   * Fetch current user session from backend.
   */
  const fetchSession = async () => {
    try {
      const accessToken = getAccessToken();

      if (!accessToken) {
        setUser(null);
        setIsLoading(false);
        return;
      }

      const response = await api.get('/auth/session');
      setUser(response.data);
    } catch (error) {
      console.error('Failed to fetch session:', getErrorMessage(error));
      setUser(null);

      // Clear invalid tokens
      clearTokens();
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * Login with access and refresh tokens.
   *
   * @param accessToken JWT access token
   * @param refreshToken JWT refresh token
   */
  const login = async (accessToken: string, refreshToken: string) => {
    try {
      // Save tokens to localStorage
      saveTokens(accessToken, refreshToken);

      // Fetch user session
      await fetchSession();
    } catch (error) {
      console.error('Login failed:', getErrorMessage(error));
      throw error;
    }
  };

  /**
   * Logout user and clear session.
   */
  const logout = async () => {
    try {
      // Call backend logout endpoint
      await api.post('/auth/logout');
    } catch (error) {
      console.error('Logout failed:', getErrorMessage(error));
      // Continue with local logout even if backend call fails
    } finally {
      // Clear tokens from localStorage
      clearTokens();

      // Clear user state
      setUser(null);

      // Redirect to login page
      router.push('/login');
    }
  };

  /**
   * Refresh user session data.
   */
  const refreshSession = async () => {
    setIsLoading(true);
    await fetchSession();
  };

  /**
   * Fetch session on component mount.
   */
  useEffect(() => {
    fetchSession();
  }, []);

  return {
    user,
    isLoading,
    isAuthenticated: user !== null,
    login,
    logout,
    refreshSession,
  };
}
