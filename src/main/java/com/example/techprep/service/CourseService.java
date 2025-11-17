package com.example.techprep.service;

import com.example.techprep.entity.Course;
import com.example.techprep.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    // Create
    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    // Read all
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // Read by ID
    public Optional<Course> getCourseById(Long id) {
        return courseRepository.findById(id);
    }

    // Update
    public Course updateCourse(Long id, Course courseDetails) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id " + id));

        course.setTitle(courseDetails.getTitle());
        course.setDescription(courseDetails.getDescription());
        course.setInstructor(courseDetails.getInstructor());
        course.setDuration(courseDetails.getDuration());
        course.setLevel(courseDetails.getLevel());
        course.setCategory(courseDetails.getCategory());
        course.setLessons(courseDetails.getLessons());
        course.setEnrolled(courseDetails.getEnrolled());
        course.setRating(courseDetails.getRating());
        course.setPrice(courseDetails.getPrice());
        course.setImage(courseDetails.getImage());
        course.setSyllabus(courseDetails.getSyllabus());

        return courseRepository.save(course);
    }

    // Delete
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id " + id));
        courseRepository.delete(course);
    }
}