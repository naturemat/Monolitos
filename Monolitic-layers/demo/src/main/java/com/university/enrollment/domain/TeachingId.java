package com.university.enrollment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeachingId implements Serializable {

    @Column(name = "professor_id")
    private Long professorId;

    @Column(name = "course_id")
    private Long courseId;
}
