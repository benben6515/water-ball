'use client';

import { useEffect, useState, useRef } from 'react';
import { useParams, useRouter } from 'next/navigation';
import api from '@/lib/api';

interface Video {
  videoId: number;
  title: string;
  description: string;
  videoUrl: string;
  durationSeconds: number;
  expReward: number;
  completed: boolean;
  demo: boolean;
  dungeonId: number;
  orderIndex: number;
}

interface Dungeon {
  dungeonId: number;
  title: string;
  videos: Video[];
}

interface Course {
  courseId: number;
  title: string;
  owned: boolean;
  free: boolean;
  dungeons: Dungeon[];
}

/**
 * Video Player Page
 * Features:
 * - Resume from last watched position
 * - Auto-save progress every 5 seconds
 * - Auto-complete at 95%
 * - Auto-advance to next video with 5-second countdown
 * - Playback speed control
 */
export default function VideoPlayerPage() {
  const params = useParams();
  const router = useRouter();
  const courseId = params.courseId as string;
  const videoId = params.videoId as string;

  const videoRef = useRef<HTMLVideoElement>(null);
  const [course, setCourse] = useState<Course | null>(null);
  const [currentVideo, setCurrentVideo] = useState<Video | null>(null);
  const [nextVideo, setNextVideo] = useState<Video | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Player state
  const [isPlaying, setIsPlaying] = useState(false);
  const [playbackSpeed, setPlaybackSpeed] = useState(1);
  const [showSpeedMenu, setShowSpeedMenu] = useState(false);
  const [watchPercentage, setWatchPercentage] = useState(0);
  const [hasCompleted, setHasCompleted] = useState(false);

  // Auto-advance countdown
  const [showCountdown, setShowCountdown] = useState(false);
  const [countdown, setCountdown] = useState(5);
  const countdownTimerRef = useRef<NodeJS.Timeout | null>(null);

  // Progress tracking
  const lastSavedPosition = useRef(0);
  const progressSaveTimer = useRef<NodeJS.Timeout | null>(null);

  // Fetch course and video data
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const response = await api.get(`/api/courses/${courseId}`);
        const courseData: Course = response.data;
        setCourse(courseData);

        // Find current video
        let foundVideo: Video | null = null;
        let foundNext: Video | null = null;
        let allVideos: Video[] = [];

        courseData.dungeons.forEach(dungeon => {
          allVideos = [...allVideos, ...dungeon.videos];
        });

        const currentIndex = allVideos.findIndex(v => v.videoId === parseInt(videoId));
        if (currentIndex >= 0) {
          foundVideo = allVideos[currentIndex];
          if (currentIndex < allVideos.length - 1) {
            foundNext = allVideos[currentIndex + 1];
          }
        }

        setCurrentVideo(foundVideo);
        setNextVideo(foundNext);
        setError(null);
      } catch (err: any) {
        console.error('Failed to fetch course:', err);
        setError('無法載入影片資料');
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [courseId, videoId]);

  // Load saved progress and resume
  useEffect(() => {
    if (!currentVideo) return;

    const loadProgress = async () => {
      try {
        const response = await api.get(`/api/videos/${videoId}/progress`);
        const { lastPositionSeconds, isCompleted } = response.data;

        setHasCompleted(isCompleted);

        // Resume from last position if not completed
        if (!isCompleted && lastPositionSeconds > 0 && videoRef.current) {
          videoRef.current.currentTime = lastPositionSeconds;
          lastSavedPosition.current = lastPositionSeconds;
        }
      } catch (err: any) {
        // Silently skip progress loading if not authenticated (401)
        // This allows watching demo/free videos without logging in
        if (err.response?.status !== 401) {
          console.error('Failed to load progress:', err);
        }
      }
    };

    loadProgress();
  }, [videoId, currentVideo]);

  // Auto-save progress every 5 seconds
  useEffect(() => {
    if (!videoRef.current || !currentVideo) return;

    const saveProgress = async () => {
      if (!videoRef.current) return;

      const currentTime = Math.floor(videoRef.current.currentTime);
      const duration = currentVideo.durationSeconds;

      // Only save if position changed by at least 2 seconds
      if (Math.abs(currentTime - lastSavedPosition.current) < 2) return;

      try {
        const response = await api.post(`/api/videos/${videoId}/progress`, {
          currentPositionSeconds: currentTime
        });

        lastSavedPosition.current = currentTime;
        setWatchPercentage(parseFloat(response.data.watchPercentage));

        // Check if completed (95%)
        if (response.data.isCompleted && !hasCompleted) {
          setHasCompleted(true);
          // Completion will be handled by backend automatically
        }
      } catch (err: any) {
        // Silently skip progress saving if not authenticated (401)
        // This allows watching demo/free videos without logging in
        if (err.response?.status !== 401) {
          console.error('Failed to save progress:', err);
        }
      }
    };

    // Save progress every 5 seconds
    progressSaveTimer.current = setInterval(saveProgress, 5000);

    return () => {
      if (progressSaveTimer.current) {
        clearInterval(progressSaveTimer.current);
      }
    };
  }, [videoId, currentVideo, hasCompleted]);

  // Handle video end - show countdown for next video
  useEffect(() => {
    const video = videoRef.current;
    if (!video || !course) return;

    const handleVideoEnd = () => {
      // Check if next video is accessible (demo, owned, or free course)
      const canAccessNextVideo = nextVideo && (
        nextVideo.demo ||
        course.owned ||
        course.free
      );

      if (canAccessNextVideo) {
        setShowCountdown(true);
        setCountdown(5);

        countdownTimerRef.current = setInterval(() => {
          setCountdown(prev => {
            if (prev <= 1) {
              if (countdownTimerRef.current) clearInterval(countdownTimerRef.current);
              return 0;
            }
            return prev - 1;
          });
        }, 1000);
      }
    };

    video.addEventListener('ended', handleVideoEnd);

    return () => {
      video.removeEventListener('ended', handleVideoEnd);
      if (countdownTimerRef.current) {
        clearInterval(countdownTimerRef.current);
      }
    };
  }, [nextVideo, course, courseId, router]);

  // Auto-advance when countdown reaches 0
  useEffect(() => {
    if (countdown === 0 && showCountdown && nextVideo) {
      router.push(`/courses/${courseId}/videos/${nextVideo.videoId}`);
    }
  }, [countdown, showCountdown, nextVideo, courseId, router]);

  // Cancel countdown
  const cancelCountdown = () => {
    if (countdownTimerRef.current) {
      clearInterval(countdownTimerRef.current);
    }
    setShowCountdown(false);
    setCountdown(5);
  };

  // Playback control
  const togglePlay = () => {
    if (!videoRef.current) return;

    if (videoRef.current.paused) {
      videoRef.current.play();
      setIsPlaying(true);
    } else {
      videoRef.current.pause();
      setIsPlaying(false);
    }
  };

  // Speed control
  const changeSpeed = (speed: number) => {
    if (videoRef.current) {
      videoRef.current.playbackRate = speed;
      setPlaybackSpeed(speed);
      setShowSpeedMenu(false);
    }
  };

  // Navigate to next/previous video
  const goToNextVideo = () => {
    if (nextVideo) {
      router.push(`/courses/${courseId}/videos/${nextVideo.videoId}`);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-black flex items-center justify-center">
        <div className="animate-spin rounded-full h-16 w-16 border-b-2 border-white"></div>
      </div>
    );
  }

  if (error || !currentVideo) {
    return (
      <div className="min-h-screen bg-black flex items-center justify-center text-white">
        <div className="text-center">
          <p className="text-xl mb-4">{error || '找不到影片'}</p>
          <button
            onClick={() => router.push(`/courses/${courseId}`)}
            className="px-6 py-2 bg-blue-600 rounded-lg hover:bg-blue-700"
          >
            返回課程
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-black">
      {/* Video Player */}
      <div className="relative w-full" style={{ paddingTop: '56.25%' }}>
        <video
          ref={videoRef}
          className="absolute top-0 left-0 w-full h-full"
          src={currentVideo.videoUrl}
          controls
          onPlay={() => setIsPlaying(true)}
          onPause={() => setIsPlaying(false)}
        />

        {/* Countdown Overlay */}
        {showCountdown && nextVideo && (
          <div className="absolute inset-0 flex items-center justify-center bg-black bg-opacity-80 z-50">
            <div className="text-center text-white">
              <h3 className="text-2xl mb-4">影片已結束</h3>
              <p className="text-lg mb-6">
                {countdown} 秒後自動播放下一個影片
              </p>
              <p className="text-gray-300 mb-6">{nextVideo.title}</p>
              <div className="flex gap-4 justify-center">
                <button
                  onClick={cancelCountdown}
                  className="px-6 py-2 bg-gray-600 rounded-lg hover:bg-gray-700"
                >
                  取消
                </button>
                <button
                  onClick={goToNextVideo}
                  className="px-6 py-2 bg-blue-600 rounded-lg hover:bg-blue-700"
                >
                  立即播放
                </button>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Video Info and Controls */}
      <div className="max-w-7xl mx-auto px-4 py-6">
        {/* Title and Stats */}
        <div className="flex items-start justify-between mb-4">
          <div className="flex-1">
            <h1 className="text-2xl font-bold text-white mb-2">
              {currentVideo.title}
            </h1>
            <p className="text-gray-400 mb-4">{currentVideo.description}</p>
            <div className="flex items-center gap-4 text-sm text-gray-400">
              <span>⏱️ {Math.floor(currentVideo.durationSeconds / 60)} 分鐘</span>
              {!currentVideo.demo && (
                <span>⭐ +{currentVideo.expReward} EXP</span>
              )}
              {hasCompleted && (
                <span className="px-3 py-1 bg-green-600 text-white rounded-full text-xs">
                  ✓ 已完成
                </span>
              )}
              {watchPercentage > 0 && (
                <span className="text-blue-400">
                  進度: {watchPercentage.toFixed(0)}%
                </span>
              )}
            </div>
          </div>

          {/* Speed Control */}
          <div className="relative">
            <button
              onClick={() => setShowSpeedMenu(!showSpeedMenu)}
              className="px-4 py-2 bg-gray-800 text-white rounded-lg hover:bg-gray-700"
            >
              速度: {playbackSpeed}x
            </button>
            {showSpeedMenu && (
              <div className="absolute right-0 mt-2 bg-gray-800 rounded-lg shadow-lg py-2 z-10">
                {[0.5, 0.75, 1, 1.25, 1.5, 1.75, 2].map(speed => (
                  <button
                    key={speed}
                    onClick={() => changeSpeed(speed)}
                    className={`block w-full px-6 py-2 text-left hover:bg-gray-700 ${
                      playbackSpeed === speed ? 'text-blue-400' : 'text-white'
                    }`}
                  >
                    {speed}x {playbackSpeed === speed && '✓'}
                  </button>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Navigation */}
        <div className="flex gap-4">
          <button
            onClick={() => router.push(`/courses/${courseId}`)}
            className="px-6 py-2 bg-gray-800 text-white rounded-lg hover:bg-gray-700"
          >
            ← 返回課程
          </button>
          {nextVideo && (
            <button
              onClick={goToNextVideo}
              className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
            >
              下一個影片 →
            </button>
          )}
        </div>

        {/* Course Progress */}
        {course && (
          <div className="mt-8 bg-gray-900 rounded-lg p-6">
            <h3 className="text-xl font-bold text-white mb-4">課程內容</h3>
            {course.dungeons.map(dungeon => (
              <div key={dungeon.dungeonId} className="mb-4">
                <h4 className="text-lg font-semibold text-gray-300 mb-2">
                  {dungeon.title}
                </h4>
                <div className="space-y-2">
                  {dungeon.videos.map(video => (
                    <button
                      key={video.videoId}
                      onClick={() => router.push(`/courses/${courseId}/videos/${video.videoId}`)}
                      className={`w-full text-left px-4 py-2 rounded-lg transition-colors ${
                        video.videoId === currentVideo.videoId
                          ? 'bg-blue-600 text-white'
                          : 'bg-gray-800 text-gray-300 hover:bg-gray-700'
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <span>{video.title}</span>
                        {video.completed && (
                          <span className="text-green-400">✓</span>
                        )}
                      </div>
                    </button>
                  ))}
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
