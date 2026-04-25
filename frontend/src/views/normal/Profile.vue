<template>
  <div class="profile-container">
    <div class="profile-header">
      <h1>个人中心</h1>
      <p>管理您的个人信息和账户设置</p>
    </div>

    <div class="profile-content">
      <el-row :gutter="20">
        <!-- 个人信息卡片 -->
        <el-col :span="8">
          <el-card class="profile-card">
            <div class="avatar-section">
              <div class="avatar-upload">
                <el-upload
                  action="/api/files/upload"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                  accept="image/*"
                  class="avatar-uploader"
                >
                  <!-- 有头像时显示头像 -->
                  <el-avatar
                    v-if="(tempAvatarUrl || userInfo.avatarUrl) && (tempAvatarUrl || userInfo.avatarUrl).trim() !== ''"
                    :size="80"
                    :src="tempAvatarUrl || userInfo.avatarUrl"
                  ></el-avatar>
                  <!-- 无头像时显示上传提示 -->
                  <div v-else class="avatar-placeholder">
                    <i class="el-icon-plus"></i>
                    <span>点击上传头像</span>
                  </div>
                  <!-- 悬停覆盖层 -->
                  <div class="avatar-overlay" v-if="(tempAvatarUrl || userInfo.avatarUrl) && (tempAvatarUrl || userInfo.avatarUrl).trim() !== ''">
                    <i class="el-icon-camera"></i>
                    <span>更换头像</span>
                  </div>
                </el-upload>
              </div>
              <h3>{{ userInfo.nickname || '未设置昵称' }}</h3>
              <p class="user-type">普通用户</p>
            </div>
            
            <div class="stats-section">
              <div class="stat-item">
                <span class="stat-number">{{ userStats.commentCount || 0 }}</span>
                <span class="stat-label">评论数</span>
              </div>
              <div class="stat-item">
                <span class="stat-number">{{ userStats.helpCount || 0 }}</span>
                <span class="stat-label">求助数</span>
              </div>
            </div>
          </el-card>
        </el-col>

        <!-- 个人信息编辑 -->
        <el-col :span="16">
          <el-card class="info-card">
            <div slot="header" class="card-header">
              <span>个人信息</span>
              <el-button type="primary" @click="editMode = !editMode">
                {{ editMode ? '取消编辑' : '编辑信息' }}
              </el-button>
            </div>

            <el-form :model="profileForm" :rules="profileRules" ref="profileForm" label-width="100px">
              <el-form-item label="用户名">
                <el-input v-model="profileForm.username" disabled></el-input>
              </el-form-item>
              
              <el-form-item label="昵称" prop="nickname">
                <el-input 
                  v-model="profileForm.nickname" 
                  :disabled="!editMode"
                  placeholder="请输入昵称"
                ></el-input>
              </el-form-item>
              
              <el-form-item label="手机号码" prop="phone">
                <el-input
                  v-model="profileForm.phone"
                  :disabled="!editMode"
                  placeholder="请输入手机号码"
                ></el-input>
              </el-form-item>

              <el-form-item label="邮箱地址" prop="email">
                <el-input
                  v-model="profileForm.email"
                  :disabled="!editMode"
                  placeholder="请输入邮箱地址"
                ></el-input>
              </el-form-item>

              <!-- 头像保存按钮 -->
              <el-form-item v-if="avatarChanged">
                <el-alert
                  title="头像已更改"
                  description="您已上传新头像，请点击保存按钮保存更改"
                  type="warning"
                  :closable="false"
                  show-icon
                  style="margin-bottom: 16px;">
                </el-alert>
                <el-button type="success" @click="saveAvatar(tempAvatarUrl)" :loading="saving">
                  <i class="el-icon-check"></i>
                  保存头像
                </el-button>
                <el-button @click="cancelAvatarChange">
                  <i class="el-icon-close"></i>
                  取消更改
                </el-button>
              </el-form-item>

              <!-- 个人信息保存按钮 -->
              <el-form-item v-if="editMode">
                <el-button type="primary" @click="saveProfile" :loading="saving">保存信息</el-button>
                <el-button @click="resetForm">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>

      <!-- 密码修改 -->
      <el-row style="margin-top: 20px;">
        <el-col :span="24">
          <el-card class="password-card">
            <div slot="header" class="card-header">
              <span>修改密码</span>
            </div>

            <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px" style="max-width: 500px;">
              <el-form-item label="当前密码" prop="currentPassword">
                <el-input 
                  v-model="passwordForm.currentPassword" 
                  type="password"
                  placeholder="请输入当前密码"
                  show-password
                ></el-input>
              </el-form-item>
              
              <el-form-item label="新密码" prop="newPassword">
                <el-input 
                  v-model="passwordForm.newPassword" 
                  type="password"
                  placeholder="请输入新密码"
                  show-password
                ></el-input>
              </el-form-item>
              
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input 
                  v-model="passwordForm.confirmPassword" 
                  type="password"
                  placeholder="请再次输入新密码"
                  show-password
                ></el-input>
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="changePassword" :loading="changingPassword">
                  修改密码
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { getNormalProfile, updateNormalProfile, changePassword } from '@/api/normal'

export default {
  name: 'NormalProfile',
  data() {
    const validateConfirmPassword = (_rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }

    return {
      editMode: false,
      saving: false,
      changingPassword: false,
      userInfo: {},
      userStats: {},
      tempAvatarUrl: '', // 临时头像URL
      avatarChanged: false, // 头像是否已更改
      profileForm: {
        username: '',
        nickname: '',
        phone: '',
        email: ''
      },
      profileRules: {
        nickname: [
          { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
        ]
      },
      uploadHeaders: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      },
      passwordForm: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        currentPassword: [
          { required: true, message: '请输入当前密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少为 6 个字符', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认密码', trigger: 'blur' },
          { validator: validateConfirmPassword, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchProfile()
  },

  mounted() {
    // 页面挂载后再次检查用户信息
    this.$nextTick(() => {
      // 页面挂载完成
    })
  },
  methods: {
    async fetchProfile() {
      try {
        const response = await getNormalProfile()

        if (response.data && response.data.code === 1) {
          const data = response.data.data
          this.userInfo = data.userInfo
          this.userStats = data.userStats

          // 填充表单
          this.profileForm = {
            username: this.userInfo.username,
            nickname: this.userInfo.nickname || '',
            phone: this.userInfo.phone || '',
            email: this.userInfo.email || ''
          }
        } else {
          this.$message.error(response.data.msg || '获取个人信息失败')
        }
      } catch (error) {
        this.$message.error('获取个人信息失败')
      }
    },

    async saveProfile() {
      this.$refs.profileForm.validate(async (valid) => {
        if (valid) {
          try {
            this.saving = true
            await updateNormalProfile(this.profileForm)
            this.$message.success('个人信息更新成功')
            this.editMode = false
            this.fetchProfile()
          } catch (error) {
            this.$message.error('更新个人信息失败')
          } finally {
            this.saving = false
          }
        }
      })
    },

    resetForm() {
      this.fetchProfile()
    },

    async changePassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          try {
            this.changingPassword = true

            // 调用修改密码的API
            const response = await changePassword({
              oldPassword: this.passwordForm.currentPassword,
              newPassword: this.passwordForm.newPassword
            })

            if (response.data && response.data.code === 1) {
              this.$message.success('密码修改成功，请重新登录')

              // 清空密码表单
              this.passwordForm = {
                currentPassword: '',
                newPassword: '',
                confirmPassword: ''
              }
              this.$refs.passwordForm.clearValidate()

              // 清除登录信息，跳转到登录页
              localStorage.removeItem('token')
              localStorage.removeItem('userInfo')
              localStorage.removeItem('userType')

              // 延迟跳转，让用户看到成功消息
              setTimeout(() => {
                this.$router.push('/login')
              }, 1500)
            } else {
              this.$message.error(response.data.msg || '密码修改失败')
            }
          } catch (error) {
            if (error.response && error.response.data) {
              this.$message.error(error.response.data.msg || '密码修改失败')
            } else {
              this.$message.error('密码修改失败，请检查网络连接')
            }
          } finally {
            this.changingPassword = false
          }
        }
      })
    },

    handleAvatarSuccess(response) {
      if (response.code === 1) {
        this.userInfo.avatarUrl = response.data
        this.tempAvatarUrl = response.data // 临时保存头像URL
        this.avatarChanged = true // 标记头像已更改
        this.$message.success('头像上传成功，请点击保存按钮保存更改')

        // 更新localStorage中的用户信息，让导航栏显示新头像
        this.updateUserInfoInStorage()
      } else {
        this.$message.error(response.msg || '头像上传失败')
      }
    },

    async saveAvatar(avatarUrl) {
      try {
        this.saving = true
        await updateNormalProfile({ avatarUrl })

        // 更新用户信息
        this.userInfo.avatarUrl = avatarUrl
        this.tempAvatarUrl = ''
        this.avatarChanged = false

        // 更新localStorage中的用户信息
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        userInfo.avatarUrl = avatarUrl
        localStorage.setItem('userInfo', JSON.stringify(userInfo))

        // 触发全局事件，通知其他组件更新头像
        this.$eventBus.$emit('avatarUpdated', avatarUrl)

        this.$message.success('头像保存成功')

        // 强制刷新当前页面的用户信息显示
        this.$forceUpdate()
      } catch (error) {
        this.$message.error('保存头像失败')
      } finally {
        this.saving = false
      }
    },

    cancelAvatarChange() {
      this.tempAvatarUrl = ''
      this.avatarChanged = false
      this.$message.info('已取消头像更改')
    },

    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('头像大小不能超过 2MB!')
        return false
      }
      return true
    },

    // 更新localStorage中的用户信息
    updateUserInfoInStorage() {
      try {
        // 更新normal_user信息
        const normalUser = localStorage.getItem('normal_user')
        if (normalUser) {
          const user = JSON.parse(normalUser)
          user.avatarUrl = this.userInfo.avatarUrl
          user.avatar_url = this.userInfo.avatarUrl // 兼容不同的字段名
          localStorage.setItem('normal_user', JSON.stringify(user))
        }

        // 也更新userInfo信息（如果存在）
        const userInfo = localStorage.getItem('userInfo')
        if (userInfo) {
          const user = JSON.parse(userInfo)
          user.avatarUrl = this.userInfo.avatarUrl
          user.avatar_url = this.userInfo.avatarUrl
          localStorage.setItem('userInfo', JSON.stringify(user))
        }

        // 触发自定义事件，通知其他组件用户信息已更新
        window.dispatchEvent(new CustomEvent('userInfoUpdated', {
          detail: {
            avatarUrl: this.userInfo.avatarUrl
          }
        }))

        // 普通用户头像信息已更新到localStorage
      } catch (error) {
        // 更新localStorage用户信息失败
      }
    }
  }
}
</script>

<style scoped>
.profile-container {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  min-height: 100vh;
  position: relative;
}

.profile-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 20% 80%, rgba(102, 126, 234, 0.03) 0%, transparent 50%),
    radial-gradient(circle at 80% 20%, rgba(118, 75, 162, 0.03) 0%, transparent 50%);
  pointer-events: none;
}

.profile-header {
  text-align: center;
  margin-bottom: 32px;
  padding: 60px 0;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 24px;
  box-shadow: 0 20px 40px rgba(102, 126, 234, 0.3);
  color: white;
  position: relative;
  z-index: 1;
  overflow: hidden;
}

.profile-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(circle at 30% 70%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
    radial-gradient(circle at 70% 30%, rgba(255, 215, 0, 0.1) 0%, transparent 50%);
  opacity: 0.6;
}

.profile-header h1 {
  font-size: 3rem;
  color: white;
  margin: 0 0 16px 0;
  font-weight: 800;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 1;
}

.profile-header p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 1.2rem;
  margin: 0;
  font-weight: 500;
  position: relative;
  z-index: 1;
}

.profile-content {
  margin-top: 24px;
  position: relative;
  z-index: 1;
}

.profile-content >>> .el-card {
  background: rgba(255, 255, 255, 0.95);
  border: none;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(20px);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.profile-content >>> .el-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
}

.profile-card {
  text-align: center;
}

.avatar-section {
  padding: 32px 0;
  border-bottom: 2px solid #e1e8ed;
  margin-bottom: 24px;
  position: relative;
}

.avatar-section::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 2px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 1px;
}

.avatar-upload {
  position: relative;
  display: inline-block;
}

.avatar-uploader {
  position: relative;
  cursor: pointer;
}

.avatar-uploader >>> .el-upload {
  position: relative;
  display: inline-block;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  color: white;
  font-size: 12px;
}

.avatar-uploader:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay i {
  font-size: 20px;
  margin-bottom: 4px;
}

.avatar-section >>> .el-avatar {
  border: 4px solid #667eea;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
  transition: all 0.3s ease;
}

.avatar-uploader:hover >>> .el-avatar {
  transform: scale(1.05);
}

.avatar-placeholder {
  width: 80px;
  height: 80px;
  border: 4px solid #667eea;
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  color: #667eea;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3);
}

.avatar-placeholder:hover {
  transform: scale(1.05);
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.avatar-placeholder i {
  font-size: 24px;
  margin-bottom: 4px;
}

.avatar-placeholder span {
  font-size: 10px;
  font-weight: 600;
  text-align: center;
  line-height: 1.2;
}

.avatar-section h3 {
  margin: 20px 0 8px 0;
  color: #2c3e50;
  font-size: 1.5rem;
  font-weight: 700;
}

.user-type {
  color: #67c23a;
  font-weight: 700;
  margin: 0;
  font-size: 1rem;
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stats-section {
  display: flex;
  justify-content: space-around;
  padding: 0 20px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  transition: all 0.3s ease;
  min-width: 80px;
}

.stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.15);
}

.stat-number {
  font-size: 2rem;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin-bottom: 6px;
}

.stat-label {
  font-size: 0.9rem;
  color: #4a5568;
  font-weight: 600;
}

.info-card,
.password-card {
  margin-bottom: 24px;
}

.info-card >>> .el-card__header,
.password-card >>> .el-card__header {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  border-bottom: 2px solid #e1e8ed;
  padding: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-size: 1.3rem;
  font-weight: 700;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 12px;
}

.card-header span::before {
  content: '';
  width: 4px;
  height: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.card-header >>> .el-button {
  border-radius: 12px;
  font-weight: 600;
  padding: 10px 20px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.card-header >>> .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.card-header >>> .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.info-card >>> .el-form,
.password-card >>> .el-form {
  padding: 24px;
}

.info-card >>> .el-form-item__label,
.password-card >>> .el-form-item__label {
  font-weight: 600;
  color: #2c3e50;
}

.info-card >>> .el-input__inner,
.password-card >>> .el-input__inner,
.info-card >>> .el-textarea__inner,
.password-card >>> .el-textarea__inner {
  border-radius: 12px;
  border: 2px solid #e1e8ed;
  transition: all 0.3s ease;
  font-weight: 500;
}

.info-card >>> .el-input__inner:focus,
.password-card >>> .el-input__inner:focus,
.info-card >>> .el-textarea__inner:focus,
.password-card >>> .el-textarea__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.info-card >>> .el-button,
.password-card >>> .el-button {
  border-radius: 12px;
  font-weight: 600;
  padding: 12px 24px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.info-card >>> .el-button--primary,
.password-card >>> .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.info-card >>> .el-button--primary:hover,
.password-card >>> .el-button--primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    padding: 10px;
  }
  
  .profile-header h1 {
    font-size: 2rem;
  }
  
  .stats-section {
    flex-direction: column;
    gap: 16px;
  }
}
</style>
