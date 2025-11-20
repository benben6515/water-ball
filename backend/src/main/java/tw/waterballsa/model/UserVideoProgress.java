package tw.waterballsa.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing user's video watch progress.
 * Tracks watch percentage and last watched position for resume functionality.
 *
 * @author Water Ball SA
 */
@Entity
@Table(name = "user_video_progress",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "video_id"}))
public class UserVideoProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long progressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "watch_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal watchPercentage = BigDecimal.ZERO;

    @Column(name = "last_position_seconds", nullable = false)
    private Integer lastPositionSeconds = 0;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Constructors

    public UserVideoProgress() {
    }

    public UserVideoProgress(User user, Video video) {
        this.user = user;
        this.video = video;
        this.watchPercentage = BigDecimal.ZERO;
        this.lastPositionSeconds = 0;
        this.updatedAt = LocalDateTime.now();
    }

    // Business Methods

    /**
     * Update watch progress.
     */
    public void updateProgress(int currentPositionSeconds, int videoDurationSeconds) {
        this.lastPositionSeconds = currentPositionSeconds;

        if (videoDurationSeconds > 0) {
            this.watchPercentage = BigDecimal.valueOf(currentPositionSeconds)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(videoDurationSeconds), 2, BigDecimal.ROUND_HALF_UP);

            // Cap at 100%
            if (this.watchPercentage.compareTo(BigDecimal.valueOf(100)) > 0) {
                this.watchPercentage = BigDecimal.valueOf(100);
            }
        }

        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if video is completed (95% threshold).
     */
    public boolean isCompleted() {
        return watchPercentage.compareTo(BigDecimal.valueOf(95)) >= 0;
    }

    // Getters and Setters

    public Long getProgressId() {
        return progressId;
    }

    public void setProgressId(Long progressId) {
        this.progressId = progressId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }

    public BigDecimal getWatchPercentage() {
        return watchPercentage;
    }

    public void setWatchPercentage(BigDecimal watchPercentage) {
        this.watchPercentage = watchPercentage;
    }

    public Integer getLastPositionSeconds() {
        return lastPositionSeconds;
    }

    public void setLastPositionSeconds(Integer lastPositionSeconds) {
        this.lastPositionSeconds = lastPositionSeconds;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
