package com.hcltech.car_purcharse_service.jwt;

import com.hcltech.car_purcharse_service.dto.AuthenticationRequestDto;
import com.hcltech.car_purcharse_service.dto.AuthenticationResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

//    @PostMapping("/register")
//    public AuthenticationRequestDto register(@RequestBody AuthenticationRequestDto authenticationRequestDto) {
//        return authenticationService.register(authenticationRequestDto);
//    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> login(
            @RequestBody AuthenticationRequestDto authenticationRequestDto) throws Exception {
        final AuthenticationResponseDto response = authenticationService.login(authenticationRequestDto);
        return ResponseEntity.ok(response);
    }
//
//    @PostMapping("/logout")
//    public ResponseEntity<AuthenticationResponseDto> logout(
//            @RequestBody AuthenticationRequestDto authenticationRequestDto) throws Exception {
//        throw new UnsupportedOperationException("Not implemented yet.");
//    }
}
