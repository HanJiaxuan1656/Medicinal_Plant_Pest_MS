package com.example.medicinalplant.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 病虫害实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PestDisease {
    
    private Integer id;
    private String name;
    private String type;  // 病害、虫害
    private String description;
    private String symptoms;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer viewCount;

    // 关联对象
} 