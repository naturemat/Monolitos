package com.university.enrollment.exception;

public class EnrollmentFullException extends RuntimeException {
    public EnrollmentFullException(String message) {
        super(message);
    }
}
