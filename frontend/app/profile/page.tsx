'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import api from '@/lib/api';

interface ProfileData {
  userId: number;
  nickname: string;
  email: string;
  gender?: string;
  birthday?: string;
  location?: string;
  occupation?: string;
  githubLink?: string;
  level: number;
  exp: number;
  expForNextLevel: number | null;
  expProgressPercentage: number;
  achievements: Achievement[];
}

interface Achievement {
  achievementType: string;
  achievementName: string;
  earnedAt: string;
}

/**
 * User Profile Page
 * Allows users to view and edit their profile information
 */
export default function ProfilePage() {
  const router = useRouter();
  const [profile, setProfile] = useState<ProfileData | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [deleteConfirmText, setDeleteConfirmText] = useState('');
  const [deleting, setDeleting] = useState(false);

  // Form state
  const [formData, setFormData] = useState({
    nickname: '',
    gender: '',
    birthday: '',
    location: '',
    occupation: '',
    githubLink: '',
  });

  // Load profile data
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        setLoading(true);
        const response = await api.get('/api/profile');
        const data: ProfileData = response.data;
        setProfile(data);

        // Populate form
        setFormData({
          nickname: data.nickname || '',
          gender: data.gender || '',
          birthday: data.birthday || '',
          location: data.location || '',
          occupation: data.occupation || '',
          githubLink: data.githubLink || '',
        });

        setError(null);
      } catch (err: any) {
        console.error('Failed to load profile:', err);
        if (err.response?.status === 401) {
          router.push('/login');
        } else {
          setError('無法載入個人資料，請稍後再試');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchProfile();
  }, [router]);

  // Handle form submission
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      setSaving(true);
      setError(null);
      setSuccess(false);

      const response = await api.put('/api/profile', formData);

      setProfile(response.data.profile);
      setSuccess(true);

      // Clear success message after 3 seconds
      setTimeout(() => setSuccess(false), 3000);
    } catch (err: any) {
      console.error('Failed to update profile:', err);
      setError(err.response?.data?.message || '儲存失敗，請稍後再試');
    } finally {
      setSaving(false);
    }
  };

  // Handle input changes
  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
  };

  // Handle account deletion
  const handleDeleteAccount = async () => {
    if (deleteConfirmText !== '刪除我的帳號') {
      setError('請輸入正確的確認文字');
      return;
    }

    try {
      setDeleting(true);
      setError(null);

      await api.delete('/api/account');

      // Clear all user data
      localStorage.removeItem('access_token');
      localStorage.removeItem('refresh_token');
      localStorage.removeItem('user');

      // Notify other components
      window.dispatchEvent(new Event('userDataChanged'));

      // Redirect to home page
      alert('您的帳號已成功刪除。感謝您使用地球軟體學院的服務。');
      window.location.href = '/';
    } catch (err: any) {
      console.error('Failed to delete account:', err);
      setError(err.response?.data?.message || '刪除帳號失敗，請稍後再試');
      setDeleting(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
        <div className="flex justify-center items-center py-20">
          <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-blue-600"></div>
        </div>
      </div>
    );
  }

  if (error && !profile) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-3xl mx-auto">
          <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
            <p className="text-red-600">{error}</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        {/* Page Header */}
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">個人資料</h1>
          <p className="text-gray-600 mt-2">管理您的個人資訊和帳號設定</p>
        </div>

        {/* Success Message */}
        {success && (
          <div className="mb-6 bg-green-50 border border-green-200 rounded-lg p-4">
            <p className="text-green-700 font-medium">✓ 個人資料已更新</p>
          </div>
        )}

        {/* Error Message */}
        {error && (
          <div className="mb-6 bg-red-50 border border-red-200 rounded-lg p-4">
            <p className="text-red-700">{error}</p>
          </div>
        )}

        {/* Level & Exp Section */}
        {profile && (
          <div className="bg-white rounded-xl shadow-lg p-6 mb-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4">遊戲化進度</h2>
            <div className="space-y-4">
              {/* Level Display */}
              <div className="flex items-center justify-between">
                <span className="text-gray-700">等級</span>
                <span className="text-2xl font-bold text-blue-600">Lv. {profile.level}</span>
              </div>

              {/* Exp Progress */}
              <div>
                <div className="flex items-center justify-between text-sm text-gray-600 mb-2">
                  <span>經驗值</span>
                  <span>{profile.exp} / {profile.expForNextLevel || profile.exp} EXP</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-3">
                  <div
                    className="bg-gradient-to-r from-blue-500 to-purple-500 h-3 rounded-full transition-all duration-300"
                    style={{ width: `${profile.expProgressPercentage}%` }}
                  ></div>
                </div>
                <p className="text-xs text-gray-500 mt-1">進度: {profile.expProgressPercentage.toFixed(0)}%</p>
              </div>

              {/* Achievements */}
              {profile.achievements && profile.achievements.length > 0 && (
                <div className="mt-6">
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">成就</h3>
                  <div className="space-y-2">
                    {profile.achievements.map((achievement, index) => (
                      <div key={index} className="flex items-center gap-3 bg-gray-50 rounded-lg p-3">
                        <span className="text-2xl">🏆</span>
                        <div>
                          <p className="font-medium text-gray-900">{achievement.achievementName}</p>
                          <p className="text-sm text-gray-500">{achievement.earnedAt}</p>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          </div>
        )}

        {/* Profile Form */}
        <div className="bg-white rounded-xl shadow-lg p-8">
          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Nickname */}
            <div>
              <label htmlFor="nickname" className="block text-sm font-medium text-gray-700 mb-2">
                暱稱 <span className="text-red-500">*</span>
              </label>
              <input
                type="text"
                id="nickname"
                name="nickname"
                value={formData.nickname}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="請輸入您的暱稱"
              />
            </div>

            {/* Email (Read-only) */}
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-2">
                電子郵件 (來自 OAuth，無法更改)
              </label>
              <input
                type="email"
                id="email"
                value={profile?.email || ''}
                disabled
                className="w-full px-4 py-2 border border-gray-300 rounded-lg bg-gray-100 text-gray-500 cursor-not-allowed"
              />
            </div>

            {/* Gender */}
            <div>
              <label htmlFor="gender" className="block text-sm font-medium text-gray-700 mb-2">
                性別
              </label>
              <select
                id="gender"
                name="gender"
                value={formData.gender}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">請選擇</option>
                <option value="男">男</option>
                <option value="女">女</option>
                <option value="其他">其他</option>
                <option value="不透露">不透露</option>
              </select>
            </div>

            {/* Birthday */}
            <div>
              <label htmlFor="birthday" className="block text-sm font-medium text-gray-700 mb-2">
                生日
              </label>
              <input
                type="date"
                id="birthday"
                name="birthday"
                value={formData.birthday}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              />
            </div>

            {/* Location */}
            <div>
              <label htmlFor="location" className="block text-sm font-medium text-gray-700 mb-2">
                地點
              </label>
              <input
                type="text"
                id="location"
                name="location"
                value={formData.location}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="例如：台北市"
              />
            </div>

            {/* Occupation */}
            <div>
              <label htmlFor="occupation" className="block text-sm font-medium text-gray-700 mb-2">
                職業
              </label>
              <input
                type="text"
                id="occupation"
                name="occupation"
                value={formData.occupation}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="例如：軟體工程師"
              />
            </div>

            {/* GitHub Link */}
            <div>
              <label htmlFor="githubLink" className="block text-sm font-medium text-gray-700 mb-2">
                GitHub 連結
              </label>
              <input
                type="url"
                id="githubLink"
                name="githubLink"
                value={formData.githubLink}
                onChange={handleChange}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                placeholder="https://github.com/yourusername"
              />
              <p className="text-sm text-gray-500 mt-1">格式：https://github.com/username</p>
            </div>

            {/* Submit Button */}
            <div className="flex gap-4">
              <button
                type="submit"
                disabled={saving}
                className="flex-1 px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-semibold shadow-md hover:shadow-lg disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {saving ? '儲存中...' : '儲存'}
              </button>
              <button
                type="button"
                onClick={() => router.push('/')}
                className="px-6 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-semibold"
              >
                取消
              </button>
            </div>
          </form>
        </div>

        {/* Delete Account Section */}
        <div className="bg-white rounded-xl shadow-lg p-8 mt-6 border-2 border-red-200">
          <h2 className="text-2xl font-bold text-red-600 mb-4">危險區域</h2>
          <p className="text-gray-700 mb-4">
            刪除帳號後，您的所有資料將被永久刪除且無法復原，包括：
          </p>
          <ul className="list-disc list-inside text-gray-700 mb-6 space-y-1">
            <li>個人資料與帳號資訊</li>
            <li>課程學習進度</li>
            <li>已購買的課程記錄</li>
            <li>成就與經驗值</li>
            <li>第三方帳號連結</li>
          </ul>

          <button
            type="button"
            onClick={() => setShowDeleteDialog(true)}
            className="px-6 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors font-semibold"
          >
            刪除帳號
          </button>
        </div>

        {/* Delete Confirmation Dialog */}
        {showDeleteDialog && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
            <div className="bg-white rounded-xl shadow-2xl max-w-md w-full p-8">
              <h3 className="text-2xl font-bold text-red-600 mb-4">確認刪除帳號</h3>
              <p className="text-gray-700 mb-6">
                此操作無法復原。刪除後，您的所有資料將被永久刪除。
              </p>
              <p className="text-gray-700 mb-4">
                請輸入 <span className="font-bold text-red-600">刪除我的帳號</span> 以確認：
              </p>

              <input
                type="text"
                value={deleteConfirmText}
                onChange={(e) => setDeleteConfirmText(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent mb-6"
                placeholder="刪除我的帳號"
                autoFocus
              />

              {error && (
                <div className="mb-4 bg-red-50 border border-red-200 rounded-lg p-3">
                  <p className="text-red-700 text-sm">{error}</p>
                </div>
              )}

              <div className="flex gap-4">
                <button
                  type="button"
                  onClick={handleDeleteAccount}
                  disabled={deleting || deleteConfirmText !== '刪除我的帳號'}
                  className="flex-1 px-6 py-3 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors font-semibold disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  {deleting ? '刪除中...' : '確認刪除'}
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowDeleteDialog(false);
                    setDeleteConfirmText('');
                    setError(null);
                  }}
                  disabled={deleting}
                  className="flex-1 px-6 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-semibold disabled:opacity-50"
                >
                  取消
                </button>
              </div>

              <p className="text-xs text-gray-500 mt-4 text-center">
                此功能符合 Facebook 平台政策要求
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
