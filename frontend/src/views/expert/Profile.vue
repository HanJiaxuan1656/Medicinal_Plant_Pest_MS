<template>
  <div class="profile-container">
    <!-- 页面头部 -->
    <div class="page-header">
      <h1 class="page-title">
        <i class="el-icon-user"></i>
        个人中心
      </h1>
      <p class="page-subtitle">管理您的个人信息和账户设置</p>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :span="6">
        <el-card class="stat-card plants-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-s-management"></i>
            </div>
            <div class="stat-info">
              <h3>{{ stats.plantsCreated }}</h3>
              <p>创建的植物</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card pests-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-warning"></i>
            </div>
            <div class="stat-info">
              <h3>{{ stats.pestsCreated }}</h3>
              <p>创建的病虫害</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card pesticides-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-s-tools"></i>
            </div>
            <div class="stat-info">
              <h3>{{ stats.pesticidesCreated }}</h3>
              <p>创建的农药</p>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card helps-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-question"></i>
            </div>
            <div class="stat-info">
              <h3>{{ stats.helpsReplied }}</h3>
              <p>回复的求助</p>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>


    <!-- 个人信息表单 -->
    <el-card class="profile-form-card" shadow="never">
      <div slot="header" class="card-header">
        <span class="card-title">
          <i class="el-icon-edit"></i>
          个人信息
        </span>
      </div>

      <el-form :model="userForm" :rules="rules" ref="userForm" label-width="120px" class="profile-form">
        <el-row :gutter="40">
          <el-col :span="8">
            <div class="avatar-section">
              <el-form-item label="头像" prop="avatarUrl">
                <el-upload
                  class="avatar-uploader"
                  action="/api/files/upload"
                  :show-file-list="false"
                  :on-success="handleAvatarSuccess"
                  :before-upload="beforeAvatarUpload"
                  :headers="uploadHeaders">
                  <img v-if="userForm.avatarUrl" :src="userForm.avatarUrl" class="avatar">
                  <div v-else class="avatar-placeholder">
                    <i class="el-icon-plus"></i>
                    <div class="upload-text">点击上传头像</div>
                  </div>
                </el-upload>
              </el-form-item>
            </div>
          </el-col>
          <el-col :span="16">
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="用户名" prop="username">
                  <el-input v-model="userForm.username" disabled>
                    <i slot="prefix" class="el-icon-user"></i>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="真实姓名" prop="name">
                  <el-input v-model="userForm.name" placeholder="请输入真实姓名">
                    <i slot="prefix" class="el-icon-s-custom"></i>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="职称" prop="title">
                  <el-input v-model="userForm.title" placeholder="请输入职称">
                    <i slot="prefix" class="el-icon-medal"></i>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="所属机构" prop="organization">
                  <el-input v-model="userForm.organization" placeholder="请输入所属机构">
                    <i slot="prefix" class="el-icon-office-building"></i>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="邮箱" prop="email">
                  <el-input v-model="userForm.email" placeholder="请输入邮箱地址">
                    <i slot="prefix" class="el-icon-message"></i>
                  </el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="联系电话" prop="phone">
                  <el-input v-model="userForm.phone" placeholder="请输入联系电话">
                    <i slot="prefix" class="el-icon-phone"></i>
                  </el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-form-item>
              <el-button type="primary" @click="submitForm" :loading="submitLoading">
                <i class="el-icon-check"></i>
                保存修改
              </el-button>
              <el-button @click="resetForm">
                <i class="el-icon-refresh"></i>
                重置
              </el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 修改密码 -->
    <el-card class="password-form-card" shadow="never">
      <div slot="header" class="card-header">
        <span class="card-title">
          <i class="el-icon-lock"></i>
          修改密码
        </span>
      </div>

      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="120px" class="password-form">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="旧密码" prop="oldPassword">
              <el-input type="password" v-model="passwordForm.oldPassword" placeholder="请输入旧密码" show-password>
                <i slot="prefix" class="el-icon-lock"></i>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="新密码" prop="newPassword">
              <el-input type="password" v-model="passwordForm.newPassword" placeholder="请输入新密码" show-password>
                <i slot="prefix" class="el-icon-key"></i>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input type="password" v-model="passwordForm.confirmPassword" placeholder="请确认新密码" show-password>
                <i slot="prefix" class="el-icon-key"></i>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item>
          <el-button type="primary" @click="submitPassword" :loading="passwordLoading">
            <i class="el-icon-check"></i>
            修改密码
          </el-button>
          <el-button @click="resetPasswordForm">
            <i class="el-icon-refresh"></i>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getExpertProfile, updateExpertProfile, updatePassword, getExpertStats } from '@/api/expert'

export default {
  name: 'ExpertProfile',
  data() {
    return {
      loading: false,
      submitLoading: false,
      passwordLoading: false,
      uploadHeaders: {
        Authorization: `Bearer ${localStorage.getItem('token')}`
      },
      currentExpert: {},
      expertOptions: [
        { id: 1, name: '张教授', title: '高级农艺师', specialization: '病虫害防治' },
        { id: 2, name: '李专家', title: '植物病理学专家', specialization: '植物病理' },
        { id: 3, name: '王博士', title: '农业技术专家', specialization: '农药应用' },
        { id: 4, name: '刘研究员', title: '植物保护专家', specialization: '生物防治' },
        { id: 5, name: '陈主任', title: '农业推广专家', specialization: '综合防治' }
      ],
      stats: {
        plantsCreated: 0,
        pestsCreated: 0,
        pesticidesCreated: 0,
        helpsReplied: 0
      },
      userForm: {
        id: null,
        username: '',
        name: '',
        title: '',
        organization: '',
        email: '',
        phone: '',
        avatarUrl: ''
      },
      originalUserForm: {},
      rules: {
        name: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        email: [
          { type: 'email', message: '请输入正确的邮箱地址', trigger: ['blur', 'change'] }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ]
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入旧密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          {
            validator: (_, value, callback) => {
              if (value !== this.passwordForm.newPassword) {
                callback(new Error('两次输入密码不一致'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  computed: {
    uploadHeaders() {
      return {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    }
  },
  created() {
    this.loadCurrentExpert()
    this.loadData()
  },
  methods: {
    loadCurrentExpert() {
      const expertInfo = localStorage.getItem('expertInfo')
      if (expertInfo) {
        this.currentExpert = JSON.parse(expertInfo)
      } else {
        // 默认设置为第一个专家
        this.currentExpert = this.expertOptions[0]
        this.saveExpertToLocalStorage()
      }
    },

    setCurrentExpert(expert) {
      this.currentExpert = { ...expert }
      this.saveExpertToLocalStorage()
      this.$message.success(`已切换到专家：${expert.name}`)
    },

    saveExpertToLocalStorage() {
      localStorage.setItem('expertInfo', JSON.stringify(this.currentExpert))
    },
    async loadData() {
      this.loading = true
      try {
        // 加载用户信息
        const profileRes = await getExpertProfile()
        if (profileRes.data.code === 1) {
          this.userForm = { ...profileRes.data.data }
          this.originalUserForm = { ...profileRes.data.data }
        } else {
          this.$message.error(profileRes.data.msg || '获取用户信息失败')
        }

        // 加载统计信息
        const statsRes = await getExpertStats()
        if (statsRes.data.code === 1) {
          this.stats = statsRes.data.data
        } else {
          this.$message.error(statsRes.data.msg || '获取统计信息失败')
        }
      } catch (error) {
        console.error('加载数据失败:', error)
        this.$message.error('加载数据失败: ' + (error.response && error.response.data && error.response.data.msg ? error.response.data.msg : error.message))
      } finally {
        this.loading = false
      }
    },

    handleAvatarSuccess(response) {
      if (response.code === 1) {
        // 根据Pests.vue的成功实现，response.data应该是文件URL
        this.userForm.avatarUrl = response.data
        this.$message.success('头像上传成功')

        // 更新localStorage中的用户信息，让导航栏显示新头像
        this.updateUserInfoInStorage()
      } else {
        this.$message.error(response.msg || '头像上传失败')
      }
    },

    beforeAvatarUpload(file) {
      const isImage = file.type.startsWith('image/')
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isImage) {
        this.$message.error('只能上传图片文件!')
        return false
      }
      if (!isLt2M) {
        this.$message.error('图片大小不能超过 2MB!')
        return false
      }
      return true
    },

    async submitForm() {
      this.$refs.userForm.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          try {
            const response = await updateExpertProfile(this.userForm)
            if (response.data.code === 1) {
              this.$message.success('保存成功!')
              this.originalUserForm = { ...this.userForm }
              // 更新localStorage中的用户信息
              const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
              Object.assign(userInfo, this.userForm)
              localStorage.setItem('userInfo', JSON.stringify(userInfo))
            } else {
              this.$message.error(response.data.msg || '保存失败')
            }
          } catch (error) {
            console.error('保存失败:', error)
            this.$message.error('保存失败: ' + (error.response && error.response.data && error.response.data.msg ? error.response.data.msg : error.message))
          } finally {
            this.submitLoading = false
          }
        }
      })
    },

    resetForm() {
      this.userForm = { ...this.originalUserForm }
      this.$refs.userForm.clearValidate()
    },

    async submitPassword() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          this.passwordLoading = true
          try {
            const response = await updatePassword(this.passwordForm)
            if (response.data.code === 1) {
              this.$message.success('密码修改成功，即将跳转到登录页面!')
              this.resetPasswordForm()

              // 延迟2秒后退出登录
              setTimeout(() => {
                this.logout()
              }, 2000)
            } else {
              this.$message.error(response.data.msg || '密码修改失败')
            }
          } catch (error) {
            console.error('密码修改失败:', error)
            this.$message.error('密码修改失败')
          } finally {
            this.passwordLoading = false
          }
        }
      })
    },

    resetPasswordForm() {
      this.passwordForm = {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      }
      this.$refs.passwordForm.clearValidate()
    },

    // 更新localStorage中的用户信息
    updateUserInfoInStorage() {
      try {
        // 更新expert_user信息
        const expertUser = localStorage.getItem('expert_user')
        if (expertUser) {
          const user = JSON.parse(expertUser)
          user.avatarUrl = this.userForm.avatarUrl
          user.avatar_url = this.userForm.avatarUrl // 兼容不同的字段名
          localStorage.setItem('expert_user', JSON.stringify(user))
        }

        // 也更新userInfo信息（如果存在）
        const userInfo = localStorage.getItem('userInfo')
        if (userInfo) {
          const user = JSON.parse(userInfo)
          user.avatarUrl = this.userForm.avatarUrl
          user.avatar_url = this.userForm.avatarUrl
          localStorage.setItem('userInfo', JSON.stringify(user))
        }

        // 触发自定义事件，通知其他组件用户信息已更新
        window.dispatchEvent(new CustomEvent('userInfoUpdated', {
          detail: {
            avatarUrl: this.userForm.avatarUrl
          }
        }))
      } catch (error) {
        console.error('更新localStorage用户信息失败:', error)
      }
    },

    logout() {
      // 清除本地存储的token和用户信息
      localStorage.removeItem('expert_token')
      localStorage.removeItem('expert_user')

      // 跳转到登录页面
      this.$router.push('/login')

      this.$message.info('已退出登录')
    }
  }
}
</script>

<style scoped>
.profile-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 60px);
}

/* 页面头部 */
.page-header {
  margin-bottom: 30px;
  text-align: center;
}

.page-title {
  font-size: 28px;
  color: #303133;
  margin: 0 0 10px 0;
  font-weight: 600;
}

.page-title i {
  margin-right: 10px;
  color: #409eff;
}

.page-subtitle {
  color: #909399;
  font-size: 14px;
  margin: 0;
}

/* 统计卡片 */
.stats-section {
  margin-bottom: 30px;
}

.stat-card {
  border-radius: 12px;
  border: none;
  transition: all 0.3s ease;
  cursor: pointer;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.plants-card .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.pests-card .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.pesticides-card .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.helps-card .stat-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info h3 {
  font-size: 24px;
  font-weight: 600;
  margin: 0 0 5px 0;
  color: #303133;
}

.stat-info p {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 表单卡片 */
.profile-form-card,
.password-form-card {
  margin-bottom: 20px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  align-items: center;
  padding: 0;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.card-title i {
  margin-right: 8px;
  color: #409eff;
}

/* 头像上传 */
.avatar-section {
  text-align: center;
}

.avatar-uploader {
  display: inline-block;
}

.avatar-uploader .el-upload {
  border: 2px dashed #d9d9d9;
  border-radius: 12px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  width: 150px;
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-uploader .el-upload:hover {
  border-color: #409eff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.avatar {
  width: 150px;
  height: 150px;
  border-radius: 12px;
  object-fit: cover;
}

.avatar-placeholder {
  text-align: center;
  color: #8c939d;
}

.avatar-placeholder i {
  font-size: 28px;
  display: block;
  margin-bottom: 8px;
}

.upload-text {
  font-size: 12px;
}

/* 表单样式 */
.profile-form,
.password-form {
  padding: 20px 0;
}

.profile-form .el-form-item,
.password-form .el-form-item {
  margin-bottom: 25px;
}

.profile-form .el-input,
.password-form .el-input {
  border-radius: 8px;
}

.profile-form .el-input__inner,
.password-form .el-input__inner {
  border-radius: 8px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.profile-form .el-input__inner:focus,
.password-form .el-input__inner:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

/* 按钮样式 */
.el-button {
  border-radius: 8px;
  padding: 12px 24px;
  font-weight: 500;
}

.el-button--primary {
  background: linear-gradient(135deg, #409eff 0%, #5a9cff 100%);
  border: none;
}

.el-button--primary:hover {
  background: linear-gradient(135deg, #66b1ff 0%, #7db4ff 100%);
}

/* 专家身份设置样式 */
.expert-id-card {
  margin-bottom: 30px;
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.expert-id-content {
  padding: 16px 0;
}

.current-expert-info {
  margin-bottom: 20px;
}

.tip-text {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #909399;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 4px solid #3498db;
  margin: 0;
}

.expert-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.expert-btn {
  margin: 0;
  border-radius: 20px;
  padding: 8px 16px;
  transition: all 0.3s ease;
}

.expert-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .profile-container {
    padding: 10px;
  }

  .stat-content {
    flex-direction: column;
    text-align: center;
  }

  .stat-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }

  .expert-options {
    flex-direction: column;
  }

  .expert-btn {
    width: 100%;
    text-align: center;
  }
}
</style>