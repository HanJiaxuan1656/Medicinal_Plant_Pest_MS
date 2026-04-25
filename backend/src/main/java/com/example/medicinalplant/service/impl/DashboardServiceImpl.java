package com.example.medicinalplant.service.impl;

import com.example.medicinalplant.mapper.CommentMapper;
import com.example.medicinalplant.mapper.HelpRequestMapper;
import com.example.medicinalplant.mapper.MedicinalPlantMapper;
import com.example.medicinalplant.mapper.PestDiseaseMapper;
import com.example.medicinalplant.mapper.PesticideMapper;
import com.example.medicinalplant.mapper.UserMapper;
import com.example.medicinalplant.service.DashboardService;
import com.example.medicinalplant.vo.DashboardDataVO;
import com.example.medicinalplant.vo.PestDiseaseOverviewVO;
import com.example.medicinalplant.vo.PesticideOverviewVO;
import com.example.medicinalplant.vo.PlantOverviewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务实现类
 */
@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private MedicinalPlantMapper plantMapper;
    
    @Autowired
    private PestDiseaseMapper pestDiseaseMapper;
    
    @Autowired
    private PesticideMapper pesticideMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private HelpRequestMapper helpRequestMapper;
    
    @Override
    public DashboardDataVO getDashboardData() {
        log.info("获取仪表盘数据");
        
        DashboardDataVO dashboardData = new DashboardDataVO();
        dashboardData.setPlantCount(plantMapper.count());
        dashboardData.setPestDiseaseCount(pestDiseaseMapper.count());
        dashboardData.setPesticideCount(pesticideMapper.count());
        dashboardData.setExpertUserCount(userMapper.countExpertUsers());
        dashboardData.setNormalUserCount(userMapper.countNormalUsers());
        dashboardData.setPendingCommentCount(commentMapper.countByStatus("pending"));
        dashboardData.setPendingHelpRequestCount(helpRequestMapper.countByStatus("pending"));
        
        return dashboardData;
    }
    
    @Override
    public PlantOverviewVO getPlantsOverview() {
        log.info("获取药用植物概览数据");
        
        PlantOverviewVO overview = new PlantOverviewVO();
        overview.setTotalCount(plantMapper.count());
        overview.setRecentAddedCount(plantMapper.countRecentAdded());
        
        // 获取热门药用植物
        List<Map<String, Object>> topPlants = new ArrayList<>();
        plantMapper.findTopPlants().forEach(plant -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", plant.getId());
            map.put("name", plant.getName());
            map.put("imageUrl", plant.getImageUrl());
            map.put("viewCount", 100); // 假设有一个浏览量字段
            topPlants.add(map);
        });
        overview.setTopPlants(topPlants);
        
        // 获取最近添加的药用植物
        List<Map<String, Object>> recentPlants = new ArrayList<>();
        plantMapper.findRecentPlants().forEach(plant -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", plant.getId());
            map.put("name", plant.getName());
            map.put("imageUrl", plant.getImageUrl());
            map.put("createdAt", plant.getCreatedAt());
            recentPlants.add(map);
        });
        overview.setRecentPlants(recentPlants);
        
        return overview;
    }
    
    @Override
    public PestDiseaseOverviewVO getPestDiseasesOverview() {
        log.info("获取病虫害概览数据");
        
        PestDiseaseOverviewVO overview = new PestDiseaseOverviewVO();
        overview.setTotalCount(pestDiseaseMapper.count());
        overview.setDiseaseCount(pestDiseaseMapper.countByType("病害"));
        overview.setPestCount(pestDiseaseMapper.countByType("虫害"));
        
        // 获取类型分布
        List<Map<String, Object>> typeDistribution = new ArrayList<>();
        Map<String, Object> disease = new HashMap<>();
        disease.put("type", "病害");
        disease.put("count", overview.getDiseaseCount());
        typeDistribution.add(disease);
        
        Map<String, Object> pest = new HashMap<>();
        pest.put("type", "虫害");
        pest.put("count", overview.getPestCount());
        typeDistribution.add(pest);
        
        overview.setTypeDistribution(typeDistribution);
        
        // 获取最近添加的病虫害
        List<Map<String, Object>> recentPestDiseases = new ArrayList<>();
        pestDiseaseMapper.findRecentPestDiseases().forEach(pestDisease -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", pestDisease.getId());
            map.put("name", pestDisease.getName());
            map.put("type", pestDisease.getType());
            map.put("imageUrl", pestDisease.getImageUrl());
            map.put("createdAt", pestDisease.getCreatedAt());
            recentPestDiseases.add(map);
        });
        overview.setRecentPestDiseases(recentPestDiseases);
        
        return overview;
    }
    
    @Override
    public PesticideOverviewVO getPesticidesOverview() {
        log.info("获取农药概览数据");
        
        PesticideOverviewVO overview = new PesticideOverviewVO();
        overview.setTotalCount(pesticideMapper.count());
        
        // 获取类别分布
        List<Map<String, Object>> categoryDistribution = new ArrayList<>();
        pesticideMapper.findCategoryDistribution().forEach(category -> {
            Map<String, Object> map = new HashMap<>();
            map.put("category", category.getCategory());
            map.put("count", category.getCount());
            categoryDistribution.add(map);
        });
        overview.setCategoryDistribution(categoryDistribution);
        
        // 获取热门农药
        List<Map<String, Object>> topPesticides = new ArrayList<>();
        pesticideMapper.findTopPesticides().forEach(pesticide -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", pesticide.getId());
            map.put("name", pesticide.getName());
            map.put("imageUrl", pesticide.getImageUrl());
            map.put("viewCount", 100); // 假设有一个浏览量字段
            topPesticides.add(map);
        });
        overview.setTopPesticides(topPesticides);
        
        // 获取最近添加的农药
        List<Map<String, Object>> recentPesticides = new ArrayList<>();
        pesticideMapper.findRecentPesticides().forEach(pesticide -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", pesticide.getId());
            map.put("name", pesticide.getName());
            map.put("category", pesticide.getCategory());
            map.put("imageUrl", pesticide.getImageUrl());
            map.put("createdAt", pesticide.getCreatedAt());
            recentPesticides.add(map);
        });
        overview.setRecentPesticides(recentPesticides);
        
        return overview;
    }
} 