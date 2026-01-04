package com.examCrux.webApp.repository;

import com.examCrux.webApp.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    // Find a role by its name (case-insensitive)
    Optional<Role> findByRoleNameIgnoreCase(String name);
}