package com.university.enrollment.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_enrollment_student_course", columnNames = {"student_id", "course_id"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @EmbeddedId
    private EnrollmentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "enrolled_at", nullable = false)
    private LocalDateTime enrolledAt;
}
