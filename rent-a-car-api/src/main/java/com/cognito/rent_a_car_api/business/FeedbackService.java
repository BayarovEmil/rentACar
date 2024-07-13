package com.cognito.rent_a_car_api.business;

import com.cognito.rent_a_car_api.core.common.PageResponse;
import com.cognito.rent_a_car_api.core.entity.User;
import com.cognito.rent_a_car_api.core.exception.OperationNotPermittedException;
import com.cognito.rent_a_car_api.dataAccess.CarRepository;
import com.cognito.rent_a_car_api.dataAccess.CarTransactionHistoryRepository;
import com.cognito.rent_a_car_api.dataAccess.FeedbackRepository;
import com.cognito.rent_a_car_api.dto.FeedbackRequest;
import com.cognito.rent_a_car_api.dto.FeedbackResponse;
import com.cognito.rent_a_car_api.dto.converter.FeedbackMapper;
import com.cognito.rent_a_car_api.entity.Car;
import com.cognito.rent_a_car_api.entity.Feedback;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final CarRepository carRepository;
    private final CarTransactionHistoryRepository carTransactionHistoryRepository;
    private final FeedbackMapper feedbackMapper;
    public Integer save(FeedbackRequest request, Authentication connectedUser) {
        Car car = carRepository.findById(request.carId())
                .orElseThrow(()-> new EntityNotFoundException("Car not found by id"));
        if (!car.isAvailable()) {
            throw new OperationNotPermittedException("You can not give a feedback");
        }
        User user = (User) connectedUser.getPrincipal();

//        if (Objects.equals(user.getId(),request.carId())) {
//            throw new OperationNotPermittedException("You can not give a feedback your car");
//        }
        boolean isCarReturnedByUser = carTransactionHistoryRepository.isCarReturnedByUser(car.getId(),user.getId());
        if (!isCarReturnedByUser) {
            throw new OperationNotPermittedException("You can't give a feedback for this car.This car not rented by your.");
        }
        Feedback feedback = feedbackMapper.toFeedback(request,user);
        return feedbackRepository.save(feedback).getId();
    }

    public PageResponse<FeedbackResponse> findAllFeedbacks(Integer carId, int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<Feedback> feedbacks = feedbackRepository.findAllFeedbackByCarId(carId,pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f-> feedbackMapper.toFeedbackResponse(f,user.getId()))
                .toList();
        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
