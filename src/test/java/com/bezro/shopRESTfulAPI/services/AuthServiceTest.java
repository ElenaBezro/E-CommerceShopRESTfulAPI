package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.JwtResponse;
import com.bezro.shopRESTfulAPI.dtos.LoginUserDto;
import com.bezro.shopRESTfulAPI.dtos.RegistrationUserDto;
import com.bezro.shopRESTfulAPI.dtos.UserDto;
import com.bezro.shopRESTfulAPI.entities.User;
import com.bezro.shopRESTfulAPI.exceptions.UserAlreadyExistsException;
import com.bezro.shopRESTfulAPI.exceptions.UserWithEmailAlreadyExistsException;
import com.bezro.shopRESTfulAPI.jwtUtils.JwtTokenUtils;
import com.bezro.shopRESTfulAPI.services.impl.AuthServiceImpl;
import com.bezro.shopRESTfulAPI.services.impl.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private JwtTokenUtils jwtTokenUtils;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void shouldReturnJwtResponseWithToken_WhenUserExists() {
        // Arrange
        String jwtToken = "token";
        UserDetails userDetails = new org.springframework.security.core.userdetails.User("User", "Password", new HashSet<>());
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setUsername("User");
        loginUserDto.setPassword("Password");

        when(userService.loadUserByUsername(eq("User"))).thenReturn(userDetails);
        when(jwtTokenUtils.generateToken(eq(userDetails))).thenReturn(jwtToken);

        // Act
        JwtResponse jwtResponse = authService.login(loginUserDto);

        // Assert
        assertEquals(jwtResponse.getToken(), jwtToken);
    }

    @Test
    void shouldReturnUserDto_WhenUserNameAndEmailAvailable() {
        // Arrange
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("User");
        registrationUserDto.setPassword("Password");
        registrationUserDto.setConfirmPassword("Password");
        registrationUserDto.setEmail("test@email.com");

        User user = new User();
        user.setId(1L);
        user.setUsername("User");
        user.setEmail("test@email.com");

        when(userService.existsByUsername(eq("User"))).thenReturn(false);
        when(userService.existsByEmail(eq("test@email.com"))).thenReturn(false);
        when(userService.createNewUser(eq(registrationUserDto))).thenReturn(user);

        // Act
        UserDto userDto = authService.createNewUser(registrationUserDto);

        // Assert
        assertEquals(userDto.getId(), 1L);
        assertEquals(userDto.getUsername(), "User");
        assertEquals(userDto.getEmail(), "test@email.com");
    }

    @Test
    void shouldThrowUserAlreadyExists_WhenUserNameTaken() {
        // Arrange
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("User");
        registrationUserDto.setPassword("Password");
        registrationUserDto.setConfirmPassword("Password");
        registrationUserDto.setEmail("test@email.com");

        when(userService.existsByUsername(eq("User"))).thenReturn(true);

        // Act
        // Assert
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> authService.createNewUser(registrationUserDto),
                "Creating a new user with an already taken username should throw UserAlreadyExistsException.");
        assertEquals("User with username User already exists", exception.getMessage());
    }

    @Test
    void shouldThrowEmailAlreadyExists_WhenEmailTaken() {
        // Arrange
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("User");
        registrationUserDto.setPassword("Password");
        registrationUserDto.setConfirmPassword("Password");
        registrationUserDto.setEmail("test@email.com");

        when(userService.existsByUsername(eq("User"))).thenReturn(false);
        when(userService.existsByEmail(eq("test@email.com"))).thenReturn(true);

        // Act
        // Assert
        UserWithEmailAlreadyExistsException exception = assertThrows(UserWithEmailAlreadyExistsException.class,
                () -> authService.createNewUser(registrationUserDto),
                "Creating a new user with an already taken email should throw UserWithEmailAlreadyExistsException.");
        assertEquals("User with e-mail test@email.com already exists", exception.getMessage());
    }
}