<template>
  <div class="register-container">
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
            <i class="el-icon-user-solid"></i>
            <span>快速注册账户</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-s-check"></i>
            <span>专家认证服务</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-s-cooperation"></i>
            <span>加入专业社区</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 右侧注册面板 -->
    <div class="register-panel">
      <div class="register-box">
        <div class="register-header">
          <h2 class="register-title">
            <i class="el-icon-plus"></i>
            用户注册
          </h2>
          <p class="register-subtitle">创建您的账户，开始使用我们的服务</p>
        </div>

        <el-form :model="registerForm" :rules="rules" ref="registerForm" label-width="0px" class="register-form">
          <!-- 用户类型选择 -->
          <div class="user-type-section">
            <label class="form-label">选择用户类型</label>
            <el-form-item>
              <div class="user-type-cards">
                <div
                  class="user-type-card"
                  :class="{ active: registerForm.userType === 'expert' }"
                  @click="registerForm.userType = 'expert'"
                >
                  <i class="el-icon-s-check"></i>
                  <span>专家用户</span>
                </div>
                <div
                  class="user-type-card"
                  :class="{ active: registerForm.userType === 'normal' }"
                  @click="registerForm.userType = 'normal'"
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
              v-model="registerForm.username"
              placeholder="请输入用户名"
              prefix-icon="el-icon-user"
              size="large"
              class="form-input"
              @blur="checkUsernameExist"
            ></el-input>
          </el-form-item>

          <!-- 密码输入 -->
          <el-form-item prop="password" class="form-item">
            <label class="form-label">密码</label>
            <el-input
              type="password"
              v-model="registerForm.password"
              placeholder="请输入密码"
              prefix-icon="el-icon-lock"
              size="large"
              class="form-input"
              show-password
            ></el-input>
          </el-form-item>

          <!-- 确认密码输入 -->
          <el-form-item prop="confirmPassword" class="form-item">
            <label class="form-label">确认密码</label>
            <el-input
              type="password"
              v-model="registerForm.confirmPassword"
              placeholder="请再次输入密码"
              prefix-icon="el-icon-lock"
              size="large"
              class="form-input"
              show-password
            ></el-input>
          </el-form-item>

          <!-- 专家用户特有字段 -->
          <template v-if="registerForm.userType === 'expert'">
            <el-form-item prop="title" class="form-item">
              <label class="form-label">职称</label>
              <el-input
                v-model="registerForm.title"
                placeholder="请输入职称"
                prefix-icon="el-icon-medal"
                size="large"
                class="form-input"
              ></el-input>
            </el-form-item>
          </template>

          <!-- 普通用户特有字段 -->
          <template v-if="registerForm.userType === 'normal'">
            <el-form-item prop="nickname" class="form-item">
              <label class="form-label">昵称</label>
              <el-input
                v-model="registerForm.nickname"
                placeholder="请输入昵称"
                prefix-icon="el-icon-user"
                size="large"
                class="form-input"
              ></el-input>
            </el-form-item>
          </template>

          <!-- 注册按钮 -->
          <el-form-item class="form-item">
            <el-button
              type="primary"
              @click="handleRegister"
              :loading="loading"
              size="large"
              class="register-btn"
            >
              <i class="el-icon-check" v-if="!loading"></i>
              {{ loading ? '注册中...' : '立即注册' }}
            </el-button>
          </el-form-item>

          <!-- 登录链接 -->
          <div class="login-link">
            <span>已有账号？</span>
            <router-link to="/login" class="login-btn">立即登录</router-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { register, checkUsername } from '@/api/auth'

export default {
  name: 'Register',
  data() {
    // 确认密码验证
    const validateConfirmPassword = (_, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.registerForm.password) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    
    return {
      registerForm: {
        username: '',
        password: '',
        confirmPassword: '',
        userType: 'normal',
        title: '',
        nickname: '',
        avatarUrl: ''
      },
      rules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少为 6 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ],
        title: [
          { required: true, message: '请输入职称', trigger: 'blur' }
        ],
        nickname: [
          { required: true, message: '请输入昵称', trigger: 'blur' }
        ]
      },
      loading: false,
      usernameExist: false
    }
  },
  methods: {
    // 检查用户名是否存在
    checkUsernameExist() {
      if (this.registerForm.username && this.registerForm.username.length >= 3) {
        checkUsername(this.registerForm.username, this.registerForm.userType)
          .then(response => {
            if (response.status === 200 && response.data.exists) {
              this.usernameExist = true
              this.$message.warning('用户名已存在，请更换')
            } else {
              this.usernameExist = false
            }
          })
      }
    },
    
    // 处理注册
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          if (this.usernameExist) {
            this.$message.warning('用户名已存在，请更换')
            return
          }
          
          this.loading = true
          
          // 根据用户类型准备数据
          const registerData = {
            username: this.registerForm.username,
            password: this.registerForm.password
          }
          
          if (this.registerForm.userType === 'expert') {
            registerData.title = this.registerForm.title
            registerData.avatarUrl = this.registerForm.avatarUrl
          } else {
            registerData.nickname = this.registerForm.nickname
            registerData.avatarUrl = this.registerForm.avatarUrl
          }
          
          register(this.registerForm.userType, registerData)
            .then(response => {
              // 检查HTTP状态码，如果是200表示成功
              if (response.status === 200) {
                this.$message.success('注册成功，请登录')
                this.$router.push('/login')
              } else {
                this.$message.error(response.data.message || '注册失败')
              }
            })
            .catch(error => {
              this.$message.error('注册失败，请稍后重试')
            })
            .finally(() => {
              this.loading = false
            })
        }
      })
    }
  },
  watch: {
    // 当用户类型改变时重置表单验证
    'registerForm.userType'() {
      this.$nextTick(() => {
        this.$refs.registerForm.clearValidate()
      })
    }
  }
}
</script>

<style scoped>
/* 主容器 */
.register-container {
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

/* 右侧注册面板 */
.register-panel {
  width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  position: relative;
  z-index: 2;
}

.register-box {
  width: 100%;
  max-width: 400px;
  padding: 50px 40px;
  animation: slideInRight 0.8s ease-out;
  max-height: 100vh;
  overflow-y: auto;
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

.register-header {
  text-align: center;
  margin-bottom: 40px;
}

.register-title {
  font-size: 28px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 10px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.register-title i {
  color: #667eea;
}

.register-subtitle {
  color: #7f8c8d;
  font-size: 14px;
  margin: 0;
  font-weight: 400;
}

/* 表单样式 */
.register-form {
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

/* 注册按钮 */
.register-btn {
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

.register-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.4);
}

.register-btn:active {
  transform: translateY(0);
}

.register-btn >>> .el-button__text {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

/* 登录链接 */
.login-link {
  text-align: center;
  margin-top: 30px;
  font-size: 14px;
  color: #7f8c8d;
}

.login-btn {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  margin-left: 5px;
  transition: all 0.3s ease;
}

.login-btn:hover {
  color: #764ba2;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
  }

  .info-panel {
    display: none;
  }

  .register-panel {
    width: 100%;
    padding: 20px;
  }

  .register-box {
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

.register-btn.is-loading {
  pointer-events: none;
}

.register-btn.is-loading >>> .el-icon-loading {
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

/* 滚动条样式 */
.register-box::-webkit-scrollbar {
  width: 6px;
}

.register-box::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.register-box::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.register-box::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
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