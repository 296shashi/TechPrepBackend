package com.example.techprep.mapper;

import com.example.techprep.dto.InterviewDTO;
import com.example.techprep.entity.Interview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InterviewMapper {

    @Mapping(target = "interviewer", ignore = true)
    @Mapping(target = "user", ignore = true)

    @Mapping(target = "meetLink", source = "meetLink")
    Interview convertDTOToEntity(InterviewDTO dto);

    @Mapping(target = "interviewerId", source = "interviewer.id")
    @Mapping(target = "interviewer", source = "interviewer.name")

    @Mapping(target = "meetLink", source = "meetLink")
    InterviewDTO convertEntityToDTO(Interview entity);

    List<Interview> convertDTOListToEntityList(List<InterviewDTO> dtos);

    List<InterviewDTO> convertEntityListToDTOList(List<Interview> entities);

}
