package com.hcltech.car_purcharse_service.dao.service;

import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.repository.SellerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerDaoService {

    private final SellerRepository sellerRepository;

    private static final Logger log = LoggerFactory.getLogger(SellerDaoService.class);

    public SellerDaoService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    public Seller saveSeller(Seller seller) {
        log.info("Seller details saved in DB");
        return sellerRepository.save(seller);
    }

    public Optional<Seller> findSellerById(Integer id) {
        log.info("seller found by id");
        return sellerRepository.findById(id);
    }

    public List<Seller> findAllSeller() {
        log.info("All Seller");
        return sellerRepository.findAll();
    }

    public void deleteSellerById(Integer id) {

        sellerRepository.deleteById(id);
    }
    public Seller updateSeller( Seller seller){

        return sellerRepository.save(seller);
    }
}
