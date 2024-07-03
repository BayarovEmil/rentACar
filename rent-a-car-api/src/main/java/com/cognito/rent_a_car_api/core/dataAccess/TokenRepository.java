package com.cognito.rent_a_car_api.core.dataAccess;

import com.cognito.rent_a_car_api.core.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token,Integer> {
    Optional<Token> findByToken(String token);
}
