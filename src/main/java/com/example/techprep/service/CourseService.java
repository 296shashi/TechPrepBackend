package com.example.techprep.service;

import com.example.techprep.dto.CourseDTO;
import com.example.techprep.entity.Course;
import com.example.techprep.entity.SyllabusSection;
import com.example.techprep.entity.User;
import com.example.techprep.mapper.CourseMapper;
import com.example.techprep.repository.CourseRepository;
import com.example.techprep.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, CourseMapper courseMapper, UserRepository userRepository) {
     this.courseRepository = courseRepository;
     this.courseMapper = courseMapper;
     this.userRepository = userRepository;
    }

   // Create (optionally enroll the creating user)
      public CourseDTO createCourse(CourseDTO courseDTO, Long userId) {
           Course course = courseMapper.convertDTOToEntity(courseDTO);
           Course saved = courseRepository.save(course);
           if (userId != null) {
                     User user = userRepository.findById(userId)
                                   .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
                     user.getEnrolledCourses().add(saved);
                     userRepository.save(user);
           }
           return courseMapper.convertEntityToDTO(saved);
      }

      // Create and optionally enroll by email (authenticated user)
      public CourseDTO createCourse(CourseDTO courseDTO, String userEmail) {
           Course course = courseMapper.convertDTOToEntity(courseDTO);
           Course saved = courseRepository.save(course);
           if (userEmail != null) {
                     User user = userRepository.findByEmail(userEmail)
                                   .orElseThrow(() -> new RuntimeException("User not found with email " + userEmail));
                     user.getEnrolledCourses().add(saved);
                     userRepository.save(user);
           }
           return courseMapper.convertEntityToDTO(saved);
      }

    // Read all (optionally for specific user by id)
    public List<CourseDTO> getAllCourses(Long userId) {
     List<Course> courses = (userId != null) ? courseRepository.findAllByEnrolledUsers_Id(userId) : courseRepository.findAll();
     return courseMapper.convertEntityListToDTOList(courses);
    }

    // Read all (optionally for specific user by email)
    public List<CourseDTO> getAllCourses(String userEmail) {
     if (userEmail == null) return getAllCourses((Long) null);
     User user = userRepository.findByEmail(userEmail)
          .orElseThrow(() -> new RuntimeException("User not found with email " + userEmail));
     return getAllCourses(user.getId());
    }

   // Read by ID
   public Optional<CourseDTO> getCourseById(Long id) {
       return courseRepository.findById(id).map(courseMapper::convertEntityToDTO);
   }

   // Update
   public CourseDTO updateCourse(Long id, CourseDTO courseDetails) {
     Course existing = courseRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Course not found with id " + id));

     Course toSave = courseMapper.convertDTOToEntity(courseDetails);
     toSave.setId(existing.getId());

     Course saved = courseRepository.save(toSave);
     return courseMapper.convertEntityToDTO(saved);
   }

   // Delete
   public void deleteCourse(Long id) {
     Course course = courseRepository.findById(id)
          .orElseThrow(() -> new RuntimeException("Course not found with id " + id));
     courseRepository.delete(course);
   }


   // enroll a user by id
   public void enrollUser(Long courseId, Long userId) {
     Course course = courseRepository.findById(courseId)
          .orElseThrow(() -> new RuntimeException("Course not found with id " + courseId));
     User user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
     user.getEnrolledCourses().add(course);
     userRepository.save(user);
   }

   // enroll a user by email (authenticated)
   public void enrollUser(Long courseId, String userEmail) {
     if (userEmail == null) throw new RuntimeException("No authenticated user");
     User user = userRepository.findByEmail(userEmail)
          .orElseThrow(() -> new RuntimeException("User not found with email " + userEmail));
     enrollUser(courseId, user.getId());
   }

   // unenroll a user by id
   public void unenrollUser(Long courseId, Long userId) {
     Course course = courseRepository.findById(courseId)
          .orElseThrow(() -> new RuntimeException("Course not found with id " + courseId));
     User user = userRepository.findById(userId)
          .orElseThrow(() -> new RuntimeException("User not found with id " + userId));
     user.getEnrolledCourses().remove(course);
     userRepository.save(user);
   }

   // unenroll a user by email (authenticated)
   public void unenrollUser(Long courseId, String userEmail) {
     if (userEmail == null) throw new RuntimeException("No authenticated user");
     User user = userRepository.findByEmail(userEmail)
          .orElseThrow(() -> new RuntimeException("User not found with email " + userEmail));
     unenrollUser(courseId, user.getId());
   }

   // Get syllabus sections by course ID
   public List<SyllabusSection> getSyllabusByCourseId(Long courseId) {
     Course course = courseRepository.findById(courseId)
          .orElseThrow(() -> new RuntimeException("Course not found with id " + courseId));
     return course.getSyllabus() != null ? course.getSyllabus() : List.of();
   }
}