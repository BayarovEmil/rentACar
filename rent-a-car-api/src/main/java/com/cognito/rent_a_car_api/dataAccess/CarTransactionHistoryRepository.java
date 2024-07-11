package com.cognito.rent_a_car_api.dataAccess;

import com.cognito.rent_a_car_api.entity.CarTransactionHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CarTransactionHistoryRepository extends JpaRepository<CarTransactionHistory,Integer> {
    @Query("""
            select history
            from CarTransactionHistory history
            where history.user.id=:userId
            and history.returned=false
            and history.returnApproved=false
            """)
    Page<CarTransactionHistory> findAllRentedCars(Pageable pageable, Integer userId);

    @Query("""
            select history
            from CarTransactionHistory history
            where history.user.id=:userId
            and history.returned=true
            and history.returnApproved=false
            """)
    Page<CarTransactionHistory> findAllReturnedCars(Pageable pageable, Integer userId);

    @Query("""
            select history
            from CarTransactionHistory history
            where history.car.owner.id=:userId
            and history.returned=true
            and history.returnApproved=false
            """)
    Page<CarTransactionHistory> findAllMyReturnedCars(Pageable pageable, Integer userId);

    @Query("""
            SELECT
            (COUNT (*) > 0) AS isRented
            from CarTransactionHistory history
            where history.user.id=:userId
            and history.car.id=:carId
            and history.returnApproved=false
            """)
    boolean isCarRentedByUser(Integer carId, Integer userId);

    @Query("""
            select
            (count (*) > 0) AS isBorrowed
            from CarTransactionHistory history
            where history.car.id=:carId
            and history.returnApproved=false
            """)
    boolean isCarRentedByOtherUser(Integer carId);

    @Query("""
            select
            (count (*) > 0) AS isBorrowed
            from CarTransactionHistory history
            where history.car.id=:carId
            and history.user.id=:userId
            and history.returned=true
            """)
    boolean isCarReturnedByUser(Integer carId, Integer userId);

    @Query("""
            select history
            from CarTransactionHistory history
            where history.user.id=:userId
            and history.car.id=:carId
            and history.returned=false
            and history.returnApproved=false
            """)
    Optional<CarTransactionHistory> findCarIdAndUserId(Integer userId, Integer carId);

    @Query("""
            select history
            from CarTransactionHistory history
            where history.car.owner.id=:userId
            and history.car.id=:carId
            and history.returned=true
            and history.returnApproved=false
            """)
    Optional<CarTransactionHistory> findCarIdAndOwnerId(Integer userId, Integer carId);

}
