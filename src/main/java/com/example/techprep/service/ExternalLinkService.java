package com.example.techprep.service;

import com.example.techprep.dto.ExternalLinkDTO;
import com.example.techprep.entity.ExternalLink;
import com.example.techprep.mapper.ExternalLinkMapper;
import com.example.techprep.repository.ExternalLinkRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ExternalLinkService {
    private final ExternalLinkRepository repository;
    private final ExternalLinkMapper mapper;

    public ExternalLinkService(ExternalLinkRepository repository, ExternalLinkMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ExternalLinkDTO> getAllExternalLinks() {
        return mapper.convertEntityListToDTOList(repository.findAll());
    }

    public Optional<ExternalLinkDTO> getExternalLinkById(Long id) {
        return repository.findById(id).map(mapper::convertEntityToDTO);
    }

    public ExternalLinkDTO createExternalLink(ExternalLinkDTO dto) {
        ExternalLink entity = mapper.convertDTOToEntity(dto);
        return mapper.convertEntityToDTO(repository.save(entity));
    }

    public ExternalLinkDTO updateExternalLink(Long id, ExternalLinkDTO dto) {
        ExternalLink existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("ExternalLink not found with id " + id));
        ExternalLink entity = mapper.convertDTOToEntity(dto);
        entity.setId(existing.getId());
        return mapper.convertEntityToDTO(repository.save(entity));
    }

    public void deleteExternalLink(Long id) {
        repository.deleteById(id);
    }
}
