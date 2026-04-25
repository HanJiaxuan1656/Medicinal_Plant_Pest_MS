<template>
  <div class="login-container">
      <!-- 左上角悬浮图标 -->
    <div class="floating-logo"></div>
    <!-- 背景装饰元素 -->
    <div class="bg-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="floating-shape shape-4"></div>
    </div>

    <!-- 左侧信息面板 -->
    <div class="info-panel">
      <div class="info-content">
        <div class="logo-section">
          <div class="logo-icon">
            <i class="el-icon-s-management"></i>
          </div>
          <h1 class="system-title">药用植物病虫害管理系统</h1>
          <p class="system-subtitle">Professional Plant Disease & Pest Management</p>
        </div>

        <div class="features">
          <div class="feature-item">
            <i class="el-icon-s-data"></i>
            <span>智能数据分析</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-s-tools"></i>
            <span>专业防治方案</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-s-cooperation"></i>
            <span>专家在线指导</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧登录面板 -->
    <div class="login-panel">
      <div class="login-box">
        <div class="login-header">
          <h2 class="login-title">
            <i class="el-icon-user"></i>
            用户登录
          </h2>
          <p class="login-subtitle">欢迎回来，请登录您的账户</p>
        </div>

        <el-form :model="loginForm" :rules="rules" ref="loginForm" label-width="0px" class="login-form">
          <!-- 用户类型选择 -->
          <div class="user-type-section">
            <label class="form-label">选择用户类型</label>
            <el-form-item>
              <div class="user-type-cards">
                <div
                  class="user-type-card"
                  :class="{ active: loginForm.userType === 'admin' }"
                  @click="loginForm.userType = 'admin'"
                >
                  <i class="el-icon-s-custom"></i>
                  <span>管理员</span>
                </div>
                <div
                  class="user-type-card"
                  :class="{ active: loginForm.userType === 'expert' }"
                  @click="loginForm.userType = 'expert'"
                >
                  <i class="el-icon-s-check"></i>
                  <span>专家用户</span>
                </div>
                <div
                  class="user-type-card"
                  :class="{ active: loginForm.userType === 'normal' }"
                  @click="loginForm.userType = 'normal'"
                >
                  <i class="el-icon-user-solid"></i>
                  <span>普通用户</span>
                </div>
              </div>
            </el-form-item>
          </div>

          <!-- 用户名输入 -->
          <el-form-item prop="username" class="form-item">
            <label class="form-label">用户名</label>
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="el-icon-user"
              size="large"
              class="form-input"
            ></el-input>
          </el-form-item>

          <!-- 密码输入 -->
          <el-form-item prop="password" class="form-item">
            <label class="form-label">密码</label>
            <el-input
              type="password"
              v-model="loginForm.password"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              size="large"
              class="form-input"
              show-password
            ></el-input>
          </el-form-item>

          <!-- 登录按钮 -->
          <el-form-item class="form-item">
            <el-button
              type="primary"
              @click="handleLogin"
              :loading="loading"
              size="large"
              class="login-btn"
            >
              <i class="el-icon-right" v-if="!loading"></i>
              {{ loading ? '登录中...' : '立即登录' }}
            </el-button>
          </el-form-item>

          <!-- 注册链接 -->
          <div class="register-link">
            <span>还没有账号？</span>
            <router-link to="/register" class="register-btn">立即注册</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { login } from '@/api/auth'

export default {
  name: 'Login',
  data() {
    return {
      loginForm: {
        username: '',
        password: '',
        userType: 'normal'
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          
          login(this.loginForm.userType, {
            username: this.loginForm.username,
            password: this.loginForm.password
          }).then(response => {

            // 检查响应状态和数据
            if ((response.status === 200||response.status === '200') && response.data) {
              // 检查登录是否成功 - 根据后端实际返回的数据结构调整判断条件
              if (response.data.code === 1 || response.data.success === true) {
                // 登录成功
                this.$message({
                  message: '登录成功',
                  type: 'success',
                  duration: 1000
                })
                
                // 保存用户信息和token
                const userData = response.data.data || response.data.user || response.data
                localStorage.setItem('userInfo', JSON.stringify(userData))
                localStorage.setItem('userType', this.loginForm.userType)
                
                // 保存token（根据实际的返回数据结构获取token）
                const token = response.data.token;
                if (token) {
                  localStorage.setItem('token', token);
                }
                
                // 根据用户类型跳转到不同页面
                switch(this.loginForm.userType) {
                  case 'admin':
                    this.$router.push('/admin/dashboard')
                    break
                  case 'expert':
                    this.$router.push('/expert/dashboard')
                    break
                  case 'normal':
                    this.$router.push('/plants')
                    break
                  default:
                    this.$message.error('未知的用户类型')
                    return
                }
              } else {
                // 登录失败
                const errorMsg = response.data.message || response.data.msg || '用户名或密码错误'
                this.$message({
                  message: errorMsg,
                  type: 'error',
                  duration: 3000,
                  showClose: true
                })
              }
            } else {
              const errorMsg = response.data && (response.data.message || response.data.msg)
              this.$message({
                message: errorMsg || '登录失败',
                type: 'error',
                duration: 3000,
                showClose: true
              })
            }
          }).catch(error => {
            console.error('登录错误:', error)
            if (error.response) {
              // 服务器返回了错误状态码
              const errorMsg = error.response.data && (error.response.data.message || error.response.data.msg)
              this.$message({
                message: errorMsg || '登录失败',
                type: 'error',
                duration: 3000,
                showClose: true
              })
            } else if (error.request) {
              // 请求发送成功，但没有收到响应
              this.$message({
                message: '服务器无响应，请稍后重试',
                type: 'error',
                duration: 3000,
                showClose: true
              })
            } else {
              // 请求配置出错
              this.$message({
                message: '请求失败：' + error.message,
                type: 'error',
                duration: 3000,
                showClose: true
              })
            }
          }).finally(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>

<style scoped>
/* 主容器 */
.login-container {
  height: 100vh;
  display: flex;
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 80px;
  height: 80px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  left: 80%;
  animation-delay: 2s;
}

.shape-3 {
  width: 60px;
  height: 60px;
  top: 80%;
  left: 20%;
  animation-delay: 4s;
}

.shape-4 {
  width: 100px;
  height: 100px;
  top: 10%;
  right: 20%;
  animation-delay: 1s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0px) rotate(0deg);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 1;
  }
}

/* 左侧信息面板 */
.info-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  position: relative;
  z-index: 2;
}

.info-content {
  max-width: 500px;
  color: white;
  text-align: center;
}

.logo-section {
  margin-bottom: 60px;
}

.logo-icon {
  width: 100px;
  height: 100px;
  margin: 0 auto 30px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: white;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    box-shadow: 0 0 0 0 rgba(255, 255, 255, 0.4);
  }
  50% {
    transform: scale(1.05);
    box-shadow: 0 0 0 20px rgba(255, 255, 255, 0);
  }
}

.system-title {
  font-size: 36px;
  font-weight: 700;
  margin: 0 0 15px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  line-height: 1.2;
}

.system-subtitle {
  font-size: 16px;
  opacity: 0.9;
  font-weight: 300;
  margin: 0;
  letter-spacing: 1px;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feature-item {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 15px;
  padding: 15px 25px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 50px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
}

.feature-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateX(10px);
}

.feature-item i {
  font-size: 20px;
}

.feature-item span {
  font-size: 16px;
  font-weight: 500;
}

/* 右侧登录面板 */
.login-panel {
  width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  position: relative;
  z-index: 2;
}

.login-box {
  width: 100%;
  max-width: 400px;
  padding: 50px 40px;
  animation: slideInRight 0.8s ease-out;
}

@keyframes slideInRight {
  from {
    opacity: 0;
    transform: translateX(50px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.login-title i {
  color: #667eea;
}

.login-subtitle {
  color: #7f8c8d;
  font-size: 14px;
  margin: 0;
  font-weight: 400;
}

/* 表单样式 */
.login-form {
  animation: fadeInUp 1s ease-out 0.3s both;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.form-label {
  display: block;
  margin-bottom: 8px;
  color: #2c3e50;
  font-weight: 600;
  font-size: 14px;
}

.form-item {
  margin-bottom: 25px;
}

/* 用户类型选择 */
.user-type-section {
  margin-bottom: 30px;
}

.user-type-cards {
  display: flex;
  gap: 10px;
  margin-top: 10px;
}

.user-type-card {
  flex: 1;
  padding: 15px 10px;
  border: 2px solid #e1e8ed;
  border-radius: 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.user-type-card:hover {
  border-color: #667eea;
  background: #f0f4ff;
  transform: translateY(-2px);
}

.user-type-card.active {
  border-color: #667eea;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.user-type-card i {
  display: block;
  font-size: 20px;
  margin-bottom: 5px;
}

.user-type-card span {
  font-size: 12px;
  font-weight: 500;
}

/* 输入框样式 */
.form-input {
  margin-top: 5px;
}

.form-input >>> .el-input__inner {
  border: 2px solid #e1e8ed;
  border-radius: 12px;
  padding: 15px 20px 15px 45px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.form-input >>> .el-input__inner:focus {
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.form-input >>> .el-input__prefix {
  left: 15px;
  color: #95a5a6;
}

.form-input >>> .el-input__suffix {
  right: 15px;
}

/* 登录按钮 */
.login-btn {
  width: 100%;
  height: 50px;
  border-radius: 12px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
}

.login-btn:active {
  transform: translateY(0);
}

.login-btn >>> .el-button__text {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* 注册链接 */
.register-link {
  text-align: center;
  margin-top: 30px;
  font-size: 14px;
  color: #7f8c8d;
}

.register-btn {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
  transition: all 0.3s ease;
}

.register-btn:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }

  .info-panel {
    display: none;
  }

  .login-panel {
    width: 100%;
    padding: 20px;
  }

  .login-box {
    padding: 30px 20px;
  }

  .user-type-cards {
    flex-direction: column;
    gap: 15px;
  }

  .user-type-card {
    padding: 20px;
  }

  .system-title {
    font-size: 24px;
  }
}

/* 加载状态动画 */
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.login-btn.is-loading {
  pointer-events: none;
}

.login-btn.is-loading >>> .el-icon-loading {
  animation: spin 1s linear infinite;
}

/* 表单验证错误样式 */
.el-form-item.is-error >>> .el-input__inner {
  border-color: #f56c6c !important;
  background: #fef0f0 !important;
}

.el-form-item.is-error >>> .el-form-item__error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 5px;
}
/* 左上角悬浮图标样式 */
.floating-logo {
  position: fixed;
  top: 20px;
  left: 30px;
  width: 250px;
  height: 160px;
  background-image: url('../assets/css/logo.png');
  background-size: contain;
  background-repeat: no-repeat;
  z-index: 100;
  cursor: pointer;
  transition: all 0.3s ease;
}

.floating-logo:hover {
  transform: scale(1.05);
}

</style>