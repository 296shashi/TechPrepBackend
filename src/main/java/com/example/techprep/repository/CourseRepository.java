package com.example.techprep.repository;

import com.example.techprep.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
	List<Course> findAllByEnrolledUsers_Id(Long userId);
}
