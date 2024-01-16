package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.dtos.JwtResponse;
import com.bezro.shopRESTfulAPI.dtos.UserDto;
import com.bezro.shopRESTfulAPI.exceptions.InvalidLoginCredentialsException;
import com.bezro.shopRESTfulAPI.jwtUtils.JwtTokenUtils;
import com.bezro.shopRESTfulAPI.services.AuthService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @MockBean
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Autowired
    //to fake http requests
    private MockMvc mockMvc;

    @MockBean
    AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @Test
    void shouldLoginAndGetToken_WhenLoginWithValidCredentials() throws Exception {
        // Arrange
        String jwtToken = "token";
        JwtResponse jwtResponse = new JwtResponse(jwtToken);

        when(authService.login(any())).thenReturn(jwtResponse);
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"User\", \"password\": \"Password1!\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(jwtToken));
    }

    @Test
    void shouldGetBadRequest_WhenLoginWithEmptyPassword() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"User\", \"password\": \"   \"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages").isNotEmpty());
    }

    @Test
    void shouldGetBadRequest_WhenLoginWithEmptyUsername() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"  \", \"password\": \"Password1!\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages").isNotEmpty());
    }

    @Test
    void shouldGetBadRequest_WhenLoginWithInvalidCredentials() throws Exception {
        // Arrange
        Authentication authentication = mock(Authentication.class);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authenticationManager.authenticate(any())).thenThrow(new InvalidLoginCredentialsException("Error message"));
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"User\", \"password\": \"Password1!\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Error message"));
    }

    @Test
    void shouldRegisterAndGetUserDto_WhenRegisterWithValidData() throws Exception {
        // Arrange
        UserDto userDto = new UserDto(1L, "User", "email@email.com");
        when(authService.createNewUser(any())).thenReturn(userDto);
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"User\"," +
                                " \"password\": \"Password1!\"," +
                                " \"confirmPassword\": \"Password1!\"," +
                                " \"email\": \"email@email.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.username").value("User"))
                .andExpect(jsonPath("$.email").value("email@email.com"));
    }

    @Test
    void shouldGetBadRequest_WhenRegisterWithEmptyData() throws Exception {
        // Arrange
        int UserDtoCheckedFieldsCount = 4;
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages[*]").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.length()").value(UserDtoCheckedFieldsCount));
    }

    @Test
    void shouldGetBadRequest_WhenRegisterWithInvalidData() throws Exception {
        // Arrange
        int UserDtoCheckedFieldsCount = 4;
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"s\"," +
                                " \"password\": \"short\"," +
                                " \"confirmPassword\": \"mismatch\"," +
                                " \"email\": \"invalidEmail\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages[*]").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.messages.length()").value(UserDtoCheckedFieldsCount));
    }
}