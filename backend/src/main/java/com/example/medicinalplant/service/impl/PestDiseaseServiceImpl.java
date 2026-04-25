package com.example.medicinalplant.service.impl;

import com.example.medicinalplant.common.PageResult;
import com.example.medicinalplant.entity.PestDisease;
import com.example.medicinalplant.mapper.PestDiseaseMapper;
import com.example.medicinalplant.service.PestDiseaseService;
import com.example.medicinalplant.util.UserIdThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 病虫害服务实现类
 */
@Slf4j
@Service
public class PestDiseaseServiceImpl implements PestDiseaseService {
    
    @Autowired
    private PestDiseaseMapper pestDiseaseMapper;
    
    @Override
    public List<PestDisease> getAllPestDiseases() {
        log.info("获取所有病虫害");
        return pestDiseaseMapper.findAll();
    }
    
    @Override
    public PestDisease getPestDiseaseById(Integer id) {
        log.info("获取病虫害ID: {}", id);
        return pestDiseaseMapper.findById(id);
    }
    
    @Override
    public void addPestDisease(PestDisease pestDisease) {
        log.info("添加病虫害: {}", pestDisease);
        pestDisease.setCreatedAt(LocalDateTime.now());
        pestDisease.setUpdatedAt(LocalDateTime.now());
        pestDisease.setCreatedBy(UserIdThreadLocal.get());
        pestDiseaseMapper.insert(pestDisease);
    }
    
    @Override
    public void updatePestDisease(PestDisease pestDisease) {
        log.info("更新病虫害: {}", pestDisease);
        pestDisease.setUpdatedAt(LocalDateTime.now());
        pestDiseaseMapper.update(pestDisease);
    }
    
    @Override
    public void deletePestDisease(Integer id) {
        log.info("删除病虫害ID: {}", id);
        pestDiseaseMapper.deleteById(id);
    }

    @Override
    public void deletePestDiseases(List<Integer> ids) {
        log.info("批量删除病虫害IDs: {}", ids);
        if (ids != null && !ids.isEmpty()) {
            pestDiseaseMapper.deleteByIds(ids);
        }
    }

    @Override
    public List<PestDisease> getPestDiseasesByType(String type) {
        log.info("根据类型获取病虫害，类型: {}", type);
        return pestDiseaseMapper.findByType(type);
    }
    
    @Override
    public List<PestDisease> searchPestDiseases(String keyword) {
        log.info("搜索病虫害，关键词: {}", keyword);
        return pestDiseaseMapper.findByName(keyword);
    }
    
    @Override
    public PageResult<PestDisease> findByConditions(String search, String type, int page, int pageSize) {
        log.info("条件查询病虫害 search: {}, type: {}, page: {}, pageSize: {}", search, type, page, pageSize);
        int offset = (page - 1) * pageSize;
        List<PestDisease> items = pestDiseaseMapper.findByConditions(search, type, offset, pageSize);
        int total = pestDiseaseMapper.countByConditions(search, type);
        return new PageResult<>(items, total);
    }
    
    @Override
    public String uploadImage(MultipartFile file) {
        // TODO: 实现文件上传逻辑
        return "http://example.com/images/" + file.getOriginalFilename();
    }

    @Override
    public PestDisease findById(Integer id) {
        log.info("根据ID查找病虫害: {}", id);
        return pestDiseaseMapper.findById(id);
    }

    @Override
    public PestDisease save(PestDisease pestDisease) {
        log.info("保存病虫害: {}", pestDisease);
        pestDisease.setCreatedAt(LocalDateTime.now());
        pestDisease.setUpdatedAt(LocalDateTime.now());
        pestDisease.setCreatedBy(UserIdThreadLocal.get());
        pestDiseaseMapper.insert(pestDisease);
        return pestDisease;
    }

    @Override
    public PestDisease update(PestDisease pestDisease) {
        log.info("更新病虫害: {}", pestDisease);
        pestDisease.setUpdatedAt(LocalDateTime.now());
        pestDiseaseMapper.update(pestDisease);
        return pestDisease;
    }

    @Override
    public void delete(Integer id) {
        log.info("删除病虫害: {}", id);
        pestDiseaseMapper.deleteById(id);
    }

    @Override
    public void incrementViewCount(Integer id) {
        log.info("增加病虫害查看次数，ID: {}", id);
        pestDiseaseMapper.incrementViewCount(id);
    }
} 