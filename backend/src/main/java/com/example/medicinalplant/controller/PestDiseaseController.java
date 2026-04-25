package com.example.medicinalplant.controller;

import com.example.medicinalplant.common.Result;
import com.example.medicinalplant.common.PageResult;
import com.example.medicinalplant.entity.PestDisease;
import com.example.medicinalplant.service.PestDiseaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 病虫害控制器
 */
@RestController
@RequestMapping("/api/pest-diseases")
public class PestDiseaseController {
    
    @Autowired
    private PestDiseaseService pestDiseaseService;
    
    /**
     * 获取所有病虫害列表（分页）
     */
    @GetMapping
    public Result<PageResult<PestDisease>> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(pestDiseaseService.findByConditions(search, type, page, pageSize));
    }

    /**
     * 根据ID获取病虫害详情
     */
    @GetMapping("/{id}")
    public Result<PestDisease> getPestDiseaseById(@PathVariable Integer id) {
        PestDisease pestDisease = pestDiseaseService.getPestDiseaseById(id);
        if (pestDisease != null) {
            return Result.success(pestDisease);
        }
        return Result.error("未找到该病虫害");
    }

    /**
     * 获取所有病虫害列表（不分页，用于下拉选择）
     */
    @GetMapping("/list/all")
    public Result<List<PestDisease>> getAllPestDiseases() {
        return Result.success(pestDiseaseService.getAllPestDiseases());
    }
    
    /**
     * 添加病虫害
     */
    @PostMapping
    public Result<PestDisease> addPestDisease(@RequestBody PestDisease pestDisease) {
        try {
            pestDiseaseService.addPestDisease(pestDisease);
            return Result.success(pestDisease);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新病虫害信息
     */
    @PutMapping("/{id}")
    public Result<PestDisease> updatePestDisease(@PathVariable Integer id, @RequestBody PestDisease pestDisease) {
        try {
            pestDisease.setId(id);
            pestDiseaseService.updatePestDisease(pestDisease);
            return Result.success(pestDisease);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除病虫害
     */
    @DeleteMapping("/{id}")
    public Result<String> deletePestDisease(@PathVariable Integer id) {
        try {
            pestDiseaseService.deletePestDisease(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }

    /**
     * 批量删除病虫害
     */
    @DeleteMapping("/batch")
    public Result<String> deletePestDiseases(@RequestBody List<Integer> ids) {
        try {
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要删除的病虫害");
            }
            pestDiseaseService.deletePestDiseases(ids);
            return Result.success("批量删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 上传病虫害图片
     */
    @PostMapping("/upload/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = pestDiseaseService.uploadImage(file);
            return Result.success(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据类型获取病虫害列表
     */
    @GetMapping("/type/{type}")
    public Result<List<PestDisease>> getPestDiseasesByType(@PathVariable String type) {
        return Result.success(pestDiseaseService.getPestDiseasesByType(type));
    }
    
    /**
     * 搜索病虫害
     */
    @GetMapping("/search")
    public Result<List<PestDisease>> searchPestDiseases(@RequestParam String keyword) {
        return Result.success(pestDiseaseService.searchPestDiseases(keyword));
    }
} 