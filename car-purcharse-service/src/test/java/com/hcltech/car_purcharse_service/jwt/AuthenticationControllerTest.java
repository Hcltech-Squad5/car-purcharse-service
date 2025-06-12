package com.hcltech.car_purcharse_service.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.car_purcharse_service.config.SecurityConfig;
import com.hcltech.car_purcharse_service.dto.AuthenticationRequestDto;
import com.hcltech.car_purcharse_service.dto.AuthenticationResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the AuthenticationController class.
 * These tests focus on verifying the behavior of the AuthenticationController
 * in isolation, by using MockMvc and mocking its service layer dependency.
 *
 * Updated to reflect the new AuthenticationRequestDto structure (including 'roles').
 */
@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc
@WithMockUser(username = "testuser", roles = "USER")
@Import({JwtUtil.class, JwtFilter.class, SecurityConfig.class})
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to perform HTTP requests to the controller

    @Autowired
    private ObjectMapper objectMapper; // Used to convert Java objects to/from JSON

    @MockitoBean // Creates a Mockito mock of AuthenticationService and adds it to the Spring context
    private AuthenticationService authenticationService;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Test
    void login_SuccessfulAuthentication_ReturnsOkAndJwt() throws Exception {
        // Given: An AuthenticationRequestDto with valid credentials (including roles)
        AuthenticationRequestDto requestDto = new AuthenticationRequestDto("testuser", "password", "USER");
        // And: The mocked authenticationService returns a successful response
        AuthenticationResponseDto responseDto = new AuthenticationResponseDto("mocked_jwt_token");

        when(authenticationService.login(any(AuthenticationRequestDto.class)))
                .thenReturn(responseDto);

        // When & Then: Perform a POST request to /v1/api/auth/login and assert the response
        mockMvc.perform(post("/v1/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))) // Convert DTO to JSON
                .andExpect(status().isOk()) // Expect HTTP 200 OK
                .andExpect(jsonPath("$.jwt").value("mocked_jwt_token")); // Expect the JWT in the response body
    }


}