package com.cognito.rent_a_car_api.core.dataAccess;

import com.cognito.rent_a_car_api.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByRole(String role);
}
