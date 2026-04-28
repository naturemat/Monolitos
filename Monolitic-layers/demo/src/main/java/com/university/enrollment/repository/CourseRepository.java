package com.university.enrollment.repository;

import com.university.enrollment.domain.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByNameContainingIgnoreCase(String name);
}
