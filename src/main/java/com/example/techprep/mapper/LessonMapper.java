package com.example.techprep.mapper;

import com.example.techprep.dto.LessonDTO;
import com.example.techprep.entity.Lesson;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    Lesson convertDTOToEntity(LessonDTO dto);

    LessonDTO convertEntityToDTO(Lesson entity);

    List<Lesson> convertDTOListToEntityList(List<LessonDTO> dtos);

    List<LessonDTO> convertEntityListToDTOList(List<Lesson> entities);

}
