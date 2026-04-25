package com.example.medicinalplant.service.impl;

import com.example.medicinalplant.entity.HelpRequest;
import com.example.medicinalplant.entity.HelpReply;
import com.example.medicinalplant.mapper.HelpRequestMapper;
import com.example.medicinalplant.service.HelpRequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 求助服务实现类
 */
@Slf4j
@Service
public class HelpRequestServiceImpl implements HelpRequestService {
    
    @Autowired
    private HelpRequestMapper helpRequestMapper;
    
    @Override
    public List<HelpRequest> getAllHelpRequests() {
        log.info("获取所有求助");
        return helpRequestMapper.findAll();
    }
    
    @Override
    public List<HelpRequest> getUserHelpRequests(Integer userId) {
        log.info("获取用户ID: {}的求助列表", userId);
        return helpRequestMapper.findByUserId(userId);
    }
    
    @Override
    public HelpRequest getHelpRequestById(Integer id) {
        log.info("获取求助ID: {}", id);
        HelpRequest helpRequest = helpRequestMapper.findById(id);
        if (helpRequest != null) {
            // 获取该求助的所有回复
            List<HelpReply> replies = helpRequestMapper.findRepliesByHelpId(id);
            helpRequest.setReplies(replies);
        }
        return helpRequest;
    }
    
    @Override
    public void addHelpRequest(HelpRequest helpRequest) {
        log.info("添加求助: {}", helpRequest);
        helpRequest.setCreatedAt(LocalDateTime.now());
        helpRequest.setStatus("pending");
        helpRequestMapper.insert(helpRequest);
    }
    
    @Override
    public String uploadImage(MultipartFile file) {
        log.info("上传求助图片: {}", file.getOriginalFilename());
        // TODO: 实现文件上传逻辑
        return "http://example.com/images/" + file.getOriginalFilename();
    }
    
    @Override
    public void reviewHelpRequest(Integer id, String status, Integer adminId) {
        log.info("审核求助ID: {}, 状态: {}, 管理员ID: {}", id, status, adminId);
        HelpRequest helpRequest = helpRequestMapper.findById(id);
        if (helpRequest == null) {
            throw new RuntimeException("求助不存在");
        }
        helpRequest.setStatus(status);
        helpRequest.setReviewAdminId(adminId);
        helpRequestMapper.update(helpRequest);
    }
    
    @Override
    public void deleteHelpRequest(Integer id) {
        log.info("删除求助ID: {}", id);
        helpRequestMapper.deleteById(id);
    }
    
    @Override
    public List<HelpReply> getHelpReplies(Integer helpId) {
        log.info("获取求助ID: {}的回复列表", helpId);
        return helpRequestMapper.findRepliesByHelpId(helpId);
    }
    
    @Override
    public void addHelpReply(HelpReply helpReply) {
        log.info("添加求助回复: {}", helpReply);
        helpReply.setCreatedAt(LocalDateTime.now());
        helpRequestMapper.insertReply(helpReply);
    }
    
    @Override
    public List<HelpRequest> getPendingHelpRequests() {
        log.info("获取待审核求助");
        return helpRequestMapper.findByStatus("pending");
    }
} 