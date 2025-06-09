package com.hcltech.car_purcharse_service.service;


import com.hcltech.car_purcharse_service.model.User;
import com.hcltech.car_purcharse_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    final private static Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserRepository repo;


    public User create(User user){
        try {
            logger.info("Inserting the user");
            User save = repo.save(user);
            return save;
        }catch (Exception e) {
            logger.error("Failed to insert the user into Database");
            throw new RuntimeException(e);
        }
    }

    public List<User> getAll() {
        try {
            logger.info("Fetching the all users from Database....");
            List<User> allUsers = repo.findAll();
            return allUsers;
        } catch (Exception e) {
            logger.error("Failed to fetch the All user from Database");
            throw new RuntimeException(e);
        }
    }


    public User getByID(Integer userId) {
        try {
            logger.info("fetching user by ID {}", userId);
            User user= repo.findById(userId).orElseThrow();
            return user;
        } catch (Exception e) {
            logger.error("Failed to fetch the user by ID {}", userId);
            throw new RuntimeException(e);
        }
    }

    public  User getByUserName(String userName){
        try {
            logger.info("fetching user by username {}", userName);
            User user= repo.findByUserName(userName).orElseThrow();
            return user;
        } catch (Exception e) {
            logger.error("Failed to fetch the USER by Username {}", userName);
            throw new RuntimeException(e);
        }
    }


    public User getByRole(String Role) {
        try {
            logger.info("fetching user by Role{}", Role);
            User user= repo.findByRole(Role).orElseThrow();
            return user;
        } catch (Exception e) {
            logger.error("Failed to fetch the user by role {}", Role);
            throw new RuntimeException(e);
        }
    }

    public void updatePasswordById(Integer userId,String newPassword){
        try {
            logger.info("Update password by id{}", userId);
            User user= repo.findById(userId).orElseThrow();
            user.setPassword(newPassword);
            repo.save(user);
        } catch (Exception e) {
            logger.error("Failed to update the password by id{}", userId);
            throw new RuntimeException(e);
        }
    }

    public void updatePasswordByusername(String userName,String newPassword){
        try {
            logger.info("Update password by username");
            User user = repo.findByUserName(userName).orElseThrow();
            user.setPassword(newPassword);
            repo.save(user);
        } catch (Exception e) {
            logger.error("Failed to update the password by username");
            throw new RuntimeException(e);
        }
    }

    public void deleteById(Integer userId) {

        try {
            logger.info("Deleting the user by ID {}", userId);
            repo.deleteById(userId);
        } catch (Exception e) {
            logger.error("Failed to delete the user by ID {}", userId);
            throw new RuntimeException(e);
        }
    }

    public void deleteByuserName(String userName) {

        logger.info("Deleting the user by username");
        if(!repo.findByUserName(userName).isPresent()){
            logger.error("Failed to delete the user by username");
            throw new RuntimeException("User not found with username: " + userName);
        }
        repo.deleteByUserName(userName);
    }
}

