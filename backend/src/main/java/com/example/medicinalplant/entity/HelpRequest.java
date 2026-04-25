package com.example.medicinalplant.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 求助实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelpRequest {
    
    private Integer id;
    private Integer userId;
    private String title;
    private String description;
    private String imageUrl;
    private String videoUrl;
    private String status;  // pending, approved, rejected
    private Integer reviewAdminId;
    private LocalDateTime createdAt;
    
    // 关联对象
    private NormalUser user;
    private Admin reviewAdmin;
    private List<HelpReply> replies;
} 