package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.dto.BuyerDto;
import com.hcltech.car_purcharse_service.model.Buyer;
import com.hcltech.car_purcharse_service.repository.BuyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BuyerService {

    @Autowired
    private BuyerRepository buyerRepository;

    private BuyerDto mapToDto(Buyer buyer) {
        return new BuyerDto(buyer.getId(), buyer.getFirstName(), buyer.getLastName(), buyer.getEmail(), buyer.getPhoneNumber());
    }

    private Buyer mapToEntity(BuyerDto dto) {
        Buyer buyer = new Buyer();
        buyer.setId(dto.getId());
        buyer.setFirstName(dto.getFirstName());
        buyer.setLastName(dto.getLastName());
        buyer.setEmail(dto.getEmail());
        buyer.setPhoneNumber(dto.getPhoneNumber());
        return buyer;
    }

    public BuyerDto createBuyer(BuyerDto buyerDto) {
        String rawPassword = buyerDto.getPassword();
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password is required for buyer creation process (even if not stored here).");
        }
        buyerDto.setPassword(null);
        Buyer buyer = mapToEntity(buyerDto);
        Buyer savedBuyer = buyerRepository.save(buyer);
        return mapToDto(savedBuyer);
    }


    public BuyerDto getBuyerById(Integer id) {
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        return mapToDto(buyer);
    }


    public List<BuyerDto> getAllBuyers() {
        return buyerRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }


    public BuyerDto updateBuyer(Integer id, BuyerDto buyerDto) {
        Buyer existing = buyerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        existing.setFirstName(buyerDto.getFirstName());
        existing.setLastName(buyerDto.getLastName());
        existing.setEmail(buyerDto.getEmail());
        existing.setPhoneNumber(buyerDto.getPhoneNumber());
        return mapToDto(buyerRepository.save(existing));
    }


    public void deleteBuyer(Integer id) {
        buyerRepository.deleteById(id);
    }


    public Map<String, String> getUserCredentials(Integer buyerId) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Map<String, String> credentials = new HashMap<>();
        credentials.put("password", "PlaceholderSecurePassword123!");
        return credentials;
    }
}