package tw.waterballsa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Response DTO for checking video completion status.
 *
 * @author Water Ball SA
 */
public class VideoCompletionStatusResponse {

    @JsonProperty("video_id")
    private Long videoId;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @JsonProperty("completed_at")
    private LocalDateTime completedAt;

    @JsonProperty("exp_awarded")
    private Integer expAwarded;

    public VideoCompletionStatusResponse(Long videoId, Boolean isCompleted,
                                          LocalDateTime completedAt, Integer expAwarded) {
        this.videoId = videoId;
        this.isCompleted = isCompleted;
        this.completedAt = completedAt;
        this.expAwarded = expAwarded;
    }

    // Getters and Setters

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Boolean getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public Integer getExpAwarded() {
        return expAwarded;
    }

    public void setExpAwarded(Integer expAwarded) {
        this.expAwarded = expAwarded;
    }
}
