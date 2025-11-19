package tw.waterballsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.waterballsa.model.VideoCompletion;

import java.util.Optional;

/**
 * Repository interface for VideoCompletion entity operations.
 * Provides methods to track and query video completions for exp rewards.
 * Ensures idempotent exp awards through unique constraint enforcement.
 *
 * @author Water Ball SA
 */
@Repository
public interface VideoCompletionRepository extends JpaRepository<VideoCompletion, Long> {

    /**
     * Check if a user has already completed a specific video.
     * Used to enforce idempotency - preventing duplicate exp awards.
     *
     * @param userId the user ID
     * @param videoId the video ID
     * @return true if user has completed this video, false otherwise
     */
    boolean existsByUserUserIdAndVideoVideoId(Long userId, Long videoId);

    /**
     * Find a video completion record by user and video.
     * Returns empty Optional if user hasn't completed this video.
     *
     * @param userId the user ID
     * @param videoId the video ID
     * @return Optional containing VideoCompletion if found, empty otherwise
     */
    Optional<VideoCompletion> findByUserUserIdAndVideoVideoId(Long userId, Long videoId);
}
