package tw.waterballsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.waterballsa.model.Course;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Course entity operations.
 *
 * @author Water Ball SA
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Find all published courses ordered by creation date (newest first).
     */
    @Query("SELECT c FROM Course c WHERE c.isPublished = true ORDER BY c.createdAt DESC")
    List<Course> findAllPublishedCourses();

    /**
     * Find a published course by ID with dungeons eagerly loaded.
     */
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.dungeons WHERE c.courseId = :courseId AND c.isPublished = true")
    Optional<Course> findPublishedCourseWithDungeons(@Param("courseId") Long courseId);

    /**
     * Find a course by ID with dungeons eagerly loaded (admin access).
     */
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.dungeons WHERE c.courseId = :courseId")
    Optional<Course> findCourseWithDungeons(@Param("courseId") Long courseId);

    /**
     * Check if a course exists and is published.
     */
    boolean existsByCourseIdAndIsPublished(Long courseId, Boolean isPublished);

    /**
     * Find all courses (including unpublished) for admin purposes.
     */
    @Query("SELECT c FROM Course c ORDER BY c.createdAt DESC")
    List<Course> findAllCoursesAdmin();
}
