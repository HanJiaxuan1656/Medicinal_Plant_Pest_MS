package com.example.medicinalplant.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 求助回复实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelpReply {
    
    private Integer id;
    private Integer helpId;
    private Integer expertId;
    private String content;
    private LocalDateTime createdAt;
    
    // 关联对象
    private HelpRequest helpRequest;
    private ExpertUser expert;
    
} 