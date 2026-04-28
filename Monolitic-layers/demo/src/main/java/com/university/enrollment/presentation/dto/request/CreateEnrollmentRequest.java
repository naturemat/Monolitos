package com.university.enrollment.presentation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnrollmentRequest {

    @NotNull(message = "studentId is required")
    @Min(value = 1, message = "studentId must be positive")
    private Long studentId;

    @NotNull(message = "courseId is required")
    @Min(value = 1, message = "courseId must be positive")
    private Long courseId;
}
