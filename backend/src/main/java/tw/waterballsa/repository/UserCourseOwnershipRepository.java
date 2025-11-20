package tw.waterballsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.waterballsa.model.UserCourseOwnership;

import java.util.List;
import java.util.Optional;

/**
 * Repository for UserCourseOwnership entity operations.
 *
 * @author Water Ball SA
 */
@Repository
public interface UserCourseOwnershipRepository extends JpaRepository<UserCourseOwnership, Long> {

    /**
     * Check if a user owns a specific course.
     */
    boolean existsByUser_UserIdAndCourse_CourseId(Long userId, Long courseId);

    /**
     * Find ownership record for a user and course.
     */
    Optional<UserCourseOwnership> findByUser_UserIdAndCourse_CourseId(Long userId, Long courseId);

    /**
     * Find all courses owned by a user.
     */
    @Query("SELECT o FROM UserCourseOwnership o WHERE o.user.userId = :userId ORDER BY o.purchasedAt DESC")
    List<UserCourseOwnership> findByUserIdOrderByPurchasedAtDesc(@Param("userId") Long userId);

    /**
     * Find all users who own a specific course.
     */
    @Query("SELECT o FROM UserCourseOwnership o WHERE o.course.courseId = :courseId")
    List<UserCourseOwnership> findByCourseId(@Param("courseId") Long courseId);

    /**
     * Count total users who own a course.
     */
    long countByCourse_CourseId(Long courseId);
}
