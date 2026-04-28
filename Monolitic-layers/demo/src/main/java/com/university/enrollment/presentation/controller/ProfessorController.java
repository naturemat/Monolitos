package com.university.enrollment.presentation.controller;

import com.university.enrollment.presentation.dto.response.ProfessorDTO;
import com.university.enrollment.service.ProfessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/professors")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @GetMapping
    public ResponseEntity<List<ProfessorDTO>> getAllProfessors() {
        return ResponseEntity.ok(professorService.getAllProfessors());
    }
}
