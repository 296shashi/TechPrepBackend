package com.example.techprep.service;

import com.example.techprep.dto.PracticeProblemDTO;
import com.example.techprep.entity.PracticeProblem;
import com.example.techprep.mapper.PracticeProblemMapper;
import com.example.techprep.repository.PracticeProblemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PracticeProblemService {
    private final PracticeProblemRepository repository;
    private final PracticeProblemMapper mapper;

    public PracticeProblemService(PracticeProblemRepository repository, PracticeProblemMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<PracticeProblemDTO> getAllPracticeProblems() {
        return mapper.convertEntityListToDTOList(repository.findAll());
    }

    public Optional<PracticeProblemDTO> getPracticeProblemById(Long id) {
        return repository.findById(id).map(mapper::convertEntityToDTO);
    }

    public PracticeProblemDTO createPracticeProblem(PracticeProblemDTO dto) {
        PracticeProblem entity = mapper.convertDTOToEntity(dto);
        return mapper.convertEntityToDTO(repository.save(entity));
    }

    public PracticeProblemDTO updatePracticeProblem(Long id, PracticeProblemDTO dto) {
        PracticeProblem existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("PracticeProblem not found with id " + id));
        PracticeProblem entity = mapper.convertDTOToEntity(dto);
        entity.setId(existing.getId());
        return mapper.convertEntityToDTO(repository.save(entity));
    }

    public void deletePracticeProblem(Long id) {
        repository.deleteById(id);
    }
}
