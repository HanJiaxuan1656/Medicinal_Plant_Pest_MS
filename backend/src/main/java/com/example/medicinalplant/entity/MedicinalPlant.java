package com.example.medicinalplant.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 药用植物实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicinalPlant {

    private Integer id;
    private String name;
    private String alias;
    private String description;
    private String medicinalParts;  // 药用部位
    private String efficacy;        // 功效
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer createdBy;
    private Integer viewCount;

    // 关联对象
    private ExpertUser creator;
    
} 