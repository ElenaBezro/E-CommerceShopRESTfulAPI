package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.entities.Role;
import com.bezro.shopRESTfulAPI.entities.User;
import com.bezro.shopRESTfulAPI.repositories.UserRepository;
import com.bezro.shopRESTfulAPI.services.AuthService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @InjectMocks
    private AuthController authController;

    @Autowired
    //to fake http requests
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get token when log in with valid credentials")
    void shouldLoginAndGetToken_WhenLoginWithValidCredentials() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"userTest\", \"password\": \"100\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 status when log in with empty password field")
    void shouldGetBadRequest_WhenLoginWithEmptyPassword() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"userTest\", \"password\": \"   \"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        Matchers.is("The password is required.")
                )));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 status when log in with empty username field")
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
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        Matchers.is("The username is required.")
                )));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 status when log in with invalid password")
    void shouldGetBadRequest_WhenLoginWithInvalidPassword() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"userTest\", \"password\": \"InvalidPassword\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Login or password is invalid"));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 status when log in with invalid username and password")
    void shouldGetBadRequest_WhenLoginWithInvalidCredentials() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"userInvalid\", \"password\": \"InvalidPassword\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Login or password is invalid"));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get user when register a new user success")
    void shouldRegisterAndGetUserDto_WhenRegisterWithValidData() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"NewUser\"," +
                                " \"password\": \"Password1!\"," +
                                " \"confirmPassword\": \"Password1!\"," +
                                " \"email\": \"email@email.com\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("5"))
                .andExpect(jsonPath("$.username").value("NewUser"))
                .andExpect(jsonPath("$.email").value("email@email.com"));
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test that user was added to the database when register")
    void shouldAddNewUserIntoDatabase_WhenRegister() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"NewUser\"," +
                                " \"password\": \"Password1!\"," +
                                " \"confirmPassword\": \"Password1!\"," +
                                " \"email\": \"email@email.com\"}"))
                .andExpect(status().isOk());

        Optional<User> newUser = userRepository.findById(5L);
        assertTrue(newUser.isPresent(), "User has been created");
        assertEquals("NewUser", newUser.get().getUsername(), "Should have the same username");
        assertEquals("ROLE_USER", ((Role) newUser.get().getRoles().toArray()[0]).getName(), "Should have role 'ROLE_USER'");
        boolean isAdminRolePresent = newUser.get().getRoles().stream()
                .anyMatch(role -> role.getName().equals("ROLE_ADMIN"));
        assertFalse(isAdminRolePresent, "User should not have ROLE_ADMIN");
    }

    @Test
    @DisplayName("Test get 400 status when register with empty payload")
    void shouldGetBadRequest_WhenRegisterWithEmptyData() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.messages[*]").isArray())
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        "The username is required.",
                        "The email is required.",
                        "The password is required.",
                        "The confirm Password is required.")));
        Optional<User> newUser = userRepository.findById(5L);
        assertTrue(newUser.isEmpty(), "User has not been created");
    }

    @Test
    @DisplayName("Test get 400 status when register with invalid data")
    void shouldGetBadRequest_WhenRegisterWithInvalidData() throws Exception {
        // Arrange
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
                .andExpect(jsonPath("$.messages", containsInAnyOrder(
                        "The username must be from 3 to 20 characters.",
                        "The email is not a valid email.",
                        "Password and Confirm Password must be matched!",
                        "Password must be 8 characters long and combination of uppercase letters, lowercase letters, numbers, special characters.")));
        Optional<User> newUser = userRepository.findById(5L);
        assertTrue(newUser.isEmpty(), "User has not been created");
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 status when register with an already existing username")
    void shouldGetBadRequest_WhenRegisterUsernameAlreadyExists() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"userTest\"," +
                                " \"password\": \"Password1!\"," +
                                " \"confirmPassword\": \"Password1!\"," +
                                " \"email\": \"email@email.com\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("User with username userTest already exists"));
        Optional<User> newUser = userRepository.findById(5L);
        assertTrue(newUser.isEmpty(), "User has not been created");
    }

    @Test
    @Sql(scripts = "classpath:db/populateDB.sql")
    @Sql(scripts = "classpath:db/dropDB.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @jakarta.transaction.Transactional
    @DisplayName("Test get 400 status when register with an already existing e-mail")
    void shouldGetBadRequest_WhenRegisterEmailAlreadyExists() throws Exception {
        // Arrange
        // Act
        // Assert
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content("{\"username\": \"New User\"," +
                                " \"password\": \"Password1!\"," +
                                " \"confirmPassword\": \"Password1!\"," +
                                " \"email\": \"user1@example.com\"}"))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("User with e-mail user1@example.com already exists"));
        Optional<User> newUser = userRepository.findById(5L);
        assertTrue(newUser.isEmpty(), "User has not been created");
    }
}