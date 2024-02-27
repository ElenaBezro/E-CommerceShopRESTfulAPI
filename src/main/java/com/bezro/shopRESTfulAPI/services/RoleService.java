package com.bezro.shopRESTfulAPI.services;

import com.bezro.shopRESTfulAPI.entities.Role;


public interface RoleService {
    Role findByName(String userRole);
}