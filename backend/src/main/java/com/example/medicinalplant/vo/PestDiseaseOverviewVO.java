package com.example.medicinalplant.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 病虫害概览VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PestDiseaseOverviewVO {
    
    private Integer totalCount; // 总数
    private Integer diseaseCount; // 病害数量
    private Integer pestCount; // 虫害数量
    private List<Map<String, Object>> typeDistribution; // 类型分布
    private List<Map<String, Object>> recentPestDiseases; // 最近添加的病虫害
    
} 