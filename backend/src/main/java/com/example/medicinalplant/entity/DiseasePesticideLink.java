package com.example.medicinalplant.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DiseasePesticideLink {
    private Integer id;
    private Integer pdId;
    private Integer pesticideId;
    private String effectiveness;  // 高、中、低
    private String usageDosage;
    private String applicationMethod;
    private Integer safeIntervalDays;
    private String sideEffects;
    private String notes;
    private Integer createdBy;
    private LocalDateTime createdAt;
    
    // 关联对象
    private PestDisease pestDisease;
    private Pesticide pesticide;
    private ExpertUser creator;
} 