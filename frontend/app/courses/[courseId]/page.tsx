'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Image from 'next/image';
import api from '@/lib/api';

interface Video {
  videoId: number;
  title: string;
  description: string;
  durationSeconds: number;
  videoUrl: string;
  thumbnailUrl: string;
  chapterNumber: number;
  orderIndex: number;
  expReward: number;
  demo: boolean;
  completed: boolean;
}

interface Dungeon {
  dungeonId: number;
  dungeonNumber: number;
  title: string;
  description: string;
  difficulty: number;
  orderIndex: number;
  videos: Video[];
}

interface CourseDetail {
  courseId: number;
  title: string;
  description: string;
  coverImageUrl: string;
  instructorName: string;
  instructorAvatarUrl: string;
  price: number;
  totalDungeons: number;
  totalVideos: number;
  free: boolean;
  owned: boolean;
  dungeons: Dungeon[];
}

/**
 * Course Detail Page
 * Shows full course information with dungeons and videos in an accordion layout.
 */
export default function CourseDetailPage() {
  const params = useParams();
  const router = useRouter();
  const courseId = params.courseId as string;

  const [course, setCourse] = useState<CourseDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedDungeons, setExpandedDungeons] = useState<Set<number>>(new Set([0]));
  const [purchasing, setPurchasing] = useState(false);
  const [purchaseError, setPurchaseError] = useState<string | null>(null);

  useEffect(() => {
    const fetchCourseDetail = async () => {
      try {
        setLoading(true);
        const response = await api.get(`/api/courses/${courseId}`);
        setCourse(response.data);
        setError(null);
      } catch (err: any) {
        console.error('Failed to fetch course detail:', err);
        if (err.response?.status === 404) {
          setError('找不到該課程');
        } else {
          setError('無法載入課程資料，請稍後再試');
        }
      } finally {
        setLoading(false);
      }
    };

    fetchCourseDetail();
  }, [courseId]);

  const handlePurchase = async () => {
    if (!course) return;

    try {
      setPurchasing(true);
      setPurchaseError(null);

      await api.post(`/api/orders/purchase?courseId=${courseId}`);

      // Redirect to order confirmation or course page
      router.push(`/orders/confirmation?courseId=${courseId}`);
    } catch (err: any) {
      console.error('Purchase failed:', err);

      if (err.response?.status === 401) {
        // Not logged in - redirect to login
        router.push(`/login?redirect=/courses/${courseId}`);
      } else if (err.response?.status === 409) {
        // Already owned
        setPurchaseError('您已擁有此課程');
        // Refresh course data
        window.location.reload();
      } else {
        setPurchaseError(err.response?.data?.message || '購買失敗，請稍後再試');
      }
    } finally {
      setPurchasing(false);
    }
  };

  const toggleDungeon = (dungeonNumber: number) => {
    setExpandedDungeons(prev => {
      const next = new Set(prev);
      if (next.has(dungeonNumber)) {
        next.delete(dungeonNumber);
      } else {
        next.add(dungeonNumber);
      }
      return next;
    });
  };

  const formatDuration = (seconds: number): string => {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes}:${secs.toString().padStart(2, '0')}`;
  };

  const renderDifficultyStars = (difficulty: number) => {
    return '★'.repeat(difficulty) + '☆'.repeat(4 - difficulty);
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

  if (error || !course) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
        <div className="max-w-4xl mx-auto">
          <div className="bg-red-50 border border-red-200 rounded-lg p-8 text-center">
            <p className="text-red-600 text-lg mb-4">{error || '找不到課程'}</p>
            <button
              onClick={() => router.push('/courses')}
              className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              返回課程列表
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-6xl mx-auto">
        {/* Back Button */}
        <button
          onClick={() => router.push('/courses')}
          className="mb-6 flex items-center text-blue-600 hover:text-blue-700 transition-colors"
        >
          <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
          返回課程列表
        </button>

        {/* Course Header */}
        <div className="bg-white rounded-xl shadow-lg overflow-hidden mb-8">
          {/* Cover Image */}
          <div className="relative h-64 md:h-80 bg-gray-200">
            <Image
              src={course.coverImageUrl}
              alt={course.title}
              fill
              className="object-cover"
            />
            {course.owned && (
              <div className="absolute top-6 right-6 bg-green-500 text-white px-4 py-2 rounded-full text-sm font-semibold shadow-lg">
                已擁有
              </div>
            )}
            {!course.owned && (
              <div className="absolute top-6 right-6 bg-gray-500 text-white px-4 py-2 rounded-full text-sm font-semibold shadow-lg">
                尚未擁有
              </div>
            )}
          </div>

          {/* Course Info */}
          <div className="p-8">
            <h1 className="text-4xl font-bold text-gray-900 mb-4">
              {course.title}
            </h1>

            <p className="text-lg text-gray-600 mb-6 leading-relaxed">
              {course.description}
            </p>

            {/* Instructor */}
            <div className="flex items-center mb-6">
              <div className="relative w-12 h-12 mr-4">
                <Image
                  src={course.instructorAvatarUrl}
                  alt={course.instructorName}
                  fill
                  className="rounded-full object-cover"
                />
              </div>
              <div>
                <p className="text-sm text-gray-500">講師</p>
                <p className="text-lg font-semibold text-gray-900">{course.instructorName}</p>
              </div>
            </div>

            {/* Course Stats */}
            <div className="flex flex-wrap gap-6 mb-6 text-gray-700">
              <div className="flex items-center">
                <span className="text-2xl mr-2">📚</span>
                <div>
                  <p className="text-sm text-gray-500">副本數量</p>
                  <p className="text-lg font-semibold">{course.totalDungeons} 個副本</p>
                </div>
              </div>
              <div className="flex items-center">
                <span className="text-2xl mr-2">🎥</span>
                <div>
                  <p className="text-sm text-gray-500">影片數量</p>
                  <p className="text-lg font-semibold">{course.totalVideos} 個影片</p>
                </div>
              </div>
            </div>

            {/* Price and Action */}
            <div className="flex items-center justify-between pt-6 border-t border-gray-200">
              <div>
                {course.free ? (
                  <span className="text-3xl font-bold text-green-600">免費</span>
                ) : (
                  <span className="text-3xl font-bold text-gray-900">
                    NT$ {course.price.toLocaleString()}
                  </span>
                )}
              </div>
              {course.owned ? (
                <button
                  onClick={() => {
                    // Navigate to first video in first dungeon
                    const firstVideo = course.dungeons[0]?.videos[0];
                    if (firstVideo) {
                      router.push(`/courses/${courseId}/videos/${firstVideo.videoId}`);
                    }
                  }}
                  className="px-8 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-semibold text-lg shadow-md"
                >
                  開始學習
                </button>
              ) : (
                <div className="flex flex-col items-end gap-2">
                  <button
                    onClick={handlePurchase}
                    disabled={purchasing}
                    className="px-8 py-3 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors font-semibold text-lg shadow-md disabled:bg-gray-400 disabled:cursor-not-allowed"
                  >
                    {purchasing ? '處理中...' : (course.free ? '立即加入' : '購買課程')}
                  </button>
                  {purchaseError && (
                    <p className="text-red-600 text-sm">{purchaseError}</p>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Dungeons Section */}
        <div className="mb-8">
          <h2 className="text-2xl font-bold text-gray-900 mb-6">課程內容</h2>

          <div className="space-y-4">
            {course.dungeons.map((dungeon) => (
              <div
                key={dungeon.dungeonId}
                className="bg-white rounded-lg shadow-md overflow-hidden"
              >
                {/* Dungeon Header */}
                <button
                  onClick={() => toggleDungeon(dungeon.dungeonNumber)}
                  className="w-full px-6 py-4 flex items-center justify-between hover:bg-gray-50 transition-colors"
                >
                  <div className="flex items-center gap-4 text-left">
                    <div className="flex-shrink-0 w-12 h-12 bg-blue-600 text-white rounded-lg flex items-center justify-center font-bold text-lg">
                      {dungeon.dungeonNumber}
                    </div>
                    <div>
                      <h3 className="text-lg font-semibold text-gray-900">
                        {dungeon.title}
                      </h3>
                      <p className="text-sm text-gray-600">{dungeon.description}</p>
                      <div className="flex items-center gap-4 mt-1">
                        <span className="text-sm text-yellow-600">
                          {renderDifficultyStars(dungeon.difficulty)}
                        </span>
                        <span className="text-sm text-gray-500">
                          {dungeon.videos.length} 個影片
                        </span>
                      </div>
                    </div>
                  </div>
                  <svg
                    className={`w-6 h-6 text-gray-400 transition-transform ${
                      expandedDungeons.has(dungeon.dungeonNumber) ? 'transform rotate-180' : ''
                    }`}
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                  </svg>
                </button>

                {/* Videos List */}
                {expandedDungeons.has(dungeon.dungeonNumber) && (
                  <div className="border-t border-gray-200 bg-gray-50">
                    {dungeon.videos.map((video, index) => (
                      <div
                        key={video.videoId}
                        className="px-6 py-4 hover:bg-white transition-colors border-b border-gray-200 last:border-b-0"
                      >
                        <div className="flex items-start gap-4">
                          {/* Video Thumbnail */}
                          <div className="relative w-32 h-20 flex-shrink-0 rounded-lg overflow-hidden bg-gray-200">
                            <Image
                              src={video.thumbnailUrl}
                              alt={video.title}
                              fill
                              className="object-cover"
                            />
                            <div className="absolute inset-0 flex items-center justify-center bg-black bg-opacity-30">
                              <svg className="w-10 h-10 text-white" fill="currentColor" viewBox="0 0 24 24">
                                <path d="M8 5v14l11-7z" />
                              </svg>
                            </div>
                          </div>

                          {/* Video Info */}
                          <div className="flex-1 min-w-0">
                            <div className="flex items-start justify-between gap-2">
                              <div className="flex-1">
                                <div className="flex items-center gap-2 mb-1">
                                  <span className="text-xs font-semibold text-gray-500">
                                    {index + 1}.
                                  </span>
                                  <h4 className="text-base font-semibold text-gray-900">
                                    {video.title}
                                  </h4>
                                </div>
                                <p className="text-sm text-gray-600 mb-2 line-clamp-2">
                                  {video.description}
                                </p>
                                <div className="flex items-center gap-3 text-xs text-gray-500">
                                  <span>⏱️ {formatDuration(video.durationSeconds)}</span>
                                  <span>⭐ +{video.expReward} EXP</span>
                                  {video.demo && (
                                    <span className="px-2 py-1 bg-blue-100 text-blue-700 rounded-full font-semibold">
                                      試看
                                    </span>
                                  )}
                                  {video.completed && (
                                    <span className="px-2 py-1 bg-green-100 text-green-700 rounded-full font-semibold">
                                      ✓ 已完成
                                    </span>
                                  )}
                                </div>
                              </div>

                              {/* Play Button */}
                              {(course.owned || video.demo) ? (
                                <button
                                  onClick={() => router.push(`/courses/${courseId}/videos/${video.videoId}`)}
                                  className="flex-shrink-0 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors text-sm font-semibold"
                                >
                                  播放
                                </button>
                              ) : (
                                <button
                                  disabled
                                  className="flex-shrink-0 px-4 py-2 bg-gray-400 text-white rounded-lg cursor-not-allowed text-sm font-semibold"
                                  title="購買課程後即可觀看"
                                >
                                  播放
                                </button>
                              )}
                            </div>
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Empty State */}
        {course.dungeons.length === 0 && (
          <div className="bg-white rounded-xl shadow-lg p-12 text-center">
            <div className="max-w-md mx-auto">
              <div className="mb-6">
                <svg
                  className="mx-auto h-24 w-24 text-gray-400"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={1.5}
                    d="M7 4v16M17 4v16M3 8h4m10 0h4M3 12h18M3 16h4m10 0h4M4 20h16a1 1 0 001-1V5a1 1 0 00-1-1H4a1 1 0 00-1 1v14a1 1 0 001 1z"
                  />
                </svg>
              </div>
              <h3 className="text-2xl font-semibold text-gray-900 mb-3">
                課程內容即將推出
              </h3>
              <p className="text-gray-600">
                我們正在準備精彩的課程內容，敬請期待！
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
