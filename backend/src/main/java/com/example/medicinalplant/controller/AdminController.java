package com.example.medicinalplant.controller;

import com.example.medicinalplant.common.Result;
import com.example.medicinalplant.entity.Admin;
import com.example.medicinalplant.entity.Comment;
import com.example.medicinalplant.entity.ExpertUser;
import com.example.medicinalplant.entity.HelpReply;
import com.example.medicinalplant.entity.HelpRequest;
import com.example.medicinalplant.entity.NormalUser;
import com.example.medicinalplant.mapper.CommentMapper;
import com.example.medicinalplant.mapper.ExpertUserMapper;
import com.example.medicinalplant.mapper.HelpRequestMapper;
import com.example.medicinalplant.mapper.NormalUserMapper;
import com.example.medicinalplant.mapper.MedicinalPlantMapper;
import com.example.medicinalplant.mapper.PestDiseaseMapper;
import com.example.medicinalplant.mapper.PesticideMapper;
import com.example.medicinalplant.mapper.AdminMapper;
import com.example.medicinalplant.mapper.ExpertUserMapper;
import com.example.medicinalplant.mapper.NormalUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private HelpRequestMapper helpRequestMapper;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private MedicinalPlantMapper medicinalPlantMapper;

    @Autowired
    private PestDiseaseMapper pestDiseaseMapper;

    @Autowired
    private PesticideMapper pesticideMapper;

    @Autowired
    private ExpertUserMapper expertUserMapper;

    @Autowired
    private NormalUserMapper normalUserMapper;

    /**
     * 获取求助列表（管理员）
     */
    @GetMapping("/help-requests")
    public Result<Map<String, Object>> getHelpRequests(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String auditStatus,
            @RequestParam(defaultValue = "created_at") String sortBy) {
        
        try {
            log.info("管理员获取求助列表 - page: {}, pageSize: {}, search: {}, auditStatus: {}, sortBy: {}", 
                    page, pageSize, search, auditStatus, sortBy);
            
            // 获取所有求助
            List<HelpRequest> helpRequests = helpRequestMapper.findAll();
            log.info("从数据库获取到 {} 条求助记录", helpRequests.size());
            
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
            
            // 审核状态筛选
            if (auditStatus != null && !auditStatus.trim().isEmpty()) {
                final String finalAuditStatus = auditStatus.trim();
                helpRequests = helpRequests.stream()
                    .filter(help -> help.getStatus() != null && help.getStatus().equals(finalAuditStatus))
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

            // 为每个求助添加回复数量信息，与普通用户API保持一致
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
            log.error("管理员获取求助列表异常: {}", e.getMessage(), e);
            return Result.error("获取求助列表失败: " + e.getMessage());
        }
    }

    /**
     * 审核通过求助
     */
    @PostMapping("/help-requests/{id}/approve")
    public Result<String> approveHelpRequest(@PathVariable Integer id) {
        try {
            log.info("管理员审核通过求助ID: {}", id);
            
            HelpRequest helpRequest = helpRequestMapper.findById(id);
            if (helpRequest == null) {
                return Result.error("求助不存在");
            }
            
            helpRequest.setStatus("approved");
            helpRequest.setReviewAdminId(1); // 临时硬编码管理员ID
            int result = helpRequestMapper.update(helpRequest);
            
            if (result > 0) {
                log.info("求助审核通过成功，ID: {}", id);
                return Result.success("审核通过成功");
            } else {
                return Result.error("审核失败");
            }
        } catch (Exception e) {
            log.error("审核通过求助异常: {}", e.getMessage(), e);
            return Result.error("审核失败: " + e.getMessage());
        }
    }

    /**
     * 审核拒绝求助
     */
    @PostMapping("/help-requests/{id}/reject")
    public Result<String> rejectHelpRequest(@PathVariable Integer id) {
        try {
            log.info("管理员审核拒绝求助ID: {}", id);
            
            HelpRequest helpRequest = helpRequestMapper.findById(id);
            if (helpRequest == null) {
                return Result.error("求助不存在");
            }
            
            helpRequest.setStatus("rejected");
            helpRequest.setReviewAdminId(1); // 临时硬编码管理员ID
            int result = helpRequestMapper.update(helpRequest);
            
            if (result > 0) {
                log.info("求助审核拒绝成功，ID: {}", id);
                return Result.success("审核拒绝成功");
            } else {
                return Result.error("审核失败");
            }
        } catch (Exception e) {
            log.error("审核拒绝求助异常: {}", e.getMessage(), e);
            return Result.error("审核失败: " + e.getMessage());
        }
    }

    /**
     * 批量审核通过
     */
    @PostMapping("/help-requests/batch-approve")
    public Result<String> batchApproveHelpRequests(@RequestBody Map<String, Object> requestData) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) requestData.get("ids");
            log.info("管理员批量审核通过求助IDs: {}", ids);
            
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要审核的求助");
            }
            
            int successCount = 0;
            for (Integer id : ids) {
                try {
                    HelpRequest helpRequest = helpRequestMapper.findById(id);
                    if (helpRequest != null) {
                        helpRequest.setStatus("approved");
                        helpRequest.setReviewAdminId(1); // 临时硬编码管理员ID
                        int result = helpRequestMapper.update(helpRequest);
                        if (result > 0) {
                            successCount++;
                        }
                    }
                } catch (Exception e) {
                    log.error("批量审核通过求助ID {} 失败: {}", id, e.getMessage());
                }
            }
            
            log.info("批量审核通过完成，成功: {}/{}", successCount, ids.size());
            return Result.success(String.format("批量审核完成，成功通过 %d/%d 个求助", successCount, ids.size()));
        } catch (Exception e) {
            log.error("批量审核通过异常: {}", e.getMessage(), e);
            return Result.error("批量审核失败: " + e.getMessage());
        }
    }

    /**
     * 批量审核拒绝
     */
    @PostMapping("/help-requests/batch-reject")
    public Result<String> batchRejectHelpRequests(@RequestBody Map<String, Object> requestData) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) requestData.get("ids");
            log.info("管理员批量审核拒绝求助IDs: {}", ids);
            
            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要审核的求助");
            }
            
            int successCount = 0;
            for (Integer id : ids) {
                try {
                    HelpRequest helpRequest = helpRequestMapper.findById(id);
                    if (helpRequest != null) {
                        helpRequest.setStatus("rejected");
                        helpRequest.setReviewAdminId(1); // 临时硬编码管理员ID
                        int result = helpRequestMapper.update(helpRequest);
                        if (result > 0) {
                            successCount++;
                        }
                    }
                } catch (Exception e) {
                    log.error("批量审核拒绝求助ID {} 失败: {}", id, e.getMessage());
                }
            }
            
            log.info("批量审核拒绝完成，成功: {}/{}", successCount, ids.size());
            return Result.success(String.format("批量审核完成，成功拒绝 %d/%d 个求助", successCount, ids.size()));
        } catch (Exception e) {
            log.error("批量审核拒绝异常: {}", e.getMessage(), e);
            return Result.error("批量审核失败: " + e.getMessage());
        }
    }

    /**
     * 获取求助详情
     */
    @GetMapping("/help-requests/{id}")
    public Result<HelpRequest> getHelpRequestDetail(@PathVariable Integer id) {
        try {
            log.info("管理员获取求助详情，ID: {}", id);

            HelpRequest helpRequest = helpRequestMapper.findById(id);
            if (helpRequest == null) {
                return Result.error("求助不存在");
            }

            return Result.success(helpRequest);
        } catch (Exception e) {
            log.error("获取求助详情异常: {}", e.getMessage(), e);
            return Result.error("获取求助详情失败: " + e.getMessage());
        }
    }

    /**
     * 测试数据库连接和数据
     */
    @GetMapping("/test/database")
    public Result<Map<String, Object>> testDatabase() {
        try {
            log.info("测试数据库连接和数据");

            // 获取所有求助
            List<HelpRequest> allHelps = helpRequestMapper.findAll();
            log.info("数据库中总共有 {} 条求助记录", allHelps.size());

            Map<String, Object> testResult = new HashMap<>();
            testResult.put("totalCount", allHelps.size());
            testResult.put("helpRequests", allHelps);

            // 如果有数据，显示前几条
            if (!allHelps.isEmpty()) {
                testResult.put("firstHelp", allHelps.get(0));
                log.info("第一条求助: ID={}, 标题={}, 状态={}",
                        allHelps.get(0).getId(),
                        allHelps.get(0).getTitle(),
                        allHelps.get(0).getStatus());
            }

            return Result.success(testResult);
        } catch (Exception e) {
            log.error("测试数据库异常: {}", e.getMessage(), e);
            return Result.error("测试数据库失败: " + e.getMessage());
        }
    }

    /**
     * 获取评论列表（管理员）
     */
    @GetMapping("/comments")
    public Result<Map<String, Object>> getComments(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String targetType,
            @RequestParam(defaultValue = "created_at") String sortBy) {

        try {
            log.info("管理员获取评论列表 - page: {}, pageSize: {}, search: {}, status: {}, targetType: {}, sortBy: {}",
                    page, pageSize, search, status, targetType, sortBy);

            // 获取所有评论（通过获取各种状态的评论合并）
            List<Comment> comments = new ArrayList<>();
            comments.addAll(commentMapper.findByStatus("pending"));
            comments.addAll(commentMapper.findByStatus("approved"));
            comments.addAll(commentMapper.findByStatus("rejected"));
            log.info("从数据库获取到 {} 条评论记录", comments.size());

            // 搜索筛选
            if (search != null && !search.trim().isEmpty()) {
                final String searchKeyword = search.trim();
                comments = comments.stream()
                    .filter(comment ->
                        (comment.getContent() != null && comment.getContent().contains(searchKeyword))
                    )
                    .collect(java.util.stream.Collectors.toList());
            }

            // 状态筛选
            if (status != null && !status.trim().isEmpty()) {
                final String finalStatus = status.trim();
                comments = comments.stream()
                    .filter(comment -> comment.getStatus() != null && comment.getStatus().equals(finalStatus))
                    .collect(java.util.stream.Collectors.toList());
            }

            // 目标类型筛选
            if (targetType != null && !targetType.trim().isEmpty()) {
                final String finalTargetType = targetType.trim();
                comments = comments.stream()
                    .filter(comment -> comment.getTargetType() != null && comment.getTargetType().equals(finalTargetType))
                    .collect(java.util.stream.Collectors.toList());
            }

            // 排序
            if ("created_at".equals(sortBy)) {
                comments.sort((a, b) -> {
                    if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt()); // 降序
                });
            }

            // 分页处理
            int total = comments.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);

            List<Comment> pagedComments = start < total ? comments.subList(start, end) : new ArrayList<>();

            // 为每个评论添加用户信息
            List<Map<String, Object>> commentsWithUserInfo = new ArrayList<>();
            for (Comment comment : pagedComments) {
                Map<String, Object> commentMap = buildCommentMap(comment);
                commentsWithUserInfo.add(commentMap);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("list", commentsWithUserInfo);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            return Result.success(data);
        } catch (Exception e) {
            log.error("管理员获取评论列表异常: {}", e.getMessage(), e);
            return Result.error("获取评论列表失败: " + e.getMessage());
        }
    }

    /**
     * 审核通过评论
     */
    @PostMapping("/comments/{id}/approve")
    public Result<String> approveComment(@PathVariable Integer id) {
        try {
            log.info("管理员审核通过评论ID: {}", id);

            Comment comment = commentMapper.findById(id);
            if (comment == null) {
                return Result.error("评论不存在");
            }

            comment.setStatus("approved");
            comment.setReviewAdminId(1); // 临时硬编码管理员ID
            int result = commentMapper.update(comment);

            if (result > 0) {
                log.info("评论审核通过成功，ID: {}", id);
                return Result.success("审核通过成功");
            } else {
                return Result.error("审核失败");
            }
        } catch (Exception e) {
            log.error("审核通过评论异常: {}", e.getMessage(), e);
            return Result.error("审核失败: " + e.getMessage());
        }
    }

    /**
     * 审核拒绝评论
     */
    @PostMapping("/comments/{id}/reject")
    public Result<String> rejectComment(@PathVariable Integer id) {
        try {
            log.info("管理员审核拒绝评论ID: {}", id);

            Comment comment = commentMapper.findById(id);
            if (comment == null) {
                return Result.error("评论不存在");
            }

            comment.setStatus("rejected");
            comment.setReviewAdminId(1); // 临时硬编码管理员ID
            int result = commentMapper.update(comment);

            if (result > 0) {
                log.info("评论审核拒绝成功，ID: {}", id);
                return Result.success("审核拒绝成功");
            } else {
                return Result.error("审核失败");
            }
        } catch (Exception e) {
            log.error("审核拒绝评论异常: {}", e.getMessage(), e);
            return Result.error("审核失败: " + e.getMessage());
        }
    }

    /**
     * 批量审核通过评论
     */
    @PostMapping("/comments/batch-approve")
    public Result<String> batchApproveComments(@RequestBody Map<String, Object> requestData) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) requestData.get("ids");
            log.info("管理员批量审核通过评论IDs: {}", ids);

            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要审核的评论");
            }

            int successCount = 0;
            for (Integer id : ids) {
                try {
                    Comment comment = commentMapper.findById(id);
                    if (comment != null) {
                        comment.setStatus("approved");
                        comment.setReviewAdminId(1); // 临时硬编码管理员ID
                        int result = commentMapper.update(comment);
                        if (result > 0) {
                            successCount++;
                        }
                    }
                } catch (Exception e) {
                    log.error("批量审核通过评论ID {} 失败: {}", id, e.getMessage());
                }
            }

            log.info("批量审核通过完成，成功: {}/{}", successCount, ids.size());
            return Result.success(String.format("批量审核完成，成功通过 %d/%d 个评论", successCount, ids.size()));
        } catch (Exception e) {
            log.error("批量审核通过异常: {}", e.getMessage(), e);
            return Result.error("批量审核失败: " + e.getMessage());
        }
    }

    /**
     * 批量审核拒绝评论
     */
    @PostMapping("/comments/batch-reject")
    public Result<String> batchRejectComments(@RequestBody Map<String, Object> requestData) {
        try {
            @SuppressWarnings("unchecked")
            List<Integer> ids = (List<Integer>) requestData.get("ids");
            log.info("管理员批量审核拒绝评论IDs: {}", ids);

            if (ids == null || ids.isEmpty()) {
                return Result.error("请选择要审核的评论");
            }

            int successCount = 0;
            for (Integer id : ids) {
                try {
                    Comment comment = commentMapper.findById(id);
                    if (comment != null) {
                        comment.setStatus("rejected");
                        comment.setReviewAdminId(1); // 临时硬编码管理员ID
                        int result = commentMapper.update(comment);
                        if (result > 0) {
                            successCount++;
                        }
                    }
                } catch (Exception e) {
                    log.error("批量审核拒绝评论ID {} 失败: {}", id, e.getMessage());
                }
            }

            log.info("批量审核拒绝完成，成功: {}/{}", successCount, ids.size());
            return Result.success(String.format("批量审核完成，成功拒绝 %d/%d 个评论", successCount, ids.size()));
        } catch (Exception e) {
            log.error("批量审核拒绝异常: {}", e.getMessage(), e);
            return Result.error("批量审核失败: " + e.getMessage());
        }
    }

    /**
     * 构建评论信息Map
     */
    private Map<String, Object> buildCommentMap(Comment comment) {
        Map<String, Object> commentMap = new HashMap<>();
        commentMap.put("id", comment.getId());
        commentMap.put("content", comment.getContent());
        commentMap.put("createdAt", comment.getCreatedAt());
        commentMap.put("userId", comment.getUserId());
        commentMap.put("userType", comment.getUserType());
        commentMap.put("targetType", comment.getTargetType());
        commentMap.put("targetId", comment.getTargetId());
        commentMap.put("status", comment.getStatus());
        commentMap.put("reviewAdminId", comment.getReviewAdminId());

        // 获取用户信息
        try {
            if ("normal".equals(comment.getUserType())) {
                NormalUser user = normalUserMapper.findById(comment.getUserId());
                if (user != null) {
                    commentMap.put("username", user.getUsername());
                    commentMap.put("nickname", user.getNickname() != null ? user.getNickname() : user.getUsername());
                    commentMap.put("avatarUrl", user.getAvatarUrl());
                }
            } else if ("expert".equals(comment.getUserType())) {
                ExpertUser user = expertUserMapper.findById(comment.getUserId());
                if (user != null) {
                    commentMap.put("username", user.getUsername());
                    commentMap.put("nickname", user.getName() != null ? user.getName() : user.getUsername());
                    commentMap.put("avatarUrl", user.getAvatarUrl());
                    commentMap.put("title", user.getTitle()); // 专家职称
                }
            }
        } catch (Exception e) {
            log.error("获取评论用户信息失败: {}", e.getMessage());
            commentMap.put("username", "未知用户");
            commentMap.put("nickname", "未知用户");
        }

        // 获取目标信息（植物、病虫害、农药名称）
        try {
            String targetName = getTargetName(comment.getTargetType(), comment.getTargetId());
            commentMap.put("targetName", targetName);
        } catch (Exception e) {
            log.error("获取目标信息失败: {}", e.getMessage());
            commentMap.put("targetName", "未知");
        }

        return commentMap;
    }

    /**
     * 获取目标名称
     */
    private String getTargetName(String targetType, Integer targetId) {
        // 这里需要根据targetType查询对应的表获取名称
        // 暂时返回类型+ID的格式
        switch (targetType) {
            case "plant":
                return "植物 #" + targetId;
            case "pest_disease":
                return "病虫害 #" + targetId;
            case "pesticide":
                return "农药 #" + targetId;
            default:
                return "未知类型 #" + targetId;
        }
    }

    /**
     * 获取管理员统计数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getAdminStats() {
        try {
            log.info("获取管理员统计数据");

            Map<String, Object> stats = new HashMap<>();

            // 获取各类数据统计
            int plantCount = 0;
            int pestDiseaseCount = 0;
            int pesticideCount = 0;
            int expertCount = 0;
            int normalUserCount = 0;

            try {
                plantCount = medicinalPlantMapper.findAll().size();
            } catch (Exception e) {
                log.error("获取植物数量失败: {}", e.getMessage());
            }

            try {
                pestDiseaseCount = pestDiseaseMapper.findAll().size();
            } catch (Exception e) {
                log.error("获取病虫害数量失败: {}", e.getMessage());
            }

            try {
                pesticideCount = pesticideMapper.findAll().size();
            } catch (Exception e) {
                log.error("获取农药数量失败: {}", e.getMessage());
            }

            try {
                // 获取真实的专家用户数量
                expertCount = expertUserMapper.findAll().size();
            } catch (Exception e) {
                log.error("获取专家数量失败: {}", e.getMessage());
                expertCount = 0;
            }

            try {
                // 获取真实的普通用户数量
                normalUserCount = normalUserMapper.findAll().size();
            } catch (Exception e) {
                log.error("获取普通用户数量失败: {}", e.getMessage());
                normalUserCount = 0;
            }

            // 获取待审核数据统计
            int pendingCommentCount = commentMapper.countByStatus("pending");
            int pendingHelpRequestCount = 0;
            try {
                List<HelpRequest> allHelps = helpRequestMapper.findAll();
                pendingHelpRequestCount = (int) allHelps.stream()
                    .filter(help -> "pending".equals(help.getStatus()))
                    .count();
            } catch (Exception e) {
                log.error("获取待审核求助数量失败: {}", e.getMessage());
            }

            // 获取最近的待审核评论
            List<Comment> pendingComments = commentMapper.findByStatus("pending");
            List<Map<String, Object>> recentPendingComments = new ArrayList<>();
            for (int i = 0; i < Math.min(5, pendingComments.size()); i++) {
                Comment comment = pendingComments.get(i);
                Map<String, Object> commentInfo = new HashMap<>();
                commentInfo.put("id", comment.getId());
                commentInfo.put("content", comment.getContent());
                commentInfo.put("createdAt", comment.getCreatedAt());
                commentInfo.put("targetType", comment.getTargetType());
                commentInfo.put("targetId", comment.getTargetId());

                // 获取用户名
                try {
                    if ("normal".equals(comment.getUserType()) && comment.getUserId() != null) {
                        com.example.medicinalplant.entity.NormalUser user = normalUserMapper.findById(comment.getUserId());
                        commentInfo.put("username", user != null ? user.getUsername() : "未知用户");
                    } else if ("expert".equals(comment.getUserType()) && comment.getUserId() != null) {
                        com.example.medicinalplant.entity.ExpertUser user = expertUserMapper.findById(comment.getUserId());
                        commentInfo.put("username", user != null ? user.getUsername() : "未知专家");
                    } else {
                        commentInfo.put("username", "未知用户");
                    }
                } catch (Exception e) {
                    commentInfo.put("username", "获取失败");
                }

                recentPendingComments.add(commentInfo);
            }

            // 获取最近的待审核求助
            List<Map<String, Object>> recentPendingHelps = new ArrayList<>();
            try {
                List<HelpRequest> allHelps = helpRequestMapper.findAll();
                List<HelpRequest> pendingHelps = allHelps.stream()
                    .filter(help -> "pending".equals(help.getStatus()))
                    .sorted((a, b) -> {
                        if (a.getCreatedAt() == null && b.getCreatedAt() == null) return 0;
                        if (a.getCreatedAt() == null) return 1;
                        if (b.getCreatedAt() == null) return -1;
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    })
                    .limit(5)
                    .collect(java.util.stream.Collectors.toList());

                for (HelpRequest help : pendingHelps) {
                    Map<String, Object> helpInfo = new HashMap<>();
                    helpInfo.put("id", help.getId());
                    helpInfo.put("title", help.getTitle());
                    helpInfo.put("createdAt", help.getCreatedAt());

                    // 获取用户名
                    try {
                        if (help.getUserId() != null) {
                            com.example.medicinalplant.entity.NormalUser user = normalUserMapper.findById(help.getUserId());
                            helpInfo.put("username", user != null ? user.getUsername() : "未知用户");
                        } else {
                            helpInfo.put("username", "未知用户");
                        }
                    } catch (Exception e) {
                        helpInfo.put("username", "获取失败");
                    }

                    recentPendingHelps.add(helpInfo);
                }
            } catch (Exception e) {
                log.error("获取待审核求助失败: {}", e.getMessage());
            }

            // 组装统计数据
            stats.put("plantCount", plantCount);
            stats.put("pestDiseaseCount", pestDiseaseCount);
            stats.put("pesticideCount", pesticideCount);
            stats.put("expertCount", expertCount);
            stats.put("normalUserCount", normalUserCount);
            stats.put("pendingCommentCount", pendingCommentCount);
            stats.put("pendingHelpRequestCount", pendingHelpRequestCount);
            stats.put("recentPendingComments", recentPendingComments);
            stats.put("recentPendingHelps", recentPendingHelps);

            log.info("管理员统计数据: 植物={}, 病虫害={}, 农药={}, 专家={}, 普通用户={}, 待审核评论={}, 待审核求助={}",
                    plantCount, pestDiseaseCount, pesticideCount, expertCount, normalUserCount,
                    pendingCommentCount, pendingHelpRequestCount);

            return Result.success(stats);
        } catch (Exception e) {
            log.error("获取管理员统计数据异常: {}", e.getMessage(), e);
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取专家用户列表
     */
    @GetMapping("/experts")
    public Result<Map<String, Object>> getExperts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            log.info("获取专家用户列表 - page: {}, pageSize: {}, keyword: {}",
                    page, pageSize, keyword);

            // 获取所有专家用户
            List<com.example.medicinalplant.entity.ExpertUser> allExperts = new ArrayList<>();
            try {
                allExperts = expertUserMapper.findAll();
                log.info("从数据库获取到 {} 个专家用户", allExperts.size());
            } catch (Exception e) {
                log.error("查询专家用户失败: {}", e.getMessage());
                // 如果数据库查询失败，返回空列表
            }

            // 转换为Map并应用筛选条件
            List<Map<String, Object>> experts = new ArrayList<>();
            for (com.example.medicinalplant.entity.ExpertUser expert : allExperts) {
                // 应用关键词筛选
                if (keyword != null && !keyword.trim().isEmpty()) {
                    String kw = keyword.toLowerCase();
                    boolean matchKeyword = false;
                    if (expert.getName() != null && expert.getName().toLowerCase().contains(kw)) {
                        matchKeyword = true;
                    }
                    if (expert.getUsername() != null && expert.getUsername().toLowerCase().contains(kw)) {
                        matchKeyword = true;
                    }
                    if (expert.getEmail() != null && expert.getEmail().toLowerCase().contains(kw)) {
                        matchKeyword = true;
                    }
                    if (!matchKeyword) {
                        continue;
                    }
                }

                // 构建专家信息Map
                Map<String, Object> expertMap = new HashMap<>();
                expertMap.put("id", expert.getId());
                expertMap.put("username", expert.getUsername());
                expertMap.put("realName", expert.getName()); // 使用name字段作为realName
                expertMap.put("email", expert.getEmail());
                expertMap.put("phone", expert.getPhone());
                expertMap.put("specialty", expert.getTitle()); // 使用title字段作为specialty显示
                expertMap.put("title", expert.getTitle());
                expertMap.put("institution", expert.getOrganization()); // 使用organization字段
                expertMap.put("status", expert.getRole() != null ? expert.getRole() : "active");
                expertMap.put("createdAt", expert.getCreatedAt());
                expertMap.put("updatedAt", expert.getUpdatedAt());
                expertMap.put("avatarUrl", expert.getAvatarUrl());
                expertMap.put("bio", ""); // 数据库中没有bio字段，设为空

                experts.add(expertMap);
            }

            // 分页处理
            int total = experts.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);

            List<Map<String, Object>> pagedExperts = start < total ?
                experts.subList(start, end) : new ArrayList<>();

            Map<String, Object> data = new HashMap<>();
            data.put("list", pagedExperts);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            log.info("专家用户列表查询成功，总数: {}, 当前页: {} 条", total, pagedExperts.size());
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取专家用户列表异常: {}", e.getMessage(), e);
            return Result.error("获取专家用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取普通用户列表
     */
    @GetMapping("/users")
    public Result<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword) {
        try {
            log.info("获取普通用户列表 - page: {}, pageSize: {}, keyword: {}",
                    page, pageSize, keyword);

            // 获取所有普通用户
            List<com.example.medicinalplant.entity.NormalUser> allUsers = new ArrayList<>();
            try {
                allUsers = normalUserMapper.findAll();
                log.info("从数据库获取到 {} 个普通用户", allUsers.size());
            } catch (Exception e) {
                log.error("查询普通用户失败: {}", e.getMessage());
                // 如果数据库查询失败，返回空列表
            }

            // 转换为Map并应用筛选条件
            List<Map<String, Object>> users = new ArrayList<>();
            for (com.example.medicinalplant.entity.NormalUser user : allUsers) {
                // 应用关键词筛选
                if (keyword != null && !keyword.trim().isEmpty()) {
                    String kw = keyword.toLowerCase();
                    boolean matchKeyword = false;
                    if (user.getNickname() != null && user.getNickname().toLowerCase().contains(kw)) {
                        matchKeyword = true;
                    }
                    if (user.getUsername() != null && user.getUsername().toLowerCase().contains(kw)) {
                        matchKeyword = true;
                    }
                    if (user.getEmail() != null && user.getEmail().toLowerCase().contains(kw)) {
                        matchKeyword = true;
                    }
                    if (!matchKeyword) {
                        continue;
                    }
                }

                // 获取用户的评论和求助数量
                int commentCount = 0;
                int helpRequestCount = 0;
                try {
                    // 查询用户的评论数量
                    commentCount = commentMapper.countByUserId(user.getId());
                } catch (Exception e) {
                    log.warn("获取用户评论数量失败，用户ID: {}, 错误: {}", user.getId(), e.getMessage());
                }

                try {
                    // 查询用户的求助数量
                    helpRequestCount = helpRequestMapper.countByUserId(user.getId());
                } catch (Exception e) {
                    log.warn("获取用户求助数量失败，用户ID: {}, 错误: {}", user.getId(), e.getMessage());
                }

                // 构建用户信息Map
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("id", user.getId());
                userMap.put("username", user.getUsername());
                userMap.put("realName", user.getNickname()); // 使用nickname作为realName
                userMap.put("email", user.getEmail());
                userMap.put("phone", user.getPhone());
                userMap.put("status", user.getRole() != null ? user.getRole() : "active");
                userMap.put("createdAt", user.getCreatedAt());
                userMap.put("updatedAt", user.getUpdatedAt());
                userMap.put("lastLoginAt", null); // 数据库中没有lastLoginAt字段
                userMap.put("avatarUrl", user.getAvatarUrl());
                userMap.put("commentCount", commentCount); // 真实的评论数量
                userMap.put("helpRequestCount", helpRequestCount); // 真实的求助数量

                users.add(userMap);
            }

            // 分页处理
            int total = users.size();
            int start = (page - 1) * pageSize;
            int end = Math.min(start + pageSize, total);

            List<Map<String, Object>> pagedUsers = start < total ?
                users.subList(start, end) : new ArrayList<>();

            Map<String, Object> data = new HashMap<>();
            data.put("list", pagedUsers);
            data.put("total", total);
            data.put("page", page);
            data.put("pageSize", pageSize);

            log.info("普通用户列表查询成功，总数: {}, 当前页: {} 条", total, pagedUsers.size());
            return Result.success(data);
        } catch (Exception e) {
            log.error("获取普通用户列表异常: {}", e.getMessage(), e);
            return Result.error("获取普通用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 更新专家用户状态
     */
    @PutMapping("/experts/{id}/status")
    public Result<String> updateExpertStatus(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        try {
            String status = (String) data.get("status");
            log.info("更新专家用户状态，ID: {}, 状态: {}", id, status);

            // 调用Mapper更新状态
            int result = expertUserMapper.updateStatus(id, status);
            if (result > 0) {
                log.info("专家用户状态更新成功，ID: {}", id);
                return Result.success("专家用户状态更新成功");
            } else {
                log.warn("专家用户状态更新失败，未找到用户，ID: {}", id);
                return Result.error("未找到该专家用户");
            }
        } catch (Exception e) {
            log.error("更新专家用户状态异常: {}", e.getMessage(), e);
            return Result.error("更新专家用户状态失败: " + e.getMessage());
        }
    }

    /**
     * 更新普通用户状态
     */
    @PutMapping("/users/{id}/status")
    public Result<String> updateUserStatus(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
        try {
            String status = (String) data.get("status");
            log.info("更新普通用户状态，ID: {}, 状态: {}", id, status);

            // 调用Mapper更新状态
            int result = normalUserMapper.updateStatus(id, status);
            if (result > 0) {
                log.info("普通用户状态更新成功，ID: {}", id);
                return Result.success("普通用户状态更新成功");
            } else {
                log.warn("普通用户状态更新失败，未找到用户，ID: {}", id);
                return Result.error("未找到该普通用户");
            }
        } catch (Exception e) {
            log.error("更新普通用户状态异常: {}", e.getMessage(), e);
            return Result.error("更新普通用户状态失败: " + e.getMessage());
        }
    }

    /**
     * 删除专家用户
     */
    @DeleteMapping("/experts/{id}")
    public Result<String> deleteExpert(@PathVariable Integer id) {
        try {
            log.info("删除专家用户，ID: {}", id);

            // 先检查专家是否存在
            ExpertUser expert = expertUserMapper.findById(id);
            if (expert == null) {
                return Result.error("专家用户不存在");
            }

            // 调用Mapper删除专家
            int result = expertUserMapper.deleteById(id);
            if (result > 0) {
                log.info("专家用户删除成功，ID: {}", id);
                return Result.success("专家用户删除成功");
            } else {
                log.warn("专家用户删除失败，ID: {}", id);
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除专家用户异常: {}", e.getMessage(), e);
            return Result.error("删除专家用户失败: " + e.getMessage());
        }
    }

    /**
     * 重置专家用户密码
     */
    @PutMapping("/experts/{id}/reset-password")
    public Result<String> resetExpertPassword(@PathVariable Integer id) {
        try {
            log.info("重置专家用户密码，ID: {}", id);

            // 先检查专家是否存在
            ExpertUser expert = expertUserMapper.findById(id);
            if (expert == null) {
                return Result.error("专家用户不存在");
            }

            // 重置密码为123456，使用BCrypt加密
            String rawPassword = "123456";
            String encodedPassword = passwordEncoder.encode(rawPassword);
            int result = expertUserMapper.updatePassword(id, encodedPassword);
            if (result > 0) {
                log.info("专家用户密码重置成功，ID: {}", id);
                return Result.success("密码重置成功，新密码为：123456");
            } else {
                log.warn("专家用户密码重置失败，ID: {}", id);
                return Result.error("密码重置失败");
            }
        } catch (Exception e) {
            log.error("重置专家用户密码异常: {}", e.getMessage(), e);
            return Result.error("重置密码失败: " + e.getMessage());
        }
    }

    /**
     * 删除普通用户
     */
    @DeleteMapping("/users/{id}")
    public Result<String> deleteUser(@PathVariable Integer id) {
        try {
            log.info("删除普通用户，ID: {}", id);

            // 先检查用户是否存在
            NormalUser user = normalUserMapper.findById(id);
            if (user == null) {
                return Result.error("普通用户不存在");
            }

            // 调用Mapper删除用户
            int result = normalUserMapper.deleteById(id);
            if (result > 0) {
                log.info("普通用户删除成功，ID: {}", id);
                return Result.success("普通用户删除成功");
            } else {
                log.warn("普通用户删除失败，ID: {}", id);
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("删除普通用户异常: {}", e.getMessage(), e);
            return Result.error("删除普通用户失败: " + e.getMessage());
        }
    }

    /**
     * 重置普通用户密码
     */
    @PutMapping("/users/{id}/reset-password")
    public Result<String> resetUserPassword(@PathVariable Integer id) {
        try {
            log.info("重置普通用户密码，ID: {}", id);

            // 先检查用户是否存在
            NormalUser user = normalUserMapper.findById(id);
            if (user == null) {
                return Result.error("普通用户不存在");
            }

            // 重置密码为123456，使用BCrypt加密
            String rawPassword = "123456";
            String encodedPassword = passwordEncoder.encode(rawPassword);
            int result = normalUserMapper.updatePassword(id, encodedPassword);
            if (result > 0) {
                log.info("普通用户密码重置成功，ID: {}", id);
                return Result.success("密码重置成功，新密码为：123456");
            } else {
                log.warn("普通用户密码重置失败，ID: {}", id);
                return Result.error("密码重置失败");
            }
        } catch (Exception e) {
            log.error("重置普通用户密码异常: {}", e.getMessage(), e);
            return Result.error("重置密码失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据分析信息
     */
    @GetMapping("/analytics")
    public Result<Map<String, Object>> getAnalyticsData() {
        try {
            log.info("获取数据分析信息");

            Map<String, Object> analyticsData = new HashMap<>();

            // 核心指标

            // 获取真实的用户统计
            int expertCount = 0;
            int normalUserCount = 0;
            try {
                expertCount = expertUserMapper.countTotal();
                normalUserCount = normalUserMapper.countTotal();
            } catch (Exception e) {
                log.warn("获取用户统计失败: {}", e.getMessage());
            }

            // 获取真实的内容统计
            int plantCount = 0;
            int pestDiseaseCount = 0;
            int pesticideCount = 0;
            try {
                plantCount = medicinalPlantMapper.findAll().size();
                pestDiseaseCount = pestDiseaseMapper.findAll().size();
                pesticideCount = pesticideMapper.findAll().size();
            } catch (Exception e) {
                log.warn("获取内容统计失败: {}", e.getMessage());
            }

            // 获取真实的互动统计
            int totalComments = 0;
            int totalHelpRequests = 0;
            try {
                totalComments = commentMapper.countTotal();
                totalHelpRequests = helpRequestMapper.countTotal();
            } catch (Exception e) {
                log.warn("获取互动统计失败: {}", e.getMessage());
            }

            // 构建核心指标
            List<Map<String, Object>> coreMetrics = new ArrayList<>();

            Map<String, Object> userMetric = new HashMap<>();
            userMetric.put("icon", "el-icon-user-solid");
            userMetric.put("label", "总用户数");
            userMetric.put("value", expertCount + normalUserCount);
            userMetric.put("trend", 12.5);
            coreMetrics.add(userMetric);

            Map<String, Object> contentMetric = new HashMap<>();
            contentMetric.put("icon", "el-icon-document");
            contentMetric.put("label", "总内容数");
            contentMetric.put("value", plantCount + pestDiseaseCount + pesticideCount);
            contentMetric.put("trend", 8.3);
            coreMetrics.add(contentMetric);

            Map<String, Object> interactionMetric = new HashMap<>();
            interactionMetric.put("icon", "el-icon-chat-dot-round");
            interactionMetric.put("label", "总互动数");
            interactionMetric.put("value", totalComments + totalHelpRequests);
            interactionMetric.put("trend", 15.7);
            coreMetrics.add(interactionMetric);

            Map<String, Object> activityMetric = new HashMap<>();
            activityMetric.put("icon", "el-icon-star-on");
            activityMetric.put("label", "活跃度");
            activityMetric.put("value", "78.5%");
            activityMetric.put("trend", 6.2);
            coreMetrics.add(activityMetric);

            analyticsData.put("metrics", coreMetrics);

            // 图表数据（使用真实数据库查询）
            Map<String, Object> charts = new HashMap<>();

            // 用户增长数据 - 获取过去7天的真实数据
            Map<String, Object> userGrowth = new HashMap<>();
            try {
                List<Map<String, Object>> expertGrowthData = expertUserMapper.countByDate(7);
                List<Map<String, Object>> normalGrowthData = normalUserMapper.countByDate(7);

                userGrowth.put("expertUsers", extractCountsFromDateData(expertGrowthData, 7));
                userGrowth.put("normalUsers", extractCountsFromDateData(normalGrowthData, 7));
            } catch (Exception e) {
                log.warn("获取用户增长数据失败: {}", e.getMessage());
                // 如果查询失败，返回空数组而不是随机数据
                userGrowth.put("expertUsers", new int[7]);
                userGrowth.put("normalUsers", new int[7]);
            }
            charts.put("userGrowth", userGrowth);

            // 内容分布数据 - 使用真实统计
            List<Map<String, Object>> contentDistribution = new ArrayList<>();
            contentDistribution.add(createPieData("药用植物", plantCount, "#4facfe"));
            contentDistribution.add(createPieData("病虫害", pestDiseaseCount, "#f093fb"));
            contentDistribution.add(createPieData("农药信息", pesticideCount, "#43e97b"));
            contentDistribution.add(createPieData("用户评论", totalComments, "#667eea"));
            contentDistribution.add(createPieData("求助信息", totalHelpRequests, "#ffeaa7"));
            charts.put("contentDistribution", contentDistribution);

            // 求助趋势数据 - 获取过去7天的真实数据
            try {
                List<Map<String, Object>> helpTrendData = helpRequestMapper.countByDate(7);
                charts.put("helpTrend", helpTrendData);
            } catch (Exception e) {
                log.warn("获取求助趋势数据失败: {}", e.getMessage());
                // 如果查询失败，返回空列表
                charts.put("helpTrend", new ArrayList<>());
            }

            // 评论趋势数据 - 获取过去7天的真实数据
            try {
                List<Map<String, Object>> commentTrendData = commentMapper.countByDate(7);
                charts.put("commentTrend", commentTrendData);
            } catch (Exception e) {
                log.warn("获取评论趋势数据失败: {}", e.getMessage());
                // 如果查询失败，返回空列表
                charts.put("commentTrend", new ArrayList<>());
            }

            analyticsData.put("charts", charts);

            log.info("数据分析信息获取成功");
            return Result.success(analyticsData);
        } catch (Exception e) {
            log.error("获取数据分析信息异常: {}", e.getMessage(), e);
            return Result.error("获取数据分析信息失败: " + e.getMessage());
        }
    }



    // 辅助方法：从日期数据中提取计数数组
    private int[] extractCountsFromDateData(List<Map<String, Object>> dateData, int days) {
        int[] counts = new int[days];

        // 创建日期到计数的映射
        Map<String, Integer> dateCountMap = new HashMap<>();
        for (Map<String, Object> data : dateData) {
            String date = data.get("date").toString();
            Integer count = ((Number) data.get("count")).intValue();
            dateCountMap.put(date, count);
        }

        // 填充过去days天的数据
        java.time.LocalDate today = java.time.LocalDate.now();
        for (int i = 0; i < days; i++) {
            java.time.LocalDate date = today.minusDays(days - 1 - i);
            String dateStr = date.toString();
            counts[i] = dateCountMap.getOrDefault(dateStr, 0);
        }

        return counts;
    }

    // 辅助方法：创建饼图数据
    private Map<String, Object> createPieData(String name, int value, String color) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("value", value);
        data.put("itemStyle", Map.of("color", color));
        return data;
    }

    /**
     * 获取管理员个人信息
     */
    @GetMapping("/profile")
    public Result<Admin> getAdminProfile(@RequestParam Integer adminId) {
        try {
            log.info("获取管理员个人信息，ID: {}", adminId);

            Admin admin = adminMapper.findById(adminId);
            if (admin == null) {
                return Result.error("管理员不存在");
            }

            // 不返回密码
            admin.setPassword(null);

            return Result.success(admin);
        } catch (Exception e) {
            log.error("获取管理员个人信息异常: {}", e.getMessage(), e);
            return Result.error("获取个人信息失败: " + e.getMessage());
        }
    }

    /**
     * 更新管理员个人信息
     */
    @PutMapping("/profile")
    public Result<String> updateAdminProfile(@RequestBody Admin admin) {
        try {
            log.info("更新管理员个人信息，ID: {}", admin.getId());

            Admin existingAdmin = adminMapper.findById(admin.getId());
            if (existingAdmin == null) {
                return Result.error("管理员不存在");
            }

            // 只更新允许修改的字段
            existingAdmin.setEmail(admin.getEmail());
            existingAdmin.setPhone(admin.getPhone());
            existingAdmin.setAvatarUrl(admin.getAvatarUrl());

            int result = adminMapper.updateProfile(existingAdmin);

            if (result > 0) {
                log.info("管理员个人信息更新成功，ID: {}", admin.getId());
                return Result.success("个人信息更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("更新管理员个人信息异常: {}", e.getMessage(), e);
            return Result.error("更新个人信息失败: " + e.getMessage());
        }
    }

    /**
     * 修改管理员密码
     */
    @PutMapping("/profile/password")
    public Result<String> updateAdminPassword(@RequestBody Map<String, String> passwordData) {
        try {
            Integer adminId = Integer.valueOf(passwordData.get("adminId"));
            String oldPassword = passwordData.get("oldPassword");
            String newPassword = passwordData.get("newPassword");

            log.info("管理员修改密码，ID: {}", adminId);

            Admin admin = adminMapper.findById(adminId);
            if (admin == null) {
                return Result.error("管理员不存在");
            }

            // 验证旧密码
            if (!passwordEncoder.matches(oldPassword, admin.getPassword())) {
                return Result.error("原密码错误");
            }

            // 加密新密码
            String encodedNewPassword = passwordEncoder.encode(newPassword);
            int result = adminMapper.updatePassword(adminId, encodedNewPassword);

            if (result > 0) {
                log.info("管理员密码修改成功，ID: {}", adminId);
                return Result.success("密码修改成功");
            } else {
                return Result.error("密码修改失败");
            }
        } catch (Exception e) {
            log.error("修改管理员密码异常: {}", e.getMessage(), e);
            return Result.error("密码修改失败: " + e.getMessage());
        }
    }


}
