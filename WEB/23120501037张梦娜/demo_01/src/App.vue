<template>
  <div class="app-wrapper">
    <div v-if="!isLogin" class="login-page">
      <h1 class="system-title">监考管理查询系统</h1>
      <div class="single-login-box">
        <div class="role-select">
          <button 
            class="role-btn" 
            :class="{ active: currentRole === 'admin' }"
            @click="switchRole('admin')"
          >
            管理员登录
          </button>
          <button 
            class="role-btn" 
            :class="{ active: currentRole === 'teacher' }"
            @click="switchRole('teacher')"
          >
            教师登录
          </button>
        </div>

        <div class="login-form">
          <!-- 账号输入框（用户名/教师工号） -->
          <div class="form-item">
            <label class="form-label">{{ currentRole === 'admin' ? '用户名：' : '教师工号：' }}</label>
            <input
              v-model="loginAccount"
              type="text"
              class="form-input"
              :placeholder="currentRole === 'admin' ? '请输入管理员账号' : '请输入教师工号'"
            />
          </div>

          <!-- 密码输入框 -->
          <div class="form-item">
            <label class="form-label">密 码：</label>
            <input
              v-model="loginPwd"
              type="password"
              class="form-input"
              placeholder="请输入登录密码"
            />
          </div>

          <!-- 登录按钮 -->
          <button class="login-btn" @click="handleLogin" :disabled="!loginAccount || !loginPwd">
            登录
          </button>
        </div>
      </div>
    </div>

    <router-view v-else />
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';

import request from './utils/request'; 

const router = useRouter();
const isLogin = ref(false);
const currentRole = ref('admin');

// 管理员/教师表单
const adminForm = ref({ username: '', password: '' });
const teacherForm = ref({ teacherNo: '', password: '' });

// 计算属性
const loginAccount = computed({
  get() {
    return currentRole.value === 'admin' ? adminForm.value.username : teacherForm.value.teacherNo;
  },
  set(val) {
    currentRole.value === 'admin' ? (adminForm.value.username = val) : (teacherForm.value.teacherNo = val);
  }
});
const loginPwd = computed({
  get() {
    return currentRole.value === 'admin' ? adminForm.value.password : teacherForm.value.password;
  },
  set(val) {
    currentRole.value === 'admin' ? (adminForm.value.password = val) : (teacherForm.value.password = val);
  }
});

// 切换角色
const switchRole = (role: 'admin' | 'teacher') => {
  currentRole.value = role;
  loginPwd.value = '';
};

// 登录逻辑
const handleLogin = async () => {
  if (!loginAccount.value.trim() || !loginPwd.value.trim()) {
    alert(`请填写完整的${currentRole.value === 'admin' ? '管理员账号和密码' : '教师工号和密码'}！`);
    return;
  }

  try {
    // 构造登录请求参数，传递角色、账号、密码
    const loginData = {
      role: currentRole.value,
      account: loginAccount.value.trim(),
      password: loginPwd.value.trim()
    };

    // 发送POST请求到后端登录接口
    const res = await request.post('/login', loginData);

    // 后端返回成功（code=200）
    if (res.data && res.data.code === 200) {
      alert(res.data.message);
      // 新增：教师登录成功后，缓存工号到localStorage，供TeacherHome.vue获取
      if (currentRole.value === 'teacher') {
        localStorage.setItem('teacherLoginInfo', JSON.stringify({
          account: loginAccount.value.trim() // 缓存教师工号
        }));
      }
      isLogin.value = true;
      // 按角色跳转对应页面
      router.push(currentRole.value === 'admin' ? '/admin/home' : '/teacher/home');
    } else {
      // 后端返回失败（账号密码错误等）
      alert(res.data?.message || '登录失败，请重试');
    }
  } catch (error) {
    // 网络错误或接口异常
    console.error('登录请求异常：', error);
    alert('登录异常，请检查后端服务是否启动');
  }
};

// 退出重置
router.afterEach(to => {
  if (to.path === '/' || to.path === '/login') {
    isLogin.value = false;
    currentRole.value = 'admin';
    adminForm.value = { username: '', password: '' };
    teacherForm.value = { teacherNo: '', password: '' };
    // 新增：退出时清除教师工号缓存
    localStorage.removeItem('teacherLoginInfo');
  }
});
</script>

<!-- 仅作用于登录页，通过 scoped 隔离，不污染其他页面 -->
<style scoped>
.app-wrapper {
  width: 100%;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 登录页样式：保留美观风格，且完全隔离 */
.login-page {
  width: 100%;
  min-height: 100vh;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  font-family: "微软雅黑", sans-serif;
  padding: 20px 0;
}

.system-title {
  color: #42b983;
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 40px;
  text-align: center;
}

.single-login-box {
  width: 350px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  padding: 30px 25px;
  box-sizing: border-box;
  text-align: center;
}

.role-select {
  display: flex;
  gap: 10px;
  margin-bottom: 25px;
}

.role-btn {
  flex: 1;
  height: 40px;
  background-color: #fafafa;
  color: #333;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}
.role-btn.active {
  background-color: #42b983;
  color: #ffffff;
  border-color: #42b983;
}
.role-btn:hover:not(.active) {
  background-color: #f0f0f0;
}

.login-form {
  width: 100%;
  text-align: left;
}

.form-item {
  width: 100%;
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  color: #666666;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  height: 40px;
  padding: 0 10px;
  border: 1px solid #dddddd;
  border-radius: 4px;
  box-sizing: border-box;
  font-size: 14px;
  outline: none;
}
.form-input:focus {
  border-color: #42b983;
  box-shadow: 0 0 0 2px rgba(66, 185, 131, 0.1);
}

.login-btn {
  width: 100%;
  height: 44px;
  background-color: #42b983;
  color: #ffffff;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s ease;
  margin-top: 10px;
}
.login-btn:disabled {
  background-color: #a5d6b9;
  cursor: not-allowed;
}
.login-btn:hover:not(:disabled) {
  background-color: #35a773;
}
</style>