package com.hcltech.car_purcharse_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.car_purcharse_service.config.SecurityConfig;
import com.hcltech.car_purcharse_service.dao.service.UserDaoService;
import com.hcltech.car_purcharse_service.jwt.JwtFilter;
import com.hcltech.car_purcharse_service.jwt.JwtUtil;
import com.hcltech.car_purcharse_service.jwt.MyUserDetailsService;
import com.hcltech.car_purcharse_service.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"}) // Assuming admin role for these endpoints
@Import({JwtUtil.class, JwtFilter.class, SecurityConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserDaoService userDaoService;

    @MockitoBean
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = new User(1, "testuser1", "encodedpass1", "USER");
        user2 = new User(2, "testadmin", "encodedpass2", "ADMIN");
    }

    @Test
    void create_Success() throws Exception {
        User newUser = new User(null, "newuser", "rawpass", "USER");
        User createdUser = new User(3, "newuser", "encodednewpass", "USER");

        when(userDaoService.create(any(User.class))).thenReturn(createdUser);

        mockMvc.perform(post("/v1/api/admins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.userName").value("newuser"))
                .andExpect(jsonPath("$.roles").value("USER"));

        verify(userDaoService, times(1)).create(any(User.class));
    }

    @Test
    void getAllUsers_Success() throws Exception {
        List<User> allUsers = Arrays.asList(user1, user2);
        when(userDaoService.getAll()).thenReturn(allUsers);

        mockMvc.perform(get("/v1/api/admins/all")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].userName").value("testuser1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].userName").value("testadmin"));

        verify(userDaoService, times(1)).getAll();
    }

    @Test
    void getUserById_Success() throws Exception {
        when(userDaoService.getByID(1)).thenReturn(user1);

        mockMvc.perform(get("/v1/api/admins/{id}", 1)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("testuser1"))
                .andExpect(jsonPath("$.roles").value("USER"));

        verify(userDaoService, times(1)).getByID(1);
    }

    @Test
    void getUserById_NotFound() throws Exception {
        when(userDaoService.getByID(99)).thenThrow(new RuntimeException("User not found"));

        mockMvc.perform(get("/v1/api/admins/{id}", 99)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError()); // Or appropriate error status

        verify(userDaoService, times(1)).getByID(99);
    }

    @Test
    void getUserByUserName_Success() throws Exception {
        when(userDaoService.getByUserName("testuser1")).thenReturn(user1);

        mockMvc.perform(get("/v1/api/admins/username/{userName}", "testuser1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("testuser1"))
                .andExpect(jsonPath("$.roles").value("USER"));

        verify(userDaoService, times(1)).getByUserName("testuser1");
    }

    @Test
    void getUserByRole_Success() throws Exception {
        when(userDaoService.getByRole("USER")).thenReturn(user1); // Assuming it returns one user for simplicity

        mockMvc.perform(get("/v1/api/admins/role/{Role}", "USER")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.userName").value("testuser1"))
                .andExpect(jsonPath("$.roles").value("USER"));

        verify(userDaoService, times(1)).getByRole("USER");
    }

    @Test
    void updatePasswordById_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setPassword("newEncodedPassword");

        doNothing().when(userDaoService).updatePasswordById(eq(1), eq("newPassword"));

        mockMvc.perform(put("/v1/api/admins/password/id/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new User(null, null, "newPassword", null))) // Only pass the new password
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("password updated successfully for user id" + 1));

        verify(userDaoService, times(1)).updatePasswordById(eq(1), eq("newPassword"));
    }

    @Test
    void updatePasswordByUsername_Success() throws Exception {
        User updatedUser = new User();
        updatedUser.setPassword("newEncodedPassword");

        doNothing().when(userDaoService).updatePasswordUsername(eq("testuser1"), eq("newPassword"));

        mockMvc.perform(put("/v1/api/admins/password/username/{userName}", "testuser1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new User(null, null, "newPassword", null)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("password updated successfully for user username" + "testuser1"));

        verify(userDaoService, times(1)).updatePasswordUsername(eq("testuser1"), eq("newPassword"));
    }

    @Test
    void deleteById_Success() throws Exception {
        doNothing().when(userDaoService).deleteById(1);

        mockMvc.perform(delete("/v1/api/admins/id/{id}", 1)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userDaoService, times(1)).deleteById(1);
    }

    @Test
    void deleteByUserName_Success() throws Exception {
        doNothing().when(userDaoService).deleteByUserName("testuser1");

        mockMvc.perform(delete("/v1/api/admins/username/{userName}", "testuser1")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userDaoService, times(1)).deleteByUserName("testuser1");
    }
}