package com.bezro.shopRESTfulAPI.jwtUtils;

import com.bezro.shopRESTfulAPI.entities.Role;
import com.bezro.shopRESTfulAPI.entities.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenUtilsTest {
    private User user;

    private JwtTokenUtils jwtTokenUtils;

    @BeforeEach
    void setUp() {
        jwtTokenUtils = new JwtTokenUtils();
        user = new User();
        user.setUsername("username");
        Role role = new Role();
        role.setName("User");
        user.setRoles(Set.of(role));
    }

    @AfterEach
    void tearDown() {
        user = null;
        jwtTokenUtils = null;
    }

    @Test
    void shouldReturnStringWhenTokenGenerated() {
        // Arrange and act
        String token = jwtTokenUtils.generateToken(user);

        // Assert
        assertFalse(token.isEmpty(), "Generated token should not be empty");
    }

    @Test
    void shouldReturnUsernameWhenExtractedFromToken() {
        // Arrange
        String token = jwtTokenUtils.generateToken(user);

        // Act
        String extractedUsername = jwtTokenUtils.getUsername(token);

        // Assert
        assertEquals(user.getUsername(), extractedUsername, "Extracted username from the token should match username");
    }

    @Test
    void shouldReturnFutureExpirationDateWhenExtractedFromToken() {
        // Arrange
        String token = jwtTokenUtils.generateToken(user);

        // Act
        Date expirationDate = jwtTokenUtils.getExpiration(token);

        // Assert
        assertTrue(expirationDate.after(new Date()), "Expiration date should be in the future");
    }

    @Test
    void shouldReturnTrueIfTokenIsNotExpired() {
        // Arrange
        String token = jwtTokenUtils.generateToken(user);

        // Act
        Boolean isExpired = jwtTokenUtils.isTokenExpired(token);

        // Assert
        assertFalse(isExpired, "Token should not be expired");
    }

    @Test
    void tokenShouldContainUserRole() {
        // Arrange
        String token = jwtTokenUtils.generateToken(user);

        // Act
        String extractedRoles = jwtTokenUtils.getRoles(token).toString();
        System.out.println(extractedRoles);

        // Assert
        assertTrue(extractedRoles.contains("User"),
                "The 'User' role should be present in the extracted roles.");
    }

    @Test
    void tokenClaimsShouldNotBeEmpty() {
        // Arrange
        String token = jwtTokenUtils.generateToken(user);

        // Act
        Claims claims = jwtTokenUtils.getAllClaimsFromToken(token);

        // Assert
        assertTrue(claims.containsKey("roles"), "Claims should contain 'roles' key");
        assertTrue(claims.containsKey("sub"), "Claims should contain 'sub' key (SUBJECT)");
        assertTrue(claims.containsKey("iat"), "Claims should contain 'iat' key (ISSUED_AT)");
        assertTrue(claims.containsKey("exp"), "Claims should contain 'exp' key (EXPIRATION)");
    }
}
