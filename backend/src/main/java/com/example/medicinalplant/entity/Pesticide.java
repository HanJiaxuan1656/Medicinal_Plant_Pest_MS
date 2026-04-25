package com.example.medicinalplant.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 农药实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pesticide {
    
    private Integer id;
    private String name;
    private String category;
    private String activeIngredient;
    private String usageInstructions;
    private String imageUrl;
    private String manualUrl;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    private Integer createdBy;
    private Integer viewCount;

    // 关联对象
    private ExpertUser creator;
    
} 