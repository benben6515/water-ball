package tw.waterballsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.waterballsa.model.Dungeon;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Dungeon entity operations.
 *
 * @author Water Ball SA
 */
@Repository
public interface DungeonRepository extends JpaRepository<Dungeon, Long> {

    /**
     * Find all dungeons for a course ordered by order_index.
     */
    @Query("SELECT d FROM Dungeon d WHERE d.course.courseId = :courseId ORDER BY d.orderIndex ASC")
    List<Dungeon> findByCourseIdOrderByOrderIndex(@Param("courseId") Long courseId);

    /**
     * Find a dungeon by course ID and dungeon number.
     */
    Optional<Dungeon> findByCourse_CourseIdAndDungeonNumber(Long courseId, Integer dungeonNumber);

    /**
     * Find a dungeon with videos eagerly loaded.
     */
    @Query("SELECT d FROM Dungeon d LEFT JOIN FETCH d.videos WHERE d.dungeonId = :dungeonId")
    Optional<Dungeon> findDungeonWithVideos(@Param("dungeonId") Long dungeonId);

    /**
     * Count total dungeons in a course.
     */
    long countByCourse_CourseId(Long courseId);
}
