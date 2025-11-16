package tw.waterballsa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Session information response DTO returned by GET /auth/session endpoint.
 *
 * Contains user profile information and linked OAuth providers.
 *
 * Format:
 * {
 *   "user_id": 123,
 *   "nickname": "小明",
 *   "email": "user@example.com",
 *   "level": 5,
 *   "role": "STUDENT",
 *   "oauth_providers": ["google", "facebook"]
 * }
 *
 * @author Water Ball SA
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SessionInfoResponse {

    @JsonProperty("user_id")
    private Long userId;

    private String nickname;

    private String email;

    private Integer level;

    private String role;

    @JsonProperty("oauth_providers")
    private List<String> oauthProviders;

    public SessionInfoResponse() {
    }

    public SessionInfoResponse(Long userId, String nickname, String email, Integer level, String role, List<String> oauthProviders) {
        this.userId = userId;
        this.nickname = nickname;
        this.email = email;
        this.level = level;
        this.role = role;
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getOauthProviders() {
        return oauthProviders;
    }

    public void setOauthProviders(List<String> oauthProviders) {
        this.oauthProviders = oauthProviders;
    }
}
