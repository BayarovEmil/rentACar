package com.cognito.rent_a_car_api.core.dto;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String token
) {
}
