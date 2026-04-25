package com.example.medicinalplant.service;

import com.example.medicinalplant.entity.HelpRequest;
import com.example.medicinalplant.entity.HelpReply;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 求助服务接口
 */
public interface HelpRequestService {
    
    /**
     * 获取所有求助
     * 
     * @return 求助列表
     */
    List<HelpRequest> getAllHelpRequests();
    
    /**
     * 获取用户的求助列表
     * 
     * @param userId 用户ID
     * @return 求助列表
     */
    List<HelpRequest> getUserHelpRequests(Integer userId);
    
    /**
     * 根据ID获取求助
     * 
     * @param id 求助ID
     * @return 求助对象
     */
    HelpRequest getHelpRequestById(Integer id);
    
    /**
     * 添加求助
     * 
     * @param helpRequest 求助对象
     */
    void addHelpRequest(HelpRequest helpRequest);
    
    /**
     * 上传求助图片
     * 
     * @param file 图片文件
     * @return 图片URL
     */
    String uploadImage(MultipartFile file);
    
    /**
     * 审核求助
     * 
     * @param id 求助ID
     * @param status 状态
     * @param adminId 管理员ID
     */
    void reviewHelpRequest(Integer id, String status, Integer adminId);
    
    /**
     * 删除求助
     * 
     * @param id 求助ID
     */
    void deleteHelpRequest(Integer id);
    
    /**
     * 获取求助的所有回复
     * 
     * @param helpId 求助ID
     * @return 回复列表
     */
    List<HelpReply> getHelpReplies(Integer helpId);
    
    /**
     * 添加求助回复
     * 
     * @param helpReply 回复对象
     */
    void addHelpReply(HelpReply helpReply);
    
    /**
     * 获取待审核求助
     * 
     * @return 待审核求助列表
     */
    List<HelpRequest> getPendingHelpRequests();
} 