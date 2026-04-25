package com.example.medicinalplant.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 药用植物概览VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlantOverviewVO {
    
    private Integer totalCount; // 总数
    private Integer recentAddedCount; // 最近添加数量
    private List<Map<String, Object>> topPlants; // 热门药用植物
    private List<Map<String, Object>> recentPlants; // 最近添加的药用植物
    
} 