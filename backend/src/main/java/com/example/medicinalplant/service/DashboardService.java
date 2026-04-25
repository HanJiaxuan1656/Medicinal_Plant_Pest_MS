package com.example.medicinalplant.service;

import com.example.medicinalplant.vo.DashboardDataVO;
import com.example.medicinalplant.vo.PlantOverviewVO;
import com.example.medicinalplant.vo.PestDiseaseOverviewVO;
import com.example.medicinalplant.vo.PesticideOverviewVO;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {
    
    /**
     * 获取仪表盘数据
     * 
     * @return 仪表盘数据
     */
    DashboardDataVO getDashboardData();
    
    /**
     * 获取药用植物概览数据
     * 
     * @return 药用植物概览数据
     */
    PlantOverviewVO getPlantsOverview();
    
    /**
     * 获取病虫害概览数据
     * 
     * @return 病虫害概览数据
     */
    PestDiseaseOverviewVO getPestDiseasesOverview();
    
    /**
     * 获取农药概览数据
     * 
     * @return 农药概览数据
     */
    PesticideOverviewVO getPesticidesOverview();
} 