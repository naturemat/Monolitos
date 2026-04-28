package com.university.enrollment.service;

import com.university.enrollment.domain.Teaching;
import com.university.enrollment.presentation.dto.response.TeachingDTO;
import com.university.enrollment.repository.TeachingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeachingService {

    private final TeachingRepository teachingRepository;

    public List<TeachingDTO> getAllTeachings() {
        return teachingRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private TeachingDTO toDTO(Teaching t) {
        return TeachingDTO.builder()
                .professorId(t.getProfessor().getId())
                .professorName(t.getProfessor().getFirstName() + " " + t.getProfessor().getLastName())
                .courseId(t.getCourse().getId())
                .courseName(t.getCourse().getName())
                .build();
    }
}
