package com.example.medicinalplant.mapper;

import com.example.medicinalplant.entity.MedicinalPlant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 药用植物Mapper接口
 */
@Mapper
public interface MedicinalPlantMapper {
    
    /**
     * 查询所有药用植物
     */
    List<MedicinalPlant> findAll();
    
    /**
     * 根据ID查询药用植物
     */
    MedicinalPlant findById(@Param("id") Integer id);
    
    /**
     * 根据名称查询药用植物
     */
    List<MedicinalPlant> findByName(@Param("name") String name);
    
    /**
     * 插入药用植物
     */
    int insert(MedicinalPlant plant);
    
    /**
     * 更新药用植物
     */
    int update(MedicinalPlant plant);
    
    /**
     * 根据ID删除药用植物
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 批量删除药用植物
     */
    int deleteByIds(@Param("ids") List<Integer> ids);

    /**
     * 统计药用植物总数
     */
    int count();
    
    /**
     * 统计最近添加的药用植物数量
     */
    int countRecentAdded();
    
    /**
     * 查询热门药用植物
     */
    List<MedicinalPlant> findTopPlants();
    
    /**
     * 查询最近添加的药用植物
     */
    List<MedicinalPlant> findRecentPlants();

    /**
     * 统计指定专家创建的植物数量
     */
    int countByCreatedBy(@Param("createdBy") Integer createdBy);

    /**
     * 增加查看次数
     */
    int incrementViewCount(@Param("id") Integer id);
} 