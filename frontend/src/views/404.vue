<template>
  <div class="not-found">
    <div class="content">
      <h1>404</h1>
      <h2>页面未找到</h2>
      <p>抱歉，您访问的页面不存在</p>
      <el-button type="primary" @click="goHome">返回首页</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFound',
  methods: {
    goHome() {
      const userType = localStorage.getItem('userType')
      
      if (userType === 'admin') {
        this.$router.push('/admin/dashboard')
      } else if (userType === 'expert') {
        this.$router.push('/expert/dashboard')
      } else if (userType === 'normal') {
        this.$router.push('/plants')
      } else {
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style scoped>
.not-found {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f3f4f6;
}

.content {
  text-align: center;
}

h1 {
  font-size: 80px;
  margin: 0;
  color: #409EFF;
}

h2 {
  margin: 10px 0;
  color: #333;
}

p {
  margin-bottom: 30px;
  color: #666;
}
</style> 