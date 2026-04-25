package com.example.medicinalplant.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 农药概览VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PesticideOverviewVO {
    
    private Integer totalCount; // 总数
    private List<Map<String, Object>> categoryDistribution; // 类别分布
    private List<Map<String, Object>> topPesticides; // 热门农药
    private List<Map<String, Object>> recentPesticides; // 最近添加的农药
    
} 