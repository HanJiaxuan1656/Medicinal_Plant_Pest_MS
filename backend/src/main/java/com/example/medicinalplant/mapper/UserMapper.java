package com.example.medicinalplant.mapper;

import com.example.medicinalplant.entity.Admin;
import com.example.medicinalplant.entity.ExpertUser;
import com.example.medicinalplant.entity.NormalUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper {
    
    /**
     * 根据用户名查询管理员
     */
    Admin findAdminByUsername(@Param("username") String username);
    
    /**
     * 根据用户名查询专家用户
     */
    ExpertUser findExpertByUsername(@Param("username") String username);
    
    /**
     * 根据用户名查询普通用户
     */
    NormalUser findNormalByUsername(@Param("username") String username);
    
    /**
     * 插入管理员
     */
    int insertAdmin(Admin admin);
    
    /**
     * 插入专家用户
     */
    int insertExpert(ExpertUser expertUser);
    
    /**
     * 插入普通用户
     */
    int insertNormal(NormalUser normalUser);
    
    /**
     * 统计专家用户数量
     */
    int countExpertUsers();
    
    /**
     * 统计普通用户数量
     */
    int countNormalUsers();
    
    /**
     * 获取所有专家用户
     */
    List<ExpertUser> findAllExperts();
    
    /**
     * 获取所有普通用户
     */
    List<NormalUser> findAllNormalUsers();
} 