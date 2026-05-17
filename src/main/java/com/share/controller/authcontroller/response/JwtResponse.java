package com.share.controller.authcontroller.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private String id;
    private String username;
    private String surname;
    private String email;
    private List<String> roles;
    private String phone;
    private byte[] profilePhoto = null;

    // Default constructor
    public JwtResponse() {}

    public JwtResponse(String accessToken, String id, String username, String surname, String email, List<String> roles, String phone) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.surname = surname;
        this.phone = phone;
    }
}
