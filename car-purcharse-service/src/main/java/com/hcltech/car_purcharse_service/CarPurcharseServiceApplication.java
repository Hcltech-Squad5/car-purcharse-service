package com.hcltech.car_purcharse_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.hcltech.car_purcharse_service", "com.anothercompany.utils.jwt"})
public class CarPurcharseServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarPurcharseServiceApplication.class, args);
	}

}

