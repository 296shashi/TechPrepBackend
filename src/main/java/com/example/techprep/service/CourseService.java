package com.example.techprep.service;

import com.example.techprep.dto.CourseDTO;
import com.example.techprep.entity.Course;
import com.example.techprep.entity.SyllabusSection;
import com.example.techprep.entity.User;
import com.example.techprep.mapper.CourseMapper;
import com.example.techprep.repository.CourseRepository;
import com.example.techprep.repository.UserRepository;
import com.example.techprep.repository.UserLessonProgressRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseMapper courseMapper;
    private final UserLessonProgressRepository progressRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository, CourseMapper courseMapper, UserLessonProgressRepository progressRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseMapper = courseMapper;
        this.progressRepository = progressRepository;
    }

    /*-----------------------------------------------------
     *  Create Course
     *----------------------------------------------------*/
    public CourseDTO createCourse(CourseDTO courseDTO, Long userId) {
        Course course = courseMapper.convertDTOToEntity(courseDTO);
        Course saved = courseRepository.save(course);

        if (userId != null) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            user.getEnrolledCourses().add(saved);
            userRepository.save(user);
        }
        return courseMapper.convertEntityToDTO(saved);
    }

    public CourseDTO createCourse(CourseDTO courseDTO, String userEmail) {
        Course course = courseMapper.convertDTOToEntity(courseDTO);
        Course saved = courseRepository.save(course);

        if (userEmail != null) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
            user.getEnrolledCourses().add(saved);
            userRepository.save(user);
        }
        return courseMapper.convertEntityToDTO(saved);
    }

    /*-----------------------------------------------------
     *  Get All Courses
     *----------------------------------------------------*/
    public List<CourseDTO> getAllCourses(Long userId) {
        List<Course> courses = (userId != null)
                ? courseRepository.findAllByEnrolledUsers_Id(userId)
                : courseRepository.findAll();

        return courseMapper.convertEntityListToDTOList(courses);
    }

    public List<CourseDTO> getAllCourses(String userEmail) {

        List<Course> courses = courseRepository.findAll();
        User user = null;
        Set<Long> enrolled = new HashSet<>();

        if (userEmail != null && !userEmail.isBlank()) {
            user = userRepository.findByEmail(userEmail).orElse(null);
            if (user != null && user.getEnrolledCourses() != null) {
                user.getEnrolledCourses().forEach(c -> enrolled.add(c.getId()));
            }
        }

        List<CourseDTO> result = new ArrayList<>();

        for (Course course : courses) {
            CourseDTO dto = courseMapper.convertEntityToDTO(course);

            dto.setEnrolled(user != null && enrolled.contains(course.getId()));
            dto.setEnrolledCount(
                    course.getEnrolledUsers() != null ? course.getEnrolledUsers().size() : 0
            );

            int totalLessons = 0;
            if (course.getSyllabus() != null) {
                for (SyllabusSection sec : course.getSyllabus()) {
                    if (sec.getLessons() != null) {
                        totalLessons += sec.getLessons().size();
                    }
                }
            }
            dto.setTotalLessons(totalLessons);

            // Compute completed lessons and ids for this user (same as getCourseById)
            if (user != null) {
                try {
                    var progresses = progressRepository.findByUser_IdAndCourse_Id(user.getId(), course.getId());
                    if (progresses != null) {
                        List<Long> completedIds = new ArrayList<>();
                        for (var p : progresses) {
                            if (Boolean.TRUE.equals(p.getCompleted()) && p.getLessonId() != null) {
                                completedIds.add(p.getLessonId());
                            }
                        }
                        dto.setCompletedLessonIds(completedIds);
                        dto.setCompletedLessons(completedIds.size());
                    }
                } catch (Exception ignored) {
                    // ignore errors computing progress
                }
            }

            result.add(dto);
        }

        return result;
    }

    /*-----------------------------------------------------
     *  Get Course by ID
     *----------------------------------------------------*/
    public Optional<CourseDTO> getCourseById(Long id, String userEmail) {

        Course course = courseRepository.findById(id).orElse(null);
        if (course == null) return Optional.empty();

        CourseDTO dto = courseMapper.convertEntityToDTO(course);

        dto.setEnrolledCount(
                course.getEnrolledUsers() != null ? course.getEnrolledUsers().size() : 0
        );

        int totalLessons = 0;
        if (course.getSyllabus() != null) {
            for (SyllabusSection sec : course.getSyllabus()) {
                if (sec.getLessons() != null) {
                    totalLessons += sec.getLessons().size();
                }
            }
        }
        dto.setTotalLessons(totalLessons);

        if (userEmail == null || userEmail.isBlank()) {
            return Optional.of(dto);
        }

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            return Optional.of(dto);
        }

        dto.setEnrolled(
                user.getEnrolledCourses() != null &&
                        user.getEnrolledCourses().stream().anyMatch(c -> c.getId().equals(id))
        );

        // Compute completed lessons and ids for this user
        try {
            var progresses = progressRepository.findByUser_IdAndCourse_Id(user.getId(), course.getId());
            if (progresses != null) {
                List<Long> completedIds = new ArrayList<>();
                for (var p : progresses) {
                    if (Boolean.TRUE.equals(p.getCompleted()) && p.getLessonId() != null) {
                        completedIds.add(p.getLessonId());
                    }
                }
                dto.setCompletedLessonIds(completedIds);
                dto.setCompletedLessons(completedIds.size());
            }
        } catch (Exception ignored) {
            // ignore errors computing progress
        }

        return Optional.of(dto);
    }

    /*-----------------------------------------------------
     *  Update Course
     *----------------------------------------------------*/
    public CourseDTO updateCourse(Long id, CourseDTO courseDetails) {
        courseRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Course not found: " + id)
        );

        Course updated = courseMapper.convertDTOToEntity(courseDetails);
        updated.setId(id);

        return courseMapper.convertEntityToDTO(courseRepository.save(updated));
    }

    /*-----------------------------------------------------
     *  Delete Course
     *----------------------------------------------------*/
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found: " + id));
        courseRepository.delete(course);
    }

    /*-----------------------------------------------------
     *  Enrollment Operations
     *----------------------------------------------------*/
    public void enrollUser(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.getEnrolledCourses().add(course);
        userRepository.save(user);
    }

    public void enrollUser(Long courseId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
        enrollUser(courseId, user.getId());
    }

    public void unenrollUser(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        user.getEnrolledCourses().remove(course);
        userRepository.save(user);
    }

    public void unenrollUser(Long courseId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
        unenrollUser(courseId, user.getId());
    }

    /*-----------------------------------------------------
     *  Syllabus Lookup
     *----------------------------------------------------*/
    public List<SyllabusSection> getSyllabusByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));

        return course.getSyllabus() != null ? course.getSyllabus() : List.of();
    }
}
