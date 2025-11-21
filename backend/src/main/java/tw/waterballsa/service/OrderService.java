package tw.waterballsa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.waterballsa.model.*;
import tw.waterballsa.repository.CourseRepository;
import tw.waterballsa.repository.OrderItemRepository;
import tw.waterballsa.repository.OrderRepository;
import tw.waterballsa.repository.UserRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service for order-related business logic.
 * Handles direct purchases, mock payment processing, and order history.
 *
 * @author Water Ball SA
 */
@Service
@Transactional(readOnly = true)
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseService courseService;

    /**
     * Create a direct purchase order for a single course (Buy Now flow).
     * Uses mock payment that automatically marks the order as paid.
     *
     * @param userId the ID of the purchasing user
     * @param courseId the ID of the course to purchase
     * @return the created and paid order
     * @throws IllegalArgumentException if user or course not found
     * @throws IllegalStateException if user already owns the course
     */
    @Transactional
    public Order createDirectPurchase(Long userId, Long courseId) {
        logger.info("Creating direct purchase: userId={}, courseId={}", userId, courseId);

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("找不到使用者"));

        // Fetch course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("找不到課程"));

        // Check if user already owns the course
        boolean alreadyOwns = courseService.userOwnsCourse(userId, courseId);
        logger.info("User ownership check: userId={}, courseId={}, alreadyOwns={}", userId, courseId, alreadyOwns);

        if (alreadyOwns) {
            throw new IllegalStateException("您已擁有此課程");
        }

        // Create order
        Order order = new Order(user);

        // Create order item
        OrderItem orderItem = new OrderItem(course, course.getPrice());
        order.addOrderItem(orderItem);

        // Save order (cascade will save order items)
        order = orderRepository.save(order);
        logger.info("Order created: orderId={}", order.getOrderId());

        // Process mock payment (automatically mark as paid)
        processMockPayment(order);
        logger.info("Payment processed: orderId={}, status={}", order.getOrderId(), order.getPaymentStatus());

        // Grant course ownership after successful payment
        courseService.grantCourseOwnership(user, course);
        logger.info("Course ownership granted: userId={}, courseId={}", userId, courseId);

        return order;
    }

    /**
     * Process mock payment for an order.
     * In a real system, this would integrate with a payment gateway.
     *
     * @param order the order to process payment for
     */
    @Transactional
    public void processMockPayment(Order order) {
        order.markAsPaid("MOCK");
        orderRepository.save(order);
    }

    /**
     * Get order history for a user.
     *
     * @param userId the user ID
     * @return list of orders, ordered by creation date (newest first)
     */
    public List<Order> getOrderHistory(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        // Force initialization of order items and their courses within transaction to avoid LazyInitializationException
        for (Order order : orders) {
            order.getOrderItems().size(); // Force initialization of order items
            for (OrderItem item : order.getOrderItems()) {
                item.getCourse().getTitle(); // Force initialization of course
            }
        }

        return orders;
    }

    /**
     * Get a specific order by ID for a user.
     *
     * @param orderId the order ID
     * @param userId the user ID (for authorization)
     * @return Optional containing the order if found and belongs to user
     */
    public Optional<Order> getOrderById(Long orderId, Long userId) {
        Optional<Order> orderOpt = orderRepository.findByOrderIdAndUserId(orderId, userId);

        // Force initialization of order items and their courses within transaction
        orderOpt.ifPresent(order -> {
            order.getOrderItems().size();
            for (OrderItem item : order.getOrderItems()) {
                item.getCourse().getTitle();
            }
        });

        return orderOpt;
    }

    /**
     * Get all paid orders for a user.
     *
     * @param userId the user ID
     * @return list of paid orders
     */
    public List<Order> getPaidOrders(Long userId) {
        List<Order> orders = orderRepository.findPaidOrdersByUserId(userId);

        // Force initialization of order items and their courses within transaction
        for (Order order : orders) {
            order.getOrderItems().size();
            for (OrderItem item : order.getOrderItems()) {
                item.getCourse().getTitle();
            }
        }

        return orders;
    }

    /**
     * Cancel an order (only if not yet paid).
     *
     * @param orderId the order ID
     * @param userId the user ID (for authorization)
     * @throws IllegalArgumentException if order not found
     * @throws IllegalStateException if order is already paid
     */
    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findByOrderIdAndUserId(orderId, userId)
                .orElseThrow(() -> new IllegalArgumentException("找不到訂單"));

        if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
            throw new IllegalStateException("已付款的訂單無法取消");
        }

        order.cancel();
        orderRepository.save(order);
    }

    /**
     * Get total number of orders for a user.
     *
     * @param userId the user ID
     * @return count of orders
     */
    public long getOrderCount(Long userId) {
        return orderRepository.countByUser_UserId(userId);
    }
}
