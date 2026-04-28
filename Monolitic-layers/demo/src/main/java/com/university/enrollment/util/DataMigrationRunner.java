package com.university.enrollment.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.university.enrollment.domain.Enrollment;
import com.university.enrollment.domain.EnrollmentId;
import com.university.enrollment.domain.Teaching;
import com.university.enrollment.domain.TeachingId;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataMigrationRunner implements CommandLineRunner {

    private final com.university.enrollment.repository.StudentRepository studentRepository;
    private final com.university.enrollment.repository.CourseRepository courseRepository;
    private final com.university.enrollment.repository.ProfessorRepository professorRepository;
    private final com.university.enrollment.repository.EnrollmentRepository enrollmentRepository;
    private final com.university.enrollment.repository.TeachingRepository teachingRepository;

    @Override
    public void run(String... args) throws Exception {
        if (studentRepository.count() > 0) {
            log.info("Database already contains data. Skipping migration.");
            return;
        }

        log.info("Starting data migration from legacy files...");

        try {
            // Migrate Students
            List<com.university.enrollment.domain.Student> students = loadStudents();
            studentRepository.saveAll(students);
            log.info("Migrated {} students", students.size());

            // Migrate Courses
            List<com.university.enrollment.domain.Course> courses = loadCourses();
            courseRepository.saveAll(courses);
            log.info("Migrated {} courses", courses.size());

            // Migrate Professors
            List<com.university.enrollment.domain.Professor> professors = loadProfessors();
            professorRepository.saveAll(professors);
            log.info("Migrated {} professors", professors.size());

            // Migrate Enrollments (requires students and courses to exist)
            List<com.university.enrollment.domain.Enrollment> enrollments = loadEnrollments(students, courses);
            enrollmentRepository.saveAll(enrollments);
            log.info("Migrated {} enrollments", enrollments.size());

            // Migrate Teaching assignments
            List<com.university.enrollment.domain.Teaching> teachings = loadTeachings(professors, courses);
            teachingRepository.saveAll(teachings);
            log.info("Migrated {} teaching assignments", teachings.size());

            log.info("Data migration completed successfully!");
        } catch (Exception e) {
            log.error("Error during data migration", e);
            throw e;
        }
    }

    private List<com.university.enrollment.domain.Student> loadStudents() throws Exception {
        ClassPathResource resource = new ClassPathResource("data-migration/students.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return br.lines()
                    .map(line -> {
                        String[] parts = line.split(",");
                        return com.university.enrollment.domain.Student.builder()
                                .id(Long.parseLong(parts[0]))
                                .firstName(parts[1].trim())
                                .lastName(parts[2].trim())
                                .age(Integer.parseInt(parts[3].trim()))
                                .semester(Integer.parseInt(parts[4].trim()))
                                .credits(Integer.parseInt(parts[5].trim()))
                                .build();
                    })
                    .toList();
        }
    }

    private List<com.university.enrollment.domain.Course> loadCourses() throws Exception {
        ClassPathResource resource = new ClassPathResource("data-migration/courses.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return br.lines()
                    .map(line -> {
                        String[] parts = line.split(",");
                        return com.university.enrollment.domain.Course.builder()
                                .id(Long.parseLong(parts[0]))
                                .name(parts[1].trim())
                                .location(parts[2].trim())
                                .build();
                    })
                    .toList();
        }
    }

    private List<com.university.enrollment.domain.Professor> loadProfessors() throws Exception {
        ClassPathResource resource = new ClassPathResource("data-migration/professors.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return br.lines()
                    .map(line -> {
                        String[] parts = line.split(",");
                        return com.university.enrollment.domain.Professor.builder()
                                .id(Long.parseLong(parts[0]))
                                .firstName(parts[1].trim())
                                .lastName(parts[2].trim())
                                .degree(parts[3].trim())
                                .employmentType(parts[4].trim())
                                .build();
                    })
                    .toList();
        }
    }

    private List<com.university.enrollment.domain.Enrollment> loadEnrollments(
            List<com.university.enrollment.domain.Student> students,
            List<com.university.enrollment.domain.Course> courses) throws Exception {

        ClassPathResource resource = new ClassPathResource("data-migration/enrollments.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return br.lines()
                    .map(line -> {
                        String[] parts = line.split(",");
                        Long studentId = Long.parseLong(parts[0].trim());
                        Long courseId = Long.parseLong(parts[1].trim());

                        com.university.enrollment.domain.Student student = students.stream()
                                .filter(s -> s.getId().equals(studentId))
                                .findFirst()
                                .orElse(null);
                        com.university.enrollment.domain.Course course = courses.stream()
                                .filter(c -> c.getId().equals(courseId))
                                .findFirst()
                                .orElse(null);

                        if (student == null || course == null) {
                            log.warn("Skipping enrollment: studentId={}, courseId={} - not found in migrated data", studentId, courseId);
                            return null;
                        }

                        return com.university.enrollment.domain.Enrollment.builder()
                                .id(new EnrollmentId(studentId, courseId))
                                .student(student)
                                .course(course)
                                .price(new BigDecimal("100.00")) // default price for legacy records
                                .enrolledAt(LocalDateTime.now())
                                .build();
                    })
                    .filter(java.util.Objects::nonNull)
                    .toList();
        }
    }

    private List<com.university.enrollment.domain.Teaching> loadTeachings(
            List<com.university.enrollment.domain.Professor> professors,
            List<com.university.enrollment.domain.Course> courses) throws Exception {

        ClassPathResource resource = new ClassPathResource("data-migration/teaching.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return br.lines()
                    .map(line -> {
                        String[] parts = line.split(",");
                        Long professorId = Long.parseLong(parts[0].trim());
                        Long courseId = Long.parseLong(parts[1].trim());

                        com.university.enrollment.domain.Professor professor = professors.stream()
                                .filter(p -> p.getId().equals(professorId))
                                .findFirst()
                                .orElse(null);
                        com.university.enrollment.domain.Course course = courses.stream()
                                .filter(c -> c.getId().equals(courseId))
                                .findFirst()
                                .orElse(null);

                        if (professor == null || course == null) {
                            log.warn("Skipping teaching assignment: professorId={}, courseId={} - not found in migrated data", professorId, courseId);
                            return null;
                        }

                        return com.university.enrollment.domain.Teaching.builder()
                                .id(new TeachingId(professorId, courseId))
                                .professor(professor)
                                .course(course)
                                .build();
                    })
                    .filter(java.util.Objects::nonNull)
                    .toList();
        }
    }
}
