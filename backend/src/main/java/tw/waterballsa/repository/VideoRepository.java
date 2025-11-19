package tw.waterballsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.waterballsa.model.Video;

/**
 * Repository interface for Video entity operations.
 * Provides CRUD operations and custom queries for learning videos.
 *
 * @author Water Ball SA
 */
@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    /**
     * Check if a video exists by its ID.
     *
     * @param videoId the video ID to check
     * @return true if video exists, false otherwise
     */
    boolean existsByVideoId(Long videoId);
}
