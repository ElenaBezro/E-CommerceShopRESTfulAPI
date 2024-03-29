package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.dtos.RegistrationUserDto;
import com.bezro.shopRESTfulAPI.entities.User;
import com.bezro.shopRESTfulAPI.entities.UserRole;
import com.bezro.shopRESTfulAPI.exceptions.InvalidMethodArgumentsException;
import com.bezro.shopRESTfulAPI.repositories.UserRepository;
import com.bezro.shopRESTfulAPI.services.RoleService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public User findById(Long id) {
        log.info("Finding user by id: {}", id);
        return userRepository.findById(id).orElseThrow(() -> new InvalidMethodArgumentsException(
                String.format("User with id: %d does not exist", id)));
    }

    public UserDetails findByUsername(String username) {
        log.info("Finding user by username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found", username)));

    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) {
        return findByUsername(username);
    }

    public User createNewUser(RegistrationUserDto registrationUserDto) {
        log.info("Creating new user: {}", registrationUserDto.getUsername());
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRoles(Set.of(roleService.findByName(UserRole.USER.getName())));
        return userRepository.save(user);
    }

    public void createNewAdmin(RegistrationUserDto registrationUserDto) {
        log.info("Creating new admin: {}", registrationUserDto.getUsername());
        User user = new User();
        user.setUsername(registrationUserDto.getUsername());
        user.setEmail(registrationUserDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationUserDto.getPassword()));
        user.setRoles(Set.of(roleService.findByName(UserRole.ADMIN.getName())));
        userRepository.save(user);
    }

    public boolean isUserMatchPrincipal(Long id, String username) {
        return Objects.equals(username, findById(id).getUsername());
    }
}
