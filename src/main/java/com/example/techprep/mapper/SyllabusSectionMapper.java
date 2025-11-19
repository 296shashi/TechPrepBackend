package com.example.techprep.mapper;

import com.example.techprep.dto.SyllabusSectionDTO;
import com.example.techprep.entity.SyllabusSection;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {LessonMapper.class})
public interface SyllabusSectionMapper {

    SyllabusSection convertDTOToEntity(SyllabusSectionDTO dto);

    SyllabusSectionDTO convertEntityToDTO(SyllabusSection entity);

    List<SyllabusSection> convertDTOListToEntityList(List<SyllabusSectionDTO> dtos);

    List<SyllabusSectionDTO> convertEntityListToDTOList(List<SyllabusSection> entities);

}
