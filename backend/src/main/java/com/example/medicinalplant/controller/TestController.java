package com.example.medicinalplant.controller;

import com.example.medicinalplant.common.Result;
import com.example.medicinalplant.entity.Comment;
import com.example.medicinalplant.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 测试接口
     */
    @GetMapping("/comment")
    public Result<List<Comment>> testComment() {
        log.info("测试CommentService");
        
        // 由于还没有实际数据，这里返回一个模拟的评论列表
        List<Comment> comments = new ArrayList<>();
        Comment comment = new Comment();
        comment.setId(1);
        comment.setUserId(1);
        comment.setUserType("normal");
        comment.setTargetType("plant");
        comment.setTargetId(1);
        comment.setContent("这是一条测试评论");
        comment.setStatus("approved");
        comments.add(comment);
        
        return Result.success(comments);
    }
} 