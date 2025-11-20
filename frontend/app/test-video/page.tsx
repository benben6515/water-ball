'use client';

import React from 'react';
import VideoCompletionCheckbox from '@/components/VideoCompletionCheckbox';

/**
 * Test page for video completion system.
 * Demonstrates the video completion checkbox component and exp reward flow.
 */
export default function TestVideoPage() {
  const videoId = 1; // Test video ID

  const handleLevelUp = (newLevel: number) => {
    console.log('User leveled up to:', newLevel);
  };

  const handleExpAwarded = (expAwarded: number, newExp: number) => {
    console.log('Exp awarded:', expAwarded, 'New total exp:', newExp);
  };

  return (
    <div className="min-h-screen bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-3xl mx-auto">
        <div className="bg-white shadow-lg rounded-lg overflow-hidden">
          {/* Video Header */}
          <div className="px-6 py-4 bg-gradient-to-r from-blue-500 to-blue-600">
            <h1 className="text-2xl font-bold text-white">
              測試影片：Spring Boot 入門
            </h1>
            <p className="text-blue-100 mt-1">
              這是一個測試影片，用於驗證經驗值系統
            </p>
          </div>

          {/* Video Player Placeholder */}
          <div className="aspect-w-16 aspect-h-9 bg-gray-900">
            <div className="flex items-center justify-center h-96">
              <div className="text-center">
                <div className="text-6xl mb-4">🎥</div>
                <p className="text-gray-400 text-lg">影片播放器占位符</p>
                <p className="text-gray-500 text-sm mt-2">
                  真實環境中，這裡會是實際的影片播放器
                </p>
              </div>
            </div>
          </div>

          {/* Video Details */}
          <div className="px-6 py-4 border-b border-gray-200">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-lg font-medium text-gray-900">
                  影片時長：10 分鐘
                </h2>
                <p className="text-sm text-gray-500 mt-1">
                  完成影片可獲得 200 經驗值
                </p>
              </div>
              <div className="flex items-center space-x-2">
                <span className="inline-flex items-center px-3 py-1 rounded-full text-sm font-medium bg-yellow-100 text-yellow-800">
                  ⭐ +200 EXP
                </span>
              </div>
            </div>
          </div>

          {/* Completion Checkbox */}
          <div className="px-6 py-6 bg-gray-50">
            <div className="flex items-center justify-between">
              <div>
                <h3 className="text-base font-medium text-gray-900">
                  完成影片
                </h3>
                <p className="text-sm text-gray-500 mt-1">
                  觀看完影片後，勾選此處獲得經驗值獎勵
                </p>
              </div>
              <div className="ml-4">
                <VideoCompletionCheckbox
                  videoId={videoId}
                  onLevelUp={handleLevelUp}
                  onExpAwarded={handleExpAwarded}
                />
              </div>
            </div>
          </div>

          {/* Instructions */}
          <div className="px-6 py-4 bg-blue-50 border-t border-blue-100">
            <div className="flex">
              <div className="flex-shrink-0">
                <svg
                  className="h-5 w-5 text-blue-400"
                  xmlns="http://www.w3.org/2000/svg"
                  viewBox="0 0 20 20"
                  fill="currentColor"
                >
                  <path
                    fillRule="evenodd"
                    d="M18 10a8 8 0 11-16 0 8 8 0 0116 0zm-7-4a1 1 0 11-2 0 1 1 0 012 0zM9 9a1 1 0 000 2v3a1 1 0 001 1h1a1 1 0 100-2v-3a1 1 0 00-1-1H9z"
                    clipRule="evenodd"
                  />
                </svg>
              </div>
              <div className="ml-3">
                <h3 className="text-sm font-medium text-blue-800">測試說明</h3>
                <div className="mt-2 text-sm text-blue-700">
                  <ul className="list-disc list-inside space-y-1">
                    <li>這是一個測試頁面，用於驗證影片完成系統</li>
                    <li>勾選「標記為完成」後，您將獲得 200 經驗值</li>
                    <li>系統會自動計算您的等級（基於經驗值）</li>
                    <li>每個影片只能完成一次（冪等性）</li>
                    <li>如果升級，會顯示升級通知</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Debug Info */}
        <div className="mt-6 bg-white shadow rounded-lg px-6 py-4">
          <h3 className="text-lg font-medium text-gray-900 mb-3">調試信息</h3>
          <div className="space-y-2 text-sm">
            <div className="flex justify-between">
              <span className="text-gray-600">影片 ID:</span>
              <span className="font-mono font-medium">{videoId}</span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">後端 API:</span>
              <span className="font-mono text-xs">
                POST /api/videos/{videoId}/complete
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-gray-600">狀態查詢 API:</span>
              <span className="font-mono text-xs">
                GET /api/videos/{videoId}/completion-status
              </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
