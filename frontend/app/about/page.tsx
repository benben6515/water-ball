import Link from 'next/link';

/**
 * About Page
 * Information about Water Ball Software Academy.
 */
export default function AboutPage() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-green-50 via-white to-blue-50 pt-24 pb-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-7xl mx-auto">
        {/* Header */}
        <div className="text-center mb-12">
          <h1 className="text-4xl font-bold text-gray-900 mb-4">
            關於我們
          </h1>
          <p className="text-lg text-gray-600 max-w-2xl mx-auto">
            地球軟體學院致力於培養下一代優秀的軟體工程師
          </p>
        </div>

        {/* Main Content */}
        <div className="bg-white rounded-xl shadow-lg overflow-hidden mb-8">
          {/* Hero Section */}
          <div className="bg-gradient-to-r from-green-600 to-blue-600 px-8 py-12 text-white text-center">
            <div className="text-6xl mb-4">🌍</div>
            <h2 className="text-3xl font-bold mb-3">地球軟體學院</h2>
            <p className="text-lg text-green-100">Water Ball Software Academy</p>
          </div>

          {/* Content Sections */}
          <div className="px-8 py-12">
            {/* Mission */}
            <div className="mb-12">
              <h3 className="text-2xl font-semibold text-gray-900 mb-4">我們的使命</h3>
              <p className="text-gray-700 leading-relaxed">
                我們相信每個人都有成為優秀軟體工程師的潛力。透過精心設計的課程、實戰專案和社群支持，
                我們致力於幫助學員建立紮實的技術基礎，並在軟體開發的道路上持續成長。
              </p>
            </div>

            {/* Features */}
            <div className="mb-12">
              <h3 className="text-2xl font-semibold text-gray-900 mb-6">我們提供</h3>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div className="flex items-start space-x-4">
                  <div className="flex-shrink-0 w-12 h-12 bg-green-100 rounded-lg flex items-center justify-center">
                    <span className="text-2xl">🎓</span>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-900 mb-1">系統化課程</h4>
                    <p className="text-gray-600 text-sm">從基礎到進階，循序漸進的學習路徑</p>
                  </div>
                </div>
                <div className="flex items-start space-x-4">
                  <div className="flex-shrink-0 w-12 h-12 bg-blue-100 rounded-lg flex items-center justify-center">
                    <span className="text-2xl">💪</span>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-900 mb-1">實戰練習</h4>
                    <p className="text-gray-600 text-sm">透過真實專案累積開發經驗</p>
                  </div>
                </div>
                <div className="flex items-start space-x-4">
                  <div className="flex-shrink-0 w-12 h-12 bg-purple-100 rounded-lg flex items-center justify-center">
                    <span className="text-2xl">👥</span>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-900 mb-1">社群支持</h4>
                    <p className="text-gray-600 text-sm">與志同道合的學習夥伴一起成長</p>
                  </div>
                </div>
                <div className="flex items-start space-x-4">
                  <div className="flex-shrink-0 w-12 h-12 bg-yellow-100 rounded-lg flex items-center justify-center">
                    <span className="text-2xl">🎮</span>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-900 mb-1">遊戲化學習</h4>
                    <p className="text-gray-600 text-sm">透過等級、成就系統提升學習動力</p>
                  </div>
                </div>
              </div>
            </div>

            {/* Contact */}
            <div className="bg-gray-50 rounded-lg p-6 text-center">
              <h3 className="text-xl font-semibold text-gray-900 mb-3">想了解更多？</h3>
              <p className="text-gray-600 mb-4">歡迎透過以下方式聯繫我們</p>
              <div className="flex justify-center space-x-4">
                <Link
                  href="/"
                  className="inline-flex items-center px-6 py-3 border border-transparent text-base font-medium rounded-lg text-white bg-green-600 hover:bg-green-700 transition-colors"
                >
                  返回首頁
                </Link>
                <Link
                  href="/courses"
                  className="inline-flex items-center px-6 py-3 border border-gray-300 text-base font-medium rounded-lg text-gray-700 bg-white hover:bg-gray-50 transition-colors"
                >
                  瀏覽課程
                </Link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
