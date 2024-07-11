package com.cognito.rent_a_car_api.dto;

import lombok.Builder;

@Builder
public record FeedbackResponse(
        Double note,
        String comment,
        boolean ownFeedback,
        String owner
) {
}
