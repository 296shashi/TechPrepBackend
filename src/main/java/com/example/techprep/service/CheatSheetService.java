package com.example.techprep.service;

import com.example.techprep.dto.CheatSheetDTO;
import com.example.techprep.entity.CheatSheet;
import com.example.techprep.mapper.CheatSheetMapper;
import com.example.techprep.repository.CheatSheetRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CheatSheetService {
    private final CheatSheetRepository repository;
    private final CheatSheetMapper mapper;

    public CheatSheetService(CheatSheetRepository repository, CheatSheetMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<CheatSheetDTO> getAllCheatSheets() {
        return mapper.convertEntityListToDTOList(repository.findAll());
    }

    public Optional<CheatSheetDTO> getCheatSheetById(Long id) {
        return repository.findById(id).map(mapper::convertEntityToDTO);
    }

    public CheatSheetDTO createCheatSheet(CheatSheetDTO dto) {
        CheatSheet entity = mapper.convertDTOToEntity(dto);
        return mapper.convertEntityToDTO(repository.save(entity));
    }

    public CheatSheetDTO updateCheatSheet(Long id, CheatSheetDTO dto) {
        CheatSheet existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("CheatSheet not found with id " + id));
        CheatSheet entity = mapper.convertDTOToEntity(dto);
        entity.setId(existing.getId());
        return mapper.convertEntityToDTO(repository.save(entity));
    }

    public void deleteCheatSheet(Long id) {
        repository.deleteById(id);
    }
}
