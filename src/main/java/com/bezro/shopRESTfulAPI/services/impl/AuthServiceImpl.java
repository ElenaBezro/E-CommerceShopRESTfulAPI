package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.JwtResponse;
import com.bezro.shopRESTfulAPI.dtos.LoginUserDto;
import com.bezro.shopRESTfulAPI.dtos.RegistrationUserDto;
import com.bezro.shopRESTfulAPI.dtos.UserDto;
import com.bezro.shopRESTfulAPI.entities.User;
import com.bezro.shopRESTfulAPI.exceptions.UserAlreadyExistsException;
import com.bezro.shopRESTfulAPI.exceptions.UserWithEmailAlreadyExistsException;
import com.bezro.shopRESTfulAPI.jwtUtils.JwtTokenUtils;
import com.bezro.shopRESTfulAPI.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final JwtTokenUtils jwtTokenUtils;

    public JwtResponse login(LoginUserDto loginRequest) {
        UserDetails userDetails = userService.loadUserByUsername(loginRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return new JwtResponse(token);
    }

    //TODO: should I make this method transactional?
    public UserDto createNewUser(RegistrationUserDto registrationUserDto) {
        if (userService.existsByUsername(registrationUserDto.getUsername())) {
            throw new UserAlreadyExistsException(String.format("User with username %s already exists", registrationUserDto.getUsername()));
        }
        if (userService.existsByEmail(registrationUserDto.getEmail())) {
            throw new UserWithEmailAlreadyExistsException(String.format("User with e-mail %s already exists", registrationUserDto.getEmail()));
        }
        User user = userService.createNewUser(registrationUserDto);
        //TODO: return token in UserDto also?
        return new UserDto(user.getId(), user.getUsername(), user.getEmail());
    }
}
