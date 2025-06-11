package com.hcltech.car_purcharse_service.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
@Entity
@Table(name = "purchased_cars")
public class PurchasedCar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "buyer_id", nullable = false)
    private Buyer buyer;

    @OneToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    @OneToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

//
//    public PurchasedCar() {}
//
//
//    public PurchasedCar(Integer id, Buyer buyer, Seller seller, Car car, LocalDate purchaseDate) {
//        this.id = id;
//        this.buyer = buyer;
//        this.seller = seller;
//        this.car = car;
//        this.purchaseDate = purchaseDate;
//    }
//
//
//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public Buyer getBuyer() {
//        return buyer;
//    }
//
//    public void setBuyer(Buyer buyer) {
//        this.buyer = buyer;
//    }
//
//    public Seller getSeller() {
//        return seller;
//    }
//
//    public void setSeller(Seller seller) {
//        this.seller = seller;
//    }
//
//    public Car getCar() {
//        return car;
//    }
//
//    public void setCar(Car car) {
//        this.car = car;
//    }
//
//    public LocalDate getPurchaseDate() {
//        return purchaseDate;
//    }
//
//    public void setPurchaseDate(LocalDate purchaseDate) {
//        this.purchaseDate = purchaseDate;
//    }
//
//    @Override
//    public String toString() {
//        return "PurchasedCar{" +
//                "id=" + id +
//                ", buyer=" + (buyer != null ? buyer.getId() : null) +
//                ", seller=" + (seller != null ? seller.getId() : null) +
//                ", car=" + (car != null ? car.getId() : null) +
//                ", purchaseDate=" + purchaseDate +
//                '}';
//    }
}
