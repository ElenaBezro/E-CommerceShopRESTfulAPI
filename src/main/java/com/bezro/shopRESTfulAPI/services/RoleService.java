package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.repositories.RoleRepository;
import com.bezro.shopRESTfulAPI.entities.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    //TODO: or use Optional?
    public Role findByName(String userRole) {
        return roleRepository.findByName(userRole).get();
    }
}
