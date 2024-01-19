package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.dtos.JwtResponse;
import com.bezro.shopRESTfulAPI.dtos.LoginUserDto;
import com.bezro.shopRESTfulAPI.dtos.RegistrationUserDto;
import com.bezro.shopRESTfulAPI.dtos.UserDto;

public interface AuthService {
    JwtResponse login(LoginUserDto loginRequest);

    UserDto createNewUser(RegistrationUserDto registrationUserDto);
}
