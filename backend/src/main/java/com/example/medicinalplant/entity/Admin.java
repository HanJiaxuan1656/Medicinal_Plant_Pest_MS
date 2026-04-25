package com.example.medicinalplant.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Admin {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private String avatarUrl;
    private LocalDateTime createdAt;
    private String role;
}