package com.cognito.rent_a_car_api.core.exception;

public class ActivationTokenException extends RuntimeException {
    public ActivationTokenException() {
    }

    public ActivationTokenException(String message) {
        super(message);
    }
}
