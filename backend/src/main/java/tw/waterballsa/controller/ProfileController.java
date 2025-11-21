package tw.waterballsa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.waterballsa.dto.ProfileResponse;
import tw.waterballsa.dto.UpdateProfileRequest;
import tw.waterballsa.service.ProfileService;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for user profile management.
 *
 * Endpoints:
 * - GET /api/profile - Get current user's profile
 * - PUT /api/profile - Update current user's profile
 */
@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "${cors.allowed-origins}", allowCredentials = "true")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * GET /api/profile - Get current user's profile
     * Returns: nickname, email, gender, birthday, location, occupation, github_link, level, achievements
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal Long userId) {
        ProfileResponse profile = profileService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * PUT /api/profile - Update current user's profile
     * Updatable fields: nickname, gender, birthday, location, occupation, github_link
     * Read-only fields: email, level, achievements (not updatable via this endpoint)
     */
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @AuthenticationPrincipal Long userId,
            @Valid @RequestBody UpdateProfileRequest request) {

        ProfileResponse updatedProfile = profileService.updateProfile(userId, request);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "個人資料已更新");
        response.put("profile", updatedProfile);

        return ResponseEntity.ok(response);
    }
}
