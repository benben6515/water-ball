import api, { getErrorMessage } from './api';

/**
 * Video Completion API Client
 *
 * Provides methods to interact with video completion endpoints.
 * Handles marking videos as complete and checking completion status.
 */

/**
 * Response from completing a video.
 */
export interface VideoCompletionResponse {
  completion_id: number;
  video_id: number;
  user_id: number;
  exp_awarded: number;
  leveled_up: boolean;
  current_level: number;
  current_exp: number;
  exp_for_next_level: number;
  exp_progress_percentage: number;
  completed_at: string;
  already_completed: boolean;
}

/**
 * Response for checking video completion status.
 */
export interface VideoCompletionStatusResponse {
  video_id: number;
  is_completed: boolean;
  completed_at?: string;
  exp_awarded?: number;
}

/**
 * Mark a video as completed and receive exp reward.
 * Idempotent - if already completed, returns existing completion without awarding additional exp.
 *
 * @param videoId - The ID of the video to complete
 * @returns VideoCompletionResponse with completion details and updated exp/level
 * @throws Error if video doesn't exist or user is not authenticated
 */
export async function completeVideo(videoId: number): Promise<VideoCompletionResponse> {
  try {
    const response = await api.post<VideoCompletionResponse>(`/api/videos/${videoId}/complete`);
    return response.data;
  } catch (error) {
    const message = getErrorMessage(error);
    throw new Error(`完成影片失敗: ${message}`);
  }
}

/**
 * Check if the current user has completed a specific video.
 *
 * @param videoId - The ID of the video to check
 * @returns VideoCompletionStatusResponse with completion status
 * @throws Error if video doesn't exist or user is not authenticated
 */
export async function getVideoCompletionStatus(videoId: number): Promise<VideoCompletionStatusResponse> {
  try {
    const response = await api.get<VideoCompletionStatusResponse>(`/api/videos/${videoId}/completion-status`);
    return response.data;
  } catch (error) {
    const message = getErrorMessage(error);
    throw new Error(`取得影片完成狀態失敗: ${message}`);
  }
}

export const videoApi = {
  completeVideo,
  getVideoCompletionStatus,
};

export default videoApi;
