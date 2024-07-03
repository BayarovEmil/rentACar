package com.cognito.rent_a_car_api.dataAccess;

import com.cognito.rent_a_car_api.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface CarRepository extends JpaRepository<Car,Integer> , JpaSpecificationExecutor<Car> {
    @Query("""
            SELECT car
            FROM Car car
            WHERE car.available=true
            AND car.owner.id != :userId
            """)
    Page<Car> findAllDisplayableCars(Pageable pageable, Integer userId);

}
