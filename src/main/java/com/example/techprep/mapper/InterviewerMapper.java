package com.example.techprep.mapper;

import com.example.techprep.dto.InterviewerDTO;
import com.example.techprep.entity.Interviewer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InterviewerMapper {

    Interviewer convertDTOToEntity(InterviewerDTO dto);

    InterviewerDTO convertEntityToDTO(Interviewer entity);

    List<Interviewer> convertDTOListToEntityList(List<InterviewerDTO> dtos);

    List<InterviewerDTO> convertEntityListToDTOList(List<Interviewer> entities);

}
