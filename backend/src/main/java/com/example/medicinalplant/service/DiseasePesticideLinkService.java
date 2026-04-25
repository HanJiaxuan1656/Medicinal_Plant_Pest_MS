package com.example.medicinalplant.service;

import com.example.medicinalplant.entity.DiseasePesticideLink;
import com.example.medicinalplant.mapper.DiseasePesticideLinkMapper;

import java.util.List;

/**
 * 病虫害和农药关联服务接口
 */
public interface DiseasePesticideLinkService {

    /**
     * 分页查询关联关系
     */
    List<DiseasePesticideLink> getPage(String diseaseName, String pesticideName, String effectiveness,
                                      int page, int pageSize);

    /**
     * 统计查询结果数量
     */
    int getCount(String diseaseName, String pesticideName, String effectiveness);

    /**
     * 添加关联关系
     */
    boolean add(DiseasePesticideLink link);

    /**
     * 更新关联关系
     */
    boolean update(DiseasePesticideLink link);

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
    DiseasePesticideLink getById(Integer id);

    /**
     * 查询所有关联关系
     */
    List<DiseasePesticideLink> getAll();

    /**
     * 根据病虫害ID查询关联关系
     */
    List<DiseasePesticideLink> getByPestDiseaseId(Integer pdId);

    /**
     * 根据农药ID查询关联关系
     */
    List<DiseasePesticideLink> getByPesticideId(Integer pesticideId);

    /**
     * 根据效果查询关联关系
     */
    List<DiseasePesticideLink> getByEffectiveness(String effectiveness);

    /**
     * 根据施用方法查询关联关系
     */
    List<DiseasePesticideLink> getByApplicationMethod(String applicationMethod);

    /**
     * 统计总数
     */
    int getTotalCount();

    /**
     * 查询效果分布统计
     */
    List<DiseasePesticideLinkMapper.EffectivenessCount> getEffectivenessDistribution();

    /**
     * 检查关联关系是否存在
     */
    boolean exists(Integer pdId, Integer pesticideId);
}
