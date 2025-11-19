package tw.waterballsa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Response DTO for video completion operations.
 * Contains completion status, exp awarded, and level-up information.
 *
 * @author Water Ball SA
 */
public class VideoCompletionResponse {

    @JsonProperty("completion_id")
    private Long completionId;

    @JsonProperty("video_id")
    private Long videoId;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("exp_awarded")
    private Integer expAwarded;

    @JsonProperty("leveled_up")
    private Boolean leveledUp;

    @JsonProperty("current_level")
    private Integer currentLevel;

    @JsonProperty("current_exp")
    private Integer currentExp;

    @JsonProperty("exp_for_next_level")
    private Integer expForNextLevel;

    @JsonProperty("exp_progress_percentage")
    private Integer expProgressPercentage;

    @JsonProperty("completed_at")
    private LocalDateTime completedAt;

    @JsonProperty("already_completed")
    private Boolean alreadyCompleted;

    // Constructor for new completion
    public VideoCompletionResponse(Long completionId, Long videoId, Long userId, Integer expAwarded,
                                    Boolean leveledUp, Integer currentLevel, Integer currentExp,
                                    Integer expForNextLevel, Integer expProgressPercentage,
                                    LocalDateTime completedAt, Boolean alreadyCompleted) {
        this.completionId = completionId;
        this.videoId = videoId;
        this.userId = userId;
        this.expAwarded = expAwarded;
        this.leveledUp = leveledUp;
        this.currentLevel = currentLevel;
        this.currentExp = currentExp;
        this.expForNextLevel = expForNextLevel;
        this.expProgressPercentage = expProgressPercentage;
        this.completedAt = completedAt;
        this.alreadyCompleted = alreadyCompleted;
    }

    // Getters and Setters

    public Long getCompletionId() {
        return completionId;
    }

    public void setCompletionId(Long completionId) {
        this.completionId = completionId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getExpAwarded() {
        return expAwarded;
    }

    public void setExpAwarded(Integer expAwarded) {
        this.expAwarded = expAwarded;
    }

    public Boolean getLeveledUp() {
        return leveledUp;
    }

    public void setLeveledUp(Boolean leveledUp) {
        this.leveledUp = leveledUp;
    }

    public Integer getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(Integer currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getCurrentExp() {
        return currentExp;
    }

    public void setCurrentExp(Integer currentExp) {
        this.currentExp = currentExp;
    }

    public Integer getExpForNextLevel() {
        return expForNextLevel;
    }

    public void setExpForNextLevel(Integer expForNextLevel) {
        this.expForNextLevel = expForNextLevel;
    }

    public Integer getExpProgressPercentage() {
        return expProgressPercentage;
    }

    public void setExpProgressPercentage(Integer expProgressPercentage) {
        this.expProgressPercentage = expProgressPercentage;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Boolean getAlreadyCompleted() {
        return alreadyCompleted;
    }

    public void setAlreadyCompleted(Boolean alreadyCompleted) {
        this.alreadyCompleted = alreadyCompleted;
    }
}
