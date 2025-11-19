package com.example.techprep.mapper;

import com.example.techprep.dto.VideoDTO;
import com.example.techprep.entity.Video;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VideoMapper {
    Video convertDTOToEntity(VideoDTO dto);
    VideoDTO convertEntityToDTO(Video entity);
    List<Video> convertDTOListToEntityList(List<VideoDTO> dtos);
    List<VideoDTO> convertEntityListToDTOList(List<Video> entities);
}
