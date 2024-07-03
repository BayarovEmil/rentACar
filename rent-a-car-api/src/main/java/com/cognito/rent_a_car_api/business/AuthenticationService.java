package com.cognito.rent_a_car_api.business;


import com.cognito.rent_a_car_api.core.dataAccess.ActivationTokenRepository;
import com.cognito.rent_a_car_api.core.dataAccess.RoleRepository;
import com.cognito.rent_a_car_api.core.dataAccess.TokenRepository;
import com.cognito.rent_a_car_api.core.dataAccess.UserRepository;
import com.cognito.rent_a_car_api.core.dto.AuthenticationRequest;
import com.cognito.rent_a_car_api.core.dto.AuthenticationResponse;
import com.cognito.rent_a_car_api.core.dto.RegistrationRequest;
import com.cognito.rent_a_car_api.core.email.EmailService;
import com.cognito.rent_a_car_api.core.email.EmailTemplateName;
import com.cognito.rent_a_car_api.core.entity.ActivationToken;
import com.cognito.rent_a_car_api.core.entity.Token;
import com.cognito.rent_a_car_api.core.entity.User;
import com.cognito.rent_a_car_api.core.security.JwtService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import static com.cognito.rent_a_car_api.core.entity.TokenType.BEARER;


@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenRepository tokenRepository;
    private final ActivationTokenRepository activationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final TokenRepository jwtTokenRepository;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByRole("USER")
                .orElseThrow(()->new UsernameNotFoundException("Role was not initiated"));
        User user = User.builder()
                .firstname(request.firstname())
                .lastname(request.lastname())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(List.of(userRole))
                .accountLocked(false)
                .enabled(false)
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var claims = new HashMap<String, Object>();
        User user = (User) auth.getPrincipal();
        claims.put("getName",user.getName());

        var jwtToken = jwtService.generateToken(claims, user);
        jwtTokenRepository.save(Token.builder()
                        .revoked(false)
                        .expired(false)
                        .token(jwtToken)
                        .user(user)
                        .tokenType(BEARER)
                .build());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public void activateAccount(String token) throws MessagingException {
        var userToken = activationTokenRepository.findByToken(token)
                .orElseThrow(()-> new UsernameNotFoundException("Token not found"));
        if (LocalDateTime.now().isAfter(userToken.getExpiresAt())) {
            sendValidationEmail(userToken.getUser());
            throw new RuntimeException("Token has been expired!");
        }

        var user = userRepository.findByEmail(userToken.getUser().getEmail())
                .orElseThrow(()-> new UsernameNotFoundException("User not found by email"));
        user.setEnabled(true);
        userRepository.save(user);

        userToken.setValidatedAt(LocalDateTime.now());
        activationTokenRepository.save(userToken);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationCode(user);

        emailService.sendEmail(
                user.getEmail(),
                user.getName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private String generateAndSaveActivationCode(User user) {
        var generateToken = generateActivationCode(6);

        ActivationToken newToken = ActivationToken.builder()
                .token(generateToken)
                .user(user)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        activationTokenRepository.save(newToken);
        return generateToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for (int i=0;i<length;i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(randomIndex);
        }

        return codeBuilder.toString();
    }
}
