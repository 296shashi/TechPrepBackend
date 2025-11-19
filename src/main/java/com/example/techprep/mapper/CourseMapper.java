package com.example.techprep.mapper;

import com.example.techprep.dto.CourseDTO;
import com.example.techprep.entity.Course;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {SyllabusSectionMapper.class, LessonMapper.class})
public interface CourseMapper {

    Course convertDTOToEntity(CourseDTO dto);

    CourseDTO convertEntityToDTO(Course entity);

    List<Course> convertDTOListToEntityList(List<CourseDTO> dtos);

    List<CourseDTO> convertEntityListToDTOList(List<Course> entities);

}
