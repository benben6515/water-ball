package tw.waterballsa.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tw.waterballsa.dto.ErrorResponse;
import tw.waterballsa.dto.UpdateUserRoleRequest;
import tw.waterballsa.dto.UserListResponse;
import tw.waterballsa.exception.ResourceNotFoundException;
import tw.waterballsa.model.UserRole;
import tw.waterballsa.service.UserService;

import java.util.List;

/**
 * Admin controller for user management operations.
 * All endpoints require ADMIN role.
 *
 * Endpoints:
 * - GET /admin/users - List all users
 * - GET /admin/users/{id} - Get user details
 * - PUT /admin/users/{id}/role - Update user role
 *
 * @author Water Ball SA
 */
@RestController
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserManagementController {

    private static final Logger logger = LoggerFactory.getLogger(UserManagementController.class);

    @Autowired
    private UserService userService;

    /**
     * Get all users with pagination.
     *
     * GET /admin/users?page=0&size=20&sort=createdAt,desc
     *
     * @param pageable pagination parameters (default: page=0, size=20, sort by userId desc)
     * @return page of users
     */
    @GetMapping
    public ResponseEntity<Page<UserListResponse>> getAllUsers(
            @PageableDefault(size = 20, sort = "userId", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        try {
            logger.info("Admin fetching users list: page={}, size={}",
                       pageable.getPageNumber(), pageable.getPageSize());

            Page<UserListResponse> users = userService.getAllUsers(pageable);

            logger.info("Successfully fetched {} users out of {} total",
                       users.getNumberOfElements(), users.getTotalElements());

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Failed to fetch users list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get all users without pagination.
     *
     * GET /admin/users/all
     *
     * @return list of all users
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserListResponse>> getAllUsersNoPagination() {
        try {
            logger.info("Admin fetching all users without pagination");

            List<UserListResponse> users = userService.getAllUsers();

            logger.info("Successfully fetched {} users", users.size());

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Failed to fetch all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get user details by ID.
     *
     * GET /admin/users/{id}
     *
     * @param id user ID
     * @return user details
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            logger.info("Admin fetching user details for userId={}", id);

            UserListResponse user = userService.getUserById(id);

            logger.info("Successfully fetched user details for userId={}", id);

            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found: userId={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("USER_NOT_FOUND", "找不到指定的使用者"));
        } catch (Exception e) {
            logger.error("Failed to fetch user details for userId={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "系統錯誤，請稍後再試"));
        }
    }

    /**
     * Update user role.
     *
     * PUT /admin/users/{id}/role
     *
     * Request body:
     * {
     *   "role": "TEACHER"
     * }
     *
     * @param id user ID
     * @param request update role request
     * @return updated user details
     */
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRoleRequest request
    ) {
        try {
            logger.info("Admin updating role for userId={} to {}", id, request.getRole());

            // Parse role from string
            UserRole newRole;
            try {
                newRole = UserRole.valueOf(request.getRole());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid role value: {}", request.getRole());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("INVALID_ROLE", "無效的角色值"));
            }

            UserListResponse updatedUser = userService.updateUserRole(id, newRole);

            logger.info("Successfully updated role for userId={} to {}", id, newRole);

            return ResponseEntity.ok(updatedUser);
        } catch (ResourceNotFoundException e) {
            logger.error("User not found: userId={}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("USER_NOT_FOUND", "找不到指定的使用者"));
        } catch (Exception e) {
            logger.error("Failed to update role for userId={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("INTERNAL_ERROR", "系統錯誤，請稍後再試"));
        }
    }
}
