package com.hcltech.car_purcharse_service.jwt;

import com.hcltech.car_purcharse_service.model.User;
import com.hcltech.car_purcharse_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    //    private PasswordEncoder passwordEncoder;
    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUserName(username).orElseThrow(() -> new UsernameNotFoundException("The user is not found"));

        return getUserDetails(user);
    }

    private UserDetails getUserDetails(User user) {

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();

        return userDetails;
    }
}
