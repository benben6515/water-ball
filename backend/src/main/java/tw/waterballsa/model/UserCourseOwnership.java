package tw.waterballsa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * UserCourseOwnership entity tracking which courses users have purchased/unlocked.
 * Enforces idempotency - users cannot purchase the same course twice.
 *
 * @author Water Ball SA
 */
@Entity
@Table(name = "user_course_ownership", uniqueConstraints = {
    @UniqueConstraint(name = "uq_user_course_ownership", columnNames = {"user_id", "course_id"})
})
public class UserCourseOwnership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ownership_id")
    private Long ownershipId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "purchased_at", nullable = false, updatable = false)
    private LocalDateTime purchasedAt;

    // Constructors

    public UserCourseOwnership() {
    }

    public UserCourseOwnership(User user, Course course) {
        this.user = user;
        this.course = course;
    }

    // Lifecycle callbacks

    @PrePersist
    protected void onCreate() {
        this.purchasedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getOwnershipId() {
        return ownershipId;
    }

    public void setOwnershipId(Long ownershipId) {
        this.ownershipId = ownershipId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDateTime getPurchasedAt() {
        return purchasedAt;
    }

    @Override
    public String toString() {
        return "UserCourseOwnership{" +
                "ownershipId=" + ownershipId +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", courseId=" + (course != null ? course.getCourseId() : null) +
                ", purchasedAt=" + purchasedAt +
                '}';
    }
}
