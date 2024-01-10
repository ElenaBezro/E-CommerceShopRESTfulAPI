package com.bezro.shopRESTfulAPI.Repositories;

import com.bezro.shopRESTfulAPI.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
