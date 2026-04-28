package com.university.enrollment.service;

import com.university.enrollment.domain.Course;
import com.university.enrollment.presentation.dto.response.CourseDTO;
import com.university.enrollment.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;

    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<CourseDTO> searchCoursesByName(String name) {
        return courseRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private CourseDTO toDTO(Course c) {
        return CourseDTO.builder()
                .id(c.getId())
                .name(c.getName())
                .location(c.getLocation())
                .build();
    }
}
