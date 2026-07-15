<template>
  <div class="admin-teacher">
    <div class="page-header">
      <h2>教师信息管理</h2>
      <button @click="goBackAdminHome" class="back-btn">返回管理员首页</button>
    </div>

    <!-- 查询区域 -->
    <div class="search-area card">
      <div class="search-item">
        <label>查询姓名/工号：</label>
        <el-input v-model="searchForm.keyword" placeholder="请输入姓名/工号" class="search-input"></el-input>
      </div>
      <button @click="handleSearch" class="btn btn-primary">查询</button>
    </div>

    <!-- 操作按钮：新增 + 批量删除 -->
    <div class="operate-area">
      <button @click="openAddDialog" class="btn btn-primary">新增教师</button>
      <button @click="handleBatchDelete" class="btn btn-danger">批量删除</button>
    </div>

    <!-- 教师列表（新增密码列） -->
    <div class="table-area">
      <table class="teacher-table">
        <thead>
          <tr>
            <th><input type="checkbox" @change="handleCheckAll" :checked="isAllChecked"></th>
            <th>教师编号</th>
            <th>教师姓名</th>
            <th>教师工号（登录账号）</th>
            <th>教师密码</th> <!-- 新增：密码列 -->
            <th>联系电话</th>
            <th>所属学院</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="teacherList.length === 0">
            <td colspan="8" style="padding:20px;color:#999;text-align:center">暂无教师数据</td> <!-- 列数从7改为8 -->
          </tr>
          <tr v-for="(teacher, index) in teacherList" :key="teacher.teacherId">
            <td><input type="checkbox" @change="handleCheckSingle(teacher.teacherId)" :checked="checkedTeacherIds.includes(teacher.teacherId)"></td>
            <td>{{ index + 1 }}</td>
            <td>{{ teacher.teacherName }}</td>
            <td>{{ teacher.teacherNo }}</td>
            <td>{{ teacher.password || '无' }}</td> <!-- 新增：显示教师密码 -->
            <td>{{ teacher.phone || '无' }}</td>
            <td>{{ teacher.college || '无' }}</td>
            <td>
              <button @click="openEditDialog(teacher)" class="btn btn-blue" style="margin-right:5px;padding:4px 8px;font-size:12px;">编辑</button>
              <button @click="handleDelete(teacher.teacherId)" class="btn btn-danger" style="padding:4px 8px;font-size:12px;">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 新增/编辑弹窗（保留密码输入项） -->
    <el-dialog v-model="dialogVisible" title="新增/编辑教师" width="400px" close-on-click-modal="false">
      <el-form :model="teacherForm" label-width="80px">
        <el-form-item label="教师姓名" prop="teacherName">
          <el-input v-model="teacherForm.teacherName" placeholder="请输入教师姓名"></el-input>
        </el-form-item>
        <el-form-item label="教师工号" prop="teacherNo">
          <el-input v-model="teacherForm.teacherNo" placeholder="请输入教师工号"></el-input>
        </el-form-item>
        <el-form-item label="教师密码" prop="password">
          <el-input v-model="teacherForm.password" type="password" placeholder="请设置教师登录密码"></el-input>
        </el-form-item>
        <el-form-item label="联系电话">
          <el-input v-model="teacherForm.phone" placeholder="请输入联系电话"></el-input>
        </el-form-item>
        <el-form-item label="所属学院">
          <el-input v-model="teacherForm.college" placeholder="请输入所属学院"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTeacher">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import request from '../utils/request';
import { ref, onMounted, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';

// 保留password字段
interface Teacher {
  teacherId: number;
  teacherName: string;
  teacherNo: string;
  password: string; // 密码字段
  phone: string;
  college: string;
}

const router = useRouter();
const teacherList = ref<Teacher[]>([]);

// 返回首页
const goBackAdminHome = () => {
  router.push('/admin/home');
};

// 查询表单
const searchForm = reactive({
  keyword: ''
});

// 弹窗相关
const dialogVisible = ref(false);
const isEdit = ref(false);
const teacherForm = ref<Partial<Teacher>>({
  teacherId: 0,
  teacherName: '',
  teacherNo: '',
  password: '',
  phone: '',
  college: ''
});

// 批量删除相关
const checkedTeacherIds = ref<number[]>([]);
const isAllChecked = computed(() => {
  return teacherList.value.length > 0 && teacherList.value.every(item => checkedTeacherIds.value.includes(item.teacherId));
});

// 获取教师列表（完全保留原有逻辑）
const getTeacherList = async () => {
  try {
    const res = await request.get('/teacher/list', {
      params: {
        keyword: searchForm.keyword
      }
    });
    if (res.data && res.data.code === 200) {
      // 先获取后端返回的原始数据
      const originalList = res.data.data || [];
      // 如果有查询关键词，进行本地精准过滤；无关键词则直接返回原始列表
      if (searchForm.keyword.trim()) {
        const keyword = searchForm.keyword.trim();
        // 精准过滤：仅匹配 教师姓名完全一致 或 教师工号完全一致
        teacherList.value = originalList.filter((teacher: { teacherName: string; teacherNo: string; }) => {
          return teacher.teacherName === keyword || teacher.teacherNo === keyword;
        });
      } else {
        teacherList.value = originalList;
      }
    } else {
      alert(res.data?.message || '获取列表失败');
    }
  } catch (error) {
    console.error('获取列表异常：', error);
    alert('获取数据失败，请检查后端是否启动');
  }
};

onMounted(() => {
  getTeacherList();
});

const handleSearch = () => {
  getTeacherList();
};

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false;
  teacherForm.value = {
    teacherId: 0,
    teacherName: '',
    teacherNo: '',
    password: '',
    phone: '',
    college: ''
  };
  dialogVisible.value = true;
};

// 打开编辑弹窗
const openEditDialog = (teacher: Teacher) => {
  isEdit.value = true;
  teacherForm.value = { ...teacher };
  dialogVisible.value = true;
};

// 提交表单（完全保留原有逻辑）
const submitTeacher = async () => {
  if (!teacherForm.value.teacherName?.trim()) {
    alert('教师姓名不能为空！');
    return;
  }
  if (!teacherForm.value.teacherNo?.trim()) {
    alert('教师工号不能为空！');
    return;
  }
  if (!teacherForm.value.password?.trim()) {
    alert('教师密码不能为空！');
    return;
  }

  const params = {
    teacherId: teacherForm.value.teacherId || 0,
    teacherName: teacherForm.value.teacherName.trim(),
    teacherNo: teacherForm.value.teacherNo.trim(),
    password: teacherForm.value.password.trim(),
    phone: teacherForm.value.phone?.trim() || '',
    college: teacherForm.value.college?.trim() || ''
  };

  try {
    let res;
    if (isEdit.value) {
      res = await request.put('/teacher/update', params);
    } else {
      res = await request.post('/teacher/add', params);
    }

    if (res.data && res.data.code === 200) {
      alert(res.data.message || '操作成功');
      dialogVisible.value = false;
      getTeacherList(); // 刷新列表，显示最新密码
    } else {
      alert(res.data?.message || '操作失败');
    }
  } catch (error) {
    console.error('提交异常：', error);
    alert('提交失败，请检查网络');
  }
};

// 单个删除
const handleDelete = async (teacherId: number) => {
  if (!confirm('确定要删除该教师吗？删除后无法恢复')) {
    return;
  }

  try {
    const res = await request.delete('/teacher/delete', {
      params: {
        teacherId
      }
    });

    if (res.data && res.data.code === 200) {
      alert(res.data.message || '删除成功');
      getTeacherList();
    } else {
      alert(res.data?.message || '删除失败');
    }
  } catch (error) {
    console.error('删除异常：', error);
    alert('删除失败，请检查网络');
  }
};

// 全选/取消全选
const handleCheckAll = (e: Event) => {
  const isChecked = (e.target as HTMLInputElement).checked;
  if (isChecked) {
    checkedTeacherIds.value = teacherList.value.map(item => item.teacherId);
  } else {
    checkedTeacherIds.value = [];
  }
};

// 单个勾选
const handleCheckSingle = (teacherId: number) => {
  const index = checkedTeacherIds.value.findIndex(id => id === teacherId);
  if (index > -1) {
    checkedTeacherIds.value.splice(index, 1);
  } else {
    checkedTeacherIds.value.push(teacherId);
  }
};

// 批量删除
const handleBatchDelete = async () => {
  if (checkedTeacherIds.value.length === 0) {
    alert('请先勾选要删除的教师！');
    return;
  }

  if (!confirm('确定要批量删除勾选的教师吗？删除后无法恢复')) {
    return;
  }

  try {
    const res = await request.post('/teacher/batchDelete', checkedTeacherIds.value);
    if (res.data && res.data.code === 200) {
      alert(res.data.message || '批量删除成功');
      checkedTeacherIds.value = [];
      getTeacherList();
    } else {
      alert(res.data?.message || '批量删除失败');
    }
  } catch (error) {
    console.error('批量删除异常：', error);
    alert('批量删除失败，请检查网络');
  }
};
</script>

<style scoped>
.admin-teacher {
  padding: 20px 30px;
  background-color: #fafafa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.back-btn {
  padding: 8px 16px;
  background-color: #42b983;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.search-area {
  background: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  margin-bottom: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.search-input {
  width: 200px;
  padding: 8px 12px;
  border: 1px solid #dcdcdc;
  border-radius: 4px;
  font-size: 14px;
}

.operate-area {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}

.btn {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: opacity 0.2s;
}

.btn-primary {
  background-color: #42b983;
  color: #fff;
}

.btn-blue {
  background-color: #1890ff;
  color: #fff;
}

.btn-danger {
  background-color: #ff4d4f;
  color: #fff;
}

.table-area {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
  overflow: hidden;
}

.teacher-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
}

.teacher-table th,
.teacher-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.teacher-table th {
  background-color: #f5f5f5;
  font-weight: bold;
  color: #333;
}

.teacher-table td {
  color: #666;
}
</style>