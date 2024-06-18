package com.rodrigomoreira.api_univesity.controllers;

import static com.rodrigomoreira.api_univesity.commons.CourseConstants.COURSE;
import static com.rodrigomoreira.api_univesity.commons.CourseConstants.INVALID_COURSE;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodrigomoreira.api_univesity.domain.courses.Course;
import com.rodrigomoreira.api_univesity.infra.AppConfig;
import com.rodrigomoreira.api_univesity.services.CourseService;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(CourseController.class)
@Import({AppConfig.class})
public class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @Test
    void testCreateCourse() throws Exception {
        when(courseService.createCourse(COURSE)).thenReturn(COURSE);

        mockMvc
        .perform(post("/courses")
            .content(objectMapper.writeValueAsString(COURSE))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value(COURSE.getName()));
    }

    @Test
    void testCreateCourseError() throws Exception {
        Course emptyCourse = new Course();

        mockMvc
        .perform(post("/courses")
            .content(objectMapper.writeValueAsString(emptyCourse))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

        mockMvc
        .perform(post("/courses")
            .content(objectMapper.writeValueAsString(INVALID_COURSE))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    }

    @Test
    void testCreateCourse_WithExistingName() throws Exception{
        when(courseService.createCourse(any())).thenThrow(new DataIntegrityViolationException("COURSES(NAME"));

        mockMvc
                .perform(post("/courses")
                    .content(objectMapper.writeValueAsString(COURSE))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Course already registered"));
        
    }

    @Test
    void testGetCourseById() throws Exception{
        when(courseService.findCourseById(1L)).thenReturn(COURSE);

        mockMvc
                .perform(
                    get("/courses/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testCourseNotFoundById() throws Exception{
        when(courseService.findCourseById(1L)).thenThrow(new EntityNotFoundException());
        mockMvc.perform(get("/courses/1"))
            .andExpect(status().isNotFound());
    }

    @Test
    void testGetCourseByName() throws Exception{
        when(courseService.findCourseByName(COURSE.getName())).thenReturn(COURSE);

        mockMvc
                .perform(
                    get("/courses/name").param("name", COURSE.getName()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(COURSE.getName()));
                
    }

    @Test
    void testCourseNotFoundByName() throws Exception{
        when(courseService.findCourseByName(COURSE.getName())).thenThrow(new EntityNotFoundException());
        mockMvc.perform(
            get("/courses/name").param("name", COURSE.getName()))
        .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllCourses() throws Exception{
        when(courseService.getAllCourses()).thenReturn(Collections.emptyList());

        mockMvc
            .perform(
                get("/courses"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testDeleteCourse_SuccessfulCase() throws Exception {
        mockMvc.perform(delete("/courses/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCourse_NotFound() throws Exception{
        final Long courseId = 1L;

        doThrow(new EntityNotFoundException()).when(courseService).deleteCourseById(courseId);
        mockMvc.perform(delete("/courses/" + courseId))
            .andExpect(status().isNotFound());
    }

}
