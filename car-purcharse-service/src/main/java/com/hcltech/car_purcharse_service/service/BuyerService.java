package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.dto.BuyerDto;
import com.hcltech.car_purcharse_service.model.Buyer;
import com.hcltech.car_purcharse_service.repository.BuyerRepository;
import com.hcltech.car_purcharse_service.service.UserService;
import com.hcltech.car_purcharse_service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BuyerService {

    private static final Logger logger = LoggerFactory.getLogger(BuyerService.class);

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private UserService userService;

    private BuyerDto mapToDto(Buyer buyer) {
        logger.debug("Mapping Buyer entity to BuyerDto for ID: {}", buyer.getId());
        return new BuyerDto(buyer.getId(), buyer.getFirstName(), buyer.getLastName(), buyer.getEmail(), buyer.getPhoneNumber());
    }

    private Buyer mapToEntity(BuyerDto dto) {
        logger.debug("Mapping BuyerDto to Buyer entity for ID: {}", dto.getId());
        Buyer buyer = new Buyer();
        buyer.setId(dto.getId());
        buyer.setFirstName(dto.getFirstName());
        buyer.setLastName(dto.getLastName());
        buyer.setEmail(dto.getEmail());
        buyer.setPhoneNumber(dto.getPhoneNumber());
        return buyer;
    }

    public BuyerDto createBuyer(BuyerDto buyerDto) {
        logger.info("Attempting to create a new buyer with email: {}", buyerDto.getEmail());
        String rawPassword = buyerDto.getPassword();
        String email = buyerDto.getEmail();

        if (rawPassword == null || rawPassword.isEmpty()) {
            logger.warn("Password missing for buyer creation: {}", email);
            throw new IllegalArgumentException("Password is required for buyer creation process (even if not stored here).");
        }

        User newUser = new User();
        newUser.setUserName(email);
        newUser.setPassword(rawPassword);
        newUser.setRole("BUYER");

        logger.debug("Preparing to create user with username: {} and role: {}", newUser.getUserName(), newUser.getRole());

        try {
            logger.info("Sending user credentials to UserService for email: {}", email);
            userService.create(newUser);
            logger.info("User account created successfully by UserService for email: {}", email);
        } catch (Exception e) {
            logger.error("Failed to create user account in UserService for email: {}. Error: {}", email, e.getMessage());
            throw new RuntimeException("Failed to create user account: " + e.getMessage(), e);
        }


        buyerDto.setPassword(null);
        Buyer buyer = mapToEntity(buyerDto);
        Buyer savedBuyer = buyerRepository.save(buyer);
        logger.info("Buyer created successfully with ID: {}", savedBuyer.getId());
        return mapToDto(savedBuyer);
    }


    public BuyerDto getBuyerById(Integer id) {
        logger.info("Attempting to retrieve buyer with ID: {}", id);
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Buyer not found for ID: {}", id);
                    return new RuntimeException("Buyer not found");
                });
        logger.info("Successfully retrieved buyer with ID: {}", id);
        return mapToDto(buyer);
    }


    public List<BuyerDto> getAllBuyers() {
        logger.info("Attempting to retrieve all buyers.");
        List<BuyerDto> buyers = buyerRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
        logger.info("Successfully retrieved {} buyers.", buyers.size());
        return buyers;
    }


    public BuyerDto updateBuyer(Integer id, BuyerDto buyerDto) {
        logger.info("Attempting to update buyer with ID: {}", id);
        Buyer existing = buyerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Buyer not found for update, ID: {}", id);
                    return new RuntimeException("Buyer not found");
                });
        logger.debug("Found existing buyer for update, ID: {}", id);
        existing.setFirstName(buyerDto.getFirstName());
        existing.setLastName(buyerDto.getLastName());
        existing.setEmail(buyerDto.getEmail());
        existing.setPhoneNumber(buyerDto.getPhoneNumber());
        Buyer updatedBuyer = buyerRepository.save(existing);
        logger.info("Buyer with ID: {} updated successfully.", updatedBuyer.getId());
        return mapToDto(updatedBuyer);
    }


    public void deleteBuyer(Integer id) {
        logger.info("Attempting to delete buyer with ID: {}", id);

        if (!buyerRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existent buyer with ID: {}", id);
            throw new RuntimeException("Buyer not found for deletion"); // Or a more specific custom exception
        }
        buyerRepository.deleteById(id);
        logger.info("Buyer with ID: {} deleted successfully.", id);
    }


    public Map<String, String> getUserCredentials(Integer buyerId) {
        logger.info("Attempting to retrieve credentials for buyer ID: {}", buyerId);
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> {
                    logger.error("Buyer not found for credentials retrieval, ID: {}", buyerId);
                    return new RuntimeException("Buyer not found");
                });

        Map<String, String> credentials = new HashMap<>();
        credentials.put("password", "PlaceholderSecurePassword123!");
        logger.warn("Returning hardcoded placeholder credentials for buyer ID: {}", buyerId);
        return credentials;
    }
}
