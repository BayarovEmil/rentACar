package com.cognito.rent_a_car_api.entity;

import com.cognito.rent_a_car_api.core.common.BaseEntity;
import com.cognito.rent_a_car_api.core.entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class CarTransactionHistory extends BaseEntity {
    private boolean returned;
    private boolean returnApproved;

    @ManyToOne
    @JoinColumn(name = "carId")
    private Car car;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
