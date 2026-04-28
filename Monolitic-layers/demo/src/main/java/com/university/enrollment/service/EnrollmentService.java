package com.university.enrollment.service;

import com.university.enrollment.domain.*;
import com.university.enrollment.exception.EntityNotFoundException;
import com.university.enrollment.exception.EnrollmentFullException;
import com.university.enrollment.exception.DuplicateEnrollmentException;
import com.university.enrollment.presentation.dto.response.EnrollmentDTO;
import com.university.enrollment.repository.CourseRepository;
import com.university.enrollment.repository.EnrollmentRepository;
import com.university.enrollment.repository.ProfessorRepository;
import com.university.enrollment.repository.StudentRepository;
import com.university.enrollment.util.PriceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public EnrollmentDTO enrollStudent(Long studentId, Long courseId) {
        log.info("Processing enrollment request: studentId={}, courseId={}", studentId, courseId);

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + courseId));

        boolean alreadyExists = enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
        if (alreadyExists) {
            throw new DuplicateEnrollmentException("Student already enrolled in this course");
        }

        long currentEnrollmentCount = enrollmentRepository.countByCourseId(courseId);
        if (currentEnrollmentCount >= 5) {
            throw new EnrollmentFullException("Course has reached maximum capacity of 5 students");
        }

        int studentEnrollmentCount = (int) enrollmentRepository.countByStudentId(studentId);
        BigDecimal price = PriceCalculator.calculatePrice(studentEnrollmentCount);

        Enrollment enrollment = Enrollment.builder()
                .id(new EnrollmentId(studentId, courseId))
                .student(student)
                .course(course)
                .price(price)
                .enrolledAt(LocalDateTime.now())
                .build();

        enrollmentRepository.save(enrollment);
        log.info("Enrollment created successfully: studentId={}, courseId={}, price={}", studentId, courseId, price);

        return toDTO(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getAllEnrollments() {
        return enrollmentRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByCourseName(String courseName) {
        return enrollmentRepository.findByCourseNameContainingIgnoreCase(courseName).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private EnrollmentDTO toDTO(Enrollment e) {
        return EnrollmentDTO.builder()
                .studentId(e.getStudent().getId())
                .courseId(e.getCourse().getId())
                .studentName(e.getStudent().getFirstName() + " " + e.getStudent().getLastName())
                .courseName(e.getCourse().getName())
                .price(e.getPrice())
                .enrolledAt(e.getEnrolledAt())
                .build();
    }
}
