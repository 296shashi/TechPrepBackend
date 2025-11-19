package com.example.techprep.service;

import com.example.techprep.entity.User;
import com.example.techprep.entity.UserLessonProgress;
import com.example.techprep.repository.UserLessonProgressRepository;
import com.example.techprep.repository.UserRepository;
import com.example.techprep.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    private final UserLessonProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final com.example.techprep.repository.LessonRepository lessonRepository;

    public ProgressService(UserLessonProgressRepository progressRepository, UserRepository userRepository, CourseRepository courseRepository, com.example.techprep.repository.LessonRepository lessonRepository) {
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
    }

    public List<Long> getCompletedLessonIdsForUser(String userEmail, Long courseId) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) return List.of();
        List<UserLessonProgress> list = progressRepository.findByUser_IdAndCourse_Id(user.getId(), courseId);
        return list.stream()
            .filter(p -> Boolean.TRUE.equals(p.getCompleted()))
            .filter(p -> {
                Long lid = p.getLessonId();
                if (lid == null) return false;
                try {
                    var lessonOpt = lessonRepository.findById(lid);
                    return lessonOpt.isPresent() && lessonOpt.get().getStatus() == com.example.techprep.entity.LessonStatus.ACTIVE;
                } catch (Exception e) {
                    return false;
                }
            })
            .map(UserLessonProgress::getLessonId)
            .collect(Collectors.toList());
    }

    public void setLessonCompletion(String userEmail, Long courseId, Long lessonId, boolean completed, Long syllabusId) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found"));
        com.example.techprep.entity.Course course = courseRepository.findById(courseId).orElseThrow(() -> new RuntimeException("Course not found"));
        UserLessonProgress progress = progressRepository.findByUser_IdAndCourse_IdAndLessonId(user.getId(), courseId, lessonId)
                .orElseGet(() -> {
                    UserLessonProgress p = new UserLessonProgress();
                    p.setUser(user);
                    p.setCourse(course);
                    p.setLessonId(lessonId);
                    p.setCompleted(false);
                    return p;
                });
        // Set completed flag
        progress.setCompleted(completed);
        if (syllabusId != null) {
            progress.setSyllabusId(syllabusId);
        }
        progressRepository.save(progress);
    }
}
