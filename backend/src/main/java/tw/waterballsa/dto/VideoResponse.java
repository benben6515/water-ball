package tw.waterballsa.dto;

import tw.waterballsa.model.Video;

/**
 * DTO for video information in course detail view.
 *
 * @author Water Ball SA
 */
public class VideoResponse {

    private Long videoId;
    private String title;
    private String description;
    private Integer durationSeconds;
    private String videoUrl;
    private String thumbnailUrl;
    private Integer chapterNumber;
    private Integer orderIndex;
    private Integer expReward;
    private boolean isDemo;  // Whether this is a demo/preview video that anyone can watch
    private boolean completed;  // Whether the current user has completed this video

    // Constructors

    public VideoResponse() {
    }

    public VideoResponse(Video video, boolean completed) {
        this.videoId = video.getVideoId();
        this.title = video.getTitle();
        this.description = video.getDescription();
        this.durationSeconds = video.getDurationSeconds();
        this.videoUrl = video.getVideoUrl();
        this.thumbnailUrl = video.getThumbnailUrl();
        this.chapterNumber = video.getChapterNumber();
        this.orderIndex = video.getOrderIndex();
        this.expReward = video.getExpReward();
        this.isDemo = video.getIsDemo() != null && video.getIsDemo();
        this.completed = completed;
    }

    // Getters and Setters

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
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

    public Integer getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(Integer durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public Integer getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(Integer chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Integer getExpReward() {
        return expReward;
    }

    public void setExpReward(Integer expReward) {
        this.expReward = expReward;
    }

    public boolean isDemo() {
        return isDemo;
    }

    public void setDemo(boolean demo) {
        isDemo = demo;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
