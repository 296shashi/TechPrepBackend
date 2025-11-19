package com.example.techprep.service;

import com.example.techprep.dto.VideoDTO;
import com.example.techprep.entity.Video;
import com.example.techprep.mapper.VideoMapper;
import com.example.techprep.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VideoService {
    private final VideoRepository repository;
    private final VideoMapper mapper;

    public VideoService(VideoRepository repository, VideoMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<VideoDTO> getAllVideos() {
        return mapper.convertEntityListToDTOList(repository.findAll());
    }

    public Optional<VideoDTO> getVideoById(Long id) {
        return repository.findById(id).map(mapper::convertEntityToDTO);
    }

    public VideoDTO createVideo(VideoDTO dto) {
        Video entity = mapper.convertDTOToEntity(dto);
        return mapper.convertEntityToDTO(repository.save(entity));
    }

    public VideoDTO updateVideo(Long id, VideoDTO dto) {
        Video existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Video not found with id " + id));
        Video entity = mapper.convertDTOToEntity(dto);
        entity.setId(existing.getId());
        return mapper.convertEntityToDTO(repository.save(entity));
    }

    public void deleteVideo(Long id) {
        repository.deleteById(id);
    }
}
