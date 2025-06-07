package com.hcltech.car_purcharse_service.config;

import com.cloudinary.Cloudinary;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public Cloudinary cloudinary(){
        Dotenv dotenv = Dotenv.load();
        return new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    }
}
