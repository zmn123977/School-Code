<template>
  <div class="teacher-home-container">
    <!-- 顶部导航 -->
    <div class="top-nav">
      <h3 class="nav-title">监考管理查询系统 - 教师端</h3>
      <button @click="logout" class="logout-btn">退出登录</button>
    </div>

    <!-- 主体布局 -->
    <div class="main-layout">
      <!-- 左侧菜单 -->
      <div class="left-menu">
        <div class="menu-item" :class="{ active: currentPath === '/teacher/proctorInfo' }" @click="goToPage('/teacher/proctorInfo')">
          我的监考信息
        </div>
      </div>

      <!-- 右侧内容 -->
      <div class="right-content">
        <!-- 初始提示（未点击菜单时显示） -->
        <div v-if="currentPath !== '/teacher/proctorInfo'" class="init-tip">
          <h2>请选择左侧菜单，查看对应的信息</h2>
        </div>

        <!-- 监考信息页面（新增返回首页按钮） -->
        <div v-else class="proctor-info-container">
          <!-- 新增：返回教师首页按钮 -->
          <div class="page-header">
            <h2 class="page-title">我的监考信息</h2>
            <button @click="goBackTeacherHome" class="back-btn">返回教师首页</button>
          </div>
          
          <!-- 监考信息表格 -->
          <div class="table-area">
            <table class="proctor-table">
              <thead>
                <tr>
                  <th>考试科目</th>
                  <th>监考教师</th>
                  <th>考试时间</th>
                  <th>考试地点</th>
                </tr>
              </thead>
              <tbody>
                <tr v-if="filteredProctorList.length === 0">
                  <td colspan="4" style="padding:20px;color:#999;text-align:center">暂无监考任务</td>
                </tr>
                <tr v-for="item in filteredProctorList" :key="item.subjectName + item.examTime">
                  <td>{{ item.subjectName }}</td>
                  <td>{{ item.teacherName }}</td>
                  <td>{{ item.examTime }}</td>
                  <td>{{ item.examPlace }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router';
import { ref, onMounted, computed } from 'vue';
import request from '../utils/request';

// 监考信息类型（匹配管理员字段名）
interface ProctorItem {
  subjectName: string;
  teacherName: string;
  examTime: string;
  examPlace: string;
}

const router = useRouter();
const route = useRoute();
const currentPath = ref('');
const currentTeacherNo = ref('');
const proctorList = ref<ProctorItem[]>([]);
const searchSubject = ref('');

// 初始化路由+获取教师工号
onMounted(() => {
  currentPath.value = route.path;
  router.afterEach((to) => {
    currentPath.value = to.path;
  });

  // 获取缓存的教师工号
  const loginInfo = localStorage.getItem('teacherLoginInfo');
  if (loginInfo) {
    const teacherInfo = JSON.parse(loginInfo);
    currentTeacherNo.value = teacherInfo.account;
  } else {
    alert('请先登录！');
    router.push('/login');
  }
});

// 跳转到监考信息页面（点击菜单时触发）
const goToPage = (path: string) => {
  router.push(path);
  // 跳转后再获取数据（确保点击菜单才加载）
  getMyProctorInfo();
};

// 新增：返回教师首页
const goBackTeacherHome = () => {
  router.push('/teacher/home');
};

// 退出登录
const logout = () => {
  if (confirm('确定要退出教师登录吗？')) {
    localStorage.removeItem('teacherLoginInfo');
    alert('退出成功！');
    router.push('/login');
  }
};

// 获取个人监考信息（点击菜单后才执行）
const getMyProctorInfo = async () => {
  try {
    const res = await request.get('/proctorInfo/teacher', {
      params: {
        teacherNo: currentTeacherNo.value
      }
    });
    if (res.data && res.data.code === 200) {
      proctorList.value = res.data.data as ProctorItem[];
    } else {
      alert(res.data?.message || '获取监考信息失败');
    }
  } catch (error) {
    console.error('获取监考信息异常：', error);
    alert('获取监考信息异常，请检查后端服务');
  }
};

// 筛选监考信息
const filteredProctorList = computed(() => {
  if (!searchSubject.value.trim()) {
    return proctorList.value;
  }
  return proctorList.value.filter(item => 
    item.subjectName.includes(searchSubject.value.trim())
  );
});

// 查询按钮事件
const handleSearch = () => {
  // 依赖computed自动筛选
};
</script>

<style scoped>
/* 整体容器 */
.teacher-home-container {
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

/* 右侧内容 */
.right-content {
  flex: 1;
  padding: 30px;
  background-color: #f8f9fa;
  display: block;
}

/* 初始提示样式 */
.init-tip {
  text-align: center;
  color: #999;
  font-size: 18px;
  margin-top: 50px;
}

/* 监考信息容器样式 */
.proctor-info-container {
  width: 100%;
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  margin-top: 0;
}

/* 新增：页面标题+返回按钮容器 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* 新增：返回教师首页按钮样式 */
.back-btn {
  padding: 8px 16px;
  background-color: #42b983;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.page-title {
  margin: 0;
  font-size: 18px;
  color: #333;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}

.search-area {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-input {
  width: 200px;
  padding: 8px 12px;
  border: 1px solid #dcdcdc;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
}

.search-input:focus {
  border-color: #42b983;
  box-shadow: 0 0 0 2px rgba(66, 185, 131, 0.1);
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-primary {
  background-color: #42b983;
  color: #fff;
}

.table-area {
  width: 100%;
  overflow-x: auto;
}

.proctor-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
}

.proctor-table th, .proctor-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.proctor-table th {
  background-color: #f5f5f5;
  font-weight: bold;
  color: #333;
}

.proctor-table td {
  color: #666;
}
</style>