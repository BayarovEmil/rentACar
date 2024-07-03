package com.cognito.rent_a_car_api.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
public class ActivationToken {
    @Id
    @GeneratedValue
    private Integer id;
    @Column(unique = true)
    private String token;

    private LocalDateTime createdAt;
    private LocalDateTime validatedAt;
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
}
