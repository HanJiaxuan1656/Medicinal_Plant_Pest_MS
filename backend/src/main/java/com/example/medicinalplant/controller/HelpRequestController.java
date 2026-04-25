package com.example.medicinalplant.controller;

import com.example.medicinalplant.common.Result;
import com.example.medicinalplant.entity.HelpRequest;
import com.example.medicinalplant.entity.HelpReply;
import com.example.medicinalplant.service.HelpRequestService;
import com.example.medicinalplant.util.UserIdThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/help")
public class HelpRequestController {
    
    @Autowired
    private HelpRequestService helpRequestService;
    
    /**
     * 获取所有求助列表
     */
    @GetMapping
    public Result<List<HelpRequest>> getAllHelpRequests() {
        log.info("获取所有求助列表");
        return Result.success(helpRequestService.getAllHelpRequests());
    }
    
    /**
     * 获取用户的求助列表
     */
    @GetMapping("/user/{userId}")
    public Result<List<HelpRequest>> getUserHelpRequests(@PathVariable Integer userId) {
        log.info("获取用户ID: {}的求助列表", userId);
        return Result.success(helpRequestService.getUserHelpRequests(userId));
    }

    /**
     * 获取我的的求助列表
     */
    @GetMapping("/my")
    public Result<List<HelpRequest>> getMyHelpRequests() {
        Integer userId = UserIdThreadLocal.get();
        return Result.success(helpRequestService.getUserHelpRequests(userId));
    }
    
    /**
     * 根据ID获取求助详情
     */
    @GetMapping("/{id}")
    public Result<HelpRequest> getHelpRequestById(@PathVariable Integer id) {
        log.info("获取求助ID: {}", id);
        HelpRequest helpRequest = helpRequestService.getHelpRequestById(id);
        if (helpRequest != null) {
            return Result.success(helpRequest);
        }
        return Result.error("未找到该求助");
    }
    
    /**
     * 添加求助
     */
    @PostMapping
    public Result<HelpRequest> addHelpRequest(@RequestBody HelpRequest helpRequest) {
        try {
            log.info("添加求助: {}", helpRequest);
            helpRequestService.addHelpRequest(helpRequest);
            return Result.success(helpRequest);
        } catch (Exception e) {
            log.error("添加求助失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传求助图片
     */
    @PostMapping("/upload/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            log.info("上传求助图片");
            String imageUrl = helpRequestService.uploadImage(file);
            return Result.success(imageUrl);
        } catch (Exception e) {
            log.error("上传求助图片失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审核求助
     */
    @PutMapping("/{id}/review")
    public Result<String> reviewHelpRequest(
            @PathVariable Integer id, 
            @RequestParam String status,
            @RequestParam Integer adminId) {
        try {
            log.info("审核求助ID: {}, 状态: {}, 管理员ID: {}", id, status, adminId);
            helpRequestService.reviewHelpRequest(id, status, adminId);
            return Result.success("审核成功");
        } catch (Exception e) {
            log.error("审核求助失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除求助
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteHelpRequest(@PathVariable Integer id) {
        try {
            log.info("删除求助ID: {}", id);
            helpRequestService.deleteHelpRequest(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除求助失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取求助的所有回复
     */
    @GetMapping("/{helpId}/replies")
    public Result<List<HelpReply>> getHelpReplies(@PathVariable Integer helpId) {
        log.info("获取求助ID: {}的所有回复", helpId);
        return Result.success(helpRequestService.getHelpReplies(helpId));
    }
    
    /**
     * 添加求助回复
     */
    @PostMapping("/{helpId}/replies")
    public Result<HelpReply> addHelpReply(
            @PathVariable Integer helpId, 
            @RequestBody HelpReply helpReply) {
        try {
            log.info("添加求助ID: {}的回复: {}", helpId, helpReply);
            helpReply.setHelpId(helpId);
            helpRequestService.addHelpReply(helpReply);
            return Result.success(helpReply);
        } catch (Exception e) {
            log.error("添加求助回复失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取待审核求助
     */
    @GetMapping("/pending")
    public Result<List<HelpRequest>> getPendingHelpRequests() {
        log.info("获取待审核求助");
        return Result.success(helpRequestService.getPendingHelpRequests());
    }
} 