package com.example.techprep.repository;

import com.example.techprep.entity.UserLessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserLessonProgressRepository extends JpaRepository<UserLessonProgress, Long> {
    List<UserLessonProgress> findByUser_IdAndCourse_Id(Long userId, Long courseId);
    Optional<UserLessonProgress> findByUser_IdAndCourse_IdAndLessonId(Long userId, Long courseId, Long lessonId);
}
