package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.Repositories.RoleRepository;
import com.bezro.shopRESTfulAPI.entities.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService {
    private RoleRepository roleRepository;

    public Optional<Role> findByName(String userRole) {
        return roleRepository.findByName(userRole);
    }
}
