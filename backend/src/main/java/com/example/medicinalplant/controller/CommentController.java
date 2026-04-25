package com.example.medicinalplant.controller;

import com.example.medicinalplant.common.Result;
import com.example.medicinalplant.entity.Comment;
import com.example.medicinalplant.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/comments")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 获取某个目标的所有评论
     */
    @GetMapping
    public Result<List<Comment>> getCommentsByTarget(
            @RequestParam String targetType, 
            @RequestParam Integer targetId) {
        log.info("获取{}类型ID为{}的评论", targetType, targetId);
        return Result.success(commentService.getCommentsByTarget(targetType, targetId));
    }
    
    /**
     * 添加评论
     */
    @PostMapping
    public Result<Comment> addComment(@RequestBody Comment comment) {
        try {
            log.info("添加评论: {}", comment);
            commentService.addComment(comment);
            return Result.success(comment);
        } catch (Exception e) {
            log.error("添加评论失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 审核评论
     */
    @PutMapping("/{id}/review")
    public Result<String> reviewComment(
            @PathVariable Integer id, 
            @RequestParam String status,
            @RequestParam Integer adminId) {
        try {
            log.info("审核评论ID: {}, 状态: {}, 管理员ID: {}", id, status, adminId);
            commentService.reviewComment(id, status, adminId);
            return Result.success("审核成功");
        } catch (Exception e) {
            log.error("审核评论失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteComment(@PathVariable Integer id) {
        try {
            log.info("删除评论ID: {}", id);
            commentService.deleteComment(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除评论失败", e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取待审核评论
     */
    @GetMapping("/pending")
    public Result<List<Comment>> getPendingComments() {
        log.info("获取待审核评论");
        return Result.success(commentService.getPendingComments());
    }
} 