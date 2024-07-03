package com.cognito.rent_a_car_api.dataAccess;

import com.cognito.rent_a_car_api.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback,Integer> {
    @Query("""
            select feedback
            from Feedback feedback
            where feedback.car.id=:carId
            """)
    Page<Feedback> findAllFeedbackByCarId(Integer carId, Pageable pageable);
}
