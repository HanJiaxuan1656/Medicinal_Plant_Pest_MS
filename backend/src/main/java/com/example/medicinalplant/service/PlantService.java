package com.example.medicinalplant.service;

import com.example.medicinalplant.entity.MedicinalPlant;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 药用植物服务接口
 */
public interface PlantService {
    
    /**
     * 获取所有药用植物
     * 
     * @return 药用植物列表
     */
    List<MedicinalPlant> getAllPlants();
    
    /**
     * 根据ID获取药用植物
     * 
     * @param id 药用植物ID
     * @return 药用植物对象
     */
    MedicinalPlant getPlantById(Integer id);
    
    /**
     * 添加药用植物
     * 
     * @param plant 药用植物对象
     */
    void addPlant(MedicinalPlant plant);
    
    /**
     * 更新药用植物
     * 
     * @param plant 药用植物对象
     */
    void updatePlant(MedicinalPlant plant);
    
    /**
     * 删除药用植物
     *
     * @param id 药用植物ID
     */
    void deletePlant(Integer id);

    /**
     * 批量删除药用植物
     *
     * @param ids 药用植物ID列表
     */
    void deletePlants(List<Integer> ids);

    /**
     * 上传药用植物图片
     *
     * @param file 图片文件
     * @return 图片URL
     */
    String uploadImage(MultipartFile file);
    
    /**
     * 搜索药用植物
     *
     * @param keyword 关键词
     * @return 药用植物列表
     */
    List<MedicinalPlant> searchPlants(String keyword);

    /**
     * 增加查看次数
     *
     * @param id 药用植物ID
     */
    void incrementViewCount(Integer id);
} 