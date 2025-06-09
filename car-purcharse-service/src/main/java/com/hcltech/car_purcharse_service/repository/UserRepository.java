package com.hcltech.car_purcharse_service.repository;

import com.hcltech.car_purcharse_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User,Integer> {

    Optional<User> findByUserName(String userName);
    Optional<User> findByRole(String Role);
    void deleteByUserName(String userName);
}

