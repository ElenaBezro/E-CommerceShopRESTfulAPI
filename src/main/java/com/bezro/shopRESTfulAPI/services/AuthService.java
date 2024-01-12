package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.LoginUserDto;
import com.bezro.shopRESTfulAPI.dtos.JwtResponse;
import com.bezro.shopRESTfulAPI.dtos.RegistrationUserDto;
import com.bezro.shopRESTfulAPI.dtos.UserDto;
import com.bezro.shopRESTfulAPI.entities.User;
import com.bezro.shopRESTfulAPI.exceptions.AuthException;
import com.bezro.shopRESTfulAPI.jwtUtils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> login(@RequestBody LoginUserDto loginRequest) {
        //TODO: use Global exception handler

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AuthException(HttpStatus.UNAUTHORIZED.value(),
                    "Login or password is incorrect"),
                    HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    //TODO: should I make this method transactional?
    public ResponseEntity<?> createNewUser(@RequestBody RegistrationUserDto registrationUserDto) {
        //TODO: use Global exception handler
        if (!registrationUserDto.getPassword().equals(registrationUserDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AuthException(HttpStatus.BAD_REQUEST.value(),
                    "Passwords do not match"),
                    HttpStatus.BAD_REQUEST);
        }
        if (userService.findByUsername(registrationUserDto.getUsername()).isPresent()) {
            return new ResponseEntity<>(new AuthException(HttpStatus.BAD_REQUEST.value(),
                    String.format("User with username %s already exists", registrationUserDto.getUsername())),
                    HttpStatus.BAD_REQUEST);
        }
        User user = userService.createNewUser(registrationUserDto);
        //TODO: return token in UserDto also?
        return ResponseEntity.ok(new UserDto(user.getId(), user.getUsername(), user.getEmail()));
    }
}