package com.hcltech.car_purcharse_service.controller;


import com.hcltech.car_purcharse_service.model.User;
import com.hcltech.car_purcharse_service.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/admin")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        ResponseEntity<User> responseEntity = new ResponseEntity<>(userService.create(user), HttpStatus.CREATED);
        return responseEntity;
    }


    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> all = userService.getAll();
        return ResponseEntity.ok(all);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserByID(@PathVariable Integer id) {
        ResponseEntity responseEntity = new ResponseEntity(userService.getByID(id), HttpStatus.OK);
        return responseEntity;
    }

    @GetMapping("/username/{userName}")
    public ResponseEntity<User> getUserByusername(@PathVariable String userName) {
        ResponseEntity responseEntity = new ResponseEntity(userService.getByUserName(userName), HttpStatus.OK);
        return responseEntity;
    }

    @GetMapping("/role/{Role}")
    public ResponseEntity<User> getUserByRole(@PathVariable String Role) {
        ResponseEntity responseEntity = new ResponseEntity(userService.getByRole(Role), HttpStatus.OK);
        return responseEntity;
    }

    @PutMapping("/password/id/{id}")
    public ResponseEntity<String> updatebyId(@PathVariable Integer id, @RequestBody User user) {

        userService.updatePasswordById(id, user.getPassword());

        return ResponseEntity.ok("password updated successfully for user id" + id);

    }

    @PutMapping("/password/username/{userName}")
    public ResponseEntity<String> updatebyId(@PathVariable String userName, @RequestBody User user) {

        userService.updatePasswordByusername(userName, user.getPassword());

        return ResponseEntity.ok("password updated successfully for user username" + userName);

    }

    @DeleteMapping("/id/{id}")
    public ResponseEntity<Void> deleteByid(@PathVariable Integer id) {
        userService.deleteById(id);
        ResponseEntity<Void> responseEntity = new ResponseEntity(HttpStatus.OK);
        return responseEntity;
    }


    @DeleteMapping("/username/{userName}")
    public ResponseEntity<Void> deleteByusername(@PathVariable String username) {
        userService.deleteByuserName(username);
        ResponseEntity<Void> responseEntity = new ResponseEntity(HttpStatus.OK);
        return responseEntity;
    }

}


