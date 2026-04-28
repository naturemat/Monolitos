package com.university.enrollment.repository;

import com.university.enrollment.domain.Enrollment;
import com.university.enrollment.domain.EnrollmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {

    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);

    long countByCourseId(Long courseId);

    long countByStudentId(Long studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.course.name LIKE %:courseName%")
    List<Enrollment> findByCourseNameContainingIgnoreCase(String courseName);
}
