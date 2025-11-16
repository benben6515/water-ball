'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import type { SessionInfoResponse } from '@/types/api';
import RoleBadge from '@/components/RoleBadge';

export default function UserAvatar() {
  const router = useRouter();
  const [user, setUser] = useState<SessionInfoResponse | null>(null);
  const [showDropdown, setShowDropdown] = useState(false);

  useEffect(() => {
    // Check if user is logged in
    const loadUserData = () => {
      const userDataStr = localStorage.getItem('user');
      if (userDataStr) {
        try {
          const userData: SessionInfoResponse = JSON.parse(userDataStr);
          setUser(userData);
        } catch (error) {
          console.error('Failed to parse user data:', error);
        }
      } else {
        setUser(null);
      }
    };

    // Load user data on mount
    loadUserData();

    // Listen for user data changes (after login/logout)
    const handleUserDataChanged = (event: Event) => {
      const customEvent = event as CustomEvent<SessionInfoResponse>;
      if (customEvent.detail) {
        setUser(customEvent.detail);
      } else {
        loadUserData();
      }
    };

    window.addEventListener('userDataChanged', handleUserDataChanged);

    // Cleanup listener on unmount
    return () => {
      window.removeEventListener('userDataChanged', handleUserDataChanged);
    };
  }, []);

  const handleLogout = async () => {
    const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';
    const accessToken = localStorage.getItem('access_token');

    try {
      await fetch(`${backendUrl}/auth/logout`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${accessToken}`,
        },
        credentials: 'include',
      });
    } catch (error) {
      console.error('Logout failed:', error);
    } finally {
      // Clear local storage
      localStorage.removeItem('access_token');
      localStorage.removeItem('user');

      // Dispatch event to notify components that user logged out
      window.dispatchEvent(new CustomEvent('userDataChanged'));

      // Update state to show login button
      setUser(null);
      setShowDropdown(false);

      // Redirect to homepage
      router.push('/');
    }
  };

  if (!user) {
    return (
      <Link
        href="/login"
        className="px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors"
      >
        登入
      </Link>
    );
  }

  // Get first character of nickname for avatar
  const avatarText = user.nickname?.charAt(0).toUpperCase() || 'U';

  return (
    <div className="relative">
      {/* Avatar Button */}
      <button
        onClick={() => setShowDropdown(!showDropdown)}
        className="flex items-center space-x-3 focus:outline-none focus:ring-2 focus:ring-blue-500 rounded-lg px-2 py-1"
      >
        <div className="flex items-center space-x-2">
          <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white font-semibold">
            {avatarText}
          </div>
          <div className="hidden md:block text-left">
            <p className="text-sm font-medium text-gray-900">{user.nickname}</p>
            <p className="text-xs text-gray-500">Lv. {user.level}</p>
          </div>
        </div>
        <svg
          className={`w-4 h-4 text-gray-500 transition-transform ${showDropdown ? 'rotate-180' : ''}`}
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
        </svg>
      </button>

      {/* Dropdown Menu */}
      {showDropdown && (
        <>
          {/* Backdrop to close dropdown */}
          <div
            className="fixed inset-0 z-10"
            onClick={() => setShowDropdown(false)}
          />

          {/* Dropdown Content */}
          <div className="absolute right-0 mt-2 w-64 bg-white rounded-lg shadow-lg border border-gray-200 py-2 z-20">
            {/* User Info Section */}
            <div className="px-4 py-3 border-b border-gray-200">
              <p className="text-sm font-semibold text-gray-900">{user.nickname}</p>
              <p className="text-xs text-gray-500 mt-1">{user.email}</p>
              <div className="mt-2 flex items-center flex-wrap gap-2">
                <span className="text-xs bg-blue-100 text-blue-800 px-2 py-1 rounded-full font-medium">
                  Level {user.level}
                </span>
                {user.role && <RoleBadge role={user.role} size="sm" />}
                {user.oauth_providers && user.oauth_providers.length > 0 && (
                  <span className="text-xs text-gray-500">
                    {user.oauth_providers.join(', ')}
                  </span>
                )}
              </div>
            </div>

            {/* Menu Items */}
            <div className="py-2">
              <Link
                href="/profile"
                className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                onClick={() => setShowDropdown(false)}
              >
                個人資料
              </Link>
              <Link
                href="/orders"
                className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                onClick={() => setShowDropdown(false)}
              >
                訂單記錄
              </Link>
              <Link
                href="/courses/owned"
                className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 transition-colors"
                onClick={() => setShowDropdown(false)}
              >
                我的課程
              </Link>
            </div>

            {/* Logout Button */}
            <div className="border-t border-gray-200 pt-2">
              <button
                onClick={handleLogout}
                className="block w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 transition-colors"
              >
                登出
              </button>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
