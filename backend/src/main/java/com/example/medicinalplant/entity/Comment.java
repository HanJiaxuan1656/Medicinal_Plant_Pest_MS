package com.example.medicinalplant.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 评论实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    
    private Integer id;
    private Integer userId;
    private String userType;  // normal, expert
    private String targetType;  // plant, pest_disease, pesticide
    private Integer targetId;
    private String content;
    private String status;  // pending, approved, rejected
    private Integer reviewAdminId;
    private LocalDateTime createdAt;
    
    // 关联对象
    private Admin reviewAdmin;
    private Object user;  // 可能是NormalUser或ExpertUser
    private Object target;  // 可能是MedicinalPlant、PestDisease或Pesticide
} 