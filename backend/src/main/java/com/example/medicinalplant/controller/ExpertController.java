package com.example.medicinalplant.controller;

import com.example.medicinalplant.common.Result;
import com.example.medicinalplant.entity.HelpReply;
import com.example.medicinalplant.entity.HelpRequest;
import com.example.medicinalplant.mapper.HelpRequestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专家Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/expert")
public class ExpertController {

    @Autowired
    private HelpRequestMapper helpRequestMapper;

    /**
     * 获取求助列表（专家）- 只显示已审核通过的求助
     */
    @GetMapping("/help-requests")
    public Result<Map<String, Object>> getHelpRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(required = false) String replyStatus,
            @RequestParam(defaultValue = "created_at") String sortBy) {
        
        try {
            log.info("专家获取求助列表 - page: {}, pageSize: {}, search: {}, auditStatus: {}, replyStatus: {}, sortBy: {}", 
                    page, pageSize, search, auditStatus, replyStatus, sortBy);
            
            // 获取所有求助
            List<HelpRequest> helpRequests = helpRequestMapper.findAll();
            log.info("从数据库获取到 {} 条求助记录", helpRequests.size());
            
            // 专家只能看到已审核通过的求助
            helpRequests = helpRequests.stream()
                .filter(help -> "approved".equals(help.getStatus()))
                .collect(java.util.stream.Collectors.toList());
            log.info("过滤后已审核通过的求助: {} 条", helpRequests.size());
            
            // 搜索筛选
            if (search != null && !search.trim().isEmpty()) {
                final String searchKeyword = search.trim();
                helpRequests = helpRequests.stream()
                    .filter(help -> 
                        (help.getTitle() != null && help.getTitle().contains(searchKeyword)) ||
                        (help.getDescription() != null && help.getDescription().contains(searchKeyword))
                    )
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // 审核状态筛选（虽然专家只能看到approved，但保留接口一致性）
            if (auditStatus != null && !auditStatus.trim().isEmpty()) {
                final String finalAuditStatus = auditStatus.trim();
                helpRequests = helpRequests.stream()
                    .filter(help -> help.getStatus() != null && help.getStatus().equals(finalAuditStatus))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // 回复状态筛选
            if (replyStatus != null && !replyStatus.trim().isEmpty()) {
                final String finalReplyStatus = replyStatus.trim();
                helpRequests = helpRequests.stream()
                    .filter(help -> {
                        List<HelpReply> replies = helpRequestMapper.findRepliesByHelpId(help.getId());
                        int replyCount = replies != null ? replies.size() : 0;
                        
                        switch (finalReplyStatus) {
                            case "pending":
                                return replyCount == 0; // 待回复：没有回复
                            case "replied":
                                return replyCount > 0;  // 已回复：有回复
                            default:
                                return true;
                        }
                    })
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // 排序
            if ("created_at".equals(sortBy)) {
                helpRequests.sort((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt()); // 降序
                });
            } else if ("created_at_asc".equals(sortBy)) {
                helpRequests.sort((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return a.getCreatedAt().compareTo(b.getCreatedAt()); // 升序
                });
            }
            
            // 分页处理
            int total = helpRequests.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);
            
            List<HelpRequest> pagedHelpRequests = start < total ? helpRequests.subList(start, end) : new ArrayList<>();
            
            // 为每个求助添加回复数量信息
            List<Map<String, Object>> helpRequestsWithReplyCount = new ArrayList<>();
            for (HelpRequest help : pagedHelpRequests) {
                Map<String, Object> helpMap = new HashMap<>();
                helpMap.put("id", help.getId());
                helpMap.put("userId", help.getUserId());
                helpMap.put("title", help.getTitle());
                helpMap.put("description", help.getDescription());
                helpMap.put("imageUrl", help.getImageUrl());
                helpMap.put("status", help.getStatus());
                helpMap.put("createdAt", help.getCreatedAt());
                
                // 获取回复数量
                List<HelpReply> replies = helpRequestMapper.findRepliesByHelpId(help.getId());
                helpMap.put("replyCount", replies != null ? replies.size() : 0);
                
                helpRequestsWithReplyCount.add(helpMap);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("list", helpRequestsWithReplyCount);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);
            
            return Result.success(data);
        } catch (Exception e) {
            log.error("专家获取求助列表异常: {}", e.getMessage(), e);
            return Result.error("获取求助列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取求助的回复列表
     */
    @GetMapping("/help-requests/{helpId}/replies")
    public Result<List<HelpReply>> getHelpReplies(@PathVariable Integer helpId) {
        try {
            log.info("专家获取求助回复列表，求助ID: {}", helpId);
            
            List<HelpReply> replies = helpRequestMapper.findRepliesByHelpId(helpId);
            
            return Result.success(replies);
        } catch (Exception e) {
            log.error("获取求助回复列表异常: {}", e.getMessage(), e);
            return Result.error("获取回复列表失败: " + e.getMessage());
        }
    }

    /**
     * 专家回复求助
     */
    @PostMapping("/help-requests/{helpId}/replies")
    public Result<String> createHelpReply(
            @PathVariable Integer helpId,
            @RequestBody Map<String, Object> replyData,
            @RequestHeader(value = "Expert-Id", required = false) Integer expertId,
            HttpServletRequest request) {
        try {
            log.info("专家回复求助，请求头专家ID: {}, 求助ID: {}, 回复内容: {}", expertId, helpId, replyData.get("content"));

            // 打印所有请求头信息用于调试
            log.info("所有请求头:");
            java.util.Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                log.info("  {}: {}", headerName, headerValue);
            }

            // 获取当前专家ID
            Integer currentExpertId = getCurrentExpertId(expertId);
            log.info("解析后的专家ID: {}", currentExpertId);
            if (currentExpertId == null) {
                return Result.error("无法获取专家身份信息");
            }

            // 检查求助是否存在且已审核通过
            HelpRequest helpRequest = helpRequestMapper.findById(helpId);
            if (helpRequest == null) {
                return Result.error("求助不存在");
            }
            if (!"approved".equals(helpRequest.getStatus())) {
                return Result.error("只能回复已审核通过的求助");
            }

            // 创建回复对象
            HelpReply reply = new HelpReply();
            reply.setHelpId(helpId);
            reply.setExpertId(currentExpertId);
            reply.setContent((String) replyData.get("content"));
            reply.setCreatedAt(LocalDateTime.now());
            
            // 保存回复
            int result = helpRequestMapper.insertReply(reply);
            
            if (result > 0) {
                log.info("专家回复成功，回复ID: {}", reply.getId());
                return Result.success("回复发送成功");
            } else {
                return Result.error("回复发送失败");
            }
        } catch (Exception e) {
            log.error("专家回复求助异常: {}", e.getMessage(), e);
            return Result.error("回复发送失败: " + e.getMessage());
        }
    }

    /**
     * 获取求助详情（专家）
     */
    @GetMapping("/help-requests/{id}")
    public Result<HelpRequest> getHelpRequestDetail(@PathVariable Integer id) {
        try {
            log.info("专家获取求助详情，ID: {}", id);
            
            HelpRequest helpRequest = helpRequestMapper.findById(id);
            if (helpRequest == null) {
                return Result.error("求助不存在");
            }
            
            // 专家只能查看已审核通过的求助
            if (!"approved".equals(helpRequest.getStatus())) {
                return Result.error("只能查看已审核通过的求助");
            }
            
            return Result.success(helpRequest);
        } catch (Exception e) {
            log.error("获取求助详情异常: {}", e.getMessage(), e);
            return Result.error("获取求助详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前专家ID
     * 优先级：请求头 > JWT Token > Session > 默认值
     */
    private Integer getCurrentExpertId(Integer headerExpertId) {
        // 1. 优先使用请求头中的专家ID（用于测试和临时方案）
        if (headerExpertId != null && headerExpertId > 0) {
            log.info("从请求头获取专家ID: {}", headerExpertId);
            return headerExpertId;
        }

        // 2. 从JWT Token中获取（TODO: 实现JWT解析）
        // String token = request.getHeader("Authorization");
        // if (token != null) {
        //     return parseExpertIdFromToken(token);
        // }

        // 3. 从Session中获取（TODO: 实现Session管理）
        // HttpSession session = request.getSession(false);
        // if (session != null) {
        //     return (Integer) session.getAttribute("expertId");
        // }

        // 4. 默认值（临时方案，生产环境应该移除）
        log.warn("无法获取专家ID，使用默认值1");
        return 1;
    }
}
