<template>
  <div class="admin-exam-subject">
    <div class="page-header">
      <h2>考试科目管理</h2>
      <button @click="goBackAdminHome" class="back-btn">返回管理员首页</button>
    </div>

    <!-- 查询区域 -->
    <div class="search-area card">
      <div class="search-item">
        <label>科目名称：</label>
        <el-input v-model="searchForm.subjectName" placeholder="请输入科目名称" class="search-input"></el-input>
      </div>
      <button @click="handleSearch" class="btn btn-primary">查询</button>
    </div>

    <!-- 操作按钮：新增 + 批量删除 -->
    <div class="operate-area">
      <button @click="openAddDialog" class="btn btn-primary">新增科目</button>
      <button @click="handleBatchDelete" class="btn btn-danger">批量删除</button>
    </div>

    <!-- 科目列表：全选 + 单选 + 编辑 + 单个删除 -->
    <div class="table-area">
      <table class="subject-table">
        <thead>
          <tr>
            <th><input type="checkbox" @change="handleCheckAll" :checked="isAllChecked"></th>
            <th>科目编号</th> <!-- 表头文字优化，更贴合视觉编号 -->
            <th>科目名称</th>
            <th>考试类型</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="subjectList.length === 0">
            <td colspan="5" style="padding:20px;color:#999;text-align:center">暂无科目数据</td>
          </tr>
          <!-- 添加index参数，用于视觉连续编号 -->
          <tr v-for="(subject, index) in subjectList" :key="subject.subjectId">
            <td><input type="checkbox" @change="handleCheckSingle(subject.subjectId)" :checked="checkedSubjectIds.includes(subject.subjectId)"></td>
            <td>{{ index + 1 }}</td> <!-- 用index+1显示连续视觉编号，替代原始subject.subjectId -->
            <td>{{ subject.subjectName }}</td>
            <td>{{ subject.examType || '无' }}</td>
            <td>
              <button @click="openEditDialog(subject)" class="btn btn-blue" style="margin-right:5px;padding:4px 8px;font-size:12px;">编辑</button>
              <button @click="handleDelete(subject.subjectId)" class="btn btn-danger" style="padding:4px 8px;font-size:12px;">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增/编辑科目" width="400px" close-on-click-modal="false">
      <el-form :model="subjectForm" label-width="80px">
        <el-form-item label="科目名称">
          <el-input v-model="subjectForm.subjectName" placeholder="请输入科目名称"></el-input>
        </el-form-item>
        <el-form-item label="考试类型">
          <el-input v-model="subjectForm.examType" placeholder="请输入考试类型"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSubject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
// 相对路径导入，确保能找到request.ts
import request, { type ApiResult } from '../utils/request';
import { ref, onMounted, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';

// 科目类型定义
interface ExamSubject {
  subjectId: number;
  subjectName: string;
  examType: string;
}

const router = useRouter();

// 科目列表
const subjectList = ref<ExamSubject[]>([]);

// 返回首页
const goBackAdminHome = () => {
  router.push('/admin/home');
};

// 查询表单
const searchForm = reactive({
  subjectName: ''
});

// 弹窗相关
const dialogVisible = ref(false);
const isEdit = ref(false);
const subjectForm = ref<Partial<ExamSubject>>({
  subjectId: 0,
  subjectName: '',
  examType: ''
});

// 批量删除相关
const checkedSubjectIds = ref<number[]>([]);
const isAllChecked = computed(() => {
  return subjectList.value.length > 0 && subjectList.value.every(item => checkedSubjectIds.value.includes(item.subjectId));
});

// 获取科目列表（核心查询功能）
// 获取科目列表（新增前端本地精准过滤，解决模糊匹配包含问题）
const getExamSubjectList = async () => {
  try {
    const res = await request.get('/examSubject/list', {
      params: {
        subjectName: searchForm.subjectName
      }
    });
    if (res.data && res.data.code === 200) {
      // 先获取后端返回的原始数据
      const originalList = res.data.data || [];
      // 如果有查询关键词，进行本地精准过滤；无关键词则直接返回原始列表
      if (searchForm.subjectName.trim()) {
        const targetSubjectName = searchForm.subjectName.trim();
        // 精准过滤：仅匹配 科目名称完全一致 的条目
        subjectList.value = originalList.filter((subject: { subjectName: string; }) => {
          return subject.subjectName === targetSubjectName;
        });
      } else {
        subjectList.value = originalList;
      }
    } else {
      alert(res.data?.message || '获取列表失败');
    }
  } catch (error) {
    console.error('获取列表异常：', error);
    alert('获取数据失败，请检查后端是否启动（端口8080）');
  }
};

// 页面加载初始化
onMounted(() => {
  getExamSubjectList();
});

// 点击查询
const handleSearch = () => {
  getExamSubjectList();
};

// 打开新增弹窗
const openAddDialog = () => {
  isEdit.value = false;
  subjectForm.value = {
    subjectId: 0,
    subjectName: '',
    examType: ''
  };
  dialogVisible.value = true;
};

// 打开编辑弹窗
const openEditDialog = (subject: ExamSubject) => {
  isEdit.value = true;
  subjectForm.value = { ...subject };
  dialogVisible.value = true;
};

// 提交表单（新增+编辑通用）
const submitSubject = async () => {
  if (!subjectForm.value.subjectName?.trim()) {
    alert('科目名称不能为空！');
    return;
  }

  const params = {
    subjectId: subjectForm.value.subjectId || 0,
    subjectName: subjectForm.value.subjectName.trim(),
    examType: subjectForm.value.examType?.trim() || ''
  };

  try {
    let res;
    if (isEdit.value) {
      // 编辑请求
      res = await request.put('/examSubject/update', params);
    } else {
      // 新增请求
      res = await request.post('/examSubject/add', params);
    }

    if (res.data && res.data.code === 200) {
      alert(res.data.message || '操作成功');
      dialogVisible.value = false;
      getExamSubjectList(); // 刷新列表
    } else {
      alert(res.data?.message || '操作失败');
    }
  } catch (error) {
    console.error('提交异常：', error);
    alert('提交失败，请检查网络');
  }
};

// 单个删除
const handleDelete = async (subjectId: number) => {
  if (!confirm('确定要删除该科目吗？删除后无法恢复')) {
    return;
  }

  try {
    const res = await request.delete('/examSubject/delete', {
      params: {
        subjectId
      }
    });

    if (res.data && res.data.code === 200) {
      alert(res.data.message || '删除成功');
      getExamSubjectList(); // 刷新列表
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
    checkedSubjectIds.value = subjectList.value.map(item => item.subjectId);
  } else {
    checkedSubjectIds.value = [];
  }
};

// 单个勾选/取消勾选
const handleCheckSingle = (subjectId: number) => {
  const index = checkedSubjectIds.value.findIndex(id => id === subjectId);
  if (index > -1) {
    // 取消勾选
    checkedSubjectIds.value.splice(index, 1);
  } else {
    // 勾选
    checkedSubjectIds.value.push(subjectId);
  }
};

// 批量删除
const handleBatchDelete = async () => {
  if (checkedSubjectIds.value.length === 0) {
    alert('请先勾选要删除的科目！');
    return;
  }

  if (!confirm('确定要批量删除勾选的科目吗？删除后无法恢复')) {
    return;
  }

  try {
    const res = await request.post('/examSubject/batchDelete', checkedSubjectIds.value);
    if (res.data && res.data.code === 200) {
      alert(res.data.message || '批量删除成功');
      checkedSubjectIds.value = []; // 清空勾选
      getExamSubjectList(); // 刷新列表
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
.admin-exam-subject {
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
  width: 180px;
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

.subject-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
}

.subject-table th,
.subject-table td {
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
}

.subject-table th {
  background-color: #f5f5f5;
  font-weight: bold;
  color: #333;
}

.subject-table td {
  color: #666;
}
</style>