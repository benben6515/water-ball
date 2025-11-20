'use client';

import React, { useState, useEffect } from 'react';
import { completeVideo, getVideoCompletionStatus, VideoCompletionResponse } from '@/lib/videoApi';

interface VideoCompletionCheckboxProps {
  videoId: number;
  onLevelUp?: (newLevel: number) => void;
  onExpAwarded?: (expAwarded: number, newExp: number) => void;
}

/**
 * VideoCompletionCheckbox Component
 *
 * A checkbox component that allows users to mark videos as completed and earn exp rewards.
 * Features:
 * - Displays completion status (checked if completed)
 * - Awards 200 exp on first completion (idempotent)
 * - Shows level-up notification if user levels up
 * - Shows exp reward notification
 * - Persists state across page reloads
 *
 * @param videoId - The ID of the video
 * @param onLevelUp - Optional callback when user levels up
 * @param onExpAwarded - Optional callback when exp is awarded
 */
export default function VideoCompletionCheckbox({
  videoId,
  onLevelUp,
  onExpAwarded,
}: VideoCompletionCheckboxProps) {
  const [isCompleted, setIsCompleted] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showNotification, setShowNotification] = useState(false);
  const [notificationMessage, setNotificationMessage] = useState('');
  const [error, setError] = useState<string | null>(null);

  // Check completion status on mount
  useEffect(() => {
    const checkStatus = async () => {
      try {
        setIsLoading(true);
        const status = await getVideoCompletionStatus(videoId);
        setIsCompleted(status.is_completed);
      } catch (err) {
        console.error('Failed to check video completion status:', err);
        // Don't show error to user for status check failures
      } finally {
        setIsLoading(false);
      }
    };

    checkStatus();
  }, [videoId]);

  const handleCheckboxChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const checked = e.target.checked;

    // If unchecking, prevent it (videos can only be completed once)
    if (!checked && isCompleted) {
      e.preventDefault();
      return;
    }

    // If checking, mark video as complete
    if (checked && !isCompleted) {
      setIsSubmitting(true);
      setError(null);

      try {
        const response: VideoCompletionResponse = await completeVideo(videoId);

        setIsCompleted(true);

        // Show notification
        if (response.already_completed) {
          setNotificationMessage('您已經完成過此影片了！');
        } else if (response.leveled_up) {
          setNotificationMessage(
            `🎉 恭喜！獲得 ${response.exp_awarded} 經驗值，升級到 Lv.${response.current_level}！`
          );
          onLevelUp?.(response.current_level);
        } else {
          setNotificationMessage(`✅ 獲得 ${response.exp_awarded} 經驗值！`);
        }

        setShowNotification(true);

        // Call callbacks
        if (!response.already_completed) {
          onExpAwarded?.(response.exp_awarded, response.current_exp);
        }

        // Hide notification after 5 seconds
        setTimeout(() => {
          setShowNotification(false);
        }, 5000);
      } catch (err) {
        console.error('Failed to complete video:', err);
        setError(err instanceof Error ? err.message : '完成影片失敗');
        setIsCompleted(false);
      } finally {
        setIsSubmitting(false);
      }
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center space-x-2">
        <div className="w-5 h-5 border-2 border-gray-300 border-t-transparent rounded-full animate-spin"></div>
        <span className="text-sm text-gray-500">載入中...</span>
      </div>
    );
  }

  return (
    <div className="relative">
      <label className="flex items-center space-x-2 cursor-pointer">
        <input
          type="checkbox"
          checked={isCompleted}
          onChange={handleCheckboxChange}
          disabled={isSubmitting || isCompleted}
          className={`w-5 h-5 rounded border-2 transition-colors ${
            isCompleted
              ? 'bg-green-500 border-green-500 cursor-not-allowed'
              : 'border-gray-300 hover:border-blue-500'
          } ${isSubmitting ? 'opacity-50 cursor-wait' : ''}`}
        />
        <span className={`text-sm ${isCompleted ? 'text-green-600 font-medium' : 'text-gray-700'}`}>
          {isCompleted ? '已完成 ✓' : '標記為完成'}
        </span>
      </label>

      {/* Error message */}
      {error && (
        <div className="mt-2 text-sm text-red-600">
          {error}
        </div>
      )}

      {/* Notification popup */}
      {showNotification && (
        <div className="absolute top-full mt-2 left-0 right-0 bg-blue-600 text-white px-4 py-2 rounded-md shadow-lg animate-fade-in z-10">
          {notificationMessage}
        </div>
      )}
    </div>
  );
}
