package com.example.medicinalplant.mapper;

import com.example.medicinalplant.entity.PestDisease;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 病虫害Mapper接口
 */
@Mapper
public interface PestDiseaseMapper {
    
    /**
     * 查询所有病虫害
     */
    List<PestDisease> findAll();
    
    /**
     * 根据ID查询病虫害
     */
    PestDisease findById(@Param("id") Integer id);
    
    /**
     * 根据类型查询病虫害
     */
    List<PestDisease> findByType(@Param("type") String type);
    
    /**
     * 根据名称查询病虫害
     */
    List<PestDisease> findByName(@Param("name") String name);
    
    /**
     * 插入病虫害
     */
    void insert(PestDisease pestDisease);
    
    /**
     * 更新病虫害
     */
    void update(PestDisease pestDisease);
    
    /**
     * 根据ID删除病虫害
     */
    void deleteById(@Param("id") Integer id);

    /**
     * 批量删除病虫害
     */
    void deleteByIds(@Param("ids") List<Integer> ids);

    /**
     * 统计病虫害总数
     */
    int count();
    
    /**
     * 根据类型统计病虫害数量
     */
    int countByType(@Param("type") String type);
    
    /**
     * 查询最近添加的病虫害
     */
    List<PestDisease> findRecentPestDiseases();

    /**
     * 统计指定专家创建的病虫害数量
     */
    int countByCreatedBy(@Param("createdBy") Integer createdBy);

    List<PestDisease> findByConditions(@Param("search") String search,
                                     @Param("type") String type,
                                     @Param("offset") Integer offset,
                                     @Param("pageSize") Integer pageSize);
    
    int countByConditions(@Param("search") String search,
                         @Param("type") String type);

    /**
     * 增加查看次数
     */
    int incrementViewCount(@Param("id") Integer id);
} 