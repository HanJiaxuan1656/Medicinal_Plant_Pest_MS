package com.example.medicinalplant.service.impl;

import com.example.medicinalplant.entity.Admin;
import com.example.medicinalplant.entity.ExpertUser;
import com.example.medicinalplant.entity.NormalUser;
import com.example.medicinalplant.mapper.AdminMapper;
import com.example.medicinalplant.mapper.ExpertUserMapper;
import com.example.medicinalplant.mapper.NormalUserMapper;
import com.example.medicinalplant.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AdminMapper adminMapper;
    
    @Autowired
    private ExpertUserMapper expertUserMapper;
    
    @Autowired
    private NormalUserMapper normalUserMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Admin adminLogin(String username, String password) {
        Admin admin = adminMapper.findByUsername(username);
        if (admin != null && passwordEncoder.matches(password, admin.getPassword())) {
            admin.setPassword(null); // 清除密码
            return admin;
        }
        return null;
    }

    @Override
    public ExpertUser expertLogin(String username, String password) {
        ExpertUser expertUser = expertUserMapper.findByUsername(username);
        if (expertUser != null && passwordEncoder.matches(password, expertUser.getPassword())) {
            expertUser.setPassword(null);
            return expertUser;
        }
        return null;
    }

    @Override
    public NormalUser normalLogin(String username, String password) {
        NormalUser normalUser = normalUserMapper.findByUsername(username);
        if (normalUser != null && passwordEncoder.matches(password, normalUser.getPassword())) {
            normalUser.setPassword(null);
            return normalUser;
        }
        return null;
    }

    @Override
    public void adminRegister(Admin admin) {
        if (isUsernameExist(admin.getUsername(), "admin")) {
            throw new RuntimeException("用户名已存在");
        }
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        adminMapper.insert(admin);
    }

    @Override
    public void expertRegister(ExpertUser expertUser) {
        if (isUsernameExist(expertUser.getUsername(), "expert")) {
            throw new RuntimeException("用户名已存在");
        }
        expertUser.setPassword(passwordEncoder.encode(expertUser.getPassword()));
        expertUserMapper.insert(expertUser);
    }

    @Override
    public void normalRegister(NormalUser normalUser) {
        if (isUsernameExist(normalUser.getUsername(), "normal")) {
            throw new RuntimeException("用户名已存在");
        }
        normalUser.setPassword(passwordEncoder.encode(normalUser.getPassword()));
        normalUserMapper.insert(normalUser);
    }

    @Override
    public boolean isUsernameExist(String username, String userType) {
        switch (userType) {
            case "admin":
                return adminMapper.findByUsername(username) != null;
            case "expert":
                return expertUserMapper.findByUsername(username) != null;
            case "normal":
                return normalUserMapper.findByUsername(username) != null;
            default:
                return false;
        }
    }
} 