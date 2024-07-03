package com.cognito.rent_a_car_api.api;

import com.cognito.rent_a_car_api.business.AuthenticationService;
import com.cognito.rent_a_car_api.core.dto.AuthenticationRequest;
import com.cognito.rent_a_car_api.core.dto.AuthenticationResponse;
import com.cognito.rent_a_car_api.core.dto.RegistrationRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@Tag(name = "Authentication")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping("/register")
    public ResponseEntity<Integer> register(
            @Valid @RequestBody RegistrationRequest request
    ) throws MessagingException {
       authenticationService.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @GetMapping("/activateAccount")
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        authenticationService.activateAccount(token);
    }
}
