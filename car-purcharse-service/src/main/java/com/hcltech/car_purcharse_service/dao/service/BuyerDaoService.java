package com.hcltech.car_purcharse_service.dao.service;

import com.hcltech.car_purcharse_service.dto.BuyerDto;
import com.hcltech.car_purcharse_service.model.Buyer;
import com.hcltech.car_purcharse_service.repository.BuyerRepository;
import com.hcltech.car_purcharse_service.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class BuyerDaoService {

    private static final Logger logger = LoggerFactory.getLogger(BuyerDaoService.class);

    private final BuyerRepository buyerRepository;

    private final UserDaoService userDaoService;

    private final ModelMapper modelMapper;

    public BuyerDaoService(BuyerRepository buyerRepository, UserDaoService userDaoService, ModelMapper modelMapper) {
        this.buyerRepository = buyerRepository;
        this.userDaoService = userDaoService;
        this.modelMapper = modelMapper;
    }


    public BuyerDto createBuyer(BuyerDto buyerDto) {
        logger.info("Attempting to create a new buyer with email: {}", buyerDto.getEmail());
        String rawPassword = buyerDto.getPassword();
        String email = buyerDto.getEmail();

        if (rawPassword == null || rawPassword.isEmpty()) {
            logger.warn("Password missing for buyer creation: {}", email);
            throw new IllegalArgumentException("Password is required for buyer creation process (even if not stored here).");
        }


        logger.debug("Preparing to create user with username: {} and role: {}", email, "Buyer");

        try {
            logger.info("Sending user credentials to UserService for email: {}", email);
            userDaoService.createUser(email, rawPassword, "BUYER");
            logger.info("User account created successfully by UserService for email: {}", email);
        } catch (Exception e) {
            logger.error("Failed to create user account in UserService for email: {}. Error: {}", email, e.getMessage());
            throw new RuntimeException("Failed to create user account: " + e.getMessage(), e);
        }


        Buyer buyer = modelMapper.map(buyerDto, Buyer.class);
        buyerDto.setPassword(null);

        Buyer savedBuyer = buyerRepository.save(buyer);
        logger.info("Buyer created successfully with ID: {}", savedBuyer.getId());


        return modelMapper.map(savedBuyer, BuyerDto.class);

    }


    public BuyerDto getBuyerById(Integer id) {
        logger.info("Attempting to retrieve buyer with ID: {}", id);
        Buyer buyer = buyerRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Buyer not found for ID: {}", id);
                    return new RuntimeException("Buyer not found");
                });
        logger.info("Successfully retrieved buyer with ID: {}", id);

        return modelMapper.map(buyer, BuyerDto.class);
    }


    public List<BuyerDto> getAllBuyers() {
        logger.info("Attempting to retrieve all buyers.");
        List<BuyerDto> buyers = buyerRepository.findAll().stream()

                .map(buyer -> modelMapper.map(buyer, BuyerDto.class))
                .toList();
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

        userDaoService.updateUser(userDaoService.getByUserName(existing.getEmail()), buyerDto.getEmail(), buyerDto.getPassword());

        modelMapper.map(buyerDto, existing);

        Buyer updatedBuyer = buyerRepository.save(existing);
        logger.info("Buyer with ID: {} updated successfully.", updatedBuyer.getId());


        return modelMapper.map(updatedBuyer, BuyerDto.class);

    }


    public void deleteBuyer(Integer id) {
        logger.info("Attempting to delete buyer with ID: {}", id);

        if (!buyerRepository.existsById(id)) {
            logger.warn("Attempted to delete non-existent buyer with ID: {}", id);
            throw new RuntimeException("Buyer not found for deletion");
        }
        userDaoService.deleteByUserName(getBuyerById(id).getEmail());
        buyerRepository.deleteById(id);
        logger.info("Buyer with ID: {} deleted successfully.", id);
    }

}