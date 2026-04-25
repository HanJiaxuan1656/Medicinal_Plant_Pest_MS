package com.example.medicinalplant.controller;

import com.example.medicinalplant.common.Result;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import com.example.medicinalplant.entity.Admin;
import com.example.medicinalplant.entity.ExpertUser;
import com.example.medicinalplant.entity.NormalUser;
import com.example.medicinalplant.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    // 登录接口
    @PostMapping("/admin/login")
    public Result<Admin> adminLogin(@RequestBody Map<String, String> loginForm) {
        Admin admin = authService.adminLogin(loginForm.get("username"), loginForm.get("password"));
        if (admin != null) {
            String token = Jwts.builder()
                    .setSubject(admin.getUsername())
                    .claim("userId", admin.getId())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(SignatureAlgorithm.HS512, "SecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKey")
                    .compact();
            return Result.success(admin, token);
        }
        return Result.error("用户名或密码错误");
    }

    @PostMapping("/expert/login")
    public Result<ExpertUser> expertLogin(@RequestBody Map<String, String> loginForm) {
        ExpertUser expertUser = authService.expertLogin(loginForm.get("username"), loginForm.get("password"));
        if (expertUser != null) {
            String token = Jwts.builder()
                    .setSubject(expertUser.getUsername())
                    .claim("userId", expertUser.getId())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(SignatureAlgorithm.HS512, "SecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKey")
                    .compact();
            return Result.success(expertUser, token);
        }
        return Result.error("用户名或密码错误");
    }

    @PostMapping("/normal/login")
    public Result<NormalUser> normalLogin(@RequestBody Map<String, String> loginForm) {
        NormalUser normalUser = authService.normalLogin(loginForm.get("username"), loginForm.get("password"));
        if (normalUser != null) {
            String token = Jwts.builder()
                    .setSubject(normalUser.getUsername())
                    .claim("userId", normalUser.getId())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                    .signWith(SignatureAlgorithm.HS512, "SecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKey")
                    .compact();
            return Result.success(normalUser, token);
        }
        return Result.error("用户名或密码错误");
    }

    // 注册接口
    @PostMapping("/admin/register")
    public Result<String> adminRegister(@RequestBody Admin admin) {
        try {
            authService.adminRegister(admin);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/expert/register")
    public Result<String> expertRegister(@RequestBody ExpertUser expertUser) {
        try {
            authService.expertRegister(expertUser);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/normal/register")
    public Result<String> normalRegister(@RequestBody NormalUser normalUser) {
        try {
            authService.normalRegister(normalUser);
            return Result.success("注册成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    // 检查用户名是否存在
    @GetMapping("/check-username")
    public Result<Boolean> checkUsername(@RequestParam String username, @RequestParam String userType) {
        boolean exists = authService.isUsernameExist(username, userType);
        return Result.success(exists);
    }
}