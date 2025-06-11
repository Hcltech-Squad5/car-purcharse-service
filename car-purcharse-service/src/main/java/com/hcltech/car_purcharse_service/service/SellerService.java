package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.repository.SellerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    private static final Logger log = LoggerFactory.getLogger(SellerService.class);

    public Seller saveSeller(Seller seller)
    {
        log.info("Seller details saved in DB");
        return  sellerRepository.save(seller);
    }

    public Optional<Seller> findSellerById(int id)
    {
        log.info("seller found by id");
        return sellerRepository.findById(id);
    }
    public List<Seller> findAllSeller()
    {
        log.info("All Seller");
        return sellerRepository.findAll();
    }

    public boolean deleteSellerById(int id)
    {
        Optional<Seller> recSeller = sellerRepository.findById(id);
        if(recSeller.isPresent())
        {
            log.info("deleted by id");
            sellerRepository.deleteById(id);
            return true;
        }else{
            log.error("wrong id");
            return false;
        }
    }
}
