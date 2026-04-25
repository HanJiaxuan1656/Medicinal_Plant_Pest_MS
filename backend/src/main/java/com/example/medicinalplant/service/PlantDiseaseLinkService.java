package com.example.medicinalplant.service;

import com.example.medicinalplant.entity.PlantDiseaseLink;
import com.example.medicinalplant.mapper.PlantDiseaseLinkMapper;

import java.util.List;

/**
 * 药用植物和病虫害关联服务接口
 */
public interface PlantDiseaseLinkService {

    /**
     * 分页查询关联关系
     */
    List<PlantDiseaseLink> getPage(String plantName, String diseaseName, String vulnerability,
                                  int page, int pageSize);

    /**
     * 统计查询结果数量
     */
    int getCount(String plantName, String diseaseName, String vulnerability);

    /**
     * 添加关联关系
     */
    boolean add(PlantDiseaseLink link);

    /**
     * 更新关联关系
     */
    boolean update(PlantDiseaseLink link);

    /**
     * 删除关联关系
     */
    boolean delete(Integer id);

    /**
     * 批量删除关联关系
     */
    boolean batchDelete(List<Integer> ids);

    /**
     * 根据ID查询关联关系
     */
    PlantDiseaseLink getById(Integer id);

    /**
     * 查询所有关联关系
     */
    List<PlantDiseaseLink> getAll();

    /**
     * 根据植物ID查询关联关系
     */
    List<PlantDiseaseLink> getByPlantId(Integer plantId);

    /**
     * 根据病虫害ID查询关联关系
     */
    List<PlantDiseaseLink> getByPestDiseaseId(Integer pdId);

    /**
     * 根据易感性查询关联关系
     */
    List<PlantDiseaseLink> getByVulnerability(String vulnerability);

    /**
     * 根据发生季节查询关联关系
     */
    List<PlantDiseaseLink> getByOccurrenceSeason(String occurrenceSeason);

    /**
     * 统计总数
     */
    int getTotalCount();

    /**
     * 查询易感性分布统计
     */
    List<PlantDiseaseLinkMapper.VulnerabilityCount> getVulnerabilityDistribution();

    /**
     * 检查关联关系是否存在
     */
    boolean exists(Integer plantId, Integer pdId);
}
