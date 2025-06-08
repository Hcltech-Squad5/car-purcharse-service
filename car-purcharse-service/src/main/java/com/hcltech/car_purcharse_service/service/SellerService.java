package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.model.Car;
import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SellerService {

    @Autowired
    private SellerRepository sellerRepository;

    public Seller saveSeller(Seller seller)
    {
        return  sellerRepository.save(seller);
    }

    public Optional<Seller> findSellerById(int id)
    {
        return sellerRepository.findById(id);
    }
    public List<Seller> findAllSeller()
    {
        return sellerRepository.findAll();
    }
    public List<Car> findAllCar()
    {
        return sellerRepository.findAllCar();
    }
    public boolean deleteSellerById(int id)
    {
        Optional<Seller> recSeller = sellerRepository.findById(id);
        if(recSeller.isPresent())
        {
            sellerRepository.deleteById(id);
            return true;
        }else{
            return false;
        }
    }
}
