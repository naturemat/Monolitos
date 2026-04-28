package com.university.enrollment.repository;

import com.university.enrollment.domain.Teaching;
import com.university.enrollment.domain.TeachingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeachingRepository extends JpaRepository<Teaching, TeachingId> {
}
