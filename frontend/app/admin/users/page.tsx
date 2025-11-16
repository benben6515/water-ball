'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import RoleBadge from '@/components/RoleBadge';
import type { SessionInfoResponse, UserRole } from '@/types/api';

interface UserListItem {
  user_id: number;
  nickname: string;
  email: string;
  role: UserRole;
  level: number;
  created_at: string;
  oauth_providers: string[];
}

export default function AdminUsersPage() {
  const router = useRouter();
  const [users, setUsers] = useState<UserListItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentUser, setCurrentUser] = useState<SessionInfoResponse | null>(null);
  const [updatingUserId, setUpdatingUserId] = useState<number | null>(null);
  const [mounted, setMounted] = useState(false);

  // Ensure component is mounted before accessing localStorage
  useEffect(() => {
    setMounted(true);
  }, []);

  useEffect(() => {
    if (!mounted) return; // Wait for client-side mount

    // Check if user is admin
    const userDataStr = localStorage.getItem('user');
    if (userDataStr) {
      const userData: SessionInfoResponse = JSON.parse(userDataStr);
      setCurrentUser(userData);

      if (userData.role !== 'ADMIN') {
        // Not an admin, redirect to home
        router.push('/');
        return;
      }

      // Load users
      fetchUsers();
    } else {
      // Not logged in, redirect to login
      router.push('/login');
    }
  }, [router, mounted]);

  const fetchUsers = async () => {
    const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';
    const accessToken = localStorage.getItem('access_token');

    try {
      const response = await fetch(`${backendUrl}/admin/users/all`, {
        headers: {
          'Authorization': `Bearer ${accessToken}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setUsers(data);
      } else if (response.status === 403) {
        setError('您沒有權限訪問此頁面');
        setTimeout(() => router.push('/'), 2000);
      } else {
        setError('無法載入使用者列表');
      }
    } catch (err) {
      console.error('Failed to fetch users:', err);
      setError('載入失敗，請稍後再試');
    } finally {
      setLoading(false);
    }
  };

  const updateUserRole = async (userId: number, newRole: UserRole) => {
    const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';
    const accessToken = localStorage.getItem('access_token');

    setUpdatingUserId(userId);

    try {
      const response = await fetch(`${backendUrl}/admin/users/${userId}/role`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${accessToken}`,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ role: newRole }),
      });

      if (response.ok) {
        // Update local state
        setUsers(users.map(user =>
          user.user_id === userId ? { ...user, role: newRole } : user
        ));
      } else {
        const errorData = await response.json();
        alert(`更新失敗: ${errorData.message || '未知錯誤'}`);
      }
    } catch (err) {
      console.error('Failed to update role:', err);
      alert('更新失敗，請稍後再試');
    } finally {
      setUpdatingUserId(null);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-amber-50 via-orange-50 to-amber-100">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-green-700 mx-auto"></div>
          <p className="mt-4 text-gray-600">載入中...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-amber-50 via-orange-50 to-amber-100">
        <div className="bg-white rounded-lg shadow-lg p-8 max-w-md">
          <div className="text-red-600 text-center">
            <svg className="w-16 h-16 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <p className="text-lg font-semibold">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-amber-50 via-orange-50 to-amber-100 py-8 px-4">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="bg-white rounded-lg shadow-md p-6 mb-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-gray-900">使用者管理</h1>
              <p className="text-gray-600 mt-1">管理所有使用者的角色與權限</p>
            </div>
            <div className="text-right">
              <p className="text-sm text-gray-500">目前使用者</p>
              <p className="font-semibold text-gray-900">{currentUser?.nickname}</p>
              {currentUser?.role && <RoleBadge role={currentUser.role} size="sm" />}
            </div>
          </div>
        </div>

        {/* User Stats */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
          <div className="bg-white rounded-lg shadow-md p-4">
            <p className="text-sm text-gray-500">總使用者</p>
            <p className="text-2xl font-bold text-gray-900">{users.length}</p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-4">
            <p className="text-sm text-gray-500">管理員</p>
            <p className="text-2xl font-bold text-red-600">{users.filter(u => u.role === 'ADMIN').length}</p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-4">
            <p className="text-sm text-gray-500">老師</p>
            <p className="text-2xl font-bold text-purple-600">{users.filter(u => u.role === 'TEACHER').length}</p>
          </div>
          <div className="bg-white rounded-lg shadow-md p-4">
            <p className="text-sm text-gray-500">學生</p>
            <p className="text-2xl font-bold text-blue-600">{users.filter(u => u.role === 'STUDENT').length}</p>
          </div>
        </div>

        {/* Users Table */}
        <div className="bg-white rounded-lg shadow-md overflow-hidden">
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">使用者</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Email</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">等級</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">角色</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">OAuth</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">註冊時間</th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">操作</th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {users.map((user) => (
                  <tr key={user.user_id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {user.user_id}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <div className="w-10 h-10 bg-gradient-to-br from-blue-500 to-purple-600 rounded-full flex items-center justify-center text-white font-semibold">
                          {user.nickname.charAt(0).toUpperCase()}
                        </div>
                        <div className="ml-3">
                          <p className="text-sm font-medium text-gray-900">{user.nickname}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {user.email}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="text-sm bg-blue-100 text-blue-800 px-2 py-1 rounded-full font-medium">
                        Lv. {user.level}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <RoleBadge role={user.role} size="sm" />
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {user.oauth_providers.join(', ')}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {new Date(user.created_at).toLocaleDateString('zh-TW')}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm">
                      <select
                        value={user.role}
                        onChange={(e) => updateUserRole(user.user_id, e.target.value as UserRole)}
                        disabled={updatingUserId === user.user_id || user.user_id === currentUser?.user_id}
                        className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-green-500 focus:border-green-500 disabled:bg-gray-100 disabled:cursor-not-allowed"
                      >
                        <option value="GUEST">訪客</option>
                        <option value="STUDENT">學生</option>
                        <option value="TEACHER">老師</option>
                        <option value="ADMIN">管理員</option>
                      </select>
                      {updatingUserId === user.user_id && (
                        <p className="text-xs text-gray-500 mt-1">更新中...</p>
                      )}
                      {user.user_id === currentUser?.user_id && (
                        <p className="text-xs text-gray-500 mt-1">無法修改自己</p>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  );
}
