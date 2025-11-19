package com.example.techprep.mapper;

import com.example.techprep.dto.CourseDTO;
import com.example.techprep.entity.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SyllabusSectionMapper.class, LessonMapper.class})
public interface CourseMapper {



    @Mapping(target = "enrolled", ignore = true)
    @Mapping(target = "enrolledUsers", ignore = true)
    Course convertDTOToEntity(CourseDTO dto);

    /**
     * Note: The 'enrolled' field in CourseDTO must be set manually in the service layer.
     */

    @Mapping(target = "enrolled", ignore = true)
    @Mapping(target = "enrolledCount", ignore = true)
    @Mapping(target = "completedLessons", ignore = true)
    @Mapping(target = "completedLessonIds", ignore = true)
    @Mapping(target = "totalLessons", ignore = true)
    CourseDTO convertEntityToDTO(Course entity);

    List<Course> convertDTOListToEntityList(List<CourseDTO> dtos);

    List<CourseDTO> convertEntityListToDTOList(List<Course> entities);

}
