package tw.waterballsa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.waterballsa.dto.CourseDetailResponse;
import tw.waterballsa.dto.DungeonResponse;
import tw.waterballsa.dto.VideoResponse;
import tw.waterballsa.model.Course;
import tw.waterballsa.model.Dungeon;
import tw.waterballsa.model.User;
import tw.waterballsa.model.UserCourseOwnership;
import tw.waterballsa.model.Video;
import tw.waterballsa.repository.CourseRepository;
import tw.waterballsa.repository.UserCourseOwnershipRepository;
import tw.waterballsa.repository.VideoCompletionRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service for course-related business logic.
 *
 * @author Water Ball SA
 */
@Service
@Transactional(readOnly = true)
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserCourseOwnershipRepository ownershipRepository;

    @Autowired
    private VideoCompletionRepository videoCompletionRepository;

    /**
     * Get all published courses.
     */
    public List<Course> getAllPublishedCourses() {
        return courseRepository.findAllPublishedCourses();
    }

    /**
     * Get a published course by ID with dungeons.
     */
    public Optional<Course> getPublishedCourseWithDungeons(Long courseId) {
        return courseRepository.findPublishedCourseWithDungeons(courseId);
    }

    /**
     * Check if a user owns a specific course.
     */
    public boolean userOwnsCourse(Long userId, Long courseId) {
        return ownershipRepository.existsByUser_UserIdAndCourse_CourseId(userId, courseId);
    }

    /**
     * Get all courses owned by a user.
     */
    public List<UserCourseOwnership> getUserOwnedCourses(Long userId) {
        List<UserCourseOwnership> ownerships = ownershipRepository.findByUserIdOrderByPurchasedAtDesc(userId);

        // Force initialization of course within transaction to avoid LazyInitializationException
        for (UserCourseOwnership ownership : ownerships) {
            ownership.getCourse().getTitle(); // Force initialization of course
        }

        return ownerships;
    }

    /**
     * Grant course ownership to a user (for purchases or admin actions).
     */
    @Transactional
    public UserCourseOwnership grantCourseOwnership(User user, Course course) {
        // Check if already owned
        Optional<UserCourseOwnership> existing = ownershipRepository
            .findByUser_UserIdAndCourse_CourseId(user.getUserId(), course.getCourseId());

        if (existing.isPresent()) {
            return existing.get();
        }

        // Create new ownership
        UserCourseOwnership ownership = new UserCourseOwnership(user, course);
        return ownershipRepository.save(ownership);
    }

    /**
     * Get course by ID (admin access).
     */
    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }

    /**
     * Get all courses including unpublished (admin access).
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAllCoursesAdmin();
    }

    /**
     * Create a new course (admin action).
     */
    @Transactional
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    /**
     * Update a course (admin action).
     */
    @Transactional
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    /**
     * Delete a course (admin action).
     */
    @Transactional
    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    /**
     * Get detailed course information with dungeons and videos.
     * Includes ownership status and video completion status for the user.
     *
     * @param courseId the course ID
     * @param userId the user ID (can be null for anonymous users)
     * @return Optional containing CourseDetailResponse if found, empty otherwise
     */
    public Optional<CourseDetailResponse> getCourseDetail(Long courseId, Long userId) {
        // Fetch course with dungeons
        Optional<Course> courseOpt = courseRepository.findPublishedCourseWithDungeons(courseId);

        if (courseOpt.isEmpty()) {
            return Optional.empty();
        }

        Course course = courseOpt.get();

        // Check ownership
        boolean isOwned = userId != null && userOwnsCourse(userId, courseId);

        // Build response
        CourseDetailResponse response = new CourseDetailResponse(course, isOwned);

        // Build dungeon responses with videos
        for (Dungeon dungeon : course.getDungeons()) {
            DungeonResponse dungeonResponse = new DungeonResponse(dungeon);

            // Initialize videos collection within transaction to avoid LazyInitializationException
            List<Video> videos = dungeon.getVideos();
            videos.size(); // Force initialization

            // Build video responses
            for (Video video : videos) {
                boolean completed = userId != null &&
                    videoCompletionRepository.existsByUserUserIdAndVideoVideoId(userId, video.getVideoId());
                VideoResponse videoResponse = new VideoResponse(video, completed);
                dungeonResponse.addVideo(videoResponse);
            }

            response.addDungeon(dungeonResponse);
        }

        return Optional.of(response);
    }
}
