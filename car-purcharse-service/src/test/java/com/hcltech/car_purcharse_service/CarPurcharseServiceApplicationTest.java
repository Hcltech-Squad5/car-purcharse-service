package com.hcltech.car_purcharse_service;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext; // Import this

import static org.mockito.Mockito.*;

class CarPurcharseServiceApplicationTest {

    @Test
    void mainMethod_ShouldCallSpringApplicationRun() {

        try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {


            mockedSpringApplication.when(() -> SpringApplication.run(
                    eq(CarPurcharseServiceApplication.class),
                    any(String[].class)
            )).thenReturn(mock(ConfigurableApplicationContext.class)); // Return a mock context

            CarPurcharseServiceApplication.main(new String[]{});

            mockedSpringApplication.verify(() -> SpringApplication.run(
                    eq(CarPurcharseServiceApplication.class), // Checks the first argument (your main class)
                    any(String[].class)                      // Checks the second argument (any String array)
            ), times(1));
        }
    }

    @Test
    void mainMethod_WithArgs_ShouldCallSpringApplicationRunWithArgs() {
        String[] args = {"--spring.profiles.active=test"};
        try (MockedStatic<SpringApplication> mockedSpringApplication = mockStatic(SpringApplication.class)) {
            mockedSpringApplication.when(() -> SpringApplication.run(
                    eq(CarPurcharseServiceApplication.class),
                    eq(args)
            )).thenReturn(mock(ConfigurableApplicationContext.class));

            CarPurcharseServiceApplication.main(args);

            mockedSpringApplication.verify(() -> SpringApplication.run(
                    eq(CarPurcharseServiceApplication.class),
                    eq(args)
            ), times(1));
        }
    }
}