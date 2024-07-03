package com.cognito.rent_a_car_api.core.dataAccess;


import com.cognito.rent_a_car_api.core.entity.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken,Integer> {
    Optional<ActivationToken> findByToken(String token);
}
