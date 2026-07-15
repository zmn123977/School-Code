import { createRouter, createWebHistory } from 'vue-router';
// 导入页面组件
import AdminHome from '../views/AdminHome.vue';
import AdminExamSubject from '../views/AdminExamSubject.vue';
import AdminTeacher from '../views/AdminTeacher.vue';
import AdminProctorInfo from '../views/AdminProctorInfo.vue';
// 新增：导入教师端页面
import TeacherHome from '../views/TeacherHome.vue';

const routes = [
  // 原有管理员路由
  {
    path: '/admin/home',
    name: 'AdminHome',
    component: AdminHome
  },
  {
    path: '/admin/examSubject',
    name: 'AdminExamSubject',
    component: AdminExamSubject
  },
  {
    path: '/admin/teacher',
    name: 'AdminTeacher',
    component: AdminTeacher
  },
  {
    path: '/admin/proctorInfo',
    name: 'AdminProctorInfo',
    component: AdminProctorInfo
  },
  // 新增：教师端路由
  {
    path: '/teacher/home',
    name: 'TeacherHome',
    component: TeacherHome // 教师首页，对应登录后跳转路径
  },
  {
    path: '/teacher/proctorInfo',
    name: 'TeacherProctorInfo',
    component: TeacherHome // 教师监考信息页面，复用你的TeacherHome.vue
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;