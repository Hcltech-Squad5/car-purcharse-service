package com.hcltech.car_purcharse_service.dao.service;

import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.repository.SellerRepository;
import org.junit.jupiter.api.BeforeEach;
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
public class SellerDaoServiceTest {

    @Mock
    private SellerRepository sellerRepository;

    @InjectMocks
    private SellerDaoService sellerDaoService;

    private Seller seller;

    @BeforeEach
    void setUp() {
        seller = new Seller(1, "Test Seller", 9876543210L, "test@example.com", "Test Company", null);
    }

    @Test
    void testSaveSeller() {
        when(sellerRepository.save(any(Seller.class))).thenReturn(seller);

        Seller savedSeller = sellerDaoService.saveSeller(new Seller());
        assertNotNull(savedSeller);
        assertEquals("Test Seller", savedSeller.getName());
        verify(sellerRepository, times(1)).save(any(Seller.class));
    }

    @Test
    void testFindSellerById_found() {
        when(sellerRepository.findById(anyInt())).thenReturn(Optional.of(seller));

        Optional<Seller> foundSeller = sellerDaoService.findSellerById(1);
        assertTrue(foundSeller.isPresent());
        assertEquals(1, foundSeller.get().getId());
        verify(sellerRepository, times(1)).findById(anyInt());
    }

    @Test
    void testFindSellerById_notFound() {
        when(sellerRepository.findById(anyInt())).thenReturn(Optional.empty());

        Optional<Seller> foundSeller = sellerDaoService.findSellerById(99);
        assertFalse(foundSeller.isPresent());
        verify(sellerRepository, times(1)).findById(anyInt());
    }

    @Test
    void testFindAllSeller() {
        List<Seller> sellers = Arrays.asList(seller, new Seller(2, "Another Seller", 1234567890L, "another@example.com", "Another Company", null));
        when(sellerRepository.findAll()).thenReturn(sellers);

        List<Seller> foundSellers = sellerDaoService.findAllSeller();
        assertNotNull(foundSellers);
        assertEquals(2, foundSellers.size());
        verify(sellerRepository, times(1)).findAll();
    }

    @Test
    void testDeleteSellerById_success() {
        when(sellerRepository.findById(anyInt())).thenReturn(Optional.of(seller));
        doNothing().when(sellerRepository).deleteById(anyInt());

        boolean isDeleted = sellerDaoService.deleteSellerById(1);
        assertTrue(isDeleted);
        verify(sellerRepository, times(1)).findById(anyInt());
        verify(sellerRepository, times(1)).deleteById(anyInt());
    }

    @Test
    void testDeleteSellerById_notFound() {
        when(sellerRepository.findById(anyInt())).thenReturn(Optional.empty());

        boolean isDeleted = sellerDaoService.deleteSellerById(99);
        assertFalse(isDeleted);
        verify(sellerRepository, times(1)).findById(anyInt());
        verify(sellerRepository, never()).deleteById(anyInt());
    }
}