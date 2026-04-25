package com.example.medicinalplant.service.impl;

import com.example.medicinalplant.entity.PlantDiseaseLink;
import com.example.medicinalplant.mapper.PlantDiseaseLinkMapper;
import com.example.medicinalplant.service.PlantDiseaseLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 药用植物和病虫害关联服务实现类
 */
@Service
public class PlantDiseaseLinkServiceImpl implements PlantDiseaseLinkService {

    @Autowired
    private PlantDiseaseLinkMapper plantDiseaseLinkMapper;

    @Override
    public List<PlantDiseaseLink> getPage(String plantName, String diseaseName, String vulnerability, 
                                         int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return plantDiseaseLinkMapper.selectPage(plantName, diseaseName, vulnerability, offset, pageSize);
    }

    @Override
    public int getCount(String plantName, String diseaseName, String vulnerability) {
        return plantDiseaseLinkMapper.selectCount(plantName, diseaseName, vulnerability);
    }

    @Override
    public boolean add(PlantDiseaseLink link) {
        // 检查是否已存在相同的关联关系
        int exists = plantDiseaseLinkMapper.checkExists(link.getPlantId(), link.getPdId());
        if (exists > 0) {
            throw new RuntimeException("该药用植物和病虫害的关联关系已存在");
        }
        return plantDiseaseLinkMapper.insert(link) > 0;
    }

    @Override
    public boolean update(PlantDiseaseLink link) {
        return plantDiseaseLinkMapper.update(link) > 0;
    }

    @Override
    public boolean delete(Integer id) {
        return plantDiseaseLinkMapper.delete(id) > 0;
    }

    @Override
    public boolean batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return plantDiseaseLinkMapper.deleteByIds(ids) > 0;
    }

    @Override
    public PlantDiseaseLink getById(Integer id) {
        return plantDiseaseLinkMapper.selectById(id);
    }

    @Override
    public List<PlantDiseaseLink> getAll() {
        return plantDiseaseLinkMapper.findAll();
    }

    @Override
    public List<PlantDiseaseLink> getByPlantId(Integer plantId) {
        return plantDiseaseLinkMapper.findByPlantId(plantId);
    }

    @Override
    public List<PlantDiseaseLink> getByPestDiseaseId(Integer pdId) {
        return plantDiseaseLinkMapper.findByPestDiseaseId(pdId);
    }

    @Override
    public List<PlantDiseaseLink> getByVulnerability(String vulnerability) {
        return plantDiseaseLinkMapper.findByVulnerability(vulnerability);
    }

    @Override
    public List<PlantDiseaseLink> getByOccurrenceSeason(String occurrenceSeason) {
        return plantDiseaseLinkMapper.findByOccurrenceSeason(occurrenceSeason);
    }

    @Override
    public int getTotalCount() {
        return plantDiseaseLinkMapper.count();
    }

    @Override
    public List<PlantDiseaseLinkMapper.VulnerabilityCount> getVulnerabilityDistribution() {
        return plantDiseaseLinkMapper.findVulnerabilityDistribution();
    }

    @Override
    public boolean exists(Integer plantId, Integer pdId) {
        return plantDiseaseLinkMapper.checkExists(plantId, pdId) > 0;
    }
}
