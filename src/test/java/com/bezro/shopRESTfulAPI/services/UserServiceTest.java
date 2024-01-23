package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.RegistrationUserDto;
import com.bezro.shopRESTfulAPI.entities.Role;
import com.bezro.shopRESTfulAPI.entities.User;
import com.bezro.shopRESTfulAPI.exceptions.InvalidMethodArgumentsException;
import com.bezro.shopRESTfulAPI.repositories.UserRepository;
import com.bezro.shopRESTfulAPI.services.impl.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnUser_WhenFindById() {
        // Arrange
        User userMock = new User();
        userMock.setId(1L);
        userMock.setUsername("User");
        userMock.setEmail("test@email.com");

        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(userMock));

        // Act
        User user = userService.findById(1L);

        // Assert
        assertSame(user, userMock, "Should be the same object reference");
        assertEquals(user.getUsername(), userMock.getUsername(), "Usernames should match");
        assertEquals(user.getId(), userMock.getId(), "Ids should match");
        assertEquals(user.getEmail(), userMock.getEmail(), "E-mails should match");
    }

    @Test
    void shouldThrowException_WhenNotFoundById() {
        // Arrange
        when(userRepository.findById(eq(1L))).thenReturn(Optional.empty());

        // Act
        // Assert
        InvalidMethodArgumentsException exception = assertThrows(InvalidMethodArgumentsException.class,
                () -> userService.findById(1L),
                "Getting non-existing user should throw InvalidMethodArgumentsException.");
        assertEquals(exception.getMessage(), "User with id: 1 does not exist", "Should have the same exception message");
    }

    @Test
    void shouldReturnUser_WhenFindByUsername() {
        // Arrange
        UserDetails userDetailsMock = new User();
        when(userRepository.findByUsername(eq("User"))).thenReturn(Optional.of(userDetailsMock));

        // Act
        UserDetails user = userService.findByUsername("User");

        // Assert
        assertSame(user, userDetailsMock, "Should be the same object reference");
    }

    @Test
    void shouldThrowException_WhenFindByInvalidUsername() {
        // Arrange
        when(userRepository.findByUsername(eq("User"))).thenReturn(Optional.empty());

        // Act
        // Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.findByUsername("User"),
                "Getting non-existing user should throw InvalidMethodArgumentsException.");
        assertEquals(exception.getMessage(), "User 'User' not found", "Should have the same exception message");
    }

    @Test
    void shouldReturnTrue_WhenExistsByUsername() {
        // Arrange
        User userMock = new User();
        userMock.setId(1L);
        userMock.setUsername("User");
        userMock.setEmail("test@email.com");

        when(userRepository.existsByUsername(eq("User"))).thenReturn(true);

        // Act
        Boolean isExist = userService.existsByUsername("User");

        // Assert
        assertEquals(isExist, true, "Should be True when user exists and extracted by username");
    }

    @Test
    void shouldReturnFalse_WhenDoesNotExistByUsername() {
        // Arrange
        when(userRepository.existsByUsername(eq("User"))).thenReturn(false);

        // Act
        Boolean isExist = userService.existsByUsername("User");

        // Assert
        assertEquals(isExist, false, "Should be False when user does not exist");
    }

    @Test
    void shouldReturnTrue_WhenExistsByEmail() {
        // Arrange
        User userMock = new User();
        userMock.setId(1L);
        userMock.setUsername("User");
        userMock.setEmail("test@email.com");

        when(userRepository.existsByEmail(eq("test@email.com"))).thenReturn(true);

        // Act
        Boolean isExist = userService.existsByEmail("test@email.com");

        // Assert
        assertEquals(isExist, true, "Should be True when user exists and extracted by e-mail");
    }

    @Test
    void shouldReturnFalse_WhenDoesNotExistByEmail() {
        // Arrange
        when(userRepository.existsByEmail(eq("test@email.com"))).thenReturn(false);

        // Act
        Boolean isExist = userService.existsByEmail("test@email.com");

        // Assert
        assertEquals(isExist, false, "Should be False when user does not exist");
    }

    @Test
    void shouldReturnUser_WhenLoadByUsername() {
        // Arrange
        UserDetails userDetailsMock = new User();
        when(userRepository.findByUsername(eq("User"))).thenReturn(Optional.of(userDetailsMock));

        // Act
        UserDetails user = userService.loadUserByUsername("User");

        // Assert
        assertSame(user, userDetailsMock, "Should be the same object reference");
    }

    @Test
    void shouldReturnUser_whenCreateNewUser() {
        // Arrange
        RegistrationUserDto registrationUserDto = new RegistrationUserDto();
        registrationUserDto.setUsername("User");
        registrationUserDto.setPassword("Password");
        registrationUserDto.setConfirmPassword("Password");
        registrationUserDto.setEmail("test@email.com");

        User userMock = new User();
        userMock.setId(1L);
        userMock.setUsername("User");
        userMock.setEmail("test@email.com");
        userMock.setPassword("Password");
        Role role = new Role();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        userMock.setRoles(new HashSet<>());

        when(passwordEncoder.encode(any())).thenReturn("EncodedPassword");
        when(roleService.findByName(any())).thenReturn(role);
        when(userRepository.save(any())).thenReturn(userMock);

        // Act
        User user = userService.createNewUser(registrationUserDto);

        // Assert
        assertSame(user, userMock, "Should be the same object reference");
        assertEquals(user.getId(), userMock.getId(), "Ids should match");
        assertEquals(user.getUsername(), userMock.getUsername(), "Usernames should match");
        assertEquals(user.getEmail(), userMock.getEmail(), "E-mails should match");
        assertEquals(user.getPassword(), userMock.getPassword(), "Passwords should match");
        assertSame(user.getRoles(), userMock.getRoles(), "Roles should have the same object reference");
    }
}