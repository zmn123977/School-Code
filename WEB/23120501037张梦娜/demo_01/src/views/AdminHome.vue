<template>
  <div class="admin-home-container">
    <!-- 顶部导航 -->
    <div class="top-nav">
      <h3 class="nav-title">监考管理查询系统 - 管理员后台</h3>
      <button @click="logout" class="logout-btn">退出登录</button>
    </div>

    <!-- 主体布局：左侧菜单 + 右侧欢迎区 -->
    <div class="main-layout">
      <!-- 左侧菜单 -->
      <div class="left-menu">
        <div class="menu-item" :class="{ active: currentPath === '/admin/examSubject' }" @click="goToPage('/admin/examSubject')">
          考试科目管理
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/admin/teacher' }" @click="goToPage('/admin/teacher')">
          教师信息管理
        </div>
        <div class="menu-item" :class="{ active: currentPath === '/admin/proctorInfo' }" @click="goToPage('/admin/proctorInfo')">
          监考任务管理
        </div>
      </div>

      <!-- 右侧欢迎区域 -->
      <div class="right-welcome">
        <div class="welcome-card">
          <img src="https://img.icons8.com/fluency/100/42b983/teacher.png" alt="管理员图标" class="welcome-icon">
          <h2 class="welcome-title">欢迎回来，管理员！</h2>
          <p class="welcome-desc">
            请点击左侧菜单，进入对应功能模块进行管理操作<br>
            支持考试科目、教师信息、监考安排的增删改查
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router';
import { ref, onMounted } from 'vue';

const router = useRouter();
const route = useRoute();
const currentPath = ref('');

// 初始化当前路由路径
onMounted(() => {
  currentPath.value = route.path;
  router.afterEach((to) => {
    currentPath.value = to.path;
  });
});

// 跳转到对应功能页面
const goToPage = (path: string) => {
  router.push(path);
};

// 退出登录
const logout = () => {
  if (confirm('确定要退出管理员登录吗？')) {
    alert('退出成功！');
    router.push('/login');
  }
};
</script>

<style scoped>
/* 整体容器 */
.admin-home-container {
  width: 100%;
  min-height: 100vh;
  background-color: #f8f9fa;
  font-family: "Microsoft Yahei", sans-serif;
}

/* 顶部导航 */
.top-nav {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 30px;
  background-color: #42b983;
  color: #fff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.nav-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.logout-btn {
  padding: 6px 12px;
  background-color: #fff;
  color: #42b983;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.logout-btn:hover {
  background-color: #f0f0f0;
}

/* 主体布局 */
.main-layout {
  display: flex;
  width: 100%;
  height: calc(100vh - 50px);
}

/* 左侧菜单 */
.left-menu {
  width: 220px;
  background-color: #fff;
  box-shadow: 2px 0 4px rgba(0,0,0,0.05);
  padding-top: 20px;
}

.menu-item {
  padding: 15px 25px;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.menu-item:hover, .menu-item.active {
  background-color: #f5fcf8;
  color: #42b983;
  border-left-color: #42b983;
  font-weight: 600;
}

/* 右侧欢迎区（调整大小适配） */
.right-welcome {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 30px;
}

.welcome-card {
  /* 缩小卡片尺寸，更协调 */
  width: 400px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  padding: 30px;
  text-align: center;
}

.welcome-icon {
  margin-bottom: 15px;
  /* 缩小图标 */
  width: 80px;
  height: 80px;
}

.welcome-title {
  color: #2c3e50;
  font-size: 20px;
  margin-bottom: 10px;
}

.welcome-desc {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 0;
}
</style>