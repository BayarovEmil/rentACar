package com.cognito.rent_a_car_api.dto.converter;

import com.cognito.rent_a_car_api.core.file.FileUtils;
import com.cognito.rent_a_car_api.dto.CarRequest;
import com.cognito.rent_a_car_api.dto.CarResponse;
import com.cognito.rent_a_car_api.dto.RentedCarResponse;
import com.cognito.rent_a_car_api.entity.Car;
import com.cognito.rent_a_car_api.entity.CarTransactionHistory;
import com.cognito.rent_a_car_api.entity.WaitingCar;
import org.springframework.stereotype.Service;

@Service
public class CarMapper {
    public Car toCar(CarRequest request) {
        return Car.builder()
                .brand(request.brand())
                .model(request.model())
                .manufacturingYear(request.manufacturingYear())
                .dailyRentalRate(request.dailyRentalRate())
                .fuelType(request.fuelType())
                .capacity(request.capacity())
                .available(request.available())
                .build();
    }

    public CarResponse toCarResponse(Car request) {
        return CarResponse.builder()
                .id(request.getId())
                .owner(request.getOwner().getName())
                .brand(request.getBrand())
                .model(request.getModel())
                .manufacturingYear(request.getManufacturingYear())
                .fuelType(request.getFuelType())
                .capacity(request.getCapacity())
                .available(request.isAvailable())
                .dailyRentalRate(request.getDailyRentalRate())
                .cover(FileUtils.readFileFromLocation(request.getCarCover()))
                .rate(request.getRate())
                .build();
    }

    public RentedCarResponse toRentedCarResponse(CarTransactionHistory request) {
        return RentedCarResponse.builder()
                .id(request.getCar().getId())
                .brand(request.getCar().getBrand())
                .model(request.getCar().getModel())
                .manufacturingYear(request.getCar().getManufacturingYear())
                .fuelType(request.getCar().getFuelType())
                .dailyRentalRate(request.getCar().getDailyRentalRate())
                .rate(request.getCar().getRate())
                .returned(request.isReturned())
                .returnApproved(request.isReturnApproved())
                .build();
    }

    public CarResponse toCarResponse(WaitingCar request) {
        return CarResponse.builder()
                .id(request.getId())
                .owner(request.getCar().getOwner().getName())
                .brand(request.getCar().getBrand())
                .model(request.getCar().getModel())
                .manufacturingYear(request.getCar().getManufacturingYear())
                .fuelType(request.getCar().getFuelType())
                .capacity(request.getCar().getCapacity())
                .available(request.getCar().isAvailable())
                .dailyRentalRate(request.getCar().getDailyRentalRate())
                .cover(FileUtils.readFileFromLocation(request.getCar().getCarCover()))
                .rate(request.getCar().getRate())
                .build();
    }
}
