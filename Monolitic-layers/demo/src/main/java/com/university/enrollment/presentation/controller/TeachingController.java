package com.university.enrollment.presentation.controller;

import com.university.enrollment.presentation.dto.response.TeachingDTO;
import com.university.enrollment.service.TeachingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teaching")
@RequiredArgsConstructor
public class TeachingController {

    private final TeachingService teachingService;

    @GetMapping
    public ResponseEntity<List<TeachingDTO>> getAllTeachings() {
        return ResponseEntity.ok(teachingService.getAllTeachings());
    }
}
