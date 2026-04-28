package com.university.enrollment.service;

import com.university.enrollment.domain.*;
import com.university.enrollment.exception.*;
import com.university.enrollment.presentation.dto.response.EnrollmentDTO;
import com.university.enrollment.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    @Test
    void enrollStudent_success_withStandardPrice() {
        // given
        Student student = Student.builder().id(1L).firstName("Ana").lastName("Perez").build();
        Course course = Course.builder().id(101L).name("Programming I").build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(101L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 101L)).thenReturn(false);
        when(enrollmentRepository.countByCourseId(101L)).thenReturn(2L);
        when(enrollmentRepository.countByStudentId(1L)).thenReturn(1L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(i -> {
            Enrollment e = i.getArgument(0);
            e.setId(new EnrollmentId(1L, 101L));
            e.setEnrolledAt(LocalDateTime.now());
            return e;
        });

        // when
        EnrollmentDTO result = enrollmentService.enrollStudent(1L, 101L);

        // then
        assertNotNull(result);
        assertEquals(new BigDecimal("100.00"), result.getPrice());
        assertEquals("Ana", result.getStudentName());
    }

    @Test
    void enrollStudent_success_withDiscount() {
        Student student = Student.builder().id(1L).firstName("Ana").lastName("Perez").build();
        Course course = Course.builder().id(101L).name("Programming I").build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(101L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 101L)).thenReturn(false);
        when(enrollmentRepository.countByCourseId(101L)).thenReturn(2L);
        when(enrollmentRepository.countByStudentId(1L)).thenReturn(3L);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(i -> {
            Enrollment e = i.getArgument(0);
            e.setId(new EnrollmentId(1L, 101L));
            e.setEnrolledAt(LocalDateTime.now());
            return e;
        });

        EnrollmentDTO result = enrollmentService.enrollStudent(1L, 101L);

        assertEquals(new BigDecimal("85.00"), result.getPrice());
    }

    @Test
    void enrollStudent_courseFull_throwsException() {
        Student student = Student.builder().id(1L).build();
        Course course = Course.builder().id(101L).build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(101L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.countByCourseId(101L)).thenReturn(5L);

        assertThrows(EnrollmentFullException.class, () -> enrollmentService.enrollStudent(1L, 101L));
    }

    @Test
    void enrollStudent_duplicateEnrollment_throwsException() {
        Student student = Student.builder().id(1L).build();
        Course course = Course.builder().id(101L).build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(101L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 101L)).thenReturn(true);

        assertThrows(DuplicateEnrollmentException.class, () -> enrollmentService.enrollStudent(1L, 101L));
    }

    @Test
    void enrollStudent_studentNotFound_throwsException() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> enrollmentService.enrollStudent(999L, 101L));
    }
}
