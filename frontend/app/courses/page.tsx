import Link from 'next/link';

/**
 * Courses Page
 * Displays available courses and learning paths.
 */
export default function CoursesPage() {
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

        {/* Placeholder Content */}
        <div className="bg-white rounded-xl shadow-lg p-12 text-center">
          <div className="max-w-md mx-auto">
            {/* Icon */}
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

            {/* Message */}
            <h2 className="text-2xl font-semibold text-gray-900 mb-3">
              課程即將推出
            </h2>
            <p className="text-gray-600 mb-6">
              我們正在準備豐富多樣的課程內容，包括 Java、Spring Boot、React、系統設計等主題。敬請期待！
            </p>

            {/* Action Button */}
            <Link
              href="/"
              className="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-lg text-white bg-blue-600 hover:bg-blue-700 transition-colors"
            >
              返回首頁
            </Link>
          </div>
        </div>

        {/* Coming Soon Features */}
        <div className="mt-12 grid grid-cols-1 md:grid-cols-3 gap-6">
          <div className="bg-white rounded-lg shadow p-6 text-center">
            <div className="text-3xl mb-3">💻</div>
            <h3 className="font-semibold text-gray-900 mb-2">程式設計基礎</h3>
            <p className="text-sm text-gray-600">學習 Java、Python、JavaScript 等熱門語言</p>
          </div>
          <div className="bg-white rounded-lg shadow p-6 text-center">
            <div className="text-3xl mb-3">🏗️</div>
            <h3 className="font-semibold text-gray-900 mb-2">系統架構設計</h3>
            <p className="text-sm text-gray-600">掌握微服務、分散式系統等進階主題</p>
          </div>
          <div className="bg-white rounded-lg shadow p-6 text-center">
            <div className="text-3xl mb-3">🚀</div>
            <h3 className="font-semibold text-gray-900 mb-2">專案實戰</h3>
            <p className="text-sm text-gray-600">透過實際專案累積開發經驗</p>
          </div>
        </div>
      </div>
    </div>
  );
}
