package tw.waterballsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.waterballsa.model.UserVideoProgress;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserVideoProgress entities.
 * Manages video watch progress data.
 *
 * @author Water Ball SA
 */
@Repository
public interface UserVideoProgressRepository extends JpaRepository<UserVideoProgress, Long> {

    /**
     * Find progress by user ID and video ID.
     */
    Optional<UserVideoProgress> findByUser_UserIdAndVideo_VideoId(Long userId, Long videoId);

    /**
     * Find all progress for a user.
     */
    List<UserVideoProgress> findByUser_UserId(Long userId);

    /**
     * Find all progress for a video.
     */
    List<UserVideoProgress> findByVideo_VideoId(Long videoId);

    /**
     * Find all progress for a user in a specific course.
     */
    @Query("SELECT uvp FROM UserVideoProgress uvp WHERE uvp.user.userId = :userId AND uvp.video.dungeon.course.courseId = :courseId")
    List<UserVideoProgress> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * Delete progress by user ID and video ID.
     */
    void deleteByUser_UserIdAndVideo_VideoId(Long userId, Long videoId);

    /**
     * Check if progress exists for user and video.
     */
    boolean existsByUser_UserIdAndVideo_VideoId(Long userId, Long videoId);
}
