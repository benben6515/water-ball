package tw.waterballsa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * VideoCompletion entity representing a user's completion of a video for exp reward.
 * Ensures idempotent exp awards - each user can only complete a video once for exp.
 *
 * @author Water Ball SA
 */
@Entity
@Table(name = "video_completions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "video_id"}))
public class VideoCompletion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "completion_id")
    private Long completionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(name = "exp_awarded", nullable = false)
    private Integer expAwarded;

    @Column(name = "completed_at", nullable = false)
    private LocalDateTime completedAt;

    // Constructors

    public VideoCompletion() {
    }

    public VideoCompletion(User user, Video video, Integer expAwarded) {
        this.user = user;
        this.video = video;
        this.expAwarded = expAwarded;
        this.completedAt = LocalDateTime.now();
    }

    // Lifecycle callbacks

    @PrePersist
    protected void onCreate() {
        if (this.completedAt == null) {
            this.completedAt = LocalDateTime.now();
        }
    }

    // Getters and Setters

    public Long getCompletionId() {
        return completionId;
    }

    public void setCompletionId(Long completionId) {
        this.completionId = completionId;
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

    public Integer getExpAwarded() {
        return expAwarded;
    }

    public void setExpAwarded(Integer expAwarded) {
        this.expAwarded = expAwarded;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    @Override
    public String toString() {
        return "VideoCompletion{" +
                "completionId=" + completionId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", videoId=" + (video != null ? video.getVideoId() : null) +
                ", expAwarded=" + expAwarded +
                ", completedAt=" + completedAt +
                '}';
    }
}
