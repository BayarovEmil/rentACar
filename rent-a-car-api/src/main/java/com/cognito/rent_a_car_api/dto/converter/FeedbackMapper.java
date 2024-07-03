package com.cognito.rent_a_car_api.dto.converter;

import com.cognito.rent_a_car_api.dto.FeedbackRequest;
import com.cognito.rent_a_car_api.dto.FeedbackResponse;
import com.cognito.rent_a_car_api.entity.Car;
import com.cognito.rent_a_car_api.entity.Feedback;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class FeedbackMapper {
    public Feedback toFeedback(FeedbackRequest request) {
        return Feedback.builder()
                .note(request.note())
                .comment(request.comment())
                .car(Car.builder()
                        .id(request.carId())
                        .available(false)
                        .build())
                .build();
    }

    public FeedbackResponse toFeedbackResponse(Feedback request, Integer userId) {
        return FeedbackResponse.builder()
                .note(request.getNote())
                .comment(request.getComment())
                .ownFeedback(Objects.equals(request.getId(),userId))
                .build();
    }
}
