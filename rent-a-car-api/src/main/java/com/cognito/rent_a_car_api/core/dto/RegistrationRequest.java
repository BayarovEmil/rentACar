package com.cognito.rent_a_car_api.core.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistrationRequest(
        @NotEmpty(message = "Firstname is mandatory")
        @NotNull(message = "Firstname is mandatory")
        String firstname,
        @NotEmpty(message = "Lastname is mandatory")
        @NotNull(message = "Lastname is mandatory")
        String lastname,
        @NotEmpty(message = "Email is mandatory")
        @NotNull(message = "Email is mandatory")
        @Email(message = "Email format is wrong")
        String email,
        @NotEmpty(message = "Password is mandatory")
        @NotNull(message = "Password is mandatory")
        @Size(min = 4,message = "Min size is 4 element")
        String password
) {
}
