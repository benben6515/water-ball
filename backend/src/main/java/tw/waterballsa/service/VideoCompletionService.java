package tw.waterballsa.service;

import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tw.waterballsa.model.User;
import tw.waterballsa.model.Video;
import tw.waterballsa.model.VideoCompletion;
import tw.waterballsa.repository.UserRepository;
import tw.waterballsa.repository.VideoCompletionRepository;
import tw.waterballsa.repository.VideoRepository;

/**
 * Service for handling video completion logic and exp rewards.
 * Ensures idempotent exp awards - users can only complete a video once.
 *
 * @author Water Ball SA
 */
@Service
public class VideoCompletionService {

    private static final Logger logger = LoggerFactory.getLogger(VideoCompletionService.class);

    private final VideoCompletionRepository videoCompletionRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    public VideoCompletionService(VideoCompletionRepository videoCompletionRepository,
                                   VideoRepository videoRepository,
                                   UserRepository userRepository) {
        this.videoCompletionRepository = videoCompletionRepository;
        this.videoRepository = videoRepository;
        this.userRepository = userRepository;
    }

    /**
     * Mark a video as completed by a user and award exp points.
     * This operation is idempotent - if the user already completed this video,
     * no additional exp is awarded and the existing completion is returned.
     *
     * @param userId the user ID who completed the video
     * @param videoId the video ID that was completed
     * @return VideoCompletion record (existing or newly created)
     * @throws IllegalArgumentException if user or video doesn't exist
     */
    @Transactional
    public VideoCompletion completeVideo(Long userId, Long videoId) {
        logger.info("Processing video completion: userId={}, videoId={}", userId, videoId);

        // Check if already completed (idempotency)
        if (videoCompletionRepository.existsByUserUserIdAndVideoVideoId(userId, videoId)) {
            logger.info("Video already completed by user: userId={}, videoId={}", userId, videoId);
            return videoCompletionRepository.findByUserUserIdAndVideoVideoId(userId, videoId)
                    .orElseThrow(() -> new IllegalStateException("Video completion exists but couldn't be retrieved"));
        }

        // Fetch user and video
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("Video not found: " + videoId));

        // Award exp to user (demo videos don't award exp)
        int expAwarded = video.getIsDemo() ? 0 : video.getExpReward();
        boolean leveledUp = false;

        if (expAwarded > 0) {
            leveledUp = user.addExp(expAwarded);
            // Save user with updated exp
            userRepository.save(user);
        }

        // Create completion record
        VideoCompletion completion = new VideoCompletion(user, video, expAwarded);
        VideoCompletion savedCompletion = videoCompletionRepository.save(completion);

        logger.info("Video completed successfully: userId={}, videoId={}, expAwarded={}, leveledUp={}",
                userId, videoId, expAwarded, leveledUp);

        return savedCompletion;
    }

    /**
     * Check if a user has completed a specific video.
     *
     * @param userId the user ID to check
     * @param videoId the video ID to check
     * @return true if user has completed this video, false otherwise
     */
    public boolean hasCompletedVideo(Long userId, Long videoId) {
        return videoCompletionRepository.existsByUserUserIdAndVideoVideoId(userId, videoId);
    }

    /**
     * Get the completion record for a user and video.
     *
     * @param userId the user ID
     * @param videoId the video ID
     * @return VideoCompletion if exists, null otherwise
     */
    public VideoCompletion getCompletion(Long userId, Long videoId) {
        return videoCompletionRepository.findByUserUserIdAndVideoVideoId(userId, videoId)
                .orElse(null);
    }
}
