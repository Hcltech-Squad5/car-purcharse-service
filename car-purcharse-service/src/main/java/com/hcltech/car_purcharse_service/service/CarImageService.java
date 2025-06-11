package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.dto.CarImageDto;
import com.hcltech.car_purcharse_service.model.CarImage;
import com.hcltech.car_purcharse_service.dao.service.CarImageDaoService;
import com.hcltech.car_purcharse_service.utils.CloudinaryUtilsService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class CarImageService {

    private ModelMapper modelMapper;
    private CarImageDaoService carImageDaoService;
    private CloudinaryUtilsService cloudinaryUtilsService;

    public CarImageService(ModelMapper modelMapper, CarImageDaoService carImageDaoService, CloudinaryUtilsService cloudinaryUtilsService) {
        this.modelMapper = modelMapper;
        this.carImageDaoService = carImageDaoService;
        this.cloudinaryUtilsService = cloudinaryUtilsService;
    }

    public CarImageDto upload(MultipartFile file, Integer carId) {
        try {
            Map uploadImage = cloudinaryUtilsService.uploadImage(file.getBytes());
            String publicId = (String) uploadImage.get("public_id");
            String imageUrl = (String) uploadImage.get("url");

            CarImageDto carImageDto = new CarImageDto();
            carImageDto.setCarId(carId);
            carImageDto.setPublicId(publicId);
            carImageDto.setImageUrl(imageUrl);

            CarImage carImage = carImageDaoService.create(modelMapper.map(carImageDto, CarImage.class));

            return modelMapper.map(carImage, CarImageDto.class);


        } catch (IOException e) {
            throw new RuntimeException("Error in uploading the image in Cloudinary");
        }

    }

    public CarImageDto Update(MultipartFile file, Integer id) {

        try {
            CarImage carImagebyId = carImageDaoService.getById(id);
            String status = cloudinaryUtilsService.deleteImage(carImagebyId.getPublicId());
            if (!status.equals("ok")) {
                throw new RuntimeException("The image is not delete in cloudinary");
            }
            Map uploadImage = cloudinaryUtilsService.uploadImage(file.getBytes());
            String publicId = (String) uploadImage.get("public_id");
            String imageUrl = (String) uploadImage.get("url");

            CarImageDto carImageDto = new CarImageDto();
            carImageDto.setId(carImagebyId.getId());
            carImageDto.setCarId(carImageDto.getCarId());
            carImageDto.setPublicId(publicId);
            carImageDto.setImageUrl(imageUrl);

            CarImage carImage = carImageDaoService.update(modelMapper.map(carImageDto, CarImage.class));

            return modelMapper.map(carImage, CarImageDto.class);


        } catch (IOException e) {
            throw new RuntimeException("Error in uploading the image in Cloudinary");
        }
    }

    public List<CarImageDto> getAllImageByCar(Integer carId) {
        List<CarImageDto> carImageDtoList = carImageDaoService.getByCarId(carId).stream().map(carImage -> modelMapper.map(carImage, CarImageDto.class)).toList();

        return carImageDtoList;
    }

    public CarImageDto getImageById(Integer id) {
        CarImage carImage = carImageDaoService.getById(id);
        return modelMapper.map(carImage, CarImageDto.class);
    }

    public String deleteById(Integer id) {

        String status = cloudinaryUtilsService.deleteImage(carImageDaoService.getById(id).getPublicId());

        if (!status.equals("ok")) {
            throw new RuntimeException("Error in deleting image in cloudinary");
        }

        carImageDaoService.delete(id);
        return "successfully delete image with Id" + id;
    }

    public String deleteByCarId(Integer carId) {

        carImageDaoService.getByCarId(carId).
                forEach(carImage -> deleteById(carImage.getId()));
        return "successfully delete images with of Id" + carId;

    }
}
