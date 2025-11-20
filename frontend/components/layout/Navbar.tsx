'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import UserAvatar from './UserAvatar';
import Logo from '../Logo';
import type { SessionInfoResponse } from '@/types/api';

export default function Navbar() {
  const [user, setUser] = useState<SessionInfoResponse | null>(null);

  useEffect(() => {
    // Load user data from localStorage
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
    const handleUserDataChanged = () => {
      loadUserData();
    };

    window.addEventListener('userDataChanged', handleUserDataChanged);

    // Cleanup listener on unmount
    return () => {
      window.removeEventListener('userDataChanged', handleUserDataChanged);
    };
  }, []);

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-[rgba(255,255,255,0.7)] backdrop-blur-sm border-b border-amber-200/50 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo / Brand */}
          <div className="flex items-center">
            <Link href="/" className="flex items-center space-x-3 group">
              <Logo className="w-10 h-10 transition-transform group-hover:scale-110" />
              <span className="text-2xl font-bold text-green-700">
                地球軟體學院
              </span>
            </Link>
          </div>

          {/* Navigation Links */}
          <div className="hidden md:flex items-center space-x-8">
            <Link
              href="/"
              className="text-gray-700 hover:text-green-700 transition-colors font-medium"
            >
              首頁
            </Link>
            <Link
              href="/courses"
              className="text-gray-700 hover:text-green-700 transition-colors font-medium"
            >
              課程
            </Link>
            <Link
              href="/about"
              className="text-gray-700 hover:text-green-700 transition-colors font-medium"
            >
              關於
            </Link>
            {/* Admin Link - Only show if user is ADMIN */}
            {user?.role === 'ADMIN' && (
              <Link
                href="/admin/users"
                className="text-gray-700 hover:text-green-700 transition-colors font-medium"
              >
                身份管理
              </Link>
            )}
          </div>

          {/* User Avatar */}
          <div className="flex items-center">
            <UserAvatar />
          </div>
        </div>
      </div>
    </nav>
  );
}
