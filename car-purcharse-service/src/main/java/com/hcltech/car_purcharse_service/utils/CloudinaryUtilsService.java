package com.hcltech.car_purcharse_service.utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryUtilsService {
    private Cloudinary cloudinary;

    public CloudinaryUtilsService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map uploadImage(byte[] image){
        try {
            return cloudinary.uploader().upload(image, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
