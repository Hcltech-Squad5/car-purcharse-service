package com.hcltech.car_purcharse_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
@Entity
@Table(name = "car_image")
public class CarImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "public_id", nullable = false)
    private String publicId;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;


    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
}