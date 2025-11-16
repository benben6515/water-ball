'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

/**
 * Admin index page - redirects to user management
 */
export default function AdminPage() {
  const router = useRouter();

  useEffect(() => {
    router.push('/admin/users');
  }, [router]);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-amber-50 via-orange-50 to-amber-100">
      <div className="text-center">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-700 mx-auto"></div>
        <p className="mt-4 text-gray-600">跳轉至管理頁面...</p>
      </div>
    </div>
  );
}
