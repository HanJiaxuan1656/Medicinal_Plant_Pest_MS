package com.example.medicinalplant.service.impl;

import com.example.medicinalplant.entity.ExpertUser;
import com.example.medicinalplant.mapper.*;
import com.example.medicinalplant.service.ExpertUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 专家用户服务实现类
 */
@Slf4j
@Service
public class ExpertUserServiceImpl implements ExpertUserService {
    
    @Autowired
    private ExpertUserMapper expertUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MedicinalPlantMapper plantMapper;

    @Autowired
    private PestDiseaseMapper pestDiseaseMapper;

    @Autowired
    private PesticideMapper pesticideMapper;

    @Autowired
    private PlantDiseaseLinkMapper plantDiseaseLinkMapper;

    @Autowired
    private DiseasePesticideLinkMapper diseasePesticideLinkMapper;

    @Autowired
    private HelpRequestMapper helpRequestMapper;
    
    @Override
    public ExpertUser getById(Integer id) {
        log.info("获取专家用户信息，ID: {}", id);
        ExpertUser expertUser = expertUserMapper.findById(id);
        if (expertUser != null) {
            // 清除密码信息
            expertUser.setPassword(null);
        }
        return expertUser;
    }
    
    @Override
    @Transactional
    public boolean updateProfile(ExpertUser expertUser) {
        log.info("更新专家用户信息: {}", expertUser);
        try {
            int result = expertUserMapper.updateProfile(expertUser);
            return result > 0;
        } catch (Exception e) {
            log.error("更新专家用户信息失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean updatePassword(Integer id, String oldPassword, String newPassword) {
        log.info("修改专家用户密码，ID: {}", id);
        try {
            // 先获取用户信息验证旧密码
            ExpertUser expertUser = expertUserMapper.findById(id);
            if (expertUser == null) {
                log.error("用户不存在，ID: {}", id);
                return false;
            }
            
            // 验证旧密码
            if (!passwordEncoder.matches(oldPassword, expertUser.getPassword())) {
                log.error("旧密码不正确");
                return false;
            }
            
            // 加密新密码并更新
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            int result = expertUserMapper.updatePassword(id, encodedNewPassword);
            return result > 0;
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return false;
        }
    }
    
    @Override
    public ExpertUserStats getExpertStats(Integer expertId) {
        log.info("获取专家用户统计信息，ID: {}", expertId);

        // 统计该专家创建的植物数量
        int plantsCreated = plantMapper.countByCreatedBy(expertId);

        // 统计该专家创建的病虫害数量
        int pestsCreated = pestDiseaseMapper.countByCreatedBy(expertId);

        // 统计该专家创建的农药数量
        int pesticidesCreated = pesticideMapper.countByCreatedBy(expertId);

        // 统计该专家创建的关系数量（植物-病虫害关系 + 病虫害-农药关系）
        int plantDiseaseLinks = plantDiseaseLinkMapper.countByCreatedBy(expertId);
        int diseasePesticideLinks = diseasePesticideLinkMapper.countByCreatedBy(expertId);
        int linksCreated = plantDiseaseLinks + diseasePesticideLinks;

        // 统计该专家回复的求助数量
        int helpsReplied = helpRequestMapper.countRepliedByExpert(expertId);

        log.info("专家统计信息 - 植物: {}, 病虫害: {}, 农药: {}, 关系: {}, 求助回复: {}",
                plantsCreated, pestsCreated, pesticidesCreated, linksCreated, helpsReplied);

        return new ExpertUserStats(plantsCreated, pestsCreated, pesticidesCreated, linksCreated, helpsReplied);
    }
}
