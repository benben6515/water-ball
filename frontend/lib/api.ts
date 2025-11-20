import axios, { AxiosInstance, AxiosRequestConfig, AxiosError } from 'axios';

/**
 * API Client utility with JWT token management.
 *
 * Features:
 * - Automatic JWT token injection in Authorization header
 * - Token refresh on 401 errors
 * - Error handling with Chinese messages
 * - Base URL configuration
 *
 * Usage:
 * ```typescript
 * import api from '@/lib/api';
 *
 * const response = await api.get('/profile');
 * const data = await api.post('/profile', { nickname: 'John' });
 * ```
 */

const API_BASE_URL = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

/**
 * Create Axios instance with default configuration.
 */
const api: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000, // 10 seconds
  headers: {
    'Content-Type': 'application/json',
  },
  withCredentials: true, // Enable cookies for refresh tokens
});

/**
 * Request interceptor - Add JWT token to requests.
 */
api.interceptors.request.use(
  (config) => {
    // Get access token from localStorage
    const accessToken = localStorage.getItem('access_token');

    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

/**
 * Response interceptor - Handle errors and token refresh.
 */
api.interceptors.response.use(
  (response) => {
    // Return successful response
    return response;
  },
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };

    // Handle 401 Unauthorized - attempt token refresh
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        // Attempt to refresh token
        const refreshToken = localStorage.getItem('refresh_token');

        if (refreshToken) {
          const response = await axios.post(
            `${API_BASE_URL}/auth/refresh`,
            { refresh_token: refreshToken },
            { withCredentials: true }
          );

          const { access_token } = response.data;

          // Store new access token
          localStorage.setItem('access_token', access_token);

          // Retry original request with new token
          if (originalRequest.headers) {
            originalRequest.headers.Authorization = `Bearer ${access_token}`;
          }

          return api(originalRequest);
        }
      } catch (refreshError) {
        // Refresh failed - clear tokens and redirect to login
        localStorage.removeItem('access_token');
        localStorage.removeItem('refresh_token');

        // Save current location for redirect after login
        if (typeof window !== 'undefined') {
          localStorage.setItem('redirect_after_login', window.location.pathname);
        }

        // Redirect to login
        window.location.href = '/login';

        return Promise.reject(refreshError);
      }
    }

    // Handle other errors
    return Promise.reject(error);
  }
);

/**
 * Helper function to extract error message from API response.
 */
export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    const axiosError = error as AxiosError<{ message?: string; code?: string }>;

    // Return Chinese error message from backend
    if (axiosError.response?.data?.message) {
      return axiosError.response.data.message;
    }

    // Fallback error messages in Chinese
    switch (axiosError.response?.status) {
      case 400:
        return '請求參數錯誤';
      case 401:
        return '請先登入';
      case 403:
        return '您無權訪問此資源';
      case 404:
        return '找不到請求的資源';
      case 409:
        return '資源衝突';
      case 500:
        return '伺服器內部錯誤，請稍後再試';
      default:
        return '網路錯誤，請檢查連線';
    }
  }

  return '未知錯誤';
}

export default api;
