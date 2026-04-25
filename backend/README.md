# 药用植物病虫害管理系统 - 后端

## 项目简介

药用植物病虫害管理系统后端服务，基于Spring Boot框架开发，提供完整的RESTful API接口，支持药用植物信息管理、病虫害识别、农药推荐、专家咨询等功能。

## 技术栈

- **框架**: Spring Boot 2.7.x
- **数据库**: MySQL 8.0
- **ORM**: MyBatis
- **安全**: Spring Security + JWT
- **文档**: Swagger/OpenAPI 3
- **构建工具**: Maven 3.6+
- **JDK版本**: Java 8+

## 功能特性

### 🌿 核心功能
- **植物管理**: 药用植物信息的增删改查
- **病虫害管理**: 病虫害信息管理和识别
- **农药管理**: 农药信息和使用指导
- **关联关系**: 植物-病虫害、病虫害-农药关联管理
- **求助系统**: 用户求助和专家回复
- **评论系统**: 多目标评论功能

### 👥 用户系统
- **多角色支持**: 管理员、专家用户、普通用户
- **权限控制**: 基于角色的访问控制
- **用户认证**: JWT Token认证
- **个人中心**: 用户信息管理、密码修改

### 📊 管理功能
- **数据统计**: 用户、内容、活跃度统计
- **内容审核**: 评论和求助内容审核
- **用户管理**: 用户信息管理和权限分配

## 项目结构

```
backend/
├── src/main/java/com/example/medicinalplant/
│   ├── config/              # 配置类
│   │   ├── SecurityConfig.java
│   │   └── SwaggerConfig.java
│   ├── controller/          # 控制器层
│   │   ├── AuthController.java
│   │   ├── NormalController.java
│   │   ├── ExpertController.java
│   │   ├── AdminController.java
│   │   └── FileController.java
│   ├── entity/              # 实体类
│   │   ├── Plant.java
│   │   ├── PestDisease.java
│   │   ├── Pesticide.java
│   │   ├── NormalUser.java
│   │   ├── ExpertUser.java
│   │   ├── AdminUser.java
│   │   ├── Comment.java
│   │   └── HelpRequest.java
│   ├── mapper/              # 数据访问层
│   │   ├── PlantMapper.java
│   │   ├── PestDiseaseMapper.java
│   │   └── ...
│   ├── service/             # 服务层
│   │   ├── PlantService.java
│   │   ├── PestDiseaseService.java
│   │   └── ...
│   ├── vo/                  # 视图对象
│   │   ├── PageVO.java
│   │   └── Result.java
│   └── MedicinalPlantApplication.java
├── src/main/resources/
│   ├── mapper/              # MyBatis映射文件
│   ├── application.yml      # 应用配置
│   └── schema.sql          # 数据库结构
└── pom.xml                 # Maven配置
```

## 快速开始

### 环境要求

- JDK 8 或更高版本
- Maven 3.6+
- MySQL 8.0+
- Redis (可选，用于缓存)

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd backend
```

2. **配置数据库**
```sql
-- 创建数据库
CREATE DATABASE medicinal_plant_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户（可选）
CREATE USER 'medicinal_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON medicinal_plant_db.* TO 'medicinal_user'@'localhost';
FLUSH PRIVILEGES;
```

3. **修改配置文件**
```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medicinal_plant_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: medicinal_user
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

4. **安装依赖并运行**
```bash
# 安装依赖
mvn clean install

# 运行项目
mvn spring-boot:run
```

5. **访问应用**
- API服务: http://localhost:8080
- Swagger文档: http://localhost:8080/swagger-ui.html

## API 接口

### 认证接口
```
POST /api/auth/login          # 用户登录
POST /api/auth/register       # 用户注册
POST /api/auth/logout         # 用户登出
```

### 普通用户接口
```
GET  /api/normal/plants       # 获取植物列表
GET  /api/normal/plants/{id}  # 获取植物详情
GET  /api/normal/pests        # 获取病虫害列表
GET  /api/normal/pesticides   # 获取农药列表
POST /api/normal/help-requests # 提交求助
GET  /api/normal/profile      # 获取个人信息
PUT  /api/normal/profile      # 更新个人信息
PUT  /api/normal/change-password # 修改密码
```

### 专家用户接口
```
GET  /api/expert/help-requests # 获取求助列表
POST /api/expert/help-replies  # 回复求助
POST /api/expert/plants        # 添加植物信息
PUT  /api/expert/plants/{id}   # 更新植物信息
```

### 管理员接口
```
GET  /api/admin/users         # 获取用户列表
GET  /api/admin/statistics    # 获取统计数据
POST /api/admin/users/{id}/disable # 禁用用户
```

### 文件上传接口
```
POST /api/files/upload        # 文件上传
GET  /api/files/{filename}    # 文件访问
```

## 数据库设计

### 主要数据表

- **plants**: 药用植物信息表
- **pest_diseases**: 病虫害信息表
- **pesticides**: 农药信息表
- **plant_disease_links**: 植物-病虫害关联表
- **disease_pesticide_links**: 病虫害-农药关联表
- **normal_users**: 普通用户表
- **expert_users**: 专家用户表
- **admin_users**: 管理员用户表
- **help_requests**: 求助信息表
- **help_replies**: 求助回复表
- **comments**: 评论表

## 配置说明

### 应用配置 (application.yml)
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/medicinal_plant_db
    username: your_username
    password: your_password
  
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB

file:
  upload-path: ./uploads/
  
jwt:
  secret: your-secret-key
  expiration: 86400000  # 24小时
```

## 部署指南

### 开发环境
```bash
mvn spring-boot:run
```

### 生产环境
```bash
# 打包
mvn clean package -Dmaven.test.skip=true

# 运行
java -jar target/medicinal-plant-0.0.1-SNAPSHOT.jar

# 或使用Docker
docker build -t medicinal-plant-backend .
docker run -p 8080:8080 medicinal-plant-backend
```

## 常见问题

### Q: 数据库连接失败
A: 检查数据库配置、确保MySQL服务运行、验证用户名密码

### Q: 文件上传失败
A: 检查上传目录权限、文件大小限制配置

### Q: JWT Token过期
A: 重新登录获取新的Token，或调整Token过期时间

## 开发指南

### 添加新的API接口
1. 在对应的Controller中添加方法
2. 在Service层实现业务逻辑
3. 在Mapper中添加数据库操作
4. 更新Swagger文档注解

### 数据库迁移
1. 在resources/db/migration目录添加SQL文件
2. 使用Flyway进行版本控制

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目维护者: [您的姓名]
- 邮箱: [您的邮箱]
- 项目地址: [项目仓库地址]
