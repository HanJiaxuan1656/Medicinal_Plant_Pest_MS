package com.example.medicinalplant.service.impl;

import com.example.medicinalplant.entity.Comment;
import com.example.medicinalplant.mapper.CommentMapper;
import com.example.medicinalplant.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Override
    public List<Comment> getCommentsByTarget(String targetType, Integer targetId) {
        log.info("获取{}类型ID为{}的评论", targetType, targetId);
        return commentMapper.findByTargetTypeAndTargetId(targetType, targetId);
    }
    
    @Override
    public void addComment(Comment comment) {
        log.info("添加评论: {}", comment);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setStatus("pending");
        commentMapper.insert(comment);
    }
    
    @Override
    public void reviewComment(Integer id, String status, Integer adminId) {
        log.info("审核评论ID: {}, 状态: {}, 管理员ID: {}", id, status, adminId);
        Comment comment = commentMapper.findById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        comment.setStatus(status);
        comment.setReviewAdminId(adminId);
        commentMapper.update(comment);
    }
    
    @Override
    public void deleteComment(Integer id) {
        log.info("删除评论ID: {}", id);
        commentMapper.deleteById(id);
    }
    
    @Override
    public List<Comment> getPendingComments() {
        log.info("获取待审核评论");
        return commentMapper.findByStatus("pending");
    }
} 