package com.hcltech.car_purcharse_service.service;

import com.hcltech.car_purcharse_service.dto.ResponseStructure;
import com.hcltech.car_purcharse_service.exception.IdNotFoundException;
import com.hcltech.car_purcharse_service.model.Seller;
import com.hcltech.car_purcharse_service.dao.service.SellerDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SellerService {

    @Autowired
    private SellerDaoService sellerDaoService;

    public ResponseEntity<ResponseStructure<Seller>> saveSeller(Seller seller) {
        ResponseStructure<Seller> structure = new ResponseStructure<>();
        structure.setData(sellerDaoService.saveSeller(seller));
        structure.setMessage("Seller details added");
        structure.setStatusCode(HttpStatus.CREATED.value());
        return new ResponseEntity<ResponseStructure<Seller>>(structure, HttpStatus.CREATED);
    }


    public ResponseEntity<ResponseStructure<Seller>> findSellerById(int id) {
        Optional<Seller> opt = sellerDaoService.findSellerById(id);
        ResponseStructure<Seller> structure = new ResponseStructure<>();
        if (opt.isPresent()) {
            structure.setData(opt.get());
            structure.setMessage("Seller id is present");
            structure.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<ResponseStructure<Seller>>(structure, HttpStatus.OK);
        } else {
            throw new IdNotFoundException("Supplier Id is not present");
        }
    }

    public ResponseEntity<ResponseStructure<List<Seller>>> findAllSeller() {
        ResponseStructure<List<Seller>> structure = new ResponseStructure<>();
        structure.setData(sellerDaoService.findAllSeller());
        structure.setMessage("All Seller Found");
        structure.setStatusCode(HttpStatus.OK.value());
        return new ResponseEntity<ResponseStructure<List<Seller>>>(structure,HttpStatus.OK);
    }


    public ResponseEntity<ResponseStructure<Seller>> updateSeller(Seller seller, int id) {
        Optional<Seller> opt = sellerDaoService.findSellerById(id);
        ResponseStructure<Seller> structure = new ResponseStructure<>();
        if (opt.isPresent()) {
            structure.setData(sellerDaoService.saveSeller(seller));
            structure.setMessage("Seller Updated successfully");
            structure.setStatusCode(HttpStatus.ACCEPTED.value());
            return new ResponseEntity<ResponseStructure<Seller>>(structure, HttpStatus.ACCEPTED);
        } else {
            throw new IdNotFoundException("Seller id is invalid");
        }
    }

    public ResponseEntity<ResponseStructure<Boolean>> deleteSeller(int id) {
        Optional<Seller> opt = sellerDaoService.findSellerById(id);
        ResponseStructure<Boolean> structure = new ResponseStructure<>();
        if (opt.isPresent()) {
            structure.setData(sellerDaoService.deleteSellerById(id));
            structure.setMessage("Seller details deleted successfully");
            structure.setStatusCode(HttpStatus.OK.value());
            return new ResponseEntity<ResponseStructure<Boolean>>(structure, HttpStatus.OK);
        } else {
            throw new IdNotFoundException("Seller id is not present");
        }
    }
}
