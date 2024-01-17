package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.entities.Role;
import com.bezro.shopRESTfulAPI.exceptions.RoleNotFoundException;
import com.bezro.shopRESTfulAPI.repositories.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {
    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void shouldReturnRole_WhenFindByName() {
        // Arrange
        Role roleMock = new Role();
        roleMock.setName("ROLE_TEST");

        when(roleRepository.findByName(eq("ROLE_TEST"))).thenReturn(Optional.of(roleMock));

        // Act
        Role role = roleService.findByName("ROLE_TEST");

        // Assert
        assertSame(role, roleMock, "Should be the same object reference");
        assertEquals(role.getName(), "ROLE_TEST", "Should have the same role name");
    }

    @Test
    void shouldThrowRoleNotFound_WhenRoleDoesNotExist() {
        // Arrange
        when(roleRepository.findByName(eq("ROLE_TEST"))).thenReturn(Optional.empty());

        // Act
        // Assert
        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class,
                () -> roleService.findByName("ROLE_TEST"),
                "Getting non-existing role should throw RoleNotFoundException.");
        assertEquals(exception.getMessage(), "Invalid role:ROLE_TEST", "Should have the same exception message");
    }
}