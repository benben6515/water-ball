'use client';

import { useEffect, useState } from 'react';
import axios from 'axios';
import Image from 'next/image';
import { useRouter } from 'next/navigation';

interface Course {
  courseId: number;
  title: string;
  description: string;
  coverImageUrl: string;
  instructorName: string;
  instructorAvatarUrl: string;
  price: number;
  free: boolean;
  owned: boolean;
  totalDungeons: number;
  totalVideos: number;
}

/**
 * Courses Page
 * Displays available courses from the backend API.
 */
export default function CoursesPage() {
  const router = useRouter();
  const [courses, setCourses] = useState<Course[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchCourses = async () => {
      try {
        setLoading(true);
        const backendUrl = process.env.NEXT_PUBLIC_BACKEND_URL || 'http://localhost:8080';
        const response = await axios.get(`${backendUrl}/api/courses`);
        setCourses(response.data);
        setError(null);
      } catch (err) {
        console.error('Failed to fetch courses:', err);
        setError('無法載入課程資料，請稍後再試');
      } finally {
        setLoading(false);
      }
    };

    fetchCourses();
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            課程列表
          </h1>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            探索我們精心設計的課程，從基礎到進階，幫助您成為優秀的軟體工程師
          </p>
        </div>

        {/* Loading State */}
        {loading && (
          <div className="flex justify-center items-center py-12">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
          </div>
        )}

        {/* Error State */}
        {error && (
          <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
            <p className="text-red-600">{error}</p>
          </div>
        )}

        {/* Course Grid */}
        {!loading && !error && (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {courses.map((course) => (
              <div
                key={course.courseId}
                onClick={() => router.push(`/courses/${course.courseId}`)}
                className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow duration-300 cursor-pointer"
              >
                {/* Course Cover Image */}
                <div className="relative h-48 bg-gray-200">
                  <Image
                    src={course.coverImageUrl}
                    alt={course.title}
                    fill
                    className="object-cover"
                  />
                  {course.owned && (
                    <div className="absolute top-4 right-4 bg-green-500 text-white px-3 py-1 rounded-full text-sm font-semibold">
                      已擁有
                    </div>
                  )}
                  {!course.owned && (
                    <div className="absolute top-4 right-4 bg-gray-500 text-white px-3 py-1 rounded-full text-sm font-semibold">
                      尚未擁有
                    </div>
                  )}
                </div>

                {/* Course Content */}
                <div className="p-6">
                  {/* Course Title */}
                  <h3 className="text-xl font-bold text-gray-900 mb-2">
                    {course.title}
                  </h3>

                  {/* Course Description */}
                  <p className="text-gray-600 text-sm mb-4 h-16 line-clamp-3">
                    {course.description}
                  </p>

                  {/* Instructor Info */}
                  <div className="flex items-center mb-4">
                    <div className="relative w-10 h-10 mr-3">
                      <Image
                        src={course.instructorAvatarUrl}
                        alt={course.instructorName}
                        fill
                        className="rounded-full object-cover"
                      />
                    </div>
                    <span className="text-sm text-gray-700 font-medium">
                      {course.instructorName}
                    </span>
                  </div>

                  {/* Course Stats */}
                  <div className="flex items-center gap-4 text-sm text-gray-500 mb-4">
                    <span>📚 {course.totalDungeons} 個副本</span>
                    <span>🎥 {course.totalVideos} 個影片</span>
                  </div>

                  {/* Price and Action */}
                  <div className="flex items-center justify-between pt-4 border-t border-gray-200">
                    <div>
                      {course.free ? (
                        <span className="text-2xl font-bold text-green-600">免費</span>
                      ) : (
                        <span className="text-2xl font-bold text-gray-900">
                          NT$ {course.price.toLocaleString()}
                        </span>
                      )}
                    </div>
                    {course.owned ? (
                      <button className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors font-semibold">
                        開始學習
                      </button>
                    ) : (
                      <button className="px-6 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition-colors font-semibold">
                        {course.free ? '立即加入' : '購買課程'}
                      </button>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}

        {/* Empty State */}
        {!loading && !error && courses.length === 0 && (
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
                    d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"
                  />
                </svg>
              </div>
              <h2 className="text-2xl font-semibold text-gray-900 mb-3">
                目前沒有課程
              </h2>
              <p className="text-gray-600">
                課程內容即將推出，敬請期待！
              </p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
