package com.cognito.rent_a_car_api.api;

import com.cognito.rent_a_car_api.business.CarService;
import com.cognito.rent_a_car_api.core.common.PageResponse;
import com.cognito.rent_a_car_api.dto.CarRequest;
import com.cognito.rent_a_car_api.dto.CarResponse;
import com.cognito.rent_a_car_api.dto.RentedCarResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("car")
@Tag(name = "Car")
@RequiredArgsConstructor
public class CarController {
    private final CarService carService;

    @PostMapping
    public ResponseEntity<Integer> saveCar(
            @Valid @RequestBody CarRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(carService.save(request,connectedUser));
    }

    @GetMapping("/{car_id}")
    public ResponseEntity<CarResponse> findById(
            @RequestParam("car_id") Integer carId
    ) {
        return ResponseEntity.ok(carService.findById(carId));
    }

    @GetMapping("/findAll")
    public ResponseEntity<PageResponse<CarResponse>> findAllCars(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(carService.findAllCars(page,size,connectedUser));
    }

    @GetMapping("/owner")
    public ResponseEntity<PageResponse<CarResponse>> findAllByOwner(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(carService.findAllCarsByOwner(page,size,connectedUser));
    }

    @GetMapping("/rented")
    public ResponseEntity<PageResponse<RentedCarResponse>> findAllRentedCars(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(carService.findAllRentedCars(page,size,connectedUser));
    }

    @GetMapping("/returned")
    public ResponseEntity<PageResponse<RentedCarResponse>> findAllReturnedCars(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(carService.findAllReturnedCars(page,size,connectedUser));
    }

    @PatchMapping("/available/{car-id}")
    public ResponseEntity<Integer> updateAvailableStatus(
        @PathVariable("car-id") Integer carId,
        Authentication connectedUser
    ) {
        return ResponseEntity.ok(carService.updateAvailableStatus(carId,connectedUser));
    }

    @GetMapping("rent/{car-id}")
    public ResponseEntity<Integer> rentCar(
            @PathVariable("car-id") Integer carId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(carService.rentCar(carId,connectedUser));
    }

    @GetMapping("rent/return/{car-id}")
    public ResponseEntity<Integer> returnRentedCar(
            @PathVariable("car-id") Integer carId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(carService.returnRentedCar(carId,connectedUser));
    }

    @GetMapping("rent/return/approve/{car-id}")
    public ResponseEntity<Integer> approveReturnRentedCar(
            @PathVariable("car-id") Integer carId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(carService.approveReturnRentedCar(carId,connectedUser));
    }

    @PostMapping(value = "/cover/{car-id}",consumes = "multipart/form-data")
    public ResponseEntity<?> uploadCarCoverPicture(
            @PathVariable("car-id") Integer carId,
            @Parameter()
            @RequestPart("file") MultipartFile file,
            Authentication connectedUser
            ) {
        carService.uploadCarCoverPicture(file,connectedUser,carId);
        return ResponseEntity.accepted().build();
    }
}
