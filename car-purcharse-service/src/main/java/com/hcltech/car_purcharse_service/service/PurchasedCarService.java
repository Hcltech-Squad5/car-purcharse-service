package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.dto.PurchasedCarDto;
import com.hcltech.car_purcharse_service.dto.PurchasedCarResponseDto;
import com.hcltech.car_purcharse_service.exception.ResourceNotFoundException;
import com.hcltech.car_purcharse_service.model.Buyer;
import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.PurchasedCar;
import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.repository.BuyerRepository;
import com.hcltech.car_purcharse_service.repository.CarRepository;
import com.hcltech.car_purcharse_service.repository.PurchasedCarRepository;
import com.hcltech.car_purcharse_service.repository.SellerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service

public class PurchasedCarService {

    @Autowired

    private PurchasedCarRepository purchasedCarRepository;
    @Autowired
    private BuyerRepository buyerRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired

    private ModelMapper modelMapper;


    public PurchasedCarResponseDto createPurchasedCar(PurchasedCarDto dto) {

        PurchasedCar entity = modelMapper.map(dto, PurchasedCar.class);

        PurchasedCar saved = purchasedCarRepository.save(entity);

        return modelMapper.map(saved, PurchasedCarResponseDto.class);

    }


    public List<PurchasedCarResponseDto> getAllPurchasedCars() {

        return purchasedCarRepository.findAll()

                .stream()

                .map(p -> modelMapper.map(p, PurchasedCarResponseDto.class))

                .collect(Collectors.toList());

    }


    public PurchasedCarResponseDto getPurchasedCarById(Integer id) {

        PurchasedCar pc = purchasedCarRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("PurchasedCar not found with ID: " + id));

        return modelMapper.map(pc, PurchasedCarResponseDto.class);

    }


    public PurchasedCarResponseDto updatePurchasedCar(Integer id, PurchasedCarDto dto) {

        PurchasedCar existing = purchasedCarRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("PurchasedCar not found with ID: " + id));

        Buyer buyer = buyerRepository.findById(dto.getBuyerId())

                .orElseThrow(() -> new ResourceNotFoundException("Buyer not found with ID: " + dto.getBuyerId()));

        Seller seller = sellerRepository.findById(dto.getSellerId())

                .orElseThrow(() -> new ResourceNotFoundException("Seller not found with ID: " + dto.getSellerId()));

        Car car = carRepository.findById(dto.getCarId())

                .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + dto.getCarId()));

        existing.setBuyer(buyer);

        existing.setSeller(seller);

        existing.setCar(car);

        existing.setPurchaseDate(dto.getPurchaseDate());


        PurchasedCar updated = purchasedCarRepository.save(existing);

        return modelMapper.map(updated, PurchasedCarResponseDto.class);

    }


    public void deletePurchasedCar(Integer id) {

        PurchasedCar pc = purchasedCarRepository.findById(id)

                .orElseThrow(() -> new ResourceNotFoundException("PurchasedCar not found with ID: " + id));

        purchasedCarRepository.delete(pc);

    }


    public List<PurchasedCarResponseDto> getPurchasedCarsByBuyerId(Integer buyerId) {

        return purchasedCarRepository.findByBuyerId(buyerId)

                .stream()

                .map(pc -> modelMapper.map(pc, PurchasedCarResponseDto.class))

                .collect(Collectors.toList());

    }

    public List<PurchasedCarResponseDto> getPurchasedCarsBySellerId(Integer sellerId) {

        return purchasedCarRepository.findBySellerId(sellerId)

                .stream()

                .map(pc -> modelMapper.map(pc, PurchasedCarResponseDto.class))

                .collect(Collectors.toList());

    }

    public List<PurchasedCarResponseDto> getPurchasedCarsByCarId(Integer carId) {

        return purchasedCarRepository.findByCarId(carId)

                .stream()

                .map(pc -> modelMapper.map(pc, PurchasedCarResponseDto.class))

                .collect(Collectors.toList());

    }

}

 