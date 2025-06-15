package com.hcltech.car_purcharse_service.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments

@Entity
@Table(name = "car")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String make;

    String model;

    int year;

    Double price;

    Boolean isAvailable;

    @ManyToOne
    @JoinColumn(name = "seller_Id")
    @JsonIgnore
    Seller seller;
}