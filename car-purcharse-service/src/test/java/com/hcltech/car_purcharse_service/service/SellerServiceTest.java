package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.dao.service.SellerDaoService;
import com.hcltech.car_purcharse_service.dto.ResponseStructure;
import com.hcltech.car_purcharse_service.exception.IdNotFoundException;
import com.hcltech.car_purcharse_service.model.Car; // Assuming Car model exists
import com.hcltech.car_purcharse_service.model.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceTest {

    @Mock
    private SellerDaoService sellerDaoService;

    @InjectMocks
    private SellerService sellerService;

    // Helper method to create a Seller object
    private Seller createSeller(int id, String name, long contact, String email, String companyName) {
        Seller seller = new Seller();
        seller.setId(id);
        seller.setName(name);
        seller.setContact(contact);
        seller.setEmail(email);
        seller.setCompanyName(companyName);
        seller.setCars(Collections.emptyList()); // Initialize cars list
        return seller;
    }

    @BeforeEach
    void setUp() {
        // No specific setup needed for @Mock and @InjectMocks for each test
    }

    // --- saveSeller() Tests ---
    @Test
    @DisplayName("saveSeller should successfully save a seller and return CREATED status")
    void saveSeller_success() {
        Seller sellerToSave = createSeller(0, "Test Seller", 1234567890L, "test@example.com", "Test Corp");
        Seller savedSeller = createSeller(1, "Test Seller", 1234567890L, "test@example.com", "Test Corp");

        when(sellerDaoService.saveSeller(sellerToSave)).thenReturn(savedSeller);

        ResponseEntity<ResponseStructure<Seller>> response = sellerService.saveSeller(sellerToSave);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Seller details added", response.getBody().getMessage());
        assertEquals(savedSeller.getId(), response.getBody().getData().getId());
        verify(sellerDaoService, times(1)).saveSeller(sellerToSave);
    }

    @Test
    @DisplayName("saveSeller should handle null Seller input gracefully (if DAO supports it)")
    void saveSeller_nullSellerInput_returnsNullData() {
        // Assuming sellerDaoService.saveSeller(null) returns null or throws NPE.
        // Mocking to return null for graceful handling if service is designed for it.
        when(sellerDaoService.saveSeller(eq(null))).thenReturn(null);

        ResponseEntity<ResponseStructure<Seller>> response = sellerService.saveSeller(null);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Seller details added", response.getBody().getMessage());
        assertNull(response.getBody().getData()); // Expecting null data
        verify(sellerDaoService, times(1)).saveSeller(eq(null));
    }


    // --- findSellerById() Tests ---
    @Test
    @DisplayName("findSellerById should return seller when ID is found")
    void findSellerById_found_returnsSeller() {
        int sellerId = 1;
        Seller foundSeller = createSeller(sellerId, "Found Seller", 1112223333L, "found@example.com", "Found Corp");

        when(sellerDaoService.findSellerById(sellerId)).thenReturn(Optional.of(foundSeller));

        ResponseEntity<ResponseStructure<Seller>> response = sellerService.findSellerById(sellerId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Seller id is present", response.getBody().getMessage());
        assertEquals(foundSeller.getId(), response.getBody().getData().getId());
        verify(sellerDaoService, times(1)).findSellerById(sellerId);
    }

    @Test
    @DisplayName("findSellerById should throw IdNotFoundException when ID is not found")
    void findSellerById_notFound_throwsIdNotFoundException() {
        int sellerId = 99;

        when(sellerDaoService.findSellerById(sellerId)).thenReturn(Optional.empty());

        IdNotFoundException thrown = assertThrows(IdNotFoundException.class, () -> {
            sellerService.findSellerById(sellerId);
        });

        assertEquals("Supplier Id is not present", thrown.getMessage()); // Your exception message for this case
        verify(sellerDaoService, times(1)).findSellerById(sellerId);
    }


    // --- findAllSeller() Tests ---
    @Test
    @DisplayName("findAllSeller should return list of all sellers")
    void findAllSeller_returnsListOfSellers() {
        List<Seller> sellers = Arrays.asList(
                createSeller(1, "Seller A", 1234567890L, "a@example.com", "Company A"),
                createSeller(2, "Seller B", 9876543210L, "b@example.com", "Company B")
        );

        when(sellerDaoService.findAllSeller()).thenReturn(sellers);

        ResponseEntity<ResponseStructure<List<Seller>>> response = sellerService.findAllSeller();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All Seller Found", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertEquals(2, response.getBody().getData().size());
        assertEquals(1, response.getBody().getData().get(0).getId());
        verify(sellerDaoService, times(1)).findAllSeller();
    }

    @Test
    @DisplayName("findAllSeller should return empty list if no sellers exist")
    void findAllSeller_noSellers_returnsEmptyList() {
        when(sellerDaoService.findAllSeller()).thenReturn(Collections.emptyList());

        ResponseEntity<ResponseStructure<List<Seller>>> response = sellerService.findAllSeller();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("All Seller Found", response.getBody().getMessage());
        assertNotNull(response.getBody().getData());
        assertTrue(response.getBody().getData().isEmpty());
        verify(sellerDaoService, times(1)).findAllSeller();
    }


    // --- updateSeller() Tests ---
    @Test
    @DisplayName("updateSeller should successfully update a seller and return ACCEPTED status")
    void updateSeller_success() {
        int sellerId = 1;
        Seller existingSeller = createSeller(sellerId, "Old Name", 1111111111L, "old@example.com", "Old Corp");
        Seller updatedSellerInput = createSeller(sellerId, "New Name", 2222222222L, "new@example.com", "New Corp");
        Seller savedUpdatedSeller = createSeller(sellerId, "New Name", 2222222222L, "new@example.com", "New Corp");

        when(sellerDaoService.findSellerById(sellerId)).thenReturn(Optional.of(existingSeller));
        when(sellerDaoService.saveSeller(updatedSellerInput)).thenReturn(savedUpdatedSeller);

        ResponseEntity<ResponseStructure<Seller>> response = sellerService.updateSeller(updatedSellerInput, sellerId);

        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Seller Updated successfully", response.getBody().getMessage());
        assertEquals(savedUpdatedSeller.getId(), response.getBody().getData().getId());
        assertEquals("New Name", response.getBody().getData().getName());
        verify(sellerDaoService, times(1)).findSellerById(sellerId);
        verify(sellerDaoService, times(1)).saveSeller(updatedSellerInput);
    }

    @Test
    @DisplayName("updateSeller should throw IdNotFoundException when seller to update is not found")
    void updateSeller_notFound_throwsIdNotFoundException() {
        int sellerId = 99;
        Seller updatedSellerInput = createSeller(sellerId, "New Name", 2222222222L, "new@example.com", "New Corp");

        when(sellerDaoService.findSellerById(sellerId)).thenReturn(Optional.empty());

        IdNotFoundException thrown = assertThrows(IdNotFoundException.class, () -> {
            sellerService.updateSeller(updatedSellerInput, sellerId);
        });

        assertEquals("Seller id is invalid", thrown.getMessage()); // Your exception message for update not found
        verify(sellerDaoService, times(1)).findSellerById(sellerId);
        verify(sellerDaoService, never()).saveSeller(any(Seller.class)); // Ensure save is not called
    }


    // --- deleteSeller() Tests ---
    @Test
    @DisplayName("deleteSeller should successfully delete a seller and return OK status")
    void deleteSeller_success() {
        int sellerId = 1;
        Seller existingSeller = createSeller(sellerId, "To Delete", 1231231234L, "delete@example.com", "Delete Corp");

        when(sellerDaoService.findSellerById(sellerId)).thenReturn(Optional.of(existingSeller));
        when(sellerDaoService.deleteSellerById(sellerId)).thenReturn(true);

        ResponseEntity<ResponseStructure<Boolean>> response = sellerService.deleteSeller(sellerId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Seller details deleted successfully", response.getBody().getMessage());
        assertTrue(response.getBody().getData()); // Expecting true for successful deletion
        verify(sellerDaoService, times(1)).findSellerById(sellerId);
        verify(sellerDaoService, times(1)).deleteSellerById(sellerId);
    }

    @Test
    @DisplayName("deleteSeller should throw IdNotFoundException when seller to delete is not found")
    void deleteSeller_notFound_throwsIdNotFoundException() {
        int sellerId = 99;

        when(sellerDaoService.findSellerById(sellerId)).thenReturn(Optional.empty());

        IdNotFoundException thrown = assertThrows(IdNotFoundException.class, () -> {
            sellerService.deleteSeller(sellerId);
        });

        assertEquals("Seller id is not present", thrown.getMessage()); // Your exception message for delete not found
        verify(sellerDaoService, times(1)).findSellerById(sellerId);
        verify(sellerDaoService, never()).deleteSellerById(anyInt()); // Ensure delete is not called
    }

    @Test
    @DisplayName("deleteSeller should handle DAO returning false for deletion (though service message is always success)")
    void deleteSeller_daoReturnsFalse_returnsOkAndFalseData() {
        int sellerId = 1;
        Seller existingSeller = createSeller(sellerId, "To Delete", 1231231234L, "delete@example.com", "Delete Corp");

        when(sellerDaoService.findSellerById(sellerId)).thenReturn(Optional.of(existingSeller));
        when(sellerDaoService.deleteSellerById(sellerId)).thenReturn(false); // DAO reports failure

        ResponseEntity<ResponseStructure<Boolean>> response = sellerService.deleteSeller(sellerId);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Seller details deleted successfully", response.getBody().getMessage()); // Service message is always success
        assertFalse(response.getBody().getData()); // But data reflects DAO's false
        verify(sellerDaoService, times(1)).findSellerById(sellerId);
        verify(sellerDaoService, times(1)).deleteSellerById(sellerId);
    }
}