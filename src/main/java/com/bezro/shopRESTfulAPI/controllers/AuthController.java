package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.constants.ResponseMessages;
import com.bezro.shopRESTfulAPI.dtos.JwtResponse;
import com.bezro.shopRESTfulAPI.dtos.LoginUserDto;
import com.bezro.shopRESTfulAPI.dtos.RegistrationUserDto;
import com.bezro.shopRESTfulAPI.dtos.UserDto;
import com.bezro.shopRESTfulAPI.exceptions.ApiException;
import com.bezro.shopRESTfulAPI.exceptions.ApiRequestException;
import com.bezro.shopRESTfulAPI.exceptions.InvalidLoginCredentialsException;
import com.bezro.shopRESTfulAPI.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

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
            @ApiResponse(responseCode = "401", description = "Login or password is incorrect.",
                    content = @Content(schema = @Schema(implementation = ApiException.class)))
            //TODO: add @ApiResponse invalid request data responses, user not found?
    })
    public JwtResponse login(@Valid @RequestBody LoginUserDto loginRequest) {

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            throw new InvalidLoginCredentialsException("Login or password is invalid");
        }
        return authService.login(loginRequest);
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
                    content = @Content(schema = @Schema(implementation = ApiRequestException.class))),
    })
    public UserDto registerUser(@Valid @RequestBody RegistrationUserDto registrationRequest) {
        return authService.createNewUser(registrationRequest);
    }

    //TODO: delete next methods. Only for test purposes
    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "Unsecured data";
    }

    @Parameter(in = ParameterIn.HEADER,
            description = "Authorization token",
            name = "JWT",
            content = @Content(schema = @Schema(type = "string")))
    @GetMapping("/secured")
    public String securedData() {
        return "Secured data";
    }

    @GetMapping("/admin")
    public String adminData() {
        return "Admin data";
    }

    @GetMapping("/info")
    public String userData(Principal principal) {
        return principal.getName();
    }
}
