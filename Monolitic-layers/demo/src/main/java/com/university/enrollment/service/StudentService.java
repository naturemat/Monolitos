package com.university.enrollment.service;

import com.university.enrollment.domain.Student;
import com.university.enrollment.exception.EntityNotFoundException;
import com.university.enrollment.presentation.dto.response.StudentDTO;
import com.university.enrollment.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        return toDTO(student);
    }

    private StudentDTO toDTO(Student s) {
        return StudentDTO.builder()
                .id(s.getId())
                .firstName(s.getFirstName())
                .lastName(s.getLastName())
                .age(s.getAge())
                .semester(s.getSemester())
                .credits(s.getCredits())
                .build();
    }
}
