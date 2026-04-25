package com.example.medicinalplant.service;

import com.example.medicinalplant.entity.ExpertUser;

/**
 * 专家用户服务接口
 */
public interface ExpertUserService {
    
    /**
     * 根据ID获取专家用户信息
     */
    ExpertUser getById(Integer id);
    
    /**
     * 更新专家用户个人信息
     */
    boolean updateProfile(ExpertUser expertUser);
    
    /**
     * 修改密码
     */
    boolean updatePassword(Integer id, String oldPassword, String newPassword);
    
    /**
     * 获取专家用户统计信息
     */
    ExpertUserStats getExpertStats(Integer expertId);
    
    /**
     * 专家用户统计信息内部类
     */
    class ExpertUserStats {
        private int plantsCreated;      // 创建的植物数量
        private int pestsCreated;       // 创建的病虫害数量
        private int pesticidesCreated;  // 创建的农药数量
        private int linksCreated;       // 创建的关系数量
        private int helpsReplied;       // 回复的求助数量
        
        public ExpertUserStats() {}
        
        public ExpertUserStats(int plantsCreated, int pestsCreated, int pesticidesCreated, 
                              int linksCreated, int helpsReplied) {
            this.plantsCreated = plantsCreated;
            this.pestsCreated = pestsCreated;
            this.pesticidesCreated = pesticidesCreated;
            this.linksCreated = linksCreated;
            this.helpsReplied = helpsReplied;
        }
        
        // Getters and Setters
        public int getPlantsCreated() { return plantsCreated; }
        public void setPlantsCreated(int plantsCreated) { this.plantsCreated = plantsCreated; }
        
        public int getPestsCreated() { return pestsCreated; }
        public void setPestsCreated(int pestsCreated) { this.pestsCreated = pestsCreated; }
        
        public int getPesticidesCreated() { return pesticidesCreated; }
        public void setPesticidesCreated(int pesticidesCreated) { this.pesticidesCreated = pesticidesCreated; }
        
        public int getLinksCreated() { return linksCreated; }
        public void setLinksCreated(int linksCreated) { this.linksCreated = linksCreated; }
        
        public int getHelpsReplied() { return helpsReplied; }
        public void setHelpsReplied(int helpsReplied) { this.helpsReplied = helpsReplied; }
    }
}
