package com.hcltech.car_purcharse_service.repository;

import com.hcltech.car_purcharse_service.model.Buyer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class BuyerRepositoryTest {

    @Autowired
    private BuyerRepository buyerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Buyer buyer1;
    private Buyer buyer2;

    @BeforeEach
    void setUp() {
        // Clear database before each test
        entityManager.clear();
        entityManager.flush();

        buyer1 = new Buyer();
        buyer1.setFirstName("John");
        buyer1.setLastName("Doe");
        buyer1.setEmail("john.doe@example.com");
        buyer1.setPhoneNumber("1234567890");

        buyer2 = new Buyer();
        buyer2.setFirstName("Jane");
        buyer2.setLastName("Smith");
        buyer2.setEmail("jane.smith@example.com");
        buyer2.setPhoneNumber("0987654321");
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
        Optional<Buyer> foundBuyer = buyerRepository.findById(999); // Non-existent ID
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

        Buyer foundBuyer = buyerRepository.findById(buyer1.getId()).get();
        foundBuyer.setFirstName("Johnny");
        foundBuyer.setPhoneNumber("1112223333");

        Buyer updatedBuyer = buyerRepository.save(foundBuyer);

        assertThat(updatedBuyer.getFirstName()).isEqualTo("Johnny");
        assertThat(updatedBuyer.getPhoneNumber()).isEqualTo("1112223333");
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
}