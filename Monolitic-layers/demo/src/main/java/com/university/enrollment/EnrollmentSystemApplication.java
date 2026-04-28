package com.university.enrollment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class EnrollmentSystemApplication {

    public static void main(String[] args) {
        log.info("Starting Course Enrollment System...");
        SpringApplication.run(EnrollmentSystemApplication.class, args);
    }
}
