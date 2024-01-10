package com.bezro.shopRESTfulAPI.controllers;

import com.bezro.shopRESTfulAPI.dtos.JwtRequest;
import com.bezro.shopRESTfulAPI.dtos.JwtResponse;
import com.bezro.shopRESTfulAPI.exceptions.AuthException;
import com.bezro.shopRESTfulAPI.jwtUtils.JwtTokenUtils;
import com.bezro.shopRESTfulAPI.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/auth")
    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest authRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AuthException(HttpStatus.UNAUTHORIZED.value(), "Login or password is incorrect"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(authRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "Unsecured data";
    }
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
