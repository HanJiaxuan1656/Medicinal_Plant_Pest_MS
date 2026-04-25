package com.example.medicinalplant.mapper;

import com.example.medicinalplant.entity.HelpRequest;
import com.example.medicinalplant.entity.HelpReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 求助Mapper接口
 */
@Mapper
public interface HelpRequestMapper {
    
    /**
     * 查询所有求助
     */
    List<HelpRequest> findAll();
    
    /**
     * 查询用户的求助列表
     */
    List<HelpRequest> findByUserId(@Param("userId") Integer userId);
    
    /**
     * 根据ID查询求助
     */
    HelpRequest findById(@Param("id") Integer id);
    
    /**
     * 根据状态查询求助列表
     */
    List<HelpRequest> findByStatus(@Param("status") String status);
    
    /**
     * 插入求助
     */
    int insert(HelpRequest helpRequest);
    
    /**
     * 更新求助
     */
    int update(HelpRequest helpRequest);
    
    /**
     * 根据ID删除求助
     */
    int deleteById(@Param("id") Integer id);
    
    /**
     * 查询求助的回复列表
     */
    List<HelpReply> findRepliesByHelpId(@Param("helpId") Integer helpId);
    
    /**
     * 插入求助回复
     */
    int insertReply(HelpReply helpReply);
    
    /**
     * 根据状态统计求助数量
     */
    int countByStatus(@Param("status") String status);

    /**
     * 统计指定专家回复的求助数量
     */
    int countRepliedByExpert(@Param("expertId") Integer expertId);

    /**
     * 根据用户ID统计求助数量
     */
    int countByUserId(@Param("userId") Integer userId);

    /**
     * 统计总求助数量
     */
    int countTotal();

    /**
     * 按日期统计求助数量
     */
    java.util.List<java.util.Map<String, Object>> countByDate(@Param("days") int days);
}