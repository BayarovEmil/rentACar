package com.cognito.rent_a_car_api.core.dataAccess;

import com.cognito.rent_a_car_api.core.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User> findByEmail(String email);
}
