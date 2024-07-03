package com.cognito.rent_a_car_api.entity;

import com.cognito.rent_a_car_api.core.common.BaseEntity;
import com.cognito.rent_a_car_api.core.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class Car extends BaseEntity {
    private String brand;
    private String model;
    private int manufacturingYear;
    private String fuelType;
    private double dailyRentalRate;
    private int capacity;
    private boolean available;
    private String carCover;

    @OneToMany(mappedBy = "car")
    private List<Feedback> feedbacks;
    @ManyToOne
    @JoinColumn(name = "ownerId",nullable = false)
    private User owner;
    @OneToMany(mappedBy = "car")
    private List<CarTransactionHistory> histories;

    @Transient
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        double roundedRate = Math.round(rate * 10.0) / 10.0;

        // Return 4.0 if roundedRate is less than 4.5, otherwise return 4.5
        return roundedRate;
    }
}
