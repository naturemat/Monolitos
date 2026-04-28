package com.university.enrollment.presentation.controller;

import com.university.enrollment.presentation.dto.request.CreateEnrollmentRequest;
import com.university.enrollment.presentation.dto.response.EnrollmentDTO;
import com.university.enrollment.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<EnrollmentDTO> enrollStudent(@Valid @RequestBody CreateEnrollmentRequest request) {
        EnrollmentDTO enrollment = enrollmentService.enrollStudent(request.getStudentId(), request.getCourseId());
        return new ResponseEntity<>(enrollment, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments(
            @RequestParam(required = false) String courseName) {
        List<EnrollmentDTO> result;
        if (courseName != null && !courseName.trim().isEmpty()) {
            result = enrollmentService.getEnrollmentsByCourseName(courseName);
        } else {
            result = enrollmentService.getAllEnrollments();
        }
        return ResponseEntity.ok(result);
    }
}
