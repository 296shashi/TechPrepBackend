package com.example.techprep.service;

import com.example.techprep.dto.ArticleDTO;
import com.example.techprep.entity.Article;
import com.example.techprep.mapper.ArticleMapper;
import com.example.techprep.repository.ArticleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticleService {
    private final ArticleRepository repository;
    private final ArticleMapper mapper;

    public ArticleService(ArticleRepository repository, ArticleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ArticleDTO> getAllArticles() {
        return mapper.convertEntityListToDTOList(repository.findAll());
    }

    public Optional<ArticleDTO> getArticleById(Long id) {
        return repository.findById(id).map(mapper::convertEntityToDTO);
    }

    public ArticleDTO createArticle(ArticleDTO dto) {
        Article entity = mapper.convertDTOToEntity(dto);
        return mapper.convertEntityToDTO(repository.save(entity));
    }

    public ArticleDTO updateArticle(Long id, ArticleDTO dto) {
        Article existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Article not found with id " + id));
        Article entity = mapper.convertDTOToEntity(dto);
        entity.setId(existing.getId());
        return mapper.convertEntityToDTO(repository.save(entity));
    }

    public void deleteArticle(Long id) {
        repository.deleteById(id);
    }
}
