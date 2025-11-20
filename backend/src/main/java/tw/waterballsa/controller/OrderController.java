package tw.waterballsa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.waterballsa.dto.OrderResponse;
import tw.waterballsa.model.Order;
import tw.waterballsa.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for order-related endpoints.
 * Handles course purchases and order history.
 *
 * @author Water Ball SA
 */
@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "${cors.allowed-origins}", allowCredentials = "true")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * POST /api/orders/purchase - Create a direct purchase order.
     * Requires authentication.
     *
     * @param courseId the ID of the course to purchase
     * @param userId the authenticated user ID
     * @return the created order with payment status
     */
    @PostMapping("/purchase")
    public ResponseEntity<?> createPurchase(
        @RequestParam Long courseId,
        @AuthenticationPrincipal Long userId
    ) {
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", "請先登入");
            error.put("code", "AUTHENTICATION_REQUIRED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            Order order = orderService.createDirectPurchase(userId, courseId);
            OrderResponse response = new OrderResponse(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Bad Request");
            error.put("message", e.getMessage());
            error.put("code", "INVALID_REQUEST");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (IllegalStateException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Conflict");
            error.put("message", e.getMessage());
            error.put("code", "ALREADY_OWNED");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Internal Server Error");
            error.put("message", "購買失敗，請稍後再試");
            error.put("code", "PURCHASE_FAILED");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * GET /api/orders - Get order history for authenticated user.
     * Requires authentication.
     *
     * @param userId the authenticated user ID
     * @return list of orders ordered by creation date (newest first)
     */
    @GetMapping
    public ResponseEntity<?> getOrderHistory(
        @AuthenticationPrincipal Long userId
    ) {
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", "請先登入");
            error.put("code", "AUTHENTICATION_REQUIRED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        List<Order> orders = orderService.getOrderHistory(userId);
        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/orders/{orderId} - Get a specific order by ID.
     * Requires authentication and ownership.
     *
     * @param orderId the order ID
     * @param userId the authenticated user ID
     * @return the order details
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(
        @PathVariable Long orderId,
        @AuthenticationPrincipal Long userId
    ) {
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", "請先登入");
            error.put("code", "AUTHENTICATION_REQUIRED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        Order order = orderService.getOrderById(orderId, userId).orElse(null);

        if (order == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Not Found");
            error.put("message", "找不到該訂單");
            error.put("code", "ORDER_NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        OrderResponse response = new OrderResponse(order);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/orders/{orderId}/cancel - Cancel an order.
     * Requires authentication and ownership. Only pending orders can be cancelled.
     *
     * @param orderId the order ID
     * @param userId the authenticated user ID
     * @return success message
     */
    @DeleteMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(
        @PathVariable Long orderId,
        @AuthenticationPrincipal Long userId
    ) {
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", "請先登入");
            error.put("code", "AUTHENTICATION_REQUIRED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            orderService.cancelOrder(orderId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "訂單已取消");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Not Found");
            error.put("message", e.getMessage());
            error.put("code", "ORDER_NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (IllegalStateException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Bad Request");
            error.put("message", e.getMessage());
            error.put("code", "CANNOT_CANCEL");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
