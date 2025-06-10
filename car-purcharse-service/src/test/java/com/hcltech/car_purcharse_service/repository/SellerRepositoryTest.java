package com.hcltech.car_purcharse_service.repository;

import com.hcltech.car_purcharse_service.model.Seller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) // Use your actual database, or NONE for in-memory H2 if configured
public class SellerRepositoryTest {

    @Autowired
    private SellerRepository sellerRepository;

    @Test
    void testSaveAndFindSeller() {
        Seller seller = new Seller(0, "Repo Seller", 1112223334L, "repo@example.com", "Repo Company", null);
        Seller savedSeller = sellerRepository.save(seller);

        assertNotNull(savedSeller.getId());
        assertEquals("Repo Seller", savedSeller.getName());

        Optional<Seller> foundSeller = sellerRepository.findById(savedSeller.getId());
        assertTrue(foundSeller.isPresent());
        assertEquals(savedSeller.getId(), foundSeller.get().getId());
    }

    @Test
    void testFindAllSellers() {
        sellerRepository.save(new Seller(0, "Seller One", 1000000001L, "one@example.com", "Company A", null));
        sellerRepository.save(new Seller(0, "Seller Two", 1000000002L, "two@example.com", "Company B", null));

        List<Seller> sellers = sellerRepository.findAll();
        assertNotNull(sellers);
        assertEquals(2, sellers.size()); // Assuming a clean database before tests
    }

    @Test
    void testUpdateSeller() {
        Seller seller = new Seller(0, "Original Name", 9998887776L, "original@example.com", "Original Corp", null);
        Seller savedSeller = sellerRepository.save(seller);

        savedSeller.setName("Updated Name");
        savedSeller.setEmail("updated@example.com");
        Seller updatedSeller = sellerRepository.save(savedSeller);

        assertEquals("Updated Name", updatedSeller.getName());
        assertEquals("updated@example.com", updatedSeller.getEmail());
    }

    @Test
    void testDeleteSeller() {
        Seller seller = new Seller(0, "Delete Me", 5554443332L, "delete@example.com", "Delete Corp", null);
        Seller savedSeller = sellerRepository.save(seller);

        sellerRepository.deleteById(savedSeller.getId());

        Optional<Seller> foundSeller = sellerRepository.findById(savedSeller.getId());
        assertFalse(foundSeller.isPresent());
    }
}