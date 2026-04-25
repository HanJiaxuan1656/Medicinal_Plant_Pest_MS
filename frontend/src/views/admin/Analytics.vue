<template>
  <div class="analytics-container" v-loading="loading" element-loading-text="加载数据中...">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h1 class="page-title">
            <i class="el-icon-data-analysis"></i>
            数据可视化分析
          </h1>
          <p class="page-subtitle">系统数据深度分析与可视化展示</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="refreshData" :loading="loading" icon="el-icon-refresh">
            刷新数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-section">
      <el-row :gutter="24">
        <el-col :span="6" v-for="(metric, index) in coreMetrics" :key="index">
          <div class="metric-card" :class="`metric-${index + 1}`">
            <div class="metric-icon">
              <i :class="metric.icon"></i>
            </div>
            <div class="metric-content">
              <div class="metric-value">{{ metric.value }}</div>
              <div class="metric-label">{{ metric.label }}</div>
              <div class="metric-trend" :class="metric.trend > 0 ? 'positive' : 'negative'">
                <i :class="metric.trend > 0 ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
                {{ Math.abs(metric.trend) }}%
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <el-row :gutter="24">
        <!-- 用户增长趋势折线图 -->
        <el-col :span="12">
          <div class="chart-card">
            <div class="chart-header">
              <h3><i class="el-icon-trend-charts"></i> 用户增长趋势</h3>
              <span class="chart-subtitle">最近7天新增用户统计</span>
            </div>
            <div class="chart-content">
              <div ref="userGrowthChart" class="echarts-container"></div>
            </div>
          </div>
        </el-col>

        <!-- 内容分布饼图 -->
        <el-col :span="12">
          <div class="chart-card">
            <div class="chart-header">
              <h3><i class="el-icon-pie-chart"></i> 内容分布统计</h3>
            </div>
            <div class="chart-content">
              <div ref="contentDistributionChart" class="echarts-container"></div>
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="24" style="margin-top: 24px;">
        <!-- 求助趋势面积图 -->
        <el-col :span="16">
          <div class="chart-card">
            <div class="chart-header">
              <h3><i class="el-icon-help"></i> 求助趋势分析</h3>
              <span class="chart-subtitle">过去30天求助提交趋势</span>
            </div>
            <div class="chart-content">
              <div ref="helpTrendChart" class="echarts-container"></div>
            </div>
          </div>
        </el-col>

        <!-- 近七天评论数量柱状图 -->
        <el-col :span="8">
          <div class="chart-card">
            <div class="chart-header">
              <h3><i class="el-icon-chat-dot-round"></i> 近七天评论统计</h3>
              <span class="chart-subtitle">每日评论数量趋势</span>
            </div>
            <div class="chart-content">
              <div ref="interactionChart" class="echarts-container"></div>
            </div>
          </div>
        </el-col>
      </el-row>


    </div>


  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getAnalyticsData } from '@/api/admin'

export default {
  name: 'AdminAnalytics',
  data() {
    return {
      loading: false,
      coreMetrics: [
        { icon: 'el-icon-user-solid', label: '总用户数', value: 0, trend: 0 },
        { icon: 'el-icon-document', label: '总内容数', value: 0, trend: 0 },
        { icon: 'el-icon-chat-dot-round', label: '总互动数', value: 0, trend: 0 },
        { icon: 'el-icon-star-on', label: '活跃度', value: '0%', trend: 0 }
      ],
      // ECharts实例
      userGrowthChartInstance: null,
      contentDistributionChartInstance: null,
      helpTrendChartInstance: null,
      interactionChartInstance: null
    }
  },

  mounted() {
    this.$nextTick(() => {
      this.initCharts()
      this.fetchAnalyticsData()
    })
  },

  beforeDestroy() {
    // 销毁图表实例
    if (this.userGrowthChartInstance) {
      this.userGrowthChartInstance.dispose()
    }
    if (this.contentDistributionChartInstance) {
      this.contentDistributionChartInstance.dispose()
    }
    if (this.helpTrendChartInstance) {
      this.helpTrendChartInstance.dispose()
    }
    if (this.interactionChartInstance) {
      this.interactionChartInstance.dispose()
    }
  },

  methods: {
    // 初始化所有图表
    initCharts() {
      this.initUserGrowthChart()
      this.initContentDistributionChart()
      this.initHelpTrendChart()
      this.initInteractionChart()
    },

    async fetchAnalyticsData() {
      try {
        this.loading = true
        const response = await getAnalyticsData()

        if (response.data && (response.data.code === 1 || response.data.code === 200)) {
          const data = response.data.data
          this.updateMetrics(data)
          this.updateChartsData(data)
        } else {
          this.$message.error('获取分析数据失败：' + (response.data && response.data.message ? response.data.message : '未知错误'))
        }
      } catch (error) {
        this.$message.error('获取分析数据失败：' + error.message)
      } finally {
        this.loading = false
      }
    },

    updateMetrics(data) {
      if (data.metrics) {
        this.coreMetrics = data.metrics
      }
    },



    // 初始化用户增长趋势图
    initUserGrowthChart() {
      const chartDom = this.$refs.userGrowthChart
      if (!chartDom) return

      this.userGrowthChartInstance = echarts.init(chartDom)

      const option = {
        backgroundColor: 'transparent',
        title: {
          show: false
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: '#409eff',
          textStyle: {
            color: '#fff'
          },
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          }
        },
        legend: {
          data: ['专家用户', '普通用户'],
          textStyle: {
            color: '#606266'
          },
          top: 10
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '15%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: [],
          axisLine: {
            lineStyle: {
              color: '#e4e7ed'
            }
          },
          axisLabel: {
            color: '#909399'
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            lineStyle: {
              color: '#e4e7ed'
            }
          },
          axisLabel: {
            color: '#909399'
          },
          splitLine: {
            lineStyle: {
              color: '#f5f7fa'
            }
          }
        },
        series: [
          {
            name: '专家用户',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 4,
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#667eea' },
                { offset: 1, color: '#764ba2' }
              ])
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
                { offset: 1, color: 'rgba(102, 126, 234, 0.05)' }
              ])
            },
            itemStyle: {
              color: '#667eea',
              borderColor: '#fff',
              borderWidth: 2
            },
            data: []
          },
          {
            name: '普通用户',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 4,
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#4facfe' },
                { offset: 1, color: '#00f2fe' }
              ])
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(79, 172, 254, 0.3)' },
                { offset: 1, color: 'rgba(79, 172, 254, 0.05)' }
              ])
            },
            itemStyle: {
              color: '#4facfe',
              borderColor: '#fff',
              borderWidth: 2
            },
            data: []
          }
        ],
        animationDuration: 2000,
        animationEasing: 'cubicOut'
      }

      this.userGrowthChartInstance.setOption(option)
    },

    // 初始化内容分布饼图
    initContentDistributionChart() {
      const chartDom = this.$refs.contentDistributionChart
      if (!chartDom) return

      this.contentDistributionChartInstance = echarts.init(chartDom)

      const option = {
        backgroundColor: 'transparent',
        title: {
          show: false
        },
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: '#409eff',
          textStyle: {
            color: '#fff'
          },
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          top: 'center',
          textStyle: {
            color: '#606266'
          }
        },
        series: [
          {
            name: '内容分布',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['60%', '50%'],
            avoidLabelOverlap: false,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            label: {
              show: true,
              position: 'outside',
              formatter: '{b}\n{d}%',
              fontSize: 12,
              color: '#606266'
            },
            labelLine: {
              show: true,
              length: 15,
              length2: 10
            },
            data: []
          }
        ],
        animationType: 'scale',
        animationEasing: 'elasticOut',
        animationDelay: function (idx) {
          return idx * 100 + Math.random() * 200
        }
      }

      this.contentDistributionChartInstance.setOption(option)
    },

    // 初始化求助趋势面积图
    initHelpTrendChart() {
      const chartDom = this.$refs.helpTrendChart
      if (!chartDom) return

      this.helpTrendChartInstance = echarts.init(chartDom)

      const option = {
        backgroundColor: 'transparent',
        title: {
          show: false
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: '#409eff',
          textStyle: {
            color: '#fff'
          },
          axisPointer: {
            type: 'cross'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: [],
          axisLine: {
            lineStyle: {
              color: '#e4e7ed'
            }
          },
          axisLabel: {
            color: '#909399'
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            lineStyle: {
              color: '#e4e7ed'
            }
          },
          axisLabel: {
            color: '#909399'
          },
          splitLine: {
            lineStyle: {
              color: '#f5f7fa'
            }
          }
        },
        series: [
          {
            name: '求助数量',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: {
              width: 4,
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                { offset: 0, color: '#4facfe' },
                { offset: 1, color: '#00f2fe' }
              ])
            },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(79, 172, 254, 0.6)' },
                { offset: 1, color: 'rgba(79, 172, 254, 0.1)' }
              ])
            },
            itemStyle: {
              color: '#4facfe',
              borderColor: '#fff',
              borderWidth: 3
            },
            data: []
          }
        ],
        animationDuration: 2000,
        animationEasing: 'cubicOut'
      }

      this.helpTrendChartInstance.setOption(option)
    },

    // 初始化近七天评论统计柱状图
    initInteractionChart() {
      const chartDom = this.$refs.interactionChart
      if (!chartDom) return

      this.interactionChartInstance = echarts.init(chartDom)

      const option = {
        backgroundColor: 'transparent',
        title: {
          show: false
        },
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(0, 0, 0, 0.8)',
          borderColor: '#409eff',
          textStyle: {
            color: '#fff'
          },
          axisPointer: {
            type: 'shadow'
          },
          formatter: function(params) {
            const data = params[0]
            return `${data.name}<br/>评论数量: ${data.value}`
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          data: [],
          axisLine: {
            lineStyle: {
              color: '#e4e7ed'
            }
          },
          axisLabel: {
            color: '#909399',
            rotate: 45
          }
        },
        yAxis: {
          type: 'value',
          axisLine: {
            lineStyle: {
              color: '#e4e7ed'
            }
          },
          axisLabel: {
            color: '#909399'
          },
          splitLine: {
            lineStyle: {
              color: '#f5f7fa'
            }
          }
        },
        series: [
          {
            name: '评论数量',
            type: 'bar',
            barWidth: '60%',
            itemStyle: {
              borderRadius: [6, 6, 0, 0],
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#4facfe' },
                { offset: 1, color: '#00f2fe' }
              ])
            },
            label: {
              show: true,
              position: 'top',
              color: '#606266',
              fontSize: 12,
              fontWeight: 'bold'
            },
            data: []
          }
        ],
        animationDuration: 1500,
        animationEasing: 'elasticOut',
        animationDelay: function (idx) {
          return idx * 100
        }
      }

      this.interactionChartInstance.setOption(option)
    },





    // 更新图表数据
    updateChartsData(data) {
      if (data.charts) {
        // 更新用户增长图表 - 只显示最近7天
        if (data.charts.userGrowth && this.userGrowthChartInstance) {
          const dates = this.getLast7Days()
          const expertData = data.charts.userGrowth.expertUsers || []
          const normalData = data.charts.userGrowth.normalUsers || []

          // 只取最近7天的数据
          const last7ExpertData = expertData.slice(-7)
          const last7NormalData = normalData.slice(-7)

          this.userGrowthChartInstance.setOption({
            xAxis: {
              data: dates
            },
            series: [
              { data: last7ExpertData },
              { data: last7NormalData }
            ]
          })
        }

        // 更新内容分布图表
        if (data.charts.contentDistribution && this.contentDistributionChartInstance) {
          this.contentDistributionChartInstance.setOption({
            series: [{
              data: data.charts.contentDistribution
            }]
          })
        }

        // 更新求助趋势图表 - 只显示最近7天
        if (data.charts.helpTrend && this.helpTrendChartInstance) {
          // 只取最近7天的数据
          const last7HelpTrend = data.charts.helpTrend.slice(-7)
          const dates = last7HelpTrend.map(item => item.date)
          const values = last7HelpTrend.map(item => item.count)

          this.helpTrendChartInstance.setOption({
            xAxis: {
              data: dates
            },
            series: [{
              data: values
            }]
          })
        }

        // 更新近七天评论统计图表
        if (data.charts.commentTrend && this.interactionChartInstance) {
          // 只取最近7天的数据
          const last7CommentTrend = data.charts.commentTrend.slice(-7)
          const dates = last7CommentTrend.map(item => item.date)
          const values = last7CommentTrend.map(item => item.count)

          this.interactionChartInstance.setOption({
            xAxis: {
              data: dates
            },
            series: [{
              data: values
            }]
          })
        }
      }
    },



    refreshData() {
      this.fetchAnalyticsData()
    },

    getLast7Days() {
      const days = []
      for (let i = 6; i >= 0; i--) {
        const date = new Date()
        date.setDate(date.getDate() - i)
        days.push(date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }))
      }
      return days
    }
  }
}
</script>

<style scoped>
.analytics-container {
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  min-height: 100vh;
  position: relative;
}

.analytics-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="rgba(255,255,255,0.05)"/><circle cx="75" cy="75" r="1" fill="rgba(255,255,255,0.05)"/><circle cx="50" cy="10" r="0.5" fill="rgba(255,255,255,0.03)"/><circle cx="10" cy="50" r="0.5" fill="rgba(255,255,255,0.03)"/><circle cx="90" cy="30" r="0.5" fill="rgba(255,255,255,0.03)"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  pointer-events: none;
  z-index: 0;
}

.analytics-container > * {
  position: relative;
  z-index: 1;
}

/* 页面头部 */
.page-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 32px;
  margin-bottom: 32px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.title-section {
  flex: 1;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 800;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  margin: 0 0 8px 0;
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-title i {
  font-size: 2.2rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #606266;
  margin: 0;
  font-weight: 400;
}

.header-actions {
  display: flex;
  gap: 16px;
  flex-shrink: 0;
}

.header-actions .el-button {
  padding: 12px 24px;
  border-radius: 12px;
  font-weight: 600;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.header-actions .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.15);
}

/* 核心指标卡片 */
.metrics-section {
  margin-bottom: 32px;
}

.metric-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.metric-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
}

.metric-card.metric-1::before {
  background: linear-gradient(90deg, #4facfe 0%, #00f2fe 100%);
}

.metric-card.metric-2::before {
  background: linear-gradient(90deg, #f093fb 0%, #f5576c 100%);
}

.metric-card.metric-3::before {
  background: linear-gradient(90deg, #43e97b 0%, #38f9d7 100%);
}

.metric-card.metric-4::before {
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
}

.metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
}

.metric-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  color: white;
  flex-shrink: 0;
  position: relative;
}

.metric-1 .metric-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.metric-2 .metric-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.metric-3 .metric-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.metric-4 .metric-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.metric-content {
  flex: 1;
}

.metric-value {
  font-size: 2.5rem;
  font-weight: 800;
  color: #2c3e50;
  line-height: 1;
  margin-bottom: 4px;
}

.metric-label {
  font-size: 1rem;
  color: #606266;
  font-weight: 600;
  margin-bottom: 8px;
}

.metric-trend {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 0.9rem;
  font-weight: 600;
}

.metric-trend.positive {
  color: #67c23a;
}

.metric-trend.negative {
  color: #f56c6c;
}

/* 图表区域 */
.charts-section {
  margin-bottom: 32px;
}

.chart-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  margin-bottom: 24px;
}

.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.15);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f5f7fa;
}

.chart-header h3 {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 700;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-header h3 i {
  color: #409eff;
  font-size: 1.2rem;
}

.chart-subtitle {
  font-size: 0.9rem;
  color: #909399;
  font-weight: 400;
}

.chart-content {
  position: relative;
  padding: 20px;
}

/* ECharts容器样式 */
.echarts-container {
  width: 100%;
  height: 300px;
}

.echarts-container-large {
  width: 100%;
  height: 400px;
}

/* 简化图表样式 */
.simple-chart {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 图例样式 */
.chart-legend {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin-top: 15px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

/* 内容分布图表 */
.content-distribution-chart {
  padding: 20px;
  flex-direction: row;
  gap: 40px;
}

.donut-chart {
  position: relative;
  width: 200px;
  height: 200px;
}

.donut-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  z-index: 10;
}

.center-value {
  font-size: 2rem;
  font-weight: 800;
  color: #2c3e50;
  line-height: 1;
}

.center-label {
  font-size: 0.9rem;
  color: #909399;
  margin-top: 4px;
}

.donut-segments {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: conic-gradient(
    #4facfe 0deg 126deg,
    #f093fb 126deg 198deg,
    #43e97b 198deg 252deg,
    #667eea 252deg 324deg,
    #ffeaa7 324deg 360deg
  );
  mask: radial-gradient(circle at center, transparent 60px, black 60px);
  -webkit-mask: radial-gradient(circle at center, transparent 60px, black 60px);
}

.donut-legend {
  display: flex;
  flex-direction: column;
  gap: 12px;
  justify-content: center;
}

.donut-legend .legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: #606266;
}

.donut-legend .legend-color {
  width: 14px;
  height: 14px;
  border-radius: 3px;
}

.legend-text {
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .echarts-container {
    height: 250px;
  }

  .echarts-container-large {
    height: 300px;
  }
}

/* 专家响应图表 */
.expert-response-chart {
  padding: 20px;
}

.expert-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.expert-card {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.expert-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.expert-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.2rem;
  flex-shrink: 0;
}

.expert-info {
  flex: 1;
}

.expert-name {
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.expert-stats {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}

.expert-stats .stat-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.expert-stats .stat-label {
  font-size: 0.7rem;
  color: #909399;
}

.expert-stats .stat-value {
  font-size: 0.9rem;
  font-weight: 600;
  color: #2c3e50;
}

.expert-progress {
  margin-top: 4px;
}

/* 简化图表样式 */
.simple-chart {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

/* 用户增长图表 */
.user-growth-chart {
  padding: 20px;
}

.chart-bars {
  display: flex;
  align-items: end;
  gap: 8px;
  height: 200px;
  margin-bottom: 20px;
}

.chart-bar {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  flex: 1;
}

.bar-expert,
.bar-normal {
  width: 20px;
  border-radius: 4px 4px 0 0;
  transition: all 0.3s ease;
}

.bar-expert {
  background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
  margin-bottom: 2px;
}

.bar-normal {
  background: linear-gradient(180deg, #4facfe 0%, #00f2fe 100%);
}

.bar-label {
  font-size: 0.7rem;
  color: #909399;
  transform: rotate(-45deg);
  margin-top: 8px;
}

.chart-legend {
  display: flex;
  gap: 20px;
  justify-content: center;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.9rem;
  color: #606266;
}

.legend-color {
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.expert-color {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.normal-color {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

/* 内容分布图表 */
.content-distribution-chart {
  padding: 20px;
  flex-direction: row;
  gap: 40px;
}

.donut-chart {
  position: relative;
  width: 200px;
  height: 200px;
}

.donut-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  z-index: 10;
}

.center-value {
  font-size: 2rem;
  font-weight: 800;
  color: #2c3e50;
  line-height: 1;
}

.center-label {
  font-size: 0.9rem;
  color: #909399;
  margin-top: 4px;
}

.donut-segments {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: conic-gradient(
    #4facfe 0deg 126deg,
    #f093fb 126deg 198deg,
    #43e97b 198deg 252deg,
    #667eea 252deg 324deg,
    #ffeaa7 324deg 360deg
  );
  mask: radial-gradient(circle at center, transparent 60px, black 60px);
  -webkit-mask: radial-gradient(circle at center, transparent 60px, black 60px);
}

.donut-legend {
  display: flex;
  flex-direction: column;
  gap: 12px;
  justify-content: center;
}

.donut-legend .legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: #606266;
}

.donut-legend .legend-color {
  width: 14px;
  height: 14px;
  border-radius: 3px;
}

.legend-text {
  font-weight: 500;
}

/* 热力图 */
.heatmap-chart {
  padding: 20px;
}

.heatmap-grid {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.heatmap-row {
  display: flex;
  align-items: center;
  gap: 2px;
}

.row-label {
  width: 40px;
  font-size: 0.8rem;
  color: #909399;
  text-align: right;
  padding-right: 8px;
}

.heatmap-cell {
  width: 12px;
  height: 12px;
  border-radius: 2px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.heatmap-cell:hover {
  transform: scale(1.2);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

/* 互动统计图表 */
.interaction-chart {
  padding: 20px;
}

.interaction-bars {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 100%;
}

.interaction-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.interaction-label {
  width: 60px;
  font-size: 0.9rem;
  color: #606266;
  font-weight: 500;
  text-align: right;
}

.interaction-bar-container {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 12px;
  height: 24px;
}

.interaction-bar {
  height: 100%;
  border-radius: 12px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.interaction-bar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent 0%, rgba(255, 255, 255, 0.3) 50%, transparent 100%);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.interaction-value {
  font-size: 0.9rem;
  font-weight: 600;
  color: #2c3e50;
  min-width: 40px;
}

/* 专家响应图表 */
.expert-response-chart {
  padding: 20px;
}

.expert-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.expert-card {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.expert-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.expert-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 1.2rem;
  flex-shrink: 0;
}

.expert-info {
  flex: 1;
}

.expert-name {
  font-size: 1rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.expert-stats {
  display: flex;
  gap: 12px;
  margin-bottom: 8px;
}

.expert-stats .stat-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.expert-stats .stat-label {
  font-size: 0.7rem;
  color: #909399;
}

.expert-stats .stat-value {
  font-size: 0.9rem;
  font-weight: 600;
  color: #2c3e50;
}

.expert-progress {
  margin-top: 4px;
}

/* 数据表格区域 */
.data-table-section {
  margin-bottom: 32px;
}

.table-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f5f7fa;
}

.table-header h3 {
  margin: 0;
  font-size: 1.3rem;
  font-weight: 700;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 8px;
}

.table-header h3 i {
  color: #409eff;
  font-size: 1.2rem;
}

.table-content {
  border-radius: 12px;
  overflow: hidden;
}

/* 表格样式优化 */
.el-table {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
}

.el-table th {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  color: #495057;
  font-weight: 700;
  border: none;
}

.el-table td {
  border: none;
  padding: 16px 12px;
}

.el-table tr:hover {
  background: rgba(64, 158, 255, 0.05);
}



/* 响应式设计 */
@media (max-width: 1200px) {
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-start;
  }

  .metric-card {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .metric-icon {
    width: 56px;
    height: 56px;
  }

  .metric-value {
    font-size: 2rem;
  }
}

@media (max-width: 768px) {
  .analytics-container {
    padding: 16px;
  }

  .page-header {
    padding: 24px;
  }

  .page-title {
    font-size: 2rem;
  }

  .chart-card,
  .table-card {
    padding: 20px;
  }

  .chart-header,
  .table-header {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .table-content {
    overflow-x: auto;
  }
}

/* 动画效果 */
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

.metric-card,
.chart-card,
.table-card {
  animation: fadeInUp 0.6s ease-out;
}

.metric-card:nth-child(1) { animation-delay: 0.1s; }
.metric-card:nth-child(2) { animation-delay: 0.2s; }
.metric-card:nth-child(3) { animation-delay: 0.3s; }
.metric-card:nth-child(4) { animation-delay: 0.4s; }

/* 加载状态优化 */
.el-loading-mask {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
}

.el-loading-spinner .circular {
  width: 50px;
  height: 50px;
}

.el-loading-text {
  color: #409eff;
  font-weight: 600;
}
</style>
