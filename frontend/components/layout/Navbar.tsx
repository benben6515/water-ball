'use client';

import { useEffect, useState } from 'react';
import Link from 'next/link';
import UserAvatar from './UserAvatar';
import Logo from '../Logo';
import type { SessionInfoResponse } from '@/types/api';

export default function Navbar() {
  const [user, setUser] = useState<SessionInfoResponse | null>(null);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

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

  // Close mobile menu when clicking outside
  useEffect(() => {
    if (isMobileMenuOpen) {
      const handleClickOutside = (e: MouseEvent) => {
        const target = e.target as HTMLElement;
        if (!target.closest('.mobile-menu') && !target.closest('.hamburger-button')) {
          setIsMobileMenuOpen(false);
        }
      };

      document.addEventListener('mousedown', handleClickOutside);
      return () => document.removeEventListener('mousedown', handleClickOutside);
    }
  }, [isMobileMenuOpen]);

  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-[rgba(255,255,255,0.7)] backdrop-blur-sm border-b border-amber-200/50 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Mobile Menu Button */}
          <button
            onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            className="hamburger-button md:hidden p-2 rounded-lg hover:bg-gray-100 transition-colors"
            aria-label="Toggle menu"
          >
            <svg
              className="w-6 h-6 text-gray-700"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              {isMobileMenuOpen ? (
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M6 18L18 6M6 6l12 12"
                />
              ) : (
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M4 6h16M4 12h16M4 18h16"
                />
              )}
            </svg>
          </button>

          {/* Logo / Brand */}
          <div className="flex items-center">
            <Link href="/" className="flex items-center space-x-3 group">
              <Logo className="w-10 h-10 transition-transform group-hover:scale-110" />
              <span className="text-2xl font-bold text-green-700">
                地球軟體學院
              </span>
            </Link>
          </div>

          {/* Navigation Links - Desktop */}
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

      {/* Mobile Menu Drawer */}
      <div
        className={`mobile-menu fixed top-16 left-0 h-[calc(100vh-4rem)] w-64 bg-white shadow-lg transform transition-transform duration-300 ease-in-out md:hidden ${
          isMobileMenuOpen ? 'translate-x-0' : '-translate-x-full'
        }`}
      >
        <div className="flex flex-col py-4">
          <Link
            href="/"
            onClick={() => setIsMobileMenuOpen(false)}
            className="px-6 py-3 text-gray-700 hover:bg-green-50 hover:text-green-700 transition-colors font-medium"
          >
            首頁
          </Link>
          <Link
            href="/courses"
            onClick={() => setIsMobileMenuOpen(false)}
            className="px-6 py-3 text-gray-700 hover:bg-green-50 hover:text-green-700 transition-colors font-medium"
          >
            課程
          </Link>
          <Link
            href="/about"
            onClick={() => setIsMobileMenuOpen(false)}
            className="px-6 py-3 text-gray-700 hover:bg-green-50 hover:text-green-700 transition-colors font-medium"
          >
            關於
          </Link>
          {/* Admin Link - Only show if user is ADMIN */}
          {user?.role === 'ADMIN' && (
            <Link
              href="/admin/users"
              onClick={() => setIsMobileMenuOpen(false)}
              className="px-6 py-3 text-gray-700 hover:bg-green-50 hover:text-green-700 transition-colors font-medium"
            >
              身份管理
            </Link>
          )}
        </div>
      </div>

      {/* Overlay */}
      {isMobileMenuOpen && (
        <div
          className="fixed inset-0 top-16 bg-black bg-opacity-50 md:hidden z-40"
          onClick={() => setIsMobileMenuOpen(false)}
        />
      )}
    </nav>
  );
}
