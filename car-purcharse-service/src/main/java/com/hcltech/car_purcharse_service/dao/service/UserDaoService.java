package com.hcltech.car_purcharse_service.dao.service;


import com.hcltech.car_purcharse_service.model.User;
import com.hcltech.car_purcharse_service.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDaoService {

    private static final Logger logger = LoggerFactory.getLogger(UserDaoService.class);

    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    public UserDaoService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }


    public User create(User user) {
        try {
            logger.info("Inserting the user");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to insert the user into Database");
            throw new RuntimeException(e);
        }
    }

    public List<User> getAll() {
        try {
            logger.info("Fetching the all users from Database....");
            return userRepository.findAll();
        } catch (Exception e) {
            logger.error("Failed to fetch the All user from Database");
            throw new RuntimeException(e);
        }
    }


    public User getByID(Integer userId) {
        try {
            logger.info("fetching user by ID {}", userId);
            return userRepository.findById(userId).orElseThrow();
        } catch (Exception e) {
            logger.error("Failed to fetch the user by ID {}", userId);
            throw new RuntimeException(e);
        }
    }

    public User getByUserName(String userName) {
        try {
            logger.info("fetching user by username {}", userName);
            return userRepository.findByUserName(userName).orElseThrow(()->new UsernameNotFoundException("The user is not found"));
        } catch (Exception e) {
            logger.error("Failed to fetch the USER by Username {}", userName);
            throw new RuntimeException(e);
        }
    }


    public User getByRole(String role) {
        try {
            logger.info("fetching user by role{}", role);
            return userRepository.findByRoles(role).orElseThrow();
        } catch (Exception e) {
            logger.error("Failed to fetch the user by role {}", role);
            throw new RuntimeException(e);
        }
    }

    public void updatePasswordById(Integer userId, String newPassword) {
        try {
            logger.info("Update password by id{}", userId);
            User user = userRepository.findById(userId).orElseThrow();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to update the password by id{}", userId);
            throw new RuntimeException(e);
        }
    }

    public void updatePasswordUsername(String userName, String newPassword) {
        try {
            logger.info("Update password by username");
            User user = userRepository.findByUserName(userName).orElseThrow();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to update the password by username");
            throw new RuntimeException(e);
        }
    }

    public void deleteById(Integer userId) {

        try {
            logger.info("Deleting the user by ID {}", userId);
            userRepository.deleteById(userId);
        } catch (Exception e) {
            logger.error("Failed to delete the user by ID {}", userId);
            throw new RuntimeException(e);
        }
    }

    public void deleteByUserName(String userName) {

        logger.info("Deleting the user by username");
        if (userRepository.findByUserName(userName).isEmpty()) {
            logger.error("Failed to delete the user by username");
            throw new RuntimeException("User not found with username: " + userName);
        }
        userRepository.deleteByUserName(userName);
    }

    public User createUser(String userName, String password, String role) {

        logger.info("User is creating...");

        try {
            User user = new User();
            user.setUserName(userName);
            user.setPassword(passwordEncoder.encode(password));
            user.setRoles(role);

            return userRepository.save(user);
        } catch (Exception e) {
            logger.error("Failed to create the user into Database");
            throw new RuntimeException(e);
        }

    }

    public User updateUser(User existUser, String newUserName, String newPassword) {

        existUser.setUserName(newUserName);
        existUser.setPassword(passwordEncoder.encode(newPassword));

        return create(existUser);

    }
}

