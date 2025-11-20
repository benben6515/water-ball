package tw.waterballsa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tw.waterballsa.dto.CourseDetailResponse;
import tw.waterballsa.dto.CourseListResponse;
import tw.waterballsa.model.Course;
import tw.waterballsa.model.User;
import tw.waterballsa.model.UserCourseOwnership;
import tw.waterballsa.repository.UserRepository;
import tw.waterballsa.service.CourseService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller for course-related endpoints.
 *
 * @author Water Ball SA
 */
@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "${cors.allowed-origins}", allowCredentials = "true")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    /**
     * GET /api/courses - Get all published courses with ownership status.
     * Public endpoint - doesn't require authentication.
     */
    @GetMapping
    public ResponseEntity<List<CourseListResponse>> getAllCourses(
        @AuthenticationPrincipal Long userId
    ) {
        List<Course> courses = courseService.getAllPublishedCourses();

        // Check ownership for authenticated users
        List<CourseListResponse> response = courses.stream()
            .map(course -> {
                boolean isOwned = userId != null &&
                    courseService.userOwnsCourse(userId, course.getCourseId());
                return new CourseListResponse(course, isOwned);
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/courses/{id} - Get course details with dungeons and videos.
     * Public endpoint - doesn't require authentication.
     * Includes video completion status for authenticated users.
     */
    @GetMapping("/{courseId}")
    public ResponseEntity<?> getCourseById(
        @PathVariable Long courseId,
        @AuthenticationPrincipal Long userId
    ) {
        CourseDetailResponse response = courseService.getCourseDetail(courseId, userId)
            .orElse(null);

        if (response == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Not Found");
            error.put("message", "找不到該課程");
            error.put("code", "COURSE_NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/courses/owned - Get all courses owned by the current user.
     * Requires authentication.
     */
    @GetMapping("/owned")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getOwnedCourses(@AuthenticationPrincipal Long userId) {
        if (userId == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", "請先登入");
            error.put("code", "AUTHENTICATION_REQUIRED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        List<UserCourseOwnership> ownerships = courseService.getUserOwnedCourses(userId);

        List<CourseListResponse> response = ownerships.stream()
            .map(ownership -> new CourseListResponse(ownership.getCourse(), true))
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/courses/{id}/grant - Grant course ownership to current user.
     * For testing purposes - in production this would be part of purchase flow.
     * Requires ADMIN role.
     */
    @PostMapping("/{courseId}/grant")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> grantCourseAccess(
        @PathVariable Long courseId,
        @RequestParam Long userId
    ) {
        User user = userRepository.findById(userId).orElse(null);
        Course course = courseService.getCourseById(courseId).orElse(null);

        if (user == null || course == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Not Found");
            error.put("message", "找不到使用者或課程");
            error.put("code", "USER_OR_COURSE_NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }

        UserCourseOwnership ownership = courseService.grantCourseOwnership(user, course);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "課程權限已授予");
        response.put("ownership_id", ownership.getOwnershipId());
        return ResponseEntity.ok(response);
    }
}
