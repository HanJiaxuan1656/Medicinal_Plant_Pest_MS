# 药用植物病虫害管理系统

## 项目简介

药用植物病虫害管理系统是一个综合性的数字化平台，旨在为药用植物种植者、研究人员和专家提供全面的病虫害管理解决方案。系统包含Web端管理平台和微信小程序，支持多角色用户管理、植物信息查询、病虫害识别、农药推荐、专家咨询等功能。

## 系统架构

```
药用植物病虫害管理系统
├── 后端服务 (Spring Boot)
│   ├── RESTful API
│   ├── 用户认证与授权
│   ├── 数据库管理
│   └── 文件上传服务
├── Web前端 (Vue.js)
│   ├── 管理员后台
│   ├── 专家工作台
│   └── 普通用户界面
└── 微信小程序
    ├── 植物查询
    ├── 病虫害识别
    ├── 求助系统
    └── 个人中心
```

## 技术栈

### 后端技术
- **框架**: Spring Boot 2.7.x
- **数据库**: MySQL 8.0
- **ORM**: MyBatis
- **安全**: Spring Security + JWT
- **文档**: Swagger/OpenAPI 3
- **构建**: Maven 3.6+

### 前端技术
- **框架**: Vue.js 2.6.x
- **UI库**: Element UI 2.15.x
- **路由**: Vue Router 3.x
- **状态管理**: Vuex 3.x
- **HTTP客户端**: Axios
- **图表**: ECharts 5.x

### 小程序技术
- **框架**: 微信小程序原生框架
- **开发工具**: 微信开发者工具
- **UI组件**: 小程序原生组件

## 功能特性

### 🌿 核心功能
- **植物管理**: 药用植物信息的完整管理
- **病虫害识别**: 病虫害信息管理和识别指导
- **农药推荐**: 农药信息和使用指南
- **关联管理**: 植物-病虫害-农药关系管理
- **求助系统**: 用户求助和专家回复
- **评论系统**: 多目标评论功能

### 👥 用户角色
- **管理员**: 系统管理、用户管理、数据统计
- **专家用户**: 内容管理、求助回复、专业指导
- **普通用户**: 信息查询、求助提交、评论互动

### 📱 多端支持
- **Web端**: 完整的管理和查询功能
- **小程序端**: 移动端便捷访问

## 项目结构

```
project/
├── backend/                 # 后端服务
│   ├── src/main/java/      # Java源码
│   ├── src/main/resources/ # 配置文件
│   ├── pom.xml            # Maven配置
│   └── README.md          # 后端文档
├── frontend/               # Web前端
│   ├── src/               # Vue源码
│   ├── public/            # 静态资源
│   ├── package.json       # 依赖配置
│   ├── vue.config.js      # Vue配置
│   └── README.md          # 前端文档
├── miniprogram/           # 微信小程序
│   ├── pages/             # 页面文件
│   ├── components/        # 组件文件
│   ├── utils/             # 工具函数
│   ├── app.js             # 应用入口
│   ├── app.json           # 应用配置
│   └── README.md          # 小程序文档
├── docs/                  # 项目文档
├── sql/                   # 数据库脚本
└── README.md              # 项目总览
```

## 快速开始

### 环境要求
- JDK 8+
- Node.js 14+
- MySQL 8.0+
- Maven 3.6+
- 微信开发者工具

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd project
```

2. **数据库配置**
```sql
CREATE DATABASE medicinal_plant_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. **启动后端服务**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

4. **启动前端服务**
```bash
cd frontend
npm install
npm run serve
```

5. **配置小程序**
- 使用微信开发者工具打开 `miniprogram` 目录
- 配置后端API地址
- 编译运行

### 访问地址
- 后端API: http://localhost:8080
- Web前端: http://localhost:8081
- API文档: http://localhost:8080/swagger-ui.html

## 用户指南

### 管理员功能
- **用户管理**: 管理普通用户和专家用户
- **内容审核**: 审核评论和求助内容
- **数据统计**: 查看系统使用统计
- **系统配置**: 系统参数配置

### 专家用户功能
- **内容管理**: 管理植物、病虫害、农药信息
- **关联管理**: 维护数据间的关联关系
- **求助回复**: 回复用户求助请求
- **专业指导**: 提供专业建议

### 普通用户功能
- **信息查询**: 查询植物、病虫害、农药信息
- **求助提交**: 向专家寻求帮助
- **评论互动**: 发表评论和互动
- **个人中心**: 管理个人信息

## API 接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户登出

### 数据接口
- `GET /api/normal/plants` - 获取植物列表
- `GET /api/normal/pests` - 获取病虫害列表
- `GET /api/normal/pesticides` - 获取农药列表
- `POST /api/normal/help-requests` - 提交求助

### 管理接口
- `GET /api/admin/users` - 获取用户列表
- `GET /api/admin/statistics` - 获取统计数据
- `POST /api/expert/plants` - 添加植物信息

详细API文档请访问: http://localhost:8080/swagger-ui.html

## 部署指南

### 开发环境部署
```bash
# 后端
cd backend && mvn spring-boot:run

# 前端
cd frontend && npm run serve

# 小程序
# 使用微信开发者工具打开miniprogram目录
```

### 生产环境部署
```bash
# 后端打包
cd backend && mvn clean package

# 前端打包
cd frontend && npm run build

# 部署到服务器
java -jar backend/target/medicinal-plant-0.0.1-SNAPSHOT.jar
```

### Docker部署
```bash
# 构建镜像
docker build -t medicinal-plant-backend ./backend
docker build -t medicinal-plant-frontend ./frontend

# 运行容器
docker run -p 8080:8080 medicinal-plant-backend
docker run -p 8081:8081 medicinal-plant-frontend
```

## 数据库设计

### 主要数据表
- **plants**: 药用植物信息
- **pest_diseases**: 病虫害信息
- **pesticides**: 农药信息
- **users**: 用户信息（普通用户、专家、管理员）
- **help_requests**: 求助信息
- **comments**: 评论信息
- **关联表**: 植物-病虫害、病虫害-农药关联

## 开发规范

### 代码规范
- 后端遵循阿里巴巴Java开发手册
- 前端遵循Vue.js官方风格指南
- 小程序遵循微信小程序开发规范

### 提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 代码重构

### 分支管理
- main: 主分支
- develop: 开发分支
- feature/*: 功能分支
- hotfix/*: 热修复分支

## 常见问题

### Q: 数据库连接失败
A: 检查MySQL服务状态和配置文件中的数据库连接信息

### Q: 前端页面空白
A: 检查后端服务是否启动，API地址是否正确

### Q: 小程序无法访问API
A: 检查小程序域名配置和HTTPS证书

### Q: 文件上传失败
A: 检查上传目录权限和文件大小限制

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 版本历史

- v1.0.0 - 初始版本发布
- v1.1.0 - 添加小程序支持
- v1.2.0 - 优化用户体验
- v1.3.0 - 添加数据统计功能

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目维护者: [您的姓名]
- 邮箱: [您的邮箱]
- 项目地址: [项目仓库地址]
- 问题反馈: [Issues页面]

---

**药用植物病虫害管理系统** - 专业、便捷、高效的植物病虫害管理解决方案 🌿
