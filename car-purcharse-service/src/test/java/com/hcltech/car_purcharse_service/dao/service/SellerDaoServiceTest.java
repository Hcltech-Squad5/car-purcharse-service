package com.hcltech.car_purcharse_service.dao.service;

import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerDaoServiceTest {

    @Mock
    private SellerRepository sellerRepository; // Mock the repository dependency

    @InjectMocks
    private SellerDaoService sellerDaoService; // Inject mocks into the service under test

    private Seller seller; // Common Seller object for tests

    @BeforeEach
    void setUp() {
        // Initialize a common Seller object before each test
        seller = new Seller();
        seller.setId(1);
        seller.setName("Test Seller");
        seller.setEmail("test@example.com");
        seller.setContact("1234567890");
    }

    @Test
    @DisplayName("Test saveSeller - Success")
    void testSaveSeller_Success() {
        // Arrange: Define the behavior of the mocked repository
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        // Act: Call the service method
        Seller savedSeller = sellerDaoService.saveSeller(seller);

        // Assert: Verify the outcome and interactions
        assertNotNull(savedSeller);
        assertEquals(seller.getId(), savedSeller.getId());
        assertEquals(seller.getEmail(), savedSeller.getEmail());
        assertEquals(seller.getName(), savedSeller.getName());

        // Verify that sellerRepository.save was called exactly once with any Seller object
        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    @Test
    @DisplayName("Test findSellerById - Found")
    void testFindSellerById_Found() {
        // Arrange
        when(sellerRepository.findById(1)).thenReturn(Optional.of(seller));

        // Act
        Optional<Seller> foundSeller = sellerDaoService.findSellerById(1);

        // Assert
        assertTrue(foundSeller.isPresent());
        assertEquals(seller.getId(), foundSeller.get().getId());
        assertEquals(seller.getEmail(), foundSeller.get().getEmail());

        // Verify that sellerRepository.findById was called once with ID 1
        verify(sellerRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("Test findSellerById - Not Found")
    void testFindSellerById_NotFound() {
        // Arrange
        when(sellerRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act
        Optional<Seller> foundSeller = sellerDaoService.findSellerById(99);

        // Assert
        assertFalse(foundSeller.isPresent());

        // Verify that sellerRepository.findById was called once with any integer
        verify(sellerRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Test findAllSeller - Returns List")
    void testFindAllSeller_ReturnsList() {
        // Arrange
        Seller seller2 = new Seller();
        seller2.setId(2);
        seller2.setName("Another Seller");
        seller2.setEmail("another@example.com");
        seller2.setContact("0987654321");

        List<Seller> sellers = Arrays.asList(seller, seller2);
        when(sellerRepository.findAll()).thenReturn(sellers);

        // Act
        List<Seller> allSellers = sellerDaoService.findAllSeller();

        // Assert
        assertNotNull(allSellers);
        assertEquals(2, allSellers.size());
        assertEquals(seller.getEmail(), allSellers.get(0).getEmail());
        assertEquals(seller2.getEmail(), allSellers.get(1).getEmail());

        // Verify that sellerRepository.findAll was called once
        verify(sellerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test findAllSeller - Returns Empty List")
    void testFindAllSeller_ReturnsEmptyList() {
        // Arrange
        when(sellerRepository.findAll()).thenReturn(List.of()); // Return an empty list

        // Act
        List<Seller> allSellers = sellerDaoService.findAllSeller();

        // Assert
        assertNotNull(allSellers);
        assertTrue(allSellers.isEmpty());
        assertEquals(0, allSellers.size());

        // Verify that sellerRepository.findAll was called once
        verify(sellerRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test deleteSellerById - Success")
    void testDeleteSellerById_Success() {
        // Arrange: For void methods, we use doNothing().when() if no exception is expected
        doNothing().when(sellerRepository).deleteById(1);

        // Act: Call the service method
        sellerDaoService.deleteSellerById(1);

        // Assert: Verify that sellerRepository.deleteById was called exactly once with ID 1
        verify(sellerRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("Test updateSeller - Success")
    void testUpdateSeller_Success() {
        // Arrange: Create an updated seller object
        Seller updatedSeller = new Seller();
        updatedSeller.setId(1);
        updatedSeller.setName("Updated Seller Name");
        updatedSeller.setEmail("updated@example.com");
        updatedSeller.setContact("1122334455");

        // Mock the save method to return the updated seller
        when(sellerRepository.save(updatedSeller)).thenReturn(updatedSeller);

        // Act: Call the service method
        Seller resultSeller = sellerDaoService.updateSeller(updatedSeller);

        // Assert: Verify the returned seller and repository interaction
        assertNotNull(resultSeller);
        assertEquals(updatedSeller.getId(), resultSeller.getId());
        assertEquals(updatedSeller.getEmail(), resultSeller.getEmail());
        assertEquals(updatedSeller.getName(), resultSeller.getName());

        // Verify that sellerRepository.save was called exactly once with the updated Seller object
        verify(sellerRepository, times(1)).save(updatedSeller);
    }
}