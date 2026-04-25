package com.example.medicinalplant.service.impl;

import com.example.medicinalplant.entity.DiseasePesticideLink;
import com.example.medicinalplant.mapper.DiseasePesticideLinkMapper;
import com.example.medicinalplant.service.DiseasePesticideLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 病虫害和农药关联服务实现类
 */
@Service
public class DiseasePesticideLinkServiceImpl implements DiseasePesticideLinkService {

    @Autowired
    private DiseasePesticideLinkMapper diseasePesticideLinkMapper;

    @Override
    public List<DiseasePesticideLink> getPage(String diseaseName, String pesticideName, String effectiveness, 
                                             int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return diseasePesticideLinkMapper.selectPage(diseaseName, pesticideName, effectiveness, offset, pageSize);
    }

    @Override
    public int getCount(String diseaseName, String pesticideName, String effectiveness) {
        return diseasePesticideLinkMapper.selectCount(diseaseName, pesticideName, effectiveness);
    }

    @Override
    public boolean add(DiseasePesticideLink link) {
        // 检查是否已存在相同的关联关系
        int exists = diseasePesticideLinkMapper.checkExists(link.getPdId(), link.getPesticideId());
        if (exists > 0) {
            throw new RuntimeException("该病虫害和农药的关联关系已存在");
        }
        return diseasePesticideLinkMapper.insert(link) > 0;
    }

    @Override
    public boolean update(DiseasePesticideLink link) {
        return diseasePesticideLinkMapper.update(link) > 0;
    }

    @Override
    public boolean delete(Integer id) {
        return diseasePesticideLinkMapper.delete(id) > 0;
    }

    @Override
    public boolean batchDelete(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return diseasePesticideLinkMapper.deleteByIds(ids) > 0;
    }

    @Override
    public DiseasePesticideLink getById(Integer id) {
        return diseasePesticideLinkMapper.selectById(id);
    }

    @Override
    public List<DiseasePesticideLink> getAll() {
        return diseasePesticideLinkMapper.findAll();
    }

    @Override
    public List<DiseasePesticideLink> getByPestDiseaseId(Integer pdId) {
        return diseasePesticideLinkMapper.findByPestDiseaseId(pdId);
    }

    @Override
    public List<DiseasePesticideLink> getByPesticideId(Integer pesticideId) {
        return diseasePesticideLinkMapper.findByPesticideId(pesticideId);
    }

    @Override
    public List<DiseasePesticideLink> getByEffectiveness(String effectiveness) {
        return diseasePesticideLinkMapper.findByEffectiveness(effectiveness);
    }

    @Override
    public List<DiseasePesticideLink> getByApplicationMethod(String applicationMethod) {
        return diseasePesticideLinkMapper.findByApplicationMethod(applicationMethod);
    }

    @Override
    public int getTotalCount() {
        return diseasePesticideLinkMapper.count();
    }

    @Override
    public List<DiseasePesticideLinkMapper.EffectivenessCount> getEffectivenessDistribution() {
        return diseasePesticideLinkMapper.findEffectivenessDistribution();
    }

    @Override
    public boolean exists(Integer pdId, Integer pesticideId) {
        return diseasePesticideLinkMapper.checkExists(pdId, pesticideId) > 0;
    }
}
