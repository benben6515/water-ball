'use client';

import { useRouter } from 'next/navigation';
import { useState } from 'react';

interface LogoutButtonProps {
  className?: string;
  children?: React.ReactNode;
}

/**
 * Logout button component.
 * Calls the backend logout endpoint, clears local storage, and redirects to login page.
 *
 * Example usage:
 * <LogoutButton>登出</LogoutButton>
 * <LogoutButton className="text-red-600">Sign Out</LogoutButton>
 */
export default function LogoutButton({ className, children }: LogoutButtonProps) {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);

  const handleLogout = async () => {
    setIsLoading(true);

    try {
      const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';

      // Call backend logout endpoint
      await fetch(`${backendUrl}/auth/logout`, {
        method: 'POST',
        credentials: 'include', // Include cookies (refresh_token)
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('access_token') || ''}`,
        },
      });

      // Clear local storage
      localStorage.removeItem('access_token');
      localStorage.removeItem('user');

      // Redirect to login page
      router.push('/login');
    } catch (error) {
      console.error('Logout failed:', error);

      // Clear local storage even if API call fails
      localStorage.removeItem('access_token');
      localStorage.removeItem('user');

      // Redirect to login page
      router.push('/login');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <button
      onClick={handleLogout}
      disabled={isLoading}
      className={className || 'px-4 py-2 bg-red-600 hover:bg-red-700 text-white rounded-lg transition-colors disabled:opacity-50'}
    >
      {isLoading ? '登出中...' : (children || '登出')}
    </button>
  );
}
