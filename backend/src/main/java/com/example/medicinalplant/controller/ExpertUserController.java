package com.example.medicinalplant.controller;

import com.example.medicinalplant.common.Result;
import com.example.medicinalplant.entity.ExpertUser;
import com.example.medicinalplant.service.ExpertUserService;
import com.example.medicinalplant.util.UserIdThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 专家用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/expert")
public class ExpertUserController {
    
    @Autowired
    private ExpertUserService expertUserService;
    
    /**
     * 获取当前专家用户信息
     */
    @GetMapping("/profile")
    public Result<ExpertUser> getProfile() {
        try {
            Integer userId = UserIdThreadLocal.get();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            ExpertUser expertUser = expertUserService.getById(userId);
            if (expertUser == null) {
                return Result.error("用户不存在");
            }
            
            return Result.success(expertUser);
        } catch (Exception e) {
            log.error("获取专家用户信息失败", e);
            return Result.error("获取用户信息失败");
        }
    }
    
    /**
     * 更新专家用户个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestBody ExpertUser expertUser) {
        try {
            Integer userId = UserIdThreadLocal.get();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            // 设置用户ID，确保只能更新自己的信息
            expertUser.setId(userId);
            
            boolean success = expertUserService.updateProfile(expertUser);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新专家用户信息失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody Map<String, String> passwordData) {
        try {
            Integer userId = UserIdThreadLocal.get();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");
            
            if (oldPassword == null || newPassword == null) {
                return Result.error("密码不能为空");
            }
            
            if (newPassword.length() < 6) {
                return Result.error("新密码长度至少6位");
            }
            
            boolean success = expertUserService.updatePassword(userId, oldPassword, newPassword);
            if (success) {
                return Result.success("密码修改成功");
            } else {
                return Result.error("密码修改失败，请检查旧密码是否正确");
            }
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return Result.error("修改密码失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取专家用户统计信息
     */
    @GetMapping("/stats")
    public Result<ExpertUserService.ExpertUserStats> getStats() {
        try {
            Integer userId = UserIdThreadLocal.get();
            if (userId == null) {
                return Result.error("用户未登录");
            }
            
            ExpertUserService.ExpertUserStats stats = expertUserService.getExpertStats(userId);
            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取专家用户统计信息失败", e);
            return Result.error("获取统计信息失败");
        }
    }
}
