<template>
  <div class="admin-profile">
    <div class="profile-header">
      <h2><i class="el-icon-user"></i> 个人中心</h2>
      <p>管理员个人信息管理</p>
    </div>

    <div class="profile-content">
      <el-row :gutter="20">
        <!-- 个人信息 -->
        <el-col :span="24">
          <el-card class="profile-card">
            <div slot="header" class="card-header">
              <span><i class="el-icon-edit"></i> 个人信息</span>
            </div>
            
            <el-form :model="adminForm" :rules="adminRules" ref="adminForm" label-width="100px">
              <el-form-item label="用户名" prop="username">
                <el-input v-model="adminForm.username" disabled>
                  <i slot="prefix" class="el-input__icon el-icon-user"></i>
                </el-input>
              </el-form-item>
              
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="adminForm.email" placeholder="请输入邮箱">
                  <i slot="prefix" class="el-input__icon el-icon-message"></i>
                </el-input>
              </el-form-item>
              
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="adminForm.phone" placeholder="请输入手机号">
                  <i slot="prefix" class="el-input__icon el-icon-phone"></i>
                </el-input>
              </el-form-item>
              
              <el-form-item label="注册时间">
                <el-input :value="formatDate(adminForm.createdAt)" disabled>
                  <i slot="prefix" class="el-input__icon el-icon-time"></i>
                </el-input>
              </el-form-item>
              
              <el-form-item>
                <el-button type="primary" @click="updateProfile" :loading="updating">
                  <i class="el-icon-check"></i> 保存修改
                </el-button>
                <el-button @click="resetForm">
                  <i class="el-icon-refresh"></i> 重置
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>


      </el-row>

      <!-- 密码修改和头像上传 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 左侧：密码修改 -->
        <el-col :span="16">
          <el-card class="password-card">
            <div slot="header" class="card-header">
              <span><i class="el-icon-lock"></i> 密码修改</span>
            </div>

            <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password>
                  <i slot="prefix" class="el-input__icon el-icon-lock"></i>
                </el-input>
              </el-form-item>

              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password>
                  <i slot="prefix" class="el-input__icon el-icon-key"></i>
                </el-input>
              </el-form-item>

              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password>
                  <i slot="prefix" class="el-input__icon el-icon-key"></i>
                </el-input>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" @click="updatePassword" :loading="updatingPassword">
                  <i class="el-icon-check"></i> 修改密码
                </el-button>
                <el-button @click="resetPasswordForm">
                  <i class="el-icon-refresh"></i> 重置
                </el-button>
              </el-form-item>
            </el-form>
          </el-card>
        </el-col>

        <!-- 右侧：头像上传 -->
        <el-col :span="8">
          <el-card class="avatar-card">
            <div slot="header" class="card-header">
              <span><i class="el-icon-picture"></i> 头像设置</span>
            </div>

            <div class="avatar-section">
              <div class="avatar-container" @click="triggerUpload">
                <img v-if="adminForm.avatarUrl" :src="adminForm.avatarUrl" class="avatar-image" alt="头像">
                <div v-else class="avatar-placeholder">
                  <i class="el-icon-plus"></i>
                  <div class="upload-text">点击上传头像</div>
                </div>
              </div>

              <el-upload
                ref="avatarUpload"
                class="avatar-uploader"
                :action="uploadUrl"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                :on-error="handleUploadError"
                style="display: none;"
              >
                <el-button size="small" type="primary">
                  <i class="el-icon-upload"></i> 更换头像
                </el-button>
              </el-upload>

              <div class="avatar-actions">
                <el-button size="small" type="primary" @click="triggerUpload">
                  <i class="el-icon-upload"></i> 更换头像
                </el-button>
                <el-button v-if="avatarChanged" size="small" type="success" @click="saveAvatar" :loading="savingAvatar">
                  <i class="el-icon-check"></i> 保存头像
                </el-button>
              </div>

              <div class="upload-tips">
                <p>• 支持 JPG、PNG 格式</p>
                <p>• 文件大小不超过 2MB</p>
                <p>• 建议尺寸 200x200 像素</p>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'AdminProfile',
  data() {
    // 确认密码验证
    const validateConfirmPassword = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    }

    return {
      adminForm: {
        id: null,
        username: '',
        email: '',
        phone: '',
        avatarUrl: '',
        createdAt: null
      },
      passwordForm: {
        adminId: null,
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      adminRules: {
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ]
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validateConfirmPassword, trigger: 'blur' }
        ]
      },
      updating: false,
      updatingPassword: false,
      avatarChanged: false,
      savingAvatar: false,
      originalAvatarUrl: '',
      uploadUrl: 'http://localhost:8080/api/files/upload'
    }
  },
  mounted() {
    this.loadAdminProfile()
  },
  methods: {
    // 加载管理员信息
    async loadAdminProfile() {
      try {
        // 这里应该从登录状态或路由参数获取管理员ID
        const adminId = 1 // 临时硬编码，实际应该从登录状态获取
        
        const response = await request({
          url: `/admin/profile?adminId=${adminId}`,
          method: 'get'
        })
        if (response.data.code==1) {
          this.adminForm = { ...response.data.data }
          this.passwordForm.adminId = this.adminForm.id
          this.originalAvatarUrl = this.adminForm.avatarUrl
          this.avatarChanged = false
        } else {
          this.$message.error('获取个人信息失败：' + response.data.message)
        }
      } catch (error) {
        console.error('加载管理员信息失败:', error)
        this.$message.error('获取个人信息失败')
      }
    },

    // 更新个人信息
    async updateProfile() {
      this.$refs.adminForm.validate(async (valid) => {
        if (valid) {
          this.updating = true
          try {
            const response = await request({
              url: '/admin/profile',
              method: 'put',
              data: this.adminForm
            })
            if (response.data.code==1) {
              this.$message.success('个人信息更新成功')
              this.loadAdminProfile() // 重新加载数据
            } else {
              this.$message.error('更新失败：' + response.data.message)
            }
          } catch (error) {
            console.error('更新个人信息失败:', error)
            this.$message.error('更新失败')
          } finally {
            this.updating = false
          }
        }
      })
    },

    // 修改密码
    async updatePassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          this.updatingPassword = true
          try {
            const response = await request({
              url: '/admin/profile/password',
              method: 'put',
              data: this.passwordForm
            })
            if (response.data.code==1) {
              this.$message.success('密码修改成功，即将跳转到登录页面')
              this.resetPasswordForm()
              // 延迟2秒后跳转到登录页面
              setTimeout(() => {
                this.$router.push('/login')
              }, 2000)
            } else {
              this.$message.error('密码修改失败：' + response.data.msg)
            }
          } catch (error) {
            console.error('修改密码失败:', error)
            this.$message.error('密码修改失败')
          } finally {
            this.updatingPassword = false
          }
        }
      })
    },

    // 头像上传成功
    handleAvatarSuccess(response) {
      console.log('上传响应:', response)
      if (response.code==1) {
        this.adminForm.avatarUrl = response.data
        this.avatarChanged = true
        this.$message.success('头像上传成功，请点击保存头像按钮')
      } else {
        this.$message.error('头像上传失败：' + response.message)
      }
    },

    // 头像上传前验证
    beforeAvatarUpload(file) {
      const isJPGOrPNG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPGOrPNG) {
        this.$message.error('头像只能是 JPG 或 PNG 格式!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('头像大小不能超过 2MB!')
        return false
      }
      return true
    },

    // 上传错误处理
    handleUploadError(error) {
      console.error('上传失败:', error)
      this.$message.error('头像上传失败')
    },

    // 触发文件上传
    triggerUpload() {
      this.$refs.avatarUpload.$children[0].$refs.input.click()
    },

    // 保存头像
    async saveAvatar() {
      this.savingAvatar = true
      try {
        const response = await request({
          url: '/admin/profile',
          method: 'put',
          data: this.adminForm
        })
        if (response.data.code==1) {
          this.$message.success('头像保存成功')
          this.avatarChanged = false
          this.originalAvatarUrl = this.adminForm.avatarUrl

          // 更新localStorage中的用户信息，让导航栏显示新头像
          this.updateUserInfoInStorage()
        } else {
          this.$message.error('头像保存失败：' + response.data.message)
        }
      } catch (error) {
        console.error('保存头像失败:', error)
        this.$message.error('头像保存失败')
      } finally {
        this.savingAvatar = false
      }
    },

    // 重置表单
    resetForm() {
      this.loadAdminProfile()
    },

    // 重置密码表单
    resetPasswordForm() {
      this.$refs.passwordForm.resetFields()
    },

    // 格式化日期
    formatDate(date) {
      if (!date) return ''
      return new Date(date).toLocaleString('zh-CN')
    },

    // 更新localStorage中的用户信息
    updateUserInfoInStorage() {
      try {
        // 更新admin_user信息
        const adminUser = localStorage.getItem('admin_user')
        if (adminUser) {
          const user = JSON.parse(adminUser)
          user.avatarUrl = this.adminForm.avatarUrl
          user.avatar_url = this.adminForm.avatarUrl // 兼容不同的字段名
          localStorage.setItem('admin_user', JSON.stringify(user))
        }

        // 也更新userInfo信息（如果存在）
        const userInfo = localStorage.getItem('userInfo')
        if (userInfo) {
          const user = JSON.parse(userInfo)
          user.avatarUrl = this.adminForm.avatarUrl
          user.avatar_url = this.adminForm.avatarUrl
          localStorage.setItem('userInfo', JSON.stringify(user))
        }

        // 触发自定义事件，通知其他组件用户信息已更新
        window.dispatchEvent(new CustomEvent('userInfoUpdated', {
          detail: {
            avatarUrl: this.adminForm.avatarUrl
          }
        }))

      } catch (error) {
        console.error('更新localStorage用户信息失败:', error)
      }
    }
  }
}
</script>

<style scoped>
.admin-profile {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

.profile-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 30px;
  border-radius: 10px;
  margin-bottom: 20px;
  text-align: center;
}

.profile-header h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 600;
}

.profile-header p {
  margin: 0;
  opacity: 0.9;
  font-size: 16px;
}

.profile-content {
  max-width: 1200px;
  margin: 0 auto;
}

.profile-card,
.avatar-card,
.password-card {
  border-radius: 10px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: none;
}

.card-header {
  font-weight: 600;
  color: #303133;
  font-size: 16px;
}

.card-header i {
  margin-right: 8px;
  color: #409eff;
}

/* 头像相关样式 */
.avatar-section {
  text-align: center;
}

.avatar-container {
  width: 120px;
  height: 120px;
  margin: 0 auto 20px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid #e4e7ed;
  position: relative;
  cursor: pointer;
  transition: all 0.3s;
}

.avatar-container:hover {
  border-color: #409eff;
  transform: scale(1.05);
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 100%;
  height: 100%;
  background-color: #fafafa;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #c0c4cc;
}

.avatar-placeholder i {
  font-size: 24px;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 12px;
}

.avatar-uploader {
  margin-bottom: 15px;
}

.avatar-actions {
  margin-bottom: 15px;
}

.avatar-actions .el-button {
  margin: 0 5px 5px 0;
}

.upload-tips {
  font-size: 12px;
  color: #909399;
  line-height: 1.5;
}

.upload-tips p {
  margin: 2px 0;
}

/* 表单样式 */
.el-form-item {
  margin-bottom: 22px;
}

.el-input__inner {
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s;
}

.el-input__inner:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.el-button {
  border-radius: 6px;
  padding: 10px 20px;
  font-weight: 500;
}

.el-button--primary {
  background: linear-gradient(135deg, #409eff 0%, #667eea 100%);
  border: none;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #7c8cff 100%);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-profile {
    padding: 10px;
  }
  
  .profile-header {
    padding: 20px;
  }
  
  .profile-header h2 {
    font-size: 24px;
  }
}
</style>
