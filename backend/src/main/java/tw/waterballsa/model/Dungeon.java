package tw.waterballsa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Dungeon entity representing a chapter/section within a course.
 * Dungeons (副本) organize videos with difficulty ratings and progressive unlocking.
 *
 * Difficulty Scale:
 * 1 = ★ (Easy)
 * 2 = ★★ (Medium)
 * 3 = ★★★ (Hard)
 * 4 = ★★★★ (Expert)
 *
 * @author Water Ball SA
 */
@Entity
@Table(name = "dungeons", uniqueConstraints = {
    @UniqueConstraint(name = "uq_course_dungeon_number", columnNames = {"course_id", "dungeon_number"})
})
public class Dungeon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dungeon_id")
    private Long dungeonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "dungeon_number", nullable = false)
    private Integer dungeonNumber;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "difficulty", nullable = false)
    private Integer difficulty = 1;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "dungeon", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<Video> videos = new ArrayList<>();

    // Constructors

    public Dungeon() {
    }

    public Dungeon(Integer dungeonNumber, String title, Integer difficulty) {
        this.dungeonNumber = dungeonNumber;
        this.title = title;
        this.difficulty = difficulty;
    }

    // Lifecycle callbacks

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.difficulty == null || this.difficulty < 1 || this.difficulty > 4) {
            this.difficulty = 1;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Helper methods

    public void addVideo(Video video) {
        videos.add(video);
        video.setDungeon(this);
    }

    public void removeVideo(Video video) {
        videos.remove(video);
        video.setDungeon(null);
    }

    public String getDifficultyStars() {
        return "★".repeat(Math.max(1, Math.min(4, difficulty)));
    }

    // Getters and Setters

    public Long getDungeonId() {
        return dungeonId;
    }

    public void setDungeonId(Long dungeonId) {
        this.dungeonId = dungeonId;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Integer getDungeonNumber() {
        return dungeonNumber;
    }

    public void setDungeonNumber(Integer dungeonNumber) {
        this.dungeonNumber = dungeonNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Integer difficulty) {
        if (difficulty != null && difficulty >= 1 && difficulty <= 4) {
            this.difficulty = difficulty;
        }
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    @Override
    public String toString() {
        return "Dungeon{" +
                "dungeonId=" + dungeonId +
                ", dungeonNumber=" + dungeonNumber +
                ", title='" + title + '\'' +
                ", difficulty=" + difficulty +
                '}';
    }
}
