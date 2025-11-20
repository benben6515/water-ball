package tw.waterballsa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tw.waterballsa.model.Order;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Order entity operations.
 *
 * @author Water Ball SA
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find all orders for a specific user, ordered by creation date (newest first).
     */
    @Query("SELECT o FROM Order o WHERE o.user.userId = :userId ORDER BY o.createdAt DESC")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    /**
     * Find a specific order for a user.
     */
    @Query("SELECT o FROM Order o WHERE o.orderId = :orderId AND o.user.userId = :userId")
    Optional<Order> findByOrderIdAndUserId(@Param("orderId") Long orderId, @Param("userId") Long userId);

    /**
     * Find all paid orders for a user.
     */
    @Query("SELECT o FROM Order o WHERE o.user.userId = :userId AND o.paymentStatus = 'PAID' ORDER BY o.createdAt DESC")
    List<Order> findPaidOrdersByUserId(@Param("userId") Long userId);

    /**
     * Count total orders for a user.
     */
    long countByUser_UserId(Long userId);
}
