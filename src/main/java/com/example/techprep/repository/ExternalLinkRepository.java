package com.example.techprep.repository;

import com.example.techprep.entity.ExternalLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalLinkRepository extends JpaRepository<ExternalLink, Long> {}