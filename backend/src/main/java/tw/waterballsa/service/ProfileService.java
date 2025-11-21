package tw.waterballsa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.waterballsa.dto.ProfileResponse;
import tw.waterballsa.dto.UpdateProfileRequest;
import tw.waterballsa.model.User;
import tw.waterballsa.repository.UserRepository;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Profile management service
 * Handles profile retrieval and updates
 */
@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LevelService levelService;

    /**
     * Get user profile by user ID
     * Includes level calculation and achievement list
     */
    public ProfileResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        // Calculate exp progress for next level
        var levelInfo = levelService.calculateLevelInfo(user.getExp());

        return ProfileResponse.builder()
                .userId(user.getUserId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .location(user.getLocation())
                .occupation(user.getOccupation())
                .githubLink(user.getGithubLink())
                .level(levelInfo.getLevel())
                .exp(user.getExp())
                .expForNextLevel(levelInfo.getExpForNextLevel())
                .expProgressPercentage(levelInfo.getExpProgressPercentage())
                .achievements(new ArrayList<>())  // TODO: Fetch from achievement table when gamification implemented
                .build();
    }

    /**
     * Update user profile
     * Updates: nickname, gender, birthday, location, occupation, githubLink
     * Does NOT update: email (from OAuth), level (from gamification)
     */
    @Transactional
    public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("找不到使用者"));

        // Update profile fields
        user.setNickname(request.getNickname());
        user.setGender(request.getGender());
        user.setBirthday(request.getBirthday());
        user.setLocation(request.getLocation());
        user.setOccupation(request.getOccupation());
        user.setGithubLink(request.getGithubLink());

        // Save and return updated profile
        user = userRepository.save(user);

        return getProfile(user.getUserId());
    }
}
