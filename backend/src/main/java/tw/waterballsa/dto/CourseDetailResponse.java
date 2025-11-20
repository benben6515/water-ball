package tw.waterballsa.dto;

import tw.waterballsa.model.Course;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for detailed course information including dungeons and videos.
 *
 * @author Water Ball SA
 */
public class CourseDetailResponse {

    private Long courseId;
    private String title;
    private String description;
    private String coverImageUrl;
    private String instructorName;
    private String instructorAvatarUrl;
    private BigDecimal price;
    private boolean isFree;
    private boolean isOwned;
    private int totalDungeons;
    private int totalVideos;
    private List<DungeonResponse> dungeons = new ArrayList<>();

    // Constructors

    public CourseDetailResponse() {
    }

    public CourseDetailResponse(Course course, boolean isOwned) {
        this.courseId = course.getCourseId();
        this.title = course.getTitle();
        this.description = course.getDescription();
        this.coverImageUrl = course.getCoverImageUrl();
        this.instructorName = course.getInstructorName();
        this.instructorAvatarUrl = course.getInstructorAvatarUrl();
        this.price = course.getPrice();
        this.isFree = course.isFree();
        this.isOwned = isOwned;
    }

    // Getters and Setters

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
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

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getInstructorAvatarUrl() {
        return instructorAvatarUrl;
    }

    public void setInstructorAvatarUrl(String instructorAvatarUrl) {
        this.instructorAvatarUrl = instructorAvatarUrl;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public boolean isOwned() {
        return isOwned;
    }

    public void setOwned(boolean owned) {
        isOwned = owned;
    }

    public int getTotalDungeons() {
        return totalDungeons;
    }

    public void setTotalDungeons(int totalDungeons) {
        this.totalDungeons = totalDungeons;
    }

    public int getTotalVideos() {
        return totalVideos;
    }

    public void setTotalVideos(int totalVideos) {
        this.totalVideos = totalVideos;
    }

    public List<DungeonResponse> getDungeons() {
        return dungeons;
    }

    public void setDungeons(List<DungeonResponse> dungeons) {
        this.dungeons = dungeons;
        this.totalDungeons = dungeons.size();
        this.totalVideos = dungeons.stream()
                .mapToInt(d -> d.getVideos().size())
                .sum();
    }

    public void addDungeon(DungeonResponse dungeon) {
        this.dungeons.add(dungeon);
    }
}
