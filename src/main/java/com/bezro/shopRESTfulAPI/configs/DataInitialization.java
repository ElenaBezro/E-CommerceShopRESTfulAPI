package com.bezro.shopRESTfulAPI.configs;

import com.bezro.shopRESTfulAPI.dtos.RegistrationUserDto;
import com.bezro.shopRESTfulAPI.entities.Role;
import com.bezro.shopRESTfulAPI.repositories.RoleRepository;
import com.bezro.shopRESTfulAPI.repositories.UserRepository;
import com.bezro.shopRESTfulAPI.services.impl.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitialization {
    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${admin.email}")
    private String adminEmail;

    @Bean
    public CommandLineRunner initializeData(RoleRepository roleRepository, UserRepository userRepository, UserService userService) {
        return args -> {
            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                Role userRole = new Role();
                userRole.setName("ROLE_USER");
                roleRepository.save(userRole);
            }
            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                Role adminRole = new Role();
                adminRole.setName("ROLE_ADMIN");
                roleRepository.save(adminRole);
            }
            if (!userRepository.existsByUsername(adminUsername)) {
                RegistrationUserDto userDto = new RegistrationUserDto();
                userDto.setUsername(adminUsername);
                userDto.setEmail(adminEmail);
                userDto.setPassword(adminPassword);
                userDto.setConfirmPassword(adminPassword);
                userService.createNewAdmin(userDto);
            }
        };
    }
}
