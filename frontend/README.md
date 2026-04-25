# 药用植物病虫害管理系统 - 前端

## 项目简介

药用植物病虫害管理系统前端应用，基于Vue.js框架开发，提供直观易用的Web界面，支持多角色用户管理、植物信息查询、病虫害识别、专家咨询等功能。

## 技术栈

- **框架**: Vue.js 2.6.x
- **UI组件库**: Element UI 2.15.x
- **路由**: Vue Router 3.x
- **状态管理**: Vuex 3.x
- **HTTP客户端**: Axios
- **图表库**: ECharts 5.x
- **构建工具**: Vue CLI 4.x
- **样式预处理**: Sass/SCSS

## 功能特性

### 🌿 用户功能
- **植物查询**: 药用植物信息浏览和搜索
- **病虫害识别**: 病虫害信息查询和识别指导
- **农药查询**: 农药信息和使用指南
- **求助系统**: 提交求助请求，获得专家回复
- **评论互动**: 对植物、病虫害、农药进行评论
- **个人中心**: 个人信息管理、密码修改、头像上传

### 👨‍🔬 专家功能
- **内容管理**: 植物、病虫害、农药信息管理
- **关联管理**: 植物-病虫害、病虫害-农药关系管理
- **求助回复**: 回复用户求助，提供专业指导
- **数据维护**: 维护和更新专业数据

### 👨‍💼 管理员功能
- **用户管理**: 普通用户和专家用户管理
- **内容审核**: 评论和求助内容审核
- **数据统计**: 用户活跃度、内容统计分析
- **系统监控**: 系统运行状态监控

## 项目结构

```
frontend/
├── public/                  # 静态资源
│   ├── index.html
│   └── favicon.ico
├── src/
│   ├── api/                 # API接口
│   │   ├── admin.js
│   │   ├── expert.js
│   │   ├── normal.js
│   │   └── auth.js
│   ├── assets/              # 资源文件
│   │   ├── css/
│   │   └── images/
│   ├── components/          # 公共组件
│   │   └── plant/
│   ├── router/              # 路由配置
│   │   └── index.js
│   ├── utils/               # 工具函数
│   │   ├── request.js
│   │   └── date.js
│   ├── views/               # 页面组件
│   │   ├── admin/           # 管理员页面
│   │   │   ├── Layout.vue
│   │   │   ├── Dashboard.vue
│   │   │   ├── Users.vue
│   │   │   └── Analytics.vue
│   │   ├── expert/          # 专家页面
│   │   │   ├── Layout.vue
│   │   │   ├── Dashboard.vue
│   │   │   ├── Plants.vue
│   │   │   └── Helps.vue
│   │   ├── normal/          # 普通用户页面
│   │   │   ├── Layout.vue
│   │   │   ├── Plants.vue
│   │   │   ├── PestDiseases.vue
│   │   │   ├── Pesticides.vue
│   │   │   ├── HelpCenter.vue
│   │   │   └── Profile.vue
│   │   ├── Login.vue
│   │   ├── Register.vue
│   │   └── 404.vue
│   ├── App.vue
│   └── main.js
├── package.json
├── vue.config.js
└── README.md
```

## 快速开始

### 环境要求

- Node.js 14.x 或更高版本
- npm 6.x 或 yarn 1.x
- 现代浏览器 (Chrome, Firefox, Safari, Edge)

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd frontend
```

2. **安装依赖**
```bash
# 使用npm
npm install

# 或使用yarn
yarn install
```

3. **配置环境**
```bash
# 复制环境配置文件
cp .env.example .env.local

# 编辑配置文件
vim .env.local
```

4. **启动开发服务器**
```bash
# 使用npm
npm run serve

# 或使用yarn
yarn serve
```

5. **访问应用**
- 开发服务器: http://localhost:8081
- 后端API: http://localhost:8080

## 环境配置

### 开发环境 (.env.development)
```env
VUE_APP_BASE_API=http://localhost:8080/api
VUE_APP_UPLOAD_URL=http://localhost:8080/api/files/upload
```

### 生产环境 (.env.production)
```env
VUE_APP_BASE_API=https://your-api-domain.com/api
VUE_APP_UPLOAD_URL=https://your-api-domain.com/api/files/upload
```

## 构建和部署

### 开发构建
```bash
npm run serve
```

### 生产构建
```bash
npm run build
```

### 代码检查
```bash
npm run lint
```

### 部署到静态服务器
```bash
# 构建项目
npm run build

# 部署dist目录到服务器
# 例如：上传到Nginx、Apache、或CDN
```

## 路由结构

### 公共路由
- `/` - 重定向到登录页
- `/login` - 用户登录
- `/register` - 用户注册

### 普通用户路由 (`/normal`)
- `/normal/plants` - 植物列表
- `/normal/plant-detail/:id` - 植物详情
- `/normal/pests` - 病虫害列表
- `/normal/pest-disease-detail/:id` - 病虫害详情
- `/normal/pesticides` - 农药列表
- `/normal/pesticide-detail/:id` - 农药详情
- `/normal/help` - 求助中心
- `/normal/help-detail/:id` - 求助详情
- `/normal/profile` - 个人中心

### 专家用户路由 (`/expert`)
- `/expert/dashboard` - 专家工作台
- `/expert/plants` - 植物管理
- `/expert/pests` - 病虫害管理
- `/expert/pesticides` - 农药管理
- `/expert/helps` - 求助回复
- `/expert/profile` - 个人中心

### 管理员路由 (`/admin`)
- `/admin/dashboard` - 管理控制台
- `/admin/users` - 用户管理
- `/admin/experts` - 专家管理
- `/admin/comments` - 评论审核
- `/admin/analytics` - 数据分析

## 组件说明

### 布局组件
- **Layout.vue**: 各角色的主布局组件
- **Sidebar**: 侧边导航栏
- **Header**: 顶部导航栏

### 业务组件
- **PlantCard**: 植物信息卡片
- **CommentList**: 评论列表
- **HelpRequestCard**: 求助信息卡片
- **UserAvatar**: 用户头像组件

### 表单组件
- **SearchForm**: 搜索表单
- **FilterForm**: 筛选表单
- **UploadImage**: 图片上传组件

## API 集成

### 请求拦截器
```javascript
// 自动添加认证头
service.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = 'Bearer ' + token
  }
  return config
})
```

### 响应拦截器
```javascript
// 统一错误处理
service.interceptors.response.use(
  response => response,
  error => {
    Message({
      message: error.message || '请求失败',
      type: 'error'
    })
    return Promise.reject(error)
  }
)
```

## 状态管理

### 用户状态
```javascript
// store/modules/user.js
const state = {
  token: localStorage.getItem('token'),
  userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}'),
  userType: localStorage.getItem('userType')
}
```

### 应用状态
```javascript
// store/modules/app.js
const state = {
  sidebar: {
    opened: true
  },
  device: 'desktop'
}
```

## 样式规范

### CSS变量
```css
:root {
  --primary-color: #409EFF;
  --success-color: #67C23A;
  --warning-color: #E6A23C;
  --danger-color: #F56C6C;
  --info-color: #909399;
}
```

### 响应式设计
```scss
// 移动端适配
@media (max-width: 768px) {
  .container {
    padding: 10px;
  }
}
```

## 性能优化

### 路由懒加载
```javascript
const Dashboard = () => import('../views/admin/Dashboard.vue')
```

### 组件懒加载
```javascript
components: {
  AsyncComponent: () => import('./AsyncComponent.vue')
}
```

### 图片懒加载
```vue
<img v-lazy="imageUrl" alt="植物图片">
```

## 常见问题

### Q: 开发服务器启动失败
A: 检查Node.js版本、清除node_modules重新安装依赖

### Q: API请求跨域问题
A: 检查vue.config.js中的代理配置

### Q: 打包后页面空白
A: 检查路由模式配置、publicPath设置

### Q: 图片显示不正常
A: 检查图片路径、服务器配置

## 开发指南

### 添加新页面
1. 在views目录创建Vue组件
2. 在router/index.js添加路由配置
3. 在对应的Layout中添加导航链接

### 添加新API
1. 在api目录创建或修改API文件
2. 使用统一的request实例
3. 添加错误处理

### 样式开发
1. 使用SCSS预处理器
2. 遵循BEM命名规范
3. 使用CSS变量统一主题

## 浏览器支持

- Chrome >= 60
- Firefox >= 60
- Safari >= 12
- Edge >= 79

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
