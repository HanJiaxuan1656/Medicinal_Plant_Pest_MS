package com.example.medicinalplant.service;

import com.example.medicinalplant.entity.Admin;
import com.example.medicinalplant.entity.ExpertUser;
import com.example.medicinalplant.entity.NormalUser;

public interface AuthService {
    // 登录方法
    Admin adminLogin(String username, String password);
    ExpertUser expertLogin(String username, String password);
    NormalUser normalLogin(String username, String password);
    
    // 注册方法
    void adminRegister(Admin admin);
    void expertRegister(ExpertUser expertUser);
    void normalRegister(NormalUser normalUser);
    
    // 检查用户名是否存在
    boolean isUsernameExist(String username, String userType);
} 