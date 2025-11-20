package tw.waterballsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.waterballsa.model.OrderItem;

import java.util.List;

/**
 * Repository for OrderItem entity operations.
 *
 * @author Water Ball SA
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    /**
     * Find all order items for a specific order.
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.orderId = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);

    /**
     * Find all order items for a specific course.
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.course.courseId = :courseId")
    List<OrderItem> findByCourseId(@Param("courseId") Long courseId);

    /**
     * Count how many times a course has been purchased.
     */
    long countByCourse_CourseId(Long courseId);
}
