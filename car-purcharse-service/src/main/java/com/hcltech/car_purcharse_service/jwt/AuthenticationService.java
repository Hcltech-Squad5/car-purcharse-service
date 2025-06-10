package com.hcltech.car_purcharse_service.jwt;

import com.hcltech.car_purcharse_service.dto.AuthenticationRequestDto;
import com.hcltech.car_purcharse_service.dto.AuthenticationResponseDto;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private MyUserDetailsService myUserDetailsServic;
    private JwtUtil jwtUtil;
    private AuthenticationManager authenticationManager;

    public AuthenticationService(MyUserDetailsService myUserDetailsService, JwtUtil jwtUtil, AuthenticationManager authenticationManager) {
        this.myUserDetailsServic = myUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponseDto login(AuthenticationRequestDto authenticationRequestDto) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequestDto.getUsername(),
                        authenticationRequestDto.getPassword()));
        if (authentication.isAuthenticated()) {
            final UserDetails userDetails = myUserDetailsServic.loadUserByUsername(
                    authenticationRequestDto.getUsername());
            final String jwt = jwtUtil.generateToken(userDetails);
            return new AuthenticationResponseDto(jwt);
        }
        throw new UsernameNotFoundException(authenticationRequestDto.getUsername() + " not found");
    }

    public AuthenticationResponseDto logout(AuthenticationRequestDto authenticationRequestDto) {
        return null;
    }


}
