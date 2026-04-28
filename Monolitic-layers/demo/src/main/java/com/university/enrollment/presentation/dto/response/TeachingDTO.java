package com.university.enrollment.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeachingDTO {
    private Long professorId;
    private String professorName;
    private Long courseId;
    private String courseName;
}
