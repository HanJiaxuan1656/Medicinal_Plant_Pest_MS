package com.example.medicinalplant.controller;

import com.example.medicinalplant.common.Result;
import com.example.medicinalplant.entity.MedicinalPlant;
import com.example.medicinalplant.service.PlantService;
import com.example.medicinalplant.util.UserIdThreadLocal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {
    
    @Autowired
    private PlantService plantService;
    
    /**
     * 获取所有药用植物列表
     */
    @GetMapping
    public Result<List<MedicinalPlant>> getAllPlants() {
        return Result.success(plantService.getAllPlants());
    }
    
    /**
     * 根据ID获取药用植物详情
     */
    @GetMapping("/{id}")
    public Result<MedicinalPlant> getPlantById(@PathVariable Integer id) {
        MedicinalPlant plant = plantService.getPlantById(id);
        if (plant != null) {
            return Result.success(plant);
        }
        return Result.error("未找到该药用植物");
    }
    
    /**
     * 添加药用植物
     */
    @PostMapping
    public Result<MedicinalPlant> addPlant(@RequestBody MedicinalPlant plant) {
        try {
            System.out.println(plant);
            plant.setCreatedBy(UserIdThreadLocal.get());
            plantService.addPlant(plant);
            return Result.success(plant);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新药用植物信息
     */
    @PutMapping("/{id}")
    public Result<MedicinalPlant> updatePlant(@PathVariable Integer id, @RequestBody MedicinalPlant plant) {
        try {
            plant.setId(id);
            plantService.updatePlant(plant);
            return Result.success(plant);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除药用植物
     */
    @DeleteMapping("/{id}")
    public Result<String> deletePlant(@PathVariable Integer id) {
        try {
            plantService.deletePlant(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量删除药用植物
     */
    @DeleteMapping("/batch")
    public Result<String> deletePlants(@RequestBody List<Integer> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的药用植物");
            }
            plantService.deletePlants(ids);
            return Result.success("批量删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传药用植物图片
     */
    @PostMapping("/upload/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = plantService.uploadImage(file);
            return Result.success(imageUrl);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 搜索药用植物
     */
    @GetMapping("/search")
    public Result<List<MedicinalPlant>> searchPlants(@RequestParam String keyword) {
        return Result.success(plantService.searchPlants(keyword));
    }
} 