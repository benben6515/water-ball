package tw.waterballsa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Update profile request DTO
 * Fields that can be updated by user
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @NotBlank(message = "暱稱不能為空")
    @Size(min = 1, max = 255, message = "暱稱長度必須在1-255字元之間")
    private String nickname;

    @Pattern(regexp = "^(男|女|其他|不透露)$", message = "性別必須是：男、女、其他、不透露")
    private String gender;

    private LocalDate birthday;

    @Size(max = 255, message = "地點長度不能超過255字元")
    private String location;

    @Size(max = 255, message = "職業長度不能超過255字元")
    private String occupation;

    @Pattern(regexp = "^(https://github\\.com/[a-zA-Z0-9\\-]+)?$", message = "請輸入有效的 GitHub 網址")
    private String githubLink;
}
