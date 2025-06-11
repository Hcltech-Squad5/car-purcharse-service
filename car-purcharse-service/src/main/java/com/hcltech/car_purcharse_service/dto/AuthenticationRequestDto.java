package com.hcltech.car_purcharse_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with all arguments
public class AuthenticationRequestDto {

    private String username;
    private String password;
    private String roles;

//    public String getUsername() {
//        return username;
//    }
//
//    public void setUsername(final String username) {
//        this.username = username;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(final String password) {
//        this.password = password;
//    }
//
//    public String getRoles() {
//        return roles;
//    }
//
//    public void setRoles(final String roles) {
//        this.roles = roles;
//    }
}
