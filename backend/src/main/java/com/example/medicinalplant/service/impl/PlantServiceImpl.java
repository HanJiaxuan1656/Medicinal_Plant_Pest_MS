package com.example.medicinalplant.service.impl;

import com.example.medicinalplant.entity.MedicinalPlant;
import com.example.medicinalplant.mapper.MedicinalPlantMapper;
import com.example.medicinalplant.service.PlantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 药用植物服务实现类
 */
@Slf4j
@Service
public class PlantServiceImpl implements PlantService {
    
    @Autowired
    private MedicinalPlantMapper plantMapper;
    
    @Override
    public List<MedicinalPlant> getAllPlants() {
        log.info("获取所有药用植物");
        return plantMapper.findAll();
    }
    
    @Override
    public MedicinalPlant getPlantById(Integer id) {
        log.info("获取药用植物ID: {}", id);
        return plantMapper.findById(id);
    }
    
    @Override
    public void addPlant(MedicinalPlant plant) {
        log.info("添加药用植物: {}", plant);
        plant.setCreatedAt(LocalDateTime.now());
        plant.setUpdatedAt(LocalDateTime.now());
        plantMapper.insert(plant);
    }
    
    @Override
    public void updatePlant(MedicinalPlant plant) {
        log.info("更新药用植物: {}", plant);
        plant.setUpdatedAt(LocalDateTime.now());
        plantMapper.update(plant);
    }
    
    @Override
    public void deletePlant(Integer id) {
        log.info("删除药用植物ID: {}", id);
        plantMapper.deleteById(id);
    }

    @Override
    public void deletePlants(List<Integer> ids) {
        log.info("批量删除药用植物IDs: {}", ids);
        if (ids != null && !ids.isEmpty()) {
            plantMapper.deleteByIds(ids);
        }
    }

    @Override
    public String uploadImage(MultipartFile file) {
        log.info("上传药用植物图片: {}", file.getOriginalFilename());
        // TODO: 实现文件上传逻辑
        return "http://example.com/images/" + file.getOriginalFilename();
    }
    
    @Override
    public List<MedicinalPlant> searchPlants(String keyword) {
        log.info("搜索药用植物，关键词: {}", keyword);
        return plantMapper.findByName(keyword);
    }

    @Override
    public void incrementViewCount(Integer id) {
        log.info("增加药用植物查看次数，ID: {}", id);
        plantMapper.incrementViewCount(id);
    }
} 