package tw.waterballsa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.waterballsa.model.User;
import tw.waterballsa.model.UserVideoProgress;
import tw.waterballsa.model.Video;
import tw.waterballsa.repository.UserRepository;
import tw.waterballsa.repository.UserVideoProgressRepository;
import tw.waterballsa.repository.VideoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing user video watch progress.
 * Handles saving, retrieving, and updating video progress for resume functionality.
 *
 * @author Water Ball SA
 */
@Service
@Transactional(readOnly = true)
public class VideoProgressService {

    @Autowired
    private UserVideoProgressRepository progressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoCompletionService videoCompletionService;

    /**
     * Save or update video watch progress.
     * Automatically completes video if watch percentage >= 95%.
     *
     * @param userId current position in seconds
     * @param videoId the video ID
     * @param currentPositionSeconds current playback position in seconds
     * @return the updated progress
     */
    @Transactional
    public UserVideoProgress saveProgress(Long userId, Long videoId, int currentPositionSeconds) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者"));

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("找不到影片"));

        // Find or create progress
        UserVideoProgress progress = progressRepository
                .findByUser_UserIdAndVideo_VideoId(userId, videoId)
                .orElse(new UserVideoProgress(user, video));

        // Update progress
        progress.updateProgress(currentPositionSeconds, video.getDurationSeconds());

        // Save progress
        progress = progressRepository.save(progress);

        // Auto-complete video if >= 95%
        if (progress.isCompleted()) {
            videoCompletionService.completeVideo(userId, videoId);
        }

        return progress;
    }

    /**
     * Get video progress for a user.
     *
     * @param userId the user ID
     * @param videoId the video ID
     * @return the progress, or empty if not found
     */
    public Optional<UserVideoProgress> getProgress(Long userId, Long videoId) {
        return progressRepository.findByUser_UserIdAndVideo_VideoId(userId, videoId);
    }

    /**
     * Get all video progress for a user in a course.
     *
     * @param userId the user ID
     * @param courseId the course ID
     * @return list of progress
     */
    public List<UserVideoProgress> getCourseProgress(Long userId, Long courseId) {
        return progressRepository.findByUserIdAndCourseId(userId, courseId);
    }

    /**
     * Get all video progress for a user.
     *
     * @param userId the user ID
     * @return list of all progress
     */
    public List<UserVideoProgress> getAllProgress(Long userId) {
        return progressRepository.findByUser_UserId(userId);
    }

    /**
     * Delete progress for a video.
     *
     * @param userId the user ID
     * @param videoId the video ID
     */
    @Transactional
    public void deleteProgress(Long userId, Long videoId) {
        progressRepository.deleteByUser_UserIdAndVideo_VideoId(userId, videoId);
    }

    /**
     * Get resume position for a video.
     *
     * @param userId the user ID
     * @param videoId the video ID
     * @return the last position in seconds, or 0 if no progress
     */
    public int getResumePosition(Long userId, Long videoId) {
        return progressRepository.findByUser_UserIdAndVideo_VideoId(userId, videoId)
                .map(UserVideoProgress::getLastPositionSeconds)
                .orElse(0);
    }
}
