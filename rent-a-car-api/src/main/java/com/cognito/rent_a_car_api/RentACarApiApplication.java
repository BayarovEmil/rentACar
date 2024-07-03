package com.cognito.rent_a_car_api;

import com.cognito.rent_a_car_api.core.dataAccess.RoleRepository;
import com.cognito.rent_a_car_api.core.entity.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class RentACarApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RentACarApiApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByRole("USER").isEmpty()) {
				roleRepository.save(Role.builder().role("USER").build());
			}
		};
	}
}
