package com.example.medicinalplant.config;

import com.example.medicinalplant.util.UserIdThreadLocal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final String SECRET_KEY = "SecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKeySecretKey";

    @Bean
    public OncePerRequestFilter jwtFilter() {
        return new OncePerRequestFilter() {
            // 定义不需要JWT验证的路径列表
            private final List<String> excludedPaths = Arrays.asList(
                    "/api/auth/admin/login",
                    "/api/auth/expert/login",
                    "/api/auth/normal/login",
                    "/api/auth/admin/register",
                    "/api/auth/expert/register",
                    "/api/auth/normal/register",
                    "/api/auth/check-username"
            );

            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                // 获取请求URI
                String requestURI = request.getRequestURI();

                // 检查请求URI是否在排除列表中
                if (excludedPaths.stream().anyMatch(requestURI::startsWith)) {
                    System.out.println("跳过JWT验证: " + requestURI);
                    filterChain.doFilter(request, response);
                    return;
                }

                // 原有的JWT解析逻辑
                System.out.println("触发校验token");
                String header = request.getHeader("Authorization");
                if (header != null && header.startsWith("Bearer ")) {
                    String token = header.substring(7);
                    try {
                        Claims claims = Jwts.parser()
                                .setSigningKey(SECRET_KEY)
                                .parseClaimsJws(token)
                                .getBody();
                        Integer userId = claims.get("userId", Integer.class);
                        UserIdThreadLocal.set(userId);
                        String username = claims.getSubject();
                        System.out.println("username: " + username);
                        Authentication auth = new UsernamePasswordAuthenticationToken(
                                username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );
                        SecurityContextHolder.getContext().setAuthentication(auth);
                    } catch (Exception e) {
                        System.out.println("token解密失败" + token);
                        e.printStackTrace();
                    }
                }
                filterChain.doFilter(request, response);
            }
        };
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configure(http))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtFilter(), org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}