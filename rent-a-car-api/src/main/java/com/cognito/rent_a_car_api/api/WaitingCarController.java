package com.cognito.rent_a_car_api.api;

import com.cognito.rent_a_car_api.business.WaitingCarService;
import com.cognito.rent_a_car_api.core.common.PageResponse;
import com.cognito.rent_a_car_api.dataAccess.CarTransactionHistoryRepository;
import com.cognito.rent_a_car_api.dataAccess.WaitingCarRepository;
import com.cognito.rent_a_car_api.dto.CarResponse;
import com.cognito.rent_a_car_api.dto.converter.CarMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("waiting")
@Tag(name = "Waiting Car")
@RequiredArgsConstructor
public class WaitingCarController {
    private final WaitingCarService waitingCarService;
    @GetMapping("/findAllWaitingCars")
    public ResponseEntity<PageResponse<CarResponse>> findAllWaitingCars(
            @RequestParam(name = "page",defaultValue = "0",required = false) int page,
            @RequestParam(name = "size",defaultValue = "10",required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(waitingCarService.findAllWaitingCars(page,size,connectedUser));
    }

    @GetMapping("/findCarId/{id}")
    public ResponseEntity<Integer> findCarId(
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(waitingCarService.findCarId(id));
    }
    @PostMapping("/addToWaitingList/{carId}")
    public ResponseEntity<Integer> addToWaitingList(
            @PathVariable Integer carId,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(waitingCarService.addToWaitingList(carId,connectedUser));
    }

    @DeleteMapping("/removeFromWaitingList/{waitingId}")
    public ResponseEntity<Integer> removeFromWaitingList(
            @PathVariable Integer waitingId
    ) {
        return ResponseEntity.ok(waitingCarService.removeFromWaitingList(waitingId));
    }
}
