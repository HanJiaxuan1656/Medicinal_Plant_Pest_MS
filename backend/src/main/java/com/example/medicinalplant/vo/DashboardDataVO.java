package com.example.medicinalplant.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仪表盘数据VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDataVO {
    
    private Integer plantCount; // 药用植物总数
    private Integer pestDiseaseCount; // 病虫害总数
    private Integer pesticideCount; // 农药总数
    private Integer expertUserCount; // 专家用户数
    private Integer normalUserCount; // 普通用户数
    private Integer pendingCommentCount; // 待审核评论数
    private Integer pendingHelpRequestCount; // 待审核求助数
    
} 