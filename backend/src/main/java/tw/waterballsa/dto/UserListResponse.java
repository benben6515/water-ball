package tw.waterballsa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import tw.waterballsa.model.UserRole;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response DTO for GET /admin/users endpoint.
 *
 * Contains user information for admin user management.
 * Includes user ID, nickname, email, role, level, and registration date.
 *
 * Format:
 * {
 *   "user_id": 1,
 *   "nickname": "John Doe",
 *   "email": "john@example.com",
 *   "role": "STUDENT",
 *   "level": 5,
 *   "created_at": "2025-01-15T10:30:00",
 *   "oauth_providers": ["google"]
 * }
 *
 * @author Water Ball SA
 */
public class UserListResponse {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("nickname")
    private String nickname;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private String role;

    @JsonProperty("level")
    private Integer level;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("oauth_providers")
    private List<String> oauthProviders;

    public UserListResponse() {
    }

    public UserListResponse(Long userId, String nickname, String email, UserRole role,
                           Integer level, LocalDateTime createdAt, List<String> oauthProviders) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.role = role.name();
        this.level = level;
        this.createdAt = createdAt;
        this.oauthProviders = oauthProviders;
    }

    // Getters and Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getOauthProviders() {
        return oauthProviders;
    }

    public void setOauthProviders(List<String> oauthProviders) {
        this.oauthProviders = oauthProviders;
    }
}
