package com.cognito.rent_a_car_api.core.exception;

public class OperationNotPermittedException extends RuntimeException {
    public OperationNotPermittedException() {
    }

    public OperationNotPermittedException(String message) {
        super(message);
    }
}
