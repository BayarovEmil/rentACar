package com.cognito.rent_a_car_api.dto;

import lombok.Builder;

@Builder
public record CarRequest(
        String brand,
        String model,
        int manufacturingYear,
        String fuelType,
        double dailyRentalRate,
        int capacity,
        boolean available
) {
}
