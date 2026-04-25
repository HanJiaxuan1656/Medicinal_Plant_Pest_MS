import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import './assets/css/global.css'
import request from './utils/request'

Vue.prototype.$axios = request

Vue.config.productionTip = false

// 创建事件总线
Vue.prototype.$eventBus = new Vue()

// 使用ElementUI
Vue.use(ElementUI)

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')