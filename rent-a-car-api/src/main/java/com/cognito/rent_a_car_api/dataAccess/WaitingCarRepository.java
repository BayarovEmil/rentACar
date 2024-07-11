package com.cognito.rent_a_car_api.dataAccess;

import com.cognito.rent_a_car_api.core.entity.User;
import com.cognito.rent_a_car_api.entity.WaitingCar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WaitingCarRepository extends JpaRepository<WaitingCar,Integer> {
    Page<WaitingCar> findAllByUser(User user, Pageable pageable);

    @Query("""
        select
        (count (*) > 0) as isAdded
        from WaitingCar car
        where car.car.id=:carId
        and car.user.id=:userId
        """)
    boolean isCarAdded(Integer carId,Integer userId);
}
