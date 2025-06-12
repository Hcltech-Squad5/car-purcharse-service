package com.hcltech.car_purcharse_service.controller;


import com.hcltech.car_purcharse_service.model.User;
import com.hcltech.car_purcharse_service.dao.service.UserDaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/admins")
public class UserController {

    private final UserDaoService userDaoService;

    public UserController(UserDaoService userDaoService) {
        this.userDaoService = userDaoService;
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        return new ResponseEntity<>(userDaoService.create(user), HttpStatus.CREATED);
    }


    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> all = userDaoService.getAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return new ResponseEntity<>(userDaoService.getByID(id), HttpStatus.OK);
    }

    @GetMapping("/username/{userName}")
    public ResponseEntity<User> getUserByUserName(@PathVariable String userName) {
        return new ResponseEntity<>(userDaoService.getByUserName(userName), HttpStatus.OK);
    }

    @GetMapping("/role/{Role}")
    public ResponseEntity<User> getUserByRole(@PathVariable String Role) {
        return new ResponseEntity<>(userDaoService.getByRole(Role), HttpStatus.OK);
    }

    @PutMapping("/password/id/{id}")
    public ResponseEntity<String> updateById(@PathVariable Integer id, @RequestBody User user) {

        userDaoService.updatePasswordById(id, user.getPassword());

        return ResponseEntity.ok("password updated successfully for user id" + id);

    }

    @PutMapping("/password/username/{userName}")
    public ResponseEntity<String> updateById(@PathVariable String userName, @RequestBody User user) {

        userDaoService.updatePasswordUsername(userName, user.getPassword());

        return ResponseEntity.ok("password updated successfully for user username" + userName);

    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        userDaoService.deleteById(id);
        return (ResponseEntity<Void>) new ResponseEntity(HttpStatus.OK);
    }


    @DeleteMapping("/username/{userName}")
    public ResponseEntity<Void> deleteByUserName(@PathVariable String userName) {
        userDaoService.deleteByUserName(userName);
        return (ResponseEntity<Void>) new ResponseEntity(HttpStatus.OK);
    }

}


