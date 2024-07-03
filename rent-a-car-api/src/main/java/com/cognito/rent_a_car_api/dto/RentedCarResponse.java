package com.cognito.rent_a_car_api.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RentedCarResponse {
    private String brand;
    private String model;
    private int manufacturingYear;
    private String fuelType;
    private double dailyRentalRate;
    private double rate;
    private boolean returned;
    private boolean returnApproved;
}
