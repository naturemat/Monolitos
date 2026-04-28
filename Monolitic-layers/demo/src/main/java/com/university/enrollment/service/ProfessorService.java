package com.university.enrollment.service;

import com.university.enrollment.domain.Professor;
import com.university.enrollment.presentation.dto.response.ProfessorDTO;
import com.university.enrollment.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfessorService {

    private final ProfessorRepository professorRepository;

    public List<ProfessorDTO> getAllProfessors() {
        return professorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private ProfessorDTO toDTO(Professor p) {
        return ProfessorDTO.builder()
                .id(p.getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .degree(p.getDegree())
                .employmentType(p.getEmploymentType())
                .build();
    }
}
