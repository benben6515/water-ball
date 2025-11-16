package tw.waterballsa.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.waterballsa.dto.UserListResponse;
import tw.waterballsa.exception.ResourceNotFoundException;
import tw.waterballsa.model.User;
import tw.waterballsa.model.UserRole;
import tw.waterballsa.repository.OAuthProviderLinkRepository;
import tw.waterballsa.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for user management operations.
 * Handles user listing, role updates, and user details retrieval.
 *
 * @author Water Ball SA
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OAuthProviderLinkRepository oauthProviderLinkRepository;

    /**
     * Get all users with their roles and OAuth providers.
     *
     * @return list of user responses
     */
    public List<UserListResponse> getAllUsers() {
        logger.info("Fetching all users");
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(this::mapToUserListResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all users with pagination.
     *
     * @param pageable pagination parameters
     * @return page of user responses
     */
    public Page<UserListResponse> getAllUsers(Pageable pageable) {
        logger.info("Fetching users with pagination: page={}, size={}",
                   pageable.getPageNumber(), pageable.getPageSize());
        Page<User> users = userRepository.findAll(pageable);

        return users.map(this::mapToUserListResponse);
    }

    /**
     * Get user details by ID.
     *
     * @param userId user ID
     * @return user response
     * @throws ResourceNotFoundException if user not found
     */
    public UserListResponse getUserById(Long userId) {
        logger.info("Fetching user by ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        return mapToUserListResponse(user);
    }

    /**
     * Update user's role.
     *
     * @param userId user ID
     * @param newRole new role to assign
     * @return updated user response
     * @throws ResourceNotFoundException if user not found
     */
    @Transactional
    public UserListResponse updateUserRole(Long userId, UserRole newRole) {
        logger.info("Updating role for userId={} to {}", userId, newRole);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        UserRole oldRole = user.getRole();
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);

        logger.info("Successfully updated role for userId={} from {} to {}",
                   userId, oldRole, newRole);

        return mapToUserListResponse(updatedUser);
    }

    /**
     * Map User entity to UserListResponse DTO.
     */
    private UserListResponse mapToUserListResponse(User user) {
        List<String> oauthProviders = oauthProviderLinkRepository
                .findByUser_UserId(user.getUserId())
                .stream()
                .map(link -> link.getProviderType().getValue())
                .collect(Collectors.toList());

        return new UserListResponse(
                user.getUserId(),
                user.getNickname(),
                user.getEmail(),
                user.getRole(),
                user.getLevel(),
                user.getCreatedAt(),
                oauthProviders
        );
    }
}
