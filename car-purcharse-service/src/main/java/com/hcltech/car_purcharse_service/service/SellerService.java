package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.dao.service.SellerDaoService;
import com.hcltech.car_purcharse_service.dao.service.UserDaoService;
import com.hcltech.car_purcharse_service.dto.SellerDto;
import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.model.User;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SellerService {

    private final SellerDaoService sellerDaoService;
    private final ModelMapper modelMapper;
    private final UserDaoService userDaoService;

    public SellerService(SellerDaoService sellerDaoService, ModelMapper modelMapper, UserDaoService userDaoService) {
        this.sellerDaoService = sellerDaoService;
        this.modelMapper = modelMapper;
        this.userDaoService = userDaoService;
    }

    public SellerDto saveSeller(SellerDto sellerDto) {

        userDaoService.createUser(sellerDto.getEmail(), sellerDto.getPassword(), "SELLER");

        Seller seller = sellerDaoService.saveSeller(modelMapper.map(sellerDto, Seller.class));

        SellerDto savedSellerDto = modelMapper.map(seller, SellerDto.class);

        savedSellerDto.setPassword(null);
        return savedSellerDto;
    }

    public SellerDto findSellerById(Integer id) {

        Seller seller = sellerDaoService.findSellerById(id).orElseThrow(() -> new UsernameNotFoundException("The Seller is not found"));

        return modelMapper.map(seller, SellerDto.class);

    }

    public List<SellerDto> findAllSeller() {
        return sellerDaoService.findAllSeller().stream().map(seller -> modelMapper.map(seller, SellerDto.class)).toList();
    }

    public SellerDto updateSeller(SellerDto sellerDto, Integer id) {
        Seller existSeller = sellerDaoService.findSellerById(id).orElseThrow(() -> new UsernameNotFoundException("The seller not found"));
        userDaoService.updateUser(userDaoService.getByUserName(existSeller.getEmail()), sellerDto.getEmail(), sellerDto.getPassword());


        Seller seller = sellerDaoService.updateSeller(modelMapper.map(sellerDto, Seller.class));


        return modelMapper.map(seller, SellerDto.class);
    }

    public void deleteSeller(Integer id) {

        userDaoService.deleteByUserName(sellerDaoService.findSellerById(id).orElseThrow(() -> new UsernameNotFoundException("User now fount")).getEmail());
        sellerDaoService.deleteSellerById(id);
    }
}
