package com.example.ecom.project.dto;

public class JwtResponse {
    private String token;
    private Long id;
    private String email;
    private String fullName;
    private String role;

    public JwtResponse(String token, Long id, String email, String fullName, String role) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters
    public String getToken() { return token; }
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
}
