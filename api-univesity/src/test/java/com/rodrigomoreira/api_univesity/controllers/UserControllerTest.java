package com.rodrigomoreira.api_univesity.controllers;

import static com.rodrigomoreira.api_univesity.commons.CourseConstants.COURSE;
import static com.rodrigomoreira.api_univesity.commons.UserConstants.INVALID_USER;
import static com.rodrigomoreira.api_univesity.commons.UserConstants.USER_WITH_ID;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodrigomoreira.api_univesity.domain.users.User;
import com.rodrigomoreira.api_univesity.infra.AppConfig;
import com.rodrigomoreira.api_univesity.infra.UpdateRequest;
import com.rodrigomoreira.api_univesity.services.UserService;

import jakarta.persistence.EntityNotFoundException;

@WebMvcTest(UserController.class)
@Import({AppConfig.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    
    @Test
    void createUser_WithValidData_ReturnsCreated() throws Exception{
        when(userService.createUser(USER_WITH_ID)).thenReturn(USER_WITH_ID);

        mockMvc
        .perform(post("/users")
            .content(objectMapper.writeValueAsString(USER_WITH_ID))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.document").value(USER_WITH_ID.getDocument()));
    }

    @Test
    void createUser_WithInvalidData_ReturnsBadRequest() throws Exception{
        User emptyUser = new User();
        
        mockMvc
        .perform(post("/users")
            .content(objectMapper.writeValueAsString(emptyUser))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

        mockMvc
        .perform(post("/users")
            .content(objectMapper.writeValueAsString(INVALID_USER))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
    }

    @Test
    void createUser_WithExistingDocument_ReturnsConflict() throws Exception{
        when(userService.createUser(any())).thenThrow(new DataIntegrityViolationException("Already registered user"));

        mockMvc
                .perform(post("/users")
                    .content(objectMapper.writeValueAsString(USER_WITH_ID))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void testAddCourse () throws Exception{
        when(userService.addCourse(anyString(), anyString())).thenAnswer(invocation -> {
            USER_WITH_ID.getCourses().add(COURSE);
            return USER_WITH_ID;
        });

        UpdateRequest update = new UpdateRequest(USER_WITH_ID.getDocument(), COURSE.getName());
        
        MvcResult result = mockMvc
        .perform(put("/users/addcourse")
            .content(objectMapper.writeValueAsString(update))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        User updatedUser = objectMapper.readValue(responseBody, User.class);

        assert updatedUser.getCourses().contains(COURSE);

    }

    @Test
    void testRemoveCourse() throws Exception {
        USER_WITH_ID.getCourses().add(COURSE);

        when(userService.removeCourse(anyString(), anyString())).thenAnswer(invocation -> {
            USER_WITH_ID.getCourses().remove(COURSE);
            return USER_WITH_ID;
        });

        UpdateRequest update = new UpdateRequest(USER_WITH_ID.getDocument(), COURSE.getName());
        
        MvcResult result = mockMvc
        .perform(put("/users/removecourse")
            .content(objectMapper.writeValueAsString(update))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        User updatedUser = objectMapper.readValue(responseBody, User.class);

        assertFalse(updatedUser.getCourses().contains(COURSE));

    }

    @Test
    void getUser_ByExistingId_ReturnsUser() throws Exception {
        when(userService.getUser(1L)).thenReturn(USER_WITH_ID);

        mockMvc
                .perform(
                    get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document").value(USER_WITH_ID.getDocument()));
    }

    @Test
    void getUser_ByUnexistingId_ReturnsNotFound() throws Exception {
        when(userService.getUser(1L)).thenThrow(new EntityNotFoundException());
        mockMvc.perform(get("/users/1"))
        .andExpect(status().isNotFound());
    }

    @Test
    void getUser_ByExistingDocument_ReturnsUser() throws Exception {
        when(userService.findUserByDocument(USER_WITH_ID.getDocument())).thenReturn(USER_WITH_ID);

        mockMvc
                .perform(
                    get("/users/document").param("document", USER_WITH_ID.getDocument()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.document").value(USER_WITH_ID.getDocument()));
    }

    @Test
    void getUser_ByUnexistingDocument_ReturnsNotFound() throws Exception {
        when(userService.findUserByDocument(USER_WITH_ID.getDocument())).thenThrow(new EntityNotFoundException());
        
        mockMvc.perform(
            get("/users/document").param("document", USER_WITH_ID.getDocument()))
        .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllUsers() throws Exception{
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc
            .perform(
                get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void removeUser_WithExistingId_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void removeUser_WithUnexistingId_ReturnsNotFound() throws Exception {
        doThrow(new EntityNotFoundException()).when(userService).removeUser(1L);;
        mockMvc.perform(delete("/users/" + 1L))
            .andExpect(status().isNotFound());
    }

}
