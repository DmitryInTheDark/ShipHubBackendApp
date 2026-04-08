package ru.ship.ShipHub;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShipHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShipHubApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper(){ return new ModelMapper(); }

}