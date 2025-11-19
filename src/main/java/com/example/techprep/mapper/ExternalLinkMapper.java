package com.example.techprep.mapper;

import com.example.techprep.dto.ExternalLinkDTO;
import com.example.techprep.entity.ExternalLink;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExternalLinkMapper {
    ExternalLink convertDTOToEntity(ExternalLinkDTO dto);
    ExternalLinkDTO convertEntityToDTO(ExternalLink entity);
    List<ExternalLink> convertDTOListToEntityList(List<ExternalLinkDTO> dtos);
    List<ExternalLinkDTO> convertEntityListToDTOList(List<ExternalLink> entities);
}
