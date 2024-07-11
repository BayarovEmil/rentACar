package com.cognito.rent_a_car_api.business;

import com.cognito.rent_a_car_api.core.common.PageResponse;
import com.cognito.rent_a_car_api.core.entity.User;
import com.cognito.rent_a_car_api.core.exception.OperationNotPermittedException;
import com.cognito.rent_a_car_api.dataAccess.CarRepository;
import com.cognito.rent_a_car_api.dataAccess.CarTransactionHistoryRepository;
import com.cognito.rent_a_car_api.dataAccess.WaitingCarRepository;
import com.cognito.rent_a_car_api.dto.CarResponse;
import com.cognito.rent_a_car_api.dto.converter.CarMapper;
import com.cognito.rent_a_car_api.entity.Car;
import com.cognito.rent_a_car_api.entity.WaitingCar;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitingCarService {
    private final CarRepository carRepository;
    private final CarTransactionHistoryRepository historyRepository;
    private final WaitingCarRepository waitingCarRepository;
    private final CarMapper carMapper;
    public PageResponse<CarResponse> findAllWaitingCars(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<WaitingCar> waitingCars = waitingCarRepository.findAllByUser(user,pageable);
        List<CarResponse> carResponses = waitingCars.stream()
                .map(carMapper::toCarResponse)
                .toList();
        return new PageResponse<>(
                carResponses,
                waitingCars.getNumber(),
                waitingCars.getSize(),
                waitingCars.getTotalElements(),
                waitingCars.getTotalPages(),
                waitingCars.isFirst(),
                waitingCars.isLast()
        );
    }

    public Integer findCarId(Integer id) {
        WaitingCar waitingCar = waitingCarRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Waiting car not found by id"));
        return waitingCar.getCar().getId();
    }

    public Integer addToWaitingList(Integer carId, Authentication connectedUser) {
        Car car = carRepository.findById(carId)
                .orElseThrow(()->new EntityNotFoundException("Car not found by id:: "));
        User user = (User) connectedUser.getPrincipal();
        if (waitingCarRepository.isCarAdded(carId,user.getId())) {
            throw new OperationNotPermittedException("Car already added your waiting list!");
        }
        WaitingCar waitingCar = WaitingCar.builder()
                .car(car)
                .user(user)
                .build();
        return waitingCarRepository.save(waitingCar).getId();
    }

    public Integer removeFromWaitingList(Integer waitingId) {
        WaitingCar waitingCar = waitingCarRepository.findById(waitingId)
                .orElseThrow(()->new EntityNotFoundException("Waiting car not found by id"));
        waitingCarRepository.deleteById(waitingCar.getId());
        return waitingCar.getId();
    }
}
