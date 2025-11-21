package tw.waterballsa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Profile response DTO
 * Returns user profile information including read-only fields (level, achievements)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private Long userId;
    private String nickname;
    private String email;
    private String gender;  // 男/女/其他/不透露
    private LocalDate birthday;
    private String location;
    private String occupation;
    private String githubLink;

    // Read-only fields (from gamification system)
    private Integer level;
    private Integer exp;
    private Integer expForNextLevel;
    private Double expProgressPercentage;
    private List<AchievementDto> achievements;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AchievementDto {
        private String achievementType;
        private String achievementName;
        private String earnedAt;
    }
}
