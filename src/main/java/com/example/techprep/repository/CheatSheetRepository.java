package com.example.techprep.repository;

import com.example.techprep.entity.CheatSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheatSheetRepository extends JpaRepository<CheatSheet, Long> { }