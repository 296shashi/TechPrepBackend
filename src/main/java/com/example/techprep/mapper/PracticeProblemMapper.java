package com.example.techprep.mapper;

import com.example.techprep.dto.PracticeProblemDTO;
import com.example.techprep.entity.PracticeProblem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PracticeProblemMapper {
    PracticeProblem convertDTOToEntity(PracticeProblemDTO dto);
    PracticeProblemDTO convertEntityToDTO(PracticeProblem entity);
    List<PracticeProblem> convertDTOListToEntityList(List<PracticeProblemDTO> dtos);
    List<PracticeProblemDTO> convertEntityListToDTOList(List<PracticeProblem> entities);
}
