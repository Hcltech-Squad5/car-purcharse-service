package com.hcltech.car_purcharse_service.config;

import com.cloudinary.Cloudinary;
import com.hcltech.car_purcharse_service.dto.CarImageDto;
import com.hcltech.car_purcharse_service.model.CarImage;
import io.github.cdimascio.dotenv.Dotenv;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public Cloudinary cloudinary() {
        Dotenv dotenv = Dotenv.load();
        return new Cloudinary(dotenv.get("CLOUDINARY_URL"));
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Entity to DTO mapping (CarImage -> CarImageDTO)
        modelMapper.createTypeMap(CarImage.class, CarImageDto.class)
                .addMapping(src -> src.getCar().getId(), (carImageDto, carId) -> carImageDto.setCarId((Integer) carId));

        // DTO to Entity mapping (CarImageDTO -> CarImage)
        modelMapper.createTypeMap(CarImageDto.class, CarImage.class)
                .addMappings(mapper -> mapper.skip(CarImage::setCar));
        // Skip mapping 'car' directly from DTO
        // When mapping DTO to Entity, you will manually set the 'Car' object based on carId.
        return modelMapper;
    }
}
