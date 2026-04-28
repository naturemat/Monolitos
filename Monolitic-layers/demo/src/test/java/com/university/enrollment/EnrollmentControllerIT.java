package com.university.enrollment;

import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class EnrollmentControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testGetAllEnrollments() throws Exception {
        mockMvc.perform(get("/api/v1/enrollments"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllStudents() throws Exception {
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllCourses() throws Exception {
        mockMvc.perform(get("/api/v1/courses"))
                .andExpect(status().isOk());
    }

    @Test
    public void testPostEnrollment_validRequest_returns201() throws Exception {
        String json = "{\"studentId\":1,\"courseId\":101}";
        mockMvc.perform(post("/api/v1/enrollments")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    public void testPostEnrollment_invalidRequest_returns400() throws Exception {
        String json = "{\"studentId\":null,\"courseId\":null}";
        mockMvc.perform(post("/api/v1/enrollments")
                .contentType("application/json")
                .content(json))
                .andExpect(status().isBadRequest());
    }
}
