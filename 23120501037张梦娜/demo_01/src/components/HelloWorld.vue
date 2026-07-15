<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router';
defineProps<{ msg?: string }>()
  
const count = ref(0)
const currentRole = ref('admin');
// 新增：账号输入框绑定变量，默认值为空字符串
const loginAccount = ref('');
// 新增：密码输入框绑定变量，默认值为空字符串
const loginPwd = ref('');
const router = useRouter();
// 新增：登录验证函数
const handleLogin = () => {
  // 1. 预设正确的账号密码（管理员/教师分别预设）
  let correctAccount = '';
  let correctPwd = '';
  if (currentRole.value === 'admin') {
    // 管理员正确账号：admin，密码：123456
    correctAccount = 'admin';
    correctPwd = '123456';
  } else {
    // 教师正确账号：teacher001，密码：654321
    correctAccount = 'teacher001';
    correctPwd = '654321';
  }

  // 2. 对比用户输入和正确账号密码（去除首尾空格，避免用户输入多余空格导致验证失败）
  const inputAccount = loginAccount.value.trim();
  const inputPwd = loginPwd.value.trim();

  // 3. 验证逻辑
  if (!inputAccount || !inputPwd) {
    // 账号或密码为空时，提示补全
    alert('请输入账号和密码！');
    return; // 终止函数，不继续验证
  }
  
  if (inputAccount === correctAccount && inputPwd === correctPwd) {
    // 登录成功：根据角色跳转对应首页
    alert(`恭喜！${currentRole.value === 'admin' ? '管理员' : '教师'}登录成功！`);
    
    // 核心：编程式导航跳转
    if (currentRole.value === 'admin') {
      // 管理员跳转到管理员首页
      router.push('/admin/home');
    } else {
      // 教师跳转到教师首页
      router.push('/teacher/home');
    }

    // 清空输入框
    loginAccount.value = '';
    loginPwd.value = '';
  } else {
    alert('账号或密码错误，请重新输入！');
    loginPwd.value = '';
  }
  // else {
  //   // 账号密码不匹配，提示错误
  //   alert('账号或密码错误，请重新输入！');
  //   // 清空密码输入框（账号可保留，提升用户体验）
  //   loginPwd.value = '';
  // }
};
</script>

<template>
  <router-view></router-view>
  <h1>{{ msg }}</h1>

  <div class="card">
    <!-- <button type="button" @click="count++">count is {{ count }}</button>
    <p>
      Edit
      <code>components/HelloWorld.vue</code> to test HMR
    </p> -->
    <!-- 查询表单区域 -->
    <div class="search-form">
      
    <!-- 第一步：角色切换按钮 -->
    <div class="role-tab">
      <!-- 管理员登录按钮：绑定 class 和点击事件 -->
      <button 
        class="role-btn" 
        :class="{ active: currentRole === 'admin' }" 
        @click="currentRole = 'admin'" 
      >
        管理员登录
      </button>

      <!-- 教师登录按钮：绑定 class 和点击事件 -->
      <button 
        class="role-btn" 
        :class="{ active: currentRole === 'teacher' }" 
        @click="currentRole = 'teacher'" 
      >
        教师登录
      </button>
    </div>

     <!-- 第二步：登录表单 -->
    <div class="login-form">
      <!-- 账号输入框 -->
      <div class="form-item">
        <label>账号：</label>
        <!-- 动态绑定账号占位符：如果是 admin 显示管理员账号，否则显示教师账号 -->
        <input 
          type="text" 
          :placeholder="currentRole === 'admin' ? '请输入管理员账号' : '请输入教师账号'" 
          v-model="loginAccount"
        />
      </div>
      <!-- 密码输入框 -->
      <div class="form-item">
        <label>密码：</label>
        <!-- 动态绑定密码占位符：如果是 admin 显示管理员密码，否则显示教师密码 -->
        <input 
          type="password" 
          :placeholder="currentRole === 'admin' ? '请输入管理员密码' : '请输入教师密码'" 
          v-model="loginPwd"
        />
      </div>
      <!-- 登录按钮 -->
      <button class="login-btn" @click="handleLogin">登录</button>
    </div>
    </div>
  </div>
<!-- 
  <p>
    Check out
    <a href="https://vuejs.org/guide/quick-start.html#local" target="_blank"
      >create-vue</a
    >, the official Vue + Vite starter
  </p>
  <p>
    Learn more about IDE Support for Vue in the
    <a
      href="https://vuejs.org/guide/scaling-up/tooling.html#ide-support"
      target="_blank"
      >Vue Docs Scaling up Guide</a
    >.
  </p>
  <p class="read-the-docs">Click on the Vite and Vue logos to learn more</p> -->
</template>

<style scoped>
.read-the-docs {
  /* color: #888; */
  width: 80%; /* 容器宽度占浏览器的 80%，不撑满全屏 */
  max-width: 1200px; /* 最大宽度 1200px，屏幕再大也不会变形 */
  margin: 0 auto; /* 让容器水平居中（上下 0 间距，左右自动居中） */
  padding: 20px; /* 容器内部上下左右留 20px 间距，后续放内容不贴边 */

}
/* 新增：角色切换容器样式 */
.role-tab {
  display: flex; /* 让内部的两个按钮横向排列 */
  justify-content: center; /* 让按钮水平居中显示（整体居中） */
  margin-bottom: 30px; /* 和下方的登录表单拉开 30px 间距，不拥挤 */
  gap: 2px; /* 两个按钮之间的间距，避免贴在一起 */
}
/* 普通角色按钮样式 */
.role-btn {
  padding: 10px 30px; /* 按钮内部上下 10px、左右 30px 间距，按钮更饱满 */
  border: none; /* 去掉默认的灰色边框 */
  background-color: #eee; /* 默认背景色：浅灰色 */
  cursor: pointer; /* 鼠标移上去显示“小手”图标，提示可点击 */
  font-size: 16px; /* 字体大小，更醒目 */
  border-radius: 4px 4px 0 0; /* 顶部左右圆角，底部直角，更美观 */
}
/* 新增：普通角色按钮悬浮样式 */
.role-btn:not(.active):hover {
  background-color: #ddd; /* 鼠标移上去，浅灰色变深一点，有反馈 */
  transition: background-color 0.3s; /* 柔和过渡动画 */
}
/* 选中的角色按钮样式（管理员默认选中） */
.role-btn.active {
  background-color: #42b983; /* 选中背景色：Vue 主题绿，醒目 */
  color: white; /* 选中后文字变成白色，对比明显 */
  font-weight: bold; /* 选中后文字加粗，更突出 */
}
/* 登录表单容器样式 */
.login-form {
  width: 400px; /* 固定表单宽度，不会太宽也不会太窄 */
  margin: 0 auto; /* 让表单水平居中（和角色按钮对齐） */
  display: flex; 
  flex-direction: column; /* 让表单内的元素（账号、密码、登录按钮）垂直排列 */
  gap: 20px; /* 每个表单项之间拉开 20px 间距，不拥挤 */
}
/* 单个表单项（标签+输入框）样式 */
.form-item {
  display: flex; /* 让标签和输入框横向排列 */
  align-items: center; /* 标签和输入框垂直居中对齐，不歪斜 */
  justify-content: space-between; /* 标签居左，输入框居右，自动填充中间空间 */
}
/* 表单项标签样式 */
.form-item label {
  width: 80px; /* 固定标签宽度（不管是“账号”还是“密码”，宽度一致） */
  font-size: 16px; /* 字体大小，和按钮协调 */
  color: #333; /* 文字颜色，柔和不刺眼 */
  text-align: right; /* 标签文字右对齐，更美观 */
  margin-right: 10px; /* 和输入框之间留 10px 间距 */
}
/* 表单项输入框样式 */
.form-item input {
  flex: 1; /* 自动填充剩余宽度，占满表单剩余空间 */
  padding: 10px 15px; /* 输入框内部上下 10px、左右 15px 间距，输入时不拥挤 */
  border: 1px solid #ddd; /* 浅灰色边框，比默认边框更柔和 */
  border-radius: 4px; /* 圆角样式，不生硬 */
  font-size: 16px; /* 输入文字大小，和整体协调 */
  outline: none; /* 去掉输入框聚焦时的默认蓝色边框（后续可加自定义聚焦样式） */
}
/* 新增：输入框聚焦样式（点击输入框时生效） */
.form-item input:focus {
  border-color: #42b983; /* 边框变成 Vue 主题绿，和选中角色按钮、登录按钮同色，风格统一 */
  box-shadow: 0 0 0 2px rgba(66, 185, 131, 0.2); /* 轻微的绿色外发光，更醒目，不刺眼 */
}
/* 登录按钮样式 */
.login-btn {
  padding: 12px 0; /* 上下 12px 间距，左右自适应，按钮更饱满 */
  border: none; /* 去掉默认灰色边框 */
  background-color: #42b983; /* 和选中角色按钮同色，风格统一 */
  color: white; /* 文字白色，对比明显 */
  font-size: 16px; /* 字体大小，协调统一 */
  font-weight: bold; /* 文字加粗，更醒目 */
  border-radius: 4px; /* 圆角样式，美观 */
  cursor: pointer; /* 鼠标移上去显示小手，提示可点击 */
  width: 100%; /* 按钮宽度占满表单宽度，更规整 */
}
/* 新增：登录按钮悬浮样式（鼠标移上去时生效） */
.login-btn:hover {
  background-color: #359469; /* 背景色变成稍深一点的绿色，有视觉反馈，提示可点击 */
  transition: background-color 0.3s; /* 颜色过渡动画，变化更柔和，不生硬 */
}
</style>
