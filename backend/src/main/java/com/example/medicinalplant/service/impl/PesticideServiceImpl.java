package com.example.medicinalplant.service.impl;

import com.example.medicinalplant.entity.Pesticide;
import com.example.medicinalplant.mapper.PesticideMapper;
import com.example.medicinalplant.service.PesticideService;
import com.example.medicinalplant.vo.PageVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 农药服务实现类
 */
@Service
public class PesticideServiceImpl implements PesticideService {
    
    @Autowired
    private PesticideMapper pesticideMapper;
    
    @Override
    public PageVO<Pesticide> getPage(String search, String category, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<Pesticide> list = pesticideMapper.selectPage(search, category, offset, pageSize);
        int total = pesticideMapper.selectCount(search, category);
        return new PageVO<>(list, total);
    }
    
    @Override
    public Pesticide getById(Integer id) {
        return pesticideMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public boolean add(Pesticide pesticide) {
        return pesticideMapper.insert(pesticide) > 0;
    }
    
    @Override
    @Transactional
    public boolean update(Pesticide pesticide) {
        return pesticideMapper.update(pesticide) > 0;
    }
    
    @Override
    @Transactional
    public boolean delete(Integer id) {
        return pesticideMapper.delete(id) > 0;
    }

    @Override
    @Transactional
    public boolean deleteByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return false;
        }
        return pesticideMapper.deleteByIds(ids) > 0;
    }

    @Override
    public List<Pesticide> getAllPesticides() {
        return pesticideMapper.selectAll();
    }
    
    @Override
    public List<Pesticide> getPesticidesByCategory(String category) {
        return pesticideMapper.selectByCategory(category);
    }
    
    @Override
    public List<Pesticide> searchPesticides(String keyword) {
        return pesticideMapper.selectByKeyword(keyword);
    }
    
    @Override
    public String uploadImage(MultipartFile file) {
        // TODO: 实现图片上传功能
        return null;
    }
    
    @Override
    public String uploadManual(MultipartFile file) {
        // TODO: 实现说明书上传功能
        return null;
    }

    @Override
    public void incrementViewCount(Integer id) {
        pesticideMapper.incrementViewCount(id);
    }
} 