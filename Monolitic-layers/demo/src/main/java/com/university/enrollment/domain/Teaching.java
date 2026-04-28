package com.university.enrollment.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teaching",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_teaching_professor_course", columnNames = {"professor_id", "course_id"})
       })
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Teaching {

    @EmbeddedId
    private TeachingId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("professorId")
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}
