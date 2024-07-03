package com.cognito.rent_a_car_api.business;

import com.cognito.rent_a_car_api.core.common.PageResponse;
import com.cognito.rent_a_car_api.core.entity.User;
import com.cognito.rent_a_car_api.core.exception.OperationNotPermittedException;
import com.cognito.rent_a_car_api.core.file.FileStorageService;
import com.cognito.rent_a_car_api.dataAccess.CarRepository;
import com.cognito.rent_a_car_api.dataAccess.CarTransactionHistoryRepository;
import com.cognito.rent_a_car_api.dto.CarRequest;
import com.cognito.rent_a_car_api.dto.CarResponse;
import com.cognito.rent_a_car_api.dto.RentedCarResponse;
import com.cognito.rent_a_car_api.dto.converter.CarMapper;
import com.cognito.rent_a_car_api.entity.Car;
import com.cognito.rent_a_car_api.entity.CarTransactionHistory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

import static com.cognito.rent_a_car_api.dto.CarSpecification.withOwnerId;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarService {
    private final CarRepository carRepository;
    private final CarTransactionHistoryRepository historyRepository;
    private final CarMapper carMapper;
    private final FileStorageService fileStorageService;
    public Integer save(CarRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Car car =  carMapper.toCar(request);
        car.setOwner(user);
        return carRepository.save(car).getId();
    }

    public CarResponse findById(Integer carId) {
        return carRepository.findById(carId)
                .map(carMapper::toCarResponse)
                .orElseThrow(()->new EntityNotFoundException("Car not found by id"));
    }

    public PageResponse<CarResponse> findAllCars(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<Car> cars = carRepository.findAllDisplayableCars(pageable,user.getId());
        List<CarResponse> carResponses = cars.stream()
                .map(carMapper::toCarResponse)
                .toList();
        return new PageResponse<>(
                carResponses,
                cars.getNumber(),
                cars.getSize(),
                cars.getTotalElements(),
                cars.getTotalPages(),
                cars.isFirst(),
                cars.isLast()
        );
    }


    public PageResponse<CarResponse> findAllCarsByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdDate").descending());
        Page<Car> cars = carRepository.findAll(withOwnerId(user.getId()),pageable);
        List<CarResponse> carResponses = cars.stream()
                .map(carMapper::toCarResponse)
                .toList();
        return new PageResponse<>(
                carResponses,
                cars.getNumber(),
                cars.getSize(),
                cars.getTotalElements(),
                cars.getTotalPages(),
                cars.isFirst(),
                cars.isLast()
        );
    }

    public PageResponse<RentedCarResponse> findAllRentedCars(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page,size,Sort.by("createdDate").descending());
        Page<CarTransactionHistory> histories = historyRepository.findAllRentedCars(pageable,user.getId());
        List<RentedCarResponse> carResponses = histories.stream()
                .map(carMapper::toRentedCarResponse)
                .toList();
        return new PageResponse<>(
                carResponses,
                histories.getNumber(),
                histories.getSize(),
                histories.getTotalElements(),
                histories.getTotalPages(),
                histories.isFirst(),
                histories.isLast()
        );
    }

    public PageResponse<RentedCarResponse> findAllReturnedCars(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page,size,Sort.by("createdDate").descending());
        Page<CarTransactionHistory> carTransactionHistories = historyRepository.findAllReturnedCars(pageable,user.getId());
        List<RentedCarResponse> carResponses = carTransactionHistories.stream()
                .map(carMapper::toRentedCarResponse)
                .toList();
        return new PageResponse<>(
                carResponses,
                carTransactionHistories.getNumber(),
                carTransactionHistories.getSize(),
                carTransactionHistories.getTotalElements(),
                carTransactionHistories.getTotalPages(),
                carTransactionHistories.isFirst(),
                carTransactionHistories.isLast()
        );
    }

    public Integer updateAvailableStatus(Integer carId, Authentication connectedUser) {
        Car car = carRepository.findById(carId)
                .orElseThrow(()-> new EntityNotFoundException("Car not found by id"));
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(user.getId(),car.getOwner().getId())) {
            throw new OperationNotPermittedException("You can not update other's car information");
        }
        car.setAvailable(!car.isAvailable());
        return carRepository.save(car).getId();
    }

    public Integer rentCar(Integer carId, Authentication connectedUser) {
        Car car = carRepository.findById(carId)
                .orElseThrow(()-> new EntityNotFoundException("Car not found by id"));
        if (!car.isAvailable()) {
            throw new OperationNotPermittedException("Car is not available");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(user.getId(),car.getOwner().getId())) {
            throw new OperationNotPermittedException("You can not rent your car");
        }
        final boolean isCarRentedByUser = historyRepository.isCarRentedByUser(carId,user.getId());
        if (isCarRentedByUser) {
            throw new OperationNotPermittedException("Car already rented by this user");
        }
        final boolean isCarRentedByOtherUser = historyRepository.isCarRentedByOtherUser(carId);
        if (isCarRentedByOtherUser) {
            throw new OperationNotPermittedException("Car already rented by an other user");
        }

        CarTransactionHistory carTransactionHistory = CarTransactionHistory.builder()
                .user(user)
                .car(car)
                .returned(false)
                .returnApproved(false)
                .build();
        return historyRepository.save(carTransactionHistory).getId();
    }

    public Integer returnRentedCar(Integer carId, Authentication connectedUser) {
        Car car = carRepository.findById(carId)
                .orElseThrow(()-> new EntityNotFoundException("Car not found by id"));
        if (!car.isAvailable()) {
            throw new OperationNotPermittedException("Car is not available");
        }
        User user = (User) connectedUser.getPrincipal();
        if (Objects.equals(user.getId(),car.getOwner().getId())) {
            throw new OperationNotPermittedException("You can not rent your car");
        }

        CarTransactionHistory carTransactionHistory = historyRepository.findCarIdAndUserId(user.getId(),carId)
                .orElseThrow(()-> new OperationNotPermittedException("you can not rent this car"));
        carTransactionHistory.setReturned(true);
        return historyRepository.save(carTransactionHistory).getId();
    }

    public Integer approveReturnRentedCar(Integer carId, Authentication connectedUser) {
        Car car = carRepository.findById(carId)
                .orElseThrow(()-> new EntityNotFoundException("Car not found by id"));
        if (!car.isAvailable()) {
            throw new OperationNotPermittedException("Car is not available");
        }
        User user = (User) connectedUser.getPrincipal();
        if (!Objects.equals(user.getId(),car.getOwner().getId())) {
            throw new OperationNotPermittedException("You cannot approve the return of a car you do not own");
        }

        CarTransactionHistory carTransactionHistory = historyRepository.findCarIdAndOwnerId(user.getId(),carId)
                .orElseThrow(()-> new OperationNotPermittedException("The car is not returned yet. You cannot approve its return"));
        carTransactionHistory.setReturnApproved(true);
        return historyRepository.save(carTransactionHistory).getId();
    }

    public void uploadCarCoverPicture(MultipartFile file, Authentication connectedUser, Integer carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(()->new EntityNotFoundException("No car found by id"));
        User user = (User) connectedUser.getPrincipal();
        var profilePicture  = fileStorageService.saveFile(file,carId, user.getId());
        car.setCarCover(profilePicture);
        carRepository.save(car);
    }
}
