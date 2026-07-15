
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

// 1. 必须导入 Element Plus 核心库和样式
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'

// 2. 先创建 app 实例，再依次注册路由和 Element Plus
const app = createApp(App)
app.use(router)
app.use(ElementPlus) // 全局注册 Element Plus（这一步不能少）
app.mount('#app')

// 消除 "已声明ElementPlus但从未读取" 的警告（可选，不影响功能）
console.log(ElementPlus, 'Element Plus 已全局注册')