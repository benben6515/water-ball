'use client';

import { Suspense, useEffect, useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
import type { SessionInfoResponse } from '@/types/api';

/**
 * OAuth callback content component
 * Separated to wrap in Suspense boundary for useSearchParams
 */
function OAuthCallbackContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>('loading');
  const [errorMessage, setErrorMessage] = useState<string>('');

  useEffect(() => {
    const handleCallback = async () => {
      try {
        // Extract access token from URL
        const accessToken = searchParams.get('access_token');
        const error = searchParams.get('error');

        if (error) {
          setStatus('error');
          setErrorMessage(decodeURIComponent(error));
          return;
        }

        if (!accessToken) {
          setStatus('error');
          setErrorMessage('未收到存取權杖');
          return;
        }

        // Store access token in localStorage
        localStorage.setItem('access_token', accessToken);

        // Fetch session information
        const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';
        const response = await fetch(`${backendUrl}/auth/session`, {
          headers: {
            'Authorization': `Bearer ${accessToken}`,
          },
          credentials: 'include', // Include cookies
        });

        if (!response.ok) {
          throw new Error('獲取使用者資訊失敗');
        }

        const sessionData: SessionInfoResponse = await response.json();

        // Store user data in localStorage
        localStorage.setItem('user', JSON.stringify(sessionData));

        // Dispatch custom event to notify components that user data has changed
        window.dispatchEvent(new CustomEvent('userDataChanged', {
          detail: sessionData
        }));

        setStatus('success');

        // Redirect to homepage or intended destination
        const redirectTo = localStorage.getItem('redirect_after_login') || '/';
        localStorage.removeItem('redirect_after_login');

        setTimeout(() => {
          router.push(redirectTo);
        }, 1000);

      } catch (error) {
        console.error('OAuth callback error:', error);
        setStatus('error');
        setErrorMessage(error instanceof Error ? error.message : '登入過程發生錯誤');
      }
    };

    handleCallback();
  }, [searchParams, router]);

  return (
    <div className="min-h-screen bg-gradient-to-b from-amber-50/50 to-white flex items-center justify-center px-4">
      <div className="max-w-md w-full bg-white rounded-2xl shadow-lg p-8 text-center">
        {status === 'loading' && (
          <>
            <div className="mb-4">
              <div className="inline-block animate-spin rounded-full h-12 w-12 border-4 border-green-200 border-t-green-600"></div>
            </div>
            <h2 className="text-2xl font-bold text-gray-800 mb-2">
              登入中...
            </h2>
            <p className="text-gray-600">
              正在處理您的登入資訊
            </p>
          </>
        )}

        {status === 'success' && (
          <>
            <div className="mb-4 text-green-600">
              <svg className="inline-block w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-gray-800 mb-2">
              登入成功！
            </h2>
            <p className="text-gray-600">
              即將跳轉...
            </p>
          </>
        )}

        {status === 'error' && (
          <>
            <div className="mb-4 text-red-600">
              <svg className="inline-block w-12 h-12" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
              </svg>
            </div>
            <h2 className="text-2xl font-bold text-gray-800 mb-2">
              登入失敗
            </h2>
            <p className="text-gray-600 mb-6">
              {errorMessage}
            </p>
            <button
              onClick={() => router.push('/login')}
              className="px-6 py-3 bg-green-600 hover:bg-green-700 text-white rounded-lg transition-colors"
            >
              返回登入頁面
            </button>
          </>
        )}
      </div>
    </div>
  );
}

/**
 * OAuth callback page with Suspense wrapper
 */
export default function OAuthCallbackPage() {
  return (
    <Suspense fallback={
      <div className="min-h-screen bg-gradient-to-b from-amber-50/50 to-white flex items-center justify-center px-4">
        <div className="max-w-md w-full bg-white rounded-2xl shadow-lg p-8 text-center">
          <div className="mb-4">
            <div className="inline-block animate-spin rounded-full h-12 w-12 border-4 border-green-200 border-t-green-600"></div>
          </div>
          <h2 className="text-2xl font-bold text-gray-800 mb-2">
            登入中...
          </h2>
          <p className="text-gray-600">
            正在處理您的登入資訊
          </p>
        </div>
      </div>
    }>
      <OAuthCallbackContent />
    </Suspense>
  );
}
