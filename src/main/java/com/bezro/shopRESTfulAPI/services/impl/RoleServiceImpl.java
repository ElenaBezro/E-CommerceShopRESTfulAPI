package com.bezro.shopRESTfulAPI.services.impl;

import com.bezro.shopRESTfulAPI.entities.Role;
import com.bezro.shopRESTfulAPI.exceptions.RoleNotFoundException;
import com.bezro.shopRESTfulAPI.repositories.RoleRepository;
import com.bezro.shopRESTfulAPI.services.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;

    public Role findByName(String userRole) {
        return roleRepository.findByName(userRole).
                orElseThrow(() -> new RoleNotFoundException("Invalid role:" + userRole));
    }
}