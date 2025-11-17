package com.example.techprep.repository;

import com.example.techprep.entity.PracticeProblem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeProblemRepository extends JpaRepository<PracticeProblem, Long> {}
