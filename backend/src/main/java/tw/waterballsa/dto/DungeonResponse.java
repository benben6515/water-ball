package tw.waterballsa.dto;

import tw.waterballsa.model.Dungeon;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO for dungeon information in course detail view.
 *
 * @author Water Ball SA
 */
public class DungeonResponse {

    private Long dungeonId;
    private Integer dungeonNumber;
    private String title;
    private String description;
    private Integer difficulty;
    private Integer orderIndex;
    private List<VideoResponse> videos = new ArrayList<>();

    // Constructors

    public DungeonResponse() {
    }

    public DungeonResponse(Dungeon dungeon) {
        this.dungeonId = dungeon.getDungeonId();
        this.dungeonNumber = dungeon.getDungeonNumber();
        this.title = dungeon.getTitle();
        this.description = dungeon.getDescription();
        this.difficulty = dungeon.getDifficulty();
        this.orderIndex = dungeon.getOrderIndex();
    }

    // Getters and Setters

    public Long getDungeonId() {
        return dungeonId;
    }

    public void setDungeonId(Long dungeonId) {
        this.dungeonId = dungeonId;
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
        this.difficulty = difficulty;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public List<VideoResponse> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoResponse> videos) {
        this.videos = videos;
    }

    public void addVideo(VideoResponse video) {
        this.videos.add(video);
    }
}
