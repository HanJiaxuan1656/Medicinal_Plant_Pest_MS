package com.example.medicinalplant.service;

import com.example.medicinalplant.entity.Comment;

import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService {
    
    /**
     * 根据目标类型和ID获取评论列表
     * 
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @return 评论列表
     */
    List<Comment> getCommentsByTarget(String targetType, Integer targetId);
    
    /**
     * 添加评论
     * 
     * @param comment 评论对象
     */
    void addComment(Comment comment);
    
    /**
     * 审核评论
     * 
     * @param id 评论ID
     * @param status 状态
     * @param adminId 管理员ID
     */
    void reviewComment(Integer id, String status, Integer adminId);
    
    /**
     * 删除评论
     * 
     * @param id 评论ID
     */
    void deleteComment(Integer id);
    
    /**
     * 获取待审核评论列表
     * 
     * @return 待审核评论列表
     */
    List<Comment> getPendingComments();
} 