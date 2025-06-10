package com.hcltech.car_purcharse_service.repository;

import com.hcltech.car_purcharse_service.model.Buyer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.transaction.annotation.Transactional; // Import this

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional // Ensure each test method runs in its own transaction and rolls back
public class BuyerRepositoryTest {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Buyer buyer1;
    private Buyer buyer2;

    @BeforeEach
    void setUp() {
        // Clear all data from the buyer table before each test method
        // This is a more robust way to ensure a clean slate,
        // especially when using a real database in tests.
        buyerRepository.deleteAll(); // Delete all records from the table
        entityManager.flush(); // Ensure deletion is committed if not already
        entityManager.clear(); // Clear the JPA persistence context

        // Initialize Buyer objects for use in tests
        buyer1 = new Buyer(null, "John", "Doe", "john.doe@example.com", "1234567890");
        buyer2 = new Buyer(null, "Jane", "Smith", "jane.smith@example.com", "0987654321");
    }

    @Test
    @DisplayName("Should save a buyer")
    void shouldSaveBuyer() {
        Buyer savedBuyer = buyerRepository.save(buyer1);
        assertThat(savedBuyer).isNotNull();
        assertThat(savedBuyer.getId()).isNotNull();
        assertThat(savedBuyer.getFirstName()).isEqualTo("John");
        assertThat(savedBuyer.getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should find buyer by ID")
    void shouldFindBuyerById() {
        entityManager.persistAndFlush(buyer1);
        Optional<Buyer> foundBuyer = buyerRepository.findById(buyer1.getId());
        assertThat(foundBuyer).isPresent();
        assertThat(foundBuyer.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should return empty optional if buyer not found by ID")
    void shouldReturnEmptyOptionalIfBuyerNotFound() {
        Optional<Buyer> foundBuyer = buyerRepository.findById(999);
        assertThat(foundBuyer).isEmpty();
    }

    @Test
    @DisplayName("Should find all buyers")
    void shouldFindAllBuyers() {
        entityManager.persistAndFlush(buyer1);
        entityManager.persistAndFlush(buyer2);
        List<Buyer> buyers = buyerRepository.findAll();
        assertThat(buyers).isNotNull();
        assertThat(buyers).hasSize(2);
        assertThat(buyers).extracting(Buyer::getFirstName).containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    @DisplayName("Should update an existing buyer")
    void shouldUpdateBuyer() {
        entityManager.persistAndFlush(buyer1);
        Buyer foundBuyer = buyerRepository.findById(buyer1.getId()).orElseThrow(
                () -> new AssertionError("Buyer not found for update test"));
        foundBuyer.setFirstName("Johnny");
        foundBuyer.setPhoneNumber("1112223333");
        Buyer updatedBuyer = buyerRepository.save(foundBuyer);
        assertThat(updatedBuyer.getFirstName()).isEqualTo("Johnny");
        assertThat(updatedBuyer.getPhoneNumber()).isEqualTo("1112223333");
        Optional<Buyer> verifiedBuyer = buyerRepository.findById(updatedBuyer.getId());
        assertThat(verifiedBuyer).isPresent();
        assertThat(verifiedBuyer.get().getFirstName()).isEqualTo("Johnny");
    }

    @Test
    @DisplayName("Should delete a buyer by ID")
    void shouldDeleteBuyerById() {
        entityManager.persistAndFlush(buyer1);
        entityManager.persistAndFlush(buyer2);
        buyerRepository.deleteById(buyer1.getId());
        Optional<Buyer> deletedBuyer = buyerRepository.findById(buyer1.getId());
        assertThat(deletedBuyer).isNotPresent();
        List<Buyer> remainingBuyers = buyerRepository.findAll();
        assertThat(remainingBuyers).hasSize(1);
        assertThat(remainingBuyers.get(0).getEmail()).isEqualTo("jane.smith@example.com");
    }

    @Test
    @DisplayName("Should find buyer by email")
    void shouldFindBuyerByEmail() {
        entityManager.persistAndFlush(buyer1);
        Optional<Buyer> foundBuyer = buyerRepository.findByEmail("john.doe@example.com");
        assertThat(foundBuyer).isPresent();
        assertThat(foundBuyer.get().getFirstName()).isEqualTo("John");
        assertThat(foundBuyer.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("Should return empty optional if buyer not found by email")
    void shouldReturnEmptyOptionalIfBuyerNotFoundByEmail() {
        Optional<Buyer> foundBuyer = buyerRepository.findByEmail("nonexistent@example.com");
        assertThat(foundBuyer).isEmpty();
    }
}