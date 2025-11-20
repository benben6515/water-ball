package tw.waterballsa.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.waterballsa.dto.VideoCompletionResponse;
import tw.waterballsa.dto.VideoCompletionStatusResponse;
import tw.waterballsa.model.User;
import tw.waterballsa.model.UserVideoProgress;
import tw.waterballsa.model.VideoCompletion;
import tw.waterballsa.repository.UserRepository;
import tw.waterballsa.service.VideoCompletionService;
import tw.waterballsa.service.VideoProgressService;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API Controller for video completion operations.
 * Handles marking videos as complete and checking completion status.
 *
 * @author Water Ball SA
 */
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private static final Logger logger = LoggerFactory.getLogger(VideoController.class);

    private final VideoCompletionService videoCompletionService;
    private final VideoProgressService videoProgressService;
    private final UserRepository userRepository;

    public VideoController(VideoCompletionService videoCompletionService,
                            VideoProgressService videoProgressService,
                            UserRepository userRepository) {
        this.videoCompletionService = videoCompletionService;
        this.videoProgressService = videoProgressService;
        this.userRepository = userRepository;
    }

    /**
     * Mark a video as completed and award exp points.
     * Idempotent operation - if already completed, returns existing completion without awarding additional exp.
     *
     * POST /api/videos/{videoId}/complete
     *
     * @param videoId the video ID to complete
     * @param userId the authenticated user ID from JWT token
     * @return VideoCompletionResponse with completion details and updated exp/level
     */
    @PostMapping("/{videoId}/complete")
    public ResponseEntity<VideoCompletionResponse> completeVideo(
            @PathVariable Long videoId,
            @AuthenticationPrincipal Long userId) {

        logger.info("Received video completion request: videoId={}, userId={}", videoId, userId);

        // Get authenticated user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userId));

        // Check if already completed
        boolean wasAlreadyCompleted = videoCompletionService.hasCompletedVideo(user.getUserId(), videoId);

        // Get user's state before completion (for level-up detection)
        int levelBeforeCompletion = user.getLevel();

        // Complete the video (idempotent)
        VideoCompletion completion = videoCompletionService.completeVideo(user.getUserId(), videoId);

        // Refresh user to get updated exp/level
        user = userRepository.findById(user.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found after completion"));

        // Check if user leveled up
        boolean leveledUp = user.getLevel() > levelBeforeCompletion;

        // Build response
        VideoCompletionResponse response = new VideoCompletionResponse(
                completion.getCompletionId(),
                videoId,
                user.getUserId(),
                completion.getExpAwarded(),
                leveledUp,
                user.getLevel(),
                user.getExp(),
                user.getExpForNextLevel(),
                user.getExpProgressPercentage(),
                completion.getCompletedAt(),
                wasAlreadyCompleted
        );

        logger.info("Video completion processed: videoId={}, userId={}, leveledUp={}, alreadyCompleted={}",
                videoId, user.getUserId(), leveledUp, wasAlreadyCompleted);

        return ResponseEntity.ok(response);
    }

    /**
     * Check if the authenticated user has completed a specific video.
     *
     * GET /api/videos/{videoId}/completion-status
     *
     * @param videoId the video ID to check
     * @param userId the authenticated user ID from JWT token
     * @return VideoCompletionStatusResponse with completion status
     */
    @GetMapping("/{videoId}/completion-status")
    public ResponseEntity<VideoCompletionStatusResponse> getCompletionStatus(
            @PathVariable Long videoId,
            @AuthenticationPrincipal Long userId) {

        logger.info("Checking video completion status: videoId={}, userId={}", videoId, userId);

        // Get authenticated user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found: " + userId));

        // Check completion status
        VideoCompletion completion = videoCompletionService.getCompletion(user.getUserId(), videoId);

        VideoCompletionStatusResponse response;
        if (completion != null) {
            response = new VideoCompletionStatusResponse(
                    videoId,
                    true,
                    completion.getCompletedAt(),
                    completion.getExpAwarded()
            );
        } else {
            response = new VideoCompletionStatusResponse(
                    videoId,
                    false,
                    null,
                    null
            );
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Save video watch progress.
     * Auto-completes video if watch percentage >= 95%.
     *
     * POST /api/videos/{videoId}/progress
     *
     * Request body: { "currentPositionSeconds": 120 }
     *
     * @param videoId the video ID
     * @param userId the authenticated user ID
     * @param requestBody map containing currentPositionSeconds
     * @return progress data
     */
    @PostMapping("/{videoId}/progress")
    public ResponseEntity<?> saveProgress(
            @PathVariable Long videoId,
            @AuthenticationPrincipal Long userId,
            @RequestBody Map<String, Object> requestBody) {

        logger.info("Saving video progress: videoId={}, userId={}", videoId, userId);

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登入"));
        }

        try {
            int currentPositionSeconds = (Integer) requestBody.get("currentPositionSeconds");

            UserVideoProgress progress = videoProgressService.saveProgress(userId, videoId, currentPositionSeconds);

            Map<String, Object> response = new HashMap<>();
            response.put("watchPercentage", progress.getWatchPercentage());
            response.put("lastPositionSeconds", progress.getLastPositionSeconds());
            response.put("isCompleted", progress.isCompleted());
            response.put("updatedAt", progress.getUpdatedAt());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to save progress: videoId={}, userId={}", videoId, userId, e);
            return ResponseEntity.status(500).body(Map.of("error", "儲存進度失敗"));
        }
    }

    /**
     * Get video watch progress for resume functionality.
     *
     * GET /api/videos/{videoId}/progress
     *
     * @param videoId the video ID
     * @param userId the authenticated user ID
     * @return progress data or empty if no progress
     */
    @GetMapping("/{videoId}/progress")
    public ResponseEntity<?> getProgress(
            @PathVariable Long videoId,
            @AuthenticationPrincipal Long userId) {

        logger.info("Getting video progress: videoId={}, userId={}", videoId, userId);

        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("error", "未登入"));
        }

        try {
            var progress = videoProgressService.getProgress(userId, videoId);

            if (progress.isPresent()) {
                UserVideoProgress p = progress.get();
                Map<String, Object> response = new HashMap<>();
                response.put("watchPercentage", p.getWatchPercentage());
                response.put("lastPositionSeconds", p.getLastPositionSeconds());
                response.put("isCompleted", p.isCompleted());
                response.put("updatedAt", p.getUpdatedAt());
                return ResponseEntity.ok(response);
            } else {
                // No progress yet
                Map<String, Object> response = new HashMap<>();
                response.put("watchPercentage", 0);
                response.put("lastPositionSeconds", 0);
                response.put("isCompleted", false);
                response.put("updatedAt", null);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.error("Failed to get progress: videoId={}, userId={}", videoId, userId, e);
            return ResponseEntity.status(500).body(Map.of("error", "取得進度失敗"));
        }
    }
}
