package com.rodrigomoreira.api_univesity.controllers;

import static com.rodrigomoreira.api_univesity.commons.UserConstants.INVALID_USER;
import static com.rodrigomoreira.api_univesity.commons.UserConstants.USER_WITH_ID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rodrigomoreira.api_univesity.domain.users.User;
import com.rodrigomoreira.api_univesity.infra.AppConfig;
import com.rodrigomoreira.api_univesity.services.UserService;

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
    void createUser_WithExistingName_ReturnsConflict() throws Exception{
        when(userService.createUser(any())).thenThrow(new DataIntegrityViolationException("Already registered user"));

        mockMvc
                .perform(post("/users")
                    .content(objectMapper.writeValueAsString(USER_WITH_ID))
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

}
