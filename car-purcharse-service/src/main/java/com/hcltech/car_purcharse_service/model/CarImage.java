package com.hcltech.car_purcharse_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "car_image")
public class CarImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "public_id", nullable = false)
    private String publicId;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
}
