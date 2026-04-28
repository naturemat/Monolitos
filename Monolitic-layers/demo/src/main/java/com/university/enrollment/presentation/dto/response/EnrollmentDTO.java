package com.university.enrollment.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDTO {
    private Long studentId;
    private Long courseId;
    private String studentName;
    private String courseName;
    private BigDecimal price;
    private LocalDateTime enrolledAt;
}
