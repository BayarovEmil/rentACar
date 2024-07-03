package com.cognito.rent_a_car_api.dto;

import com.cognito.rent_a_car_api.entity.Car;
import org.springframework.data.jpa.domain.Specification;

public class CarSpecification {
    public static Specification<Car> withOwnerId(Integer ownerId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), ownerId);
    }
}
