package com.example.medicinalplant.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PlantDiseaseLink {
    private Integer id;
    private Integer plantId;
    private Integer pdId;
    private String vulnerability;  // 高、中、低
    private String occurrenceSeason;
    private String affectedParts;
    private String notes;
    private Integer createdBy;
    private LocalDateTime createdAt;
    
    // 关联对象
    private MedicinalPlant plant;
    private PestDisease pestDisease;
    private ExpertUser creator;
} 