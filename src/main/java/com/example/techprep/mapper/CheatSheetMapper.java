package com.example.techprep.mapper;

import com.example.techprep.dto.CheatSheetDTO;
import com.example.techprep.entity.CheatSheet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CheatSheetMapper {
    CheatSheet convertDTOToEntity(CheatSheetDTO dto);
    CheatSheetDTO convertEntityToDTO(CheatSheet entity);
    List<CheatSheet> convertDTOListToEntityList(List<CheatSheetDTO> dtos);
    List<CheatSheetDTO> convertEntityListToDTOList(List<CheatSheet> entities);
}
