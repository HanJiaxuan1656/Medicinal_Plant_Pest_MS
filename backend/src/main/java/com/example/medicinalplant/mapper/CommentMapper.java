package com.example.medicinalplant.mapper;

import com.example.medicinalplant.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper {
    
    /**
     * 根据目标类型和ID查询评论列表
     */
    List<Comment> findByTargetTypeAndTargetId(@Param("targetType") String targetType, @Param("targetId") Integer targetId);
    
    /**
     * 根据ID查询评论
     */
    Comment findById(@Param("id") Integer id);
    
    /**
     * 根据状态查询评论列表
     */
    List<Comment> findByStatus(@Param("status") String status);
    
    /**
     * 插入评论
     */
    int insert(Comment comment);
    
    /**
     * 更新评论
     */
    int update(Comment comment);
    
    /**
     * 根据ID删除评论
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 根据状态统计评论数量
     */
    int countByStatus(@Param("status") String status);

    /**
     * 根据用户ID统计评论数量
     */
    int countByUserId(@Param("userId") Integer userId);

    /**
     * 统计总评论数量
     */
    int countTotal();

    /**
     * 按日期统计评论数量
     */
    java.util.List<java.util.Map<String, Object>> countByDate(@Param("days") int days);
}