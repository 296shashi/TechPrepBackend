package com.example.techprep.mapper;

import com.example.techprep.dto.ArticleDTO;
import com.example.techprep.entity.Article;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ArticleMapper {
    Article convertDTOToEntity(ArticleDTO dto);
    ArticleDTO convertEntityToDTO(Article entity);
    List<Article> convertDTOListToEntityList(List<ArticleDTO> dtos);
    List<ArticleDTO> convertEntityListToDTOList(List<Article> entities);
}
