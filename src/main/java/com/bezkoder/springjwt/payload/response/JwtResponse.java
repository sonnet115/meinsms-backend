package com.bezkoder.springjwt.payload.response;

import com.bezkoder.springjwt.payload.request.LoginRequest;
import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Long id;
    private String username;
    private String name;
    private List<String> roles;

    public JwtResponse(String accessToken, Long id, String name, String username, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.username = username;
        this.roles = roles;
        this.name = name;
    }
}
