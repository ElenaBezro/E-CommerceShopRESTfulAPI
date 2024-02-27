package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.constants.ResponseMessages;
import com.bezro.shopRESTfulAPI.dtos.JwtResponse;
import com.bezro.shopRESTfulAPI.dtos.LoginUserDto;
import com.bezro.shopRESTfulAPI.dtos.RegistrationUserDto;
import com.bezro.shopRESTfulAPI.dtos.UserDto;
import com.bezro.shopRESTfulAPI.exceptions.ApiException;
import com.bezro.shopRESTfulAPI.exceptions.ExceptionResponse;
import com.bezro.shopRESTfulAPI.exceptions.InvalidLoginCredentialsException;
import com.bezro.shopRESTfulAPI.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Tag(description = "Endpoints for registration and login", name = "Auth")
public class AuthController {
    private final AuthService authService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Login", tags = {"Auth"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
            @ApiResponse(responseCode = "400", description = "Login or password is incorrect.",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "The username and the password are required.",
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public JwtResponse login(@Valid @RequestBody LoginUserDto loginRequest) {
        log.info("Received login request: {}", loginRequest);

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            log.error("Invalid login credentials: {}", e.getMessage());
            throw new InvalidLoginCredentialsException("Login or password is invalid");
        }

        JwtResponse jwtResponse = authService.login(loginRequest);
        log.info("User logged in successfully: {}", jwtResponse);
        return jwtResponse;
    }

    @PostMapping("/register")
    @Operation(summary = "Registration", description = "Registration", tags = {"Auth"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "User with username [username] already exists.",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = "User with e-mail [e-mail] already exists.",
                    content = @Content(schema = @Schema(implementation = ApiException.class))),
            @ApiResponse(responseCode = "400", description = ResponseMessages.REGISTER_BAD_REQUEST_MESSAGE,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class))),
            @ApiResponse(responseCode = "400", description = ResponseMessages.REGISTER_EMPTY_REQUEST_MESSAGE,
                    content = @Content(schema = @Schema(implementation = ExceptionResponse.class)))
    })
    public UserDto registerUser(@Valid @RequestBody RegistrationUserDto registrationRequest) {
        log.info("Received registration request: {}", registrationRequest);

        UserDto userDto = authService.createNewUser(registrationRequest);
        log.info("User registered successfully: {}", userDto);
        return userDto;
    }
}
