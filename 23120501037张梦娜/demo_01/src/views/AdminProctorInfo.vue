<template>
  <div class="admin-proctor-info">
    <div class="page-header">
      <h2>监考任务管理</h2>
      <button @click="goBackAdminHome" class="back-btn">返回管理员首页</button>
    </div>

    <!-- 查询区域 -->
    <div class="search-area card">
      <div class="search-item">
        <label>查询考试科目：</label>
        <el-input v-model="searchForm.keyword" placeholder="请输入考试科目" class="search-input"></el-input>
      </div>
      <button @click="handleSearch" class="btn btn-primary">查询</button>
    </div>

    <!-- 操作按钮：新增 + 批量删除 -->
    <div class="operate-area">
      <button @click="openAddDialog" class="btn btn-primary">新增监考信息</button>
      <button @click="handleBatchDelete" class="btn btn-danger">批量删除</button>
    </div>

    <!-- 监考信息列表 -->
    <div class="table-area">
      <table class="proctor-table">
        <thead>
          <tr>
            <th><input type="checkbox" @change="handleCheckAll" :checked="isAllChecked"></th>
            <th>监考编号</th>
            <th>考试科目</th>
            <th>监考教师</th>
            <th>考试时间</th>
            <th>考试地点</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="proctorInfoList.length === 0">
            <td colspan="7" style="padding:20px;color:#999;text-align:center">暂无监考信息</td>
          </tr>
          <tr v-for="(info, index) in proctorInfoList" :key="info.proctorId">
            <td><input type="checkbox" @change="handleCheckSingle(info.proctorId)" :checked="checkedProctorIds.includes(info.proctorId)"></td>
            <td>{{ index + 1 }}</td>
            <td>{{ info.subjectName }}</td>
            <td>{{ info.teacherName }}</td>
            <td>{{ info.examTime }}</td>
            <td>{{ info.examPlace }}</td>
            <td>
              <button @click="openEditDialog(info)" class="btn btn-blue" style="margin-right:5px;padding:4px 8px;font-size:12px;">编辑</button>
              <button @click="handleDelete(info.proctorId)" class="btn btn-danger" style="padding:4px 8px;font-size:12px;">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增/编辑监考信息" width="450px" close-on-click-modal="false">
      <el-form :model="proctorForm" label-width="80px">
        <el-form-item label="考试科目" prop="subjectId">
          <el-select v-model="proctorForm.subjectId" placeholder="请选择考试科目">
            <el-option v-for="subject in subjectList" :key="subject.subjectId" :label="subject.subjectName" :value="subject.subjectId"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="监考教师" prop="teacherIds">
          <el-select
            v-model="teacherIds"
            placeholder="请选择监考教师（可多选）"
            multiple
            clearable
          >
            <el-option v-for="teacher in teacherList" :key="teacher.teacherId" :label="teacher.teacherName" :value="teacher.teacherId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="考试时间" prop="examTime">
          <el-input v-model="proctorForm.examTime" placeholder="请输入考试时间"></el-input>
        </el-form-item>
        <el-form-item label="考试地点" prop="examPlace">
          <el-input v-model="proctorForm.examPlace" placeholder="请输入考试地点"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitProctorInfo">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import request from '../utils/request';
import { ref, onMounted, reactive, computed } from 'vue';
import { useRouter } from 'vue-router';

interface ExamSubject {
  subjectId: number;
  subjectName: string;
}

interface Teacher {
  teacherId: number;
  teacherName: string;
}

interface ProctorInfo {
  proctorId: number;
  subjectId: number;
  subjectName: string;
  teacherId: number;
  teacherName: string;
  examTime: string;
  examPlace: string;
}

// 定义后端需要的请求体类型（对应ProctorAddRequest）
interface ProctorRequest {
  proctorInfo: Partial<ProctorInfo>;
  teacherIds: number[];
}

const router = useRouter();
const proctorInfoList = ref<ProctorInfo[]>([]);
const subjectList = ref<ExamSubject[]>([]);
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
const proctorForm = ref<Partial<ProctorInfo>>({
  proctorId: 0,
  subjectId: 0,
  teacherId: 0, // 保留原有字段，兼容后端逻辑
  examTime: '',
  examPlace: ''
});

// 新增多选教师ID列表（绑定弹窗多选框）
const teacherIds = ref<number[]>([]);

// 批量删除相关
const checkedProctorIds = ref<number[]>([]);
const isAllChecked = computed(() => {
  return proctorInfoList.value.length > 0 && proctorInfoList.value.every(item => checkedProctorIds.value.includes(item.proctorId));
});

// 获取基础数据（科目+教师）
const getBaseData = async () => {
  try {
    const subjectRes = await request.get('/proctorInfo/getAllSubjects');
    if (subjectRes.data && subjectRes.data.code === 200) {
      subjectList.value = subjectRes.data.data || [];
    }
    const teacherRes = await request.get('/proctorInfo/getAllTeachers');
    if (teacherRes.data && teacherRes.data.code === 200) {
      teacherList.value = teacherRes.data.data || [];
    }
  } catch (error) {
    console.error('获取基础数据异常：', error);
    alert('获取科目/教师列表失败');
  }
};

// 获取监考信息列表
const getProctorInfoList = async () => {
  try {
    const res = await request.get('/proctorInfo/list', {
      params: {
        keyword: searchForm.keyword
      }
    });
    if (res.data && res.data.code === 200) {
      // 先获取后端原始数据
      const originalList = res.data.data || [];
      // 有查询关键词则精准过滤，无关键词则返回原始列表
      if (searchForm.keyword.trim()) {
        const keyword = searchForm.keyword.trim();
        // 精准过滤：科目名称 完全匹配关键词
        proctorInfoList.value = originalList.filter((info: { subjectName: string; }) => {
          return info.subjectName === keyword ;
        });
      } else {
        proctorInfoList.value = originalList;
      }
    } else {
      alert(res.data?.message || '获取列表失败');
    }
  } catch (error) {
    console.error('获取列表异常：', error);
    alert('获取数据失败，请检查后端是否启动');
  }
};

onMounted(async () => {
  await getBaseData();
  await getProctorInfoList();
});

const handleSearch = () => {
  getProctorInfoList();
};

//打开新增弹窗时，重置教师ID列表
const openAddDialog = () => {
  isEdit.value = false;
  proctorForm.value = {
    proctorId: 0,
    subjectId: 0,
    teacherId: 0,
    examTime: '',
    examPlace: ''
  };
  teacherIds.value = []; // 重置多选教师列表
  dialogVisible.value = true;
};

// 打开编辑弹窗时，查询并回显已选教师ID列表
const openEditDialog = async (info: ProctorInfo) => {
  isEdit.value = true;
  proctorForm.value = {
    proctorId: info.proctorId,
    subjectId: info.subjectId,
    teacherId: info.teacherId,
    examTime: info.examTime,
    examPlace: info.examPlace
  };
  // 查询该监考信息绑定的教师ID列表
  try {
    const res = await request.get('/proctorInfo/getTeacherIdsByProctorId', {
      params: {
        proctorId: info.proctorId
      }
    });
    if (res.data && res.data.code === 200) {
      teacherIds.value = res.data.data || []; // 回显多选教师
    }
  } catch (error) {
    console.error('获取绑定教师列表异常：', error);
    alert('获取已选教师失败');
  }
  dialogVisible.value = true;
};

// 提交表单（构造多教师请求体）
const submitProctorInfo = async () => {
  // 校验逻辑调整：校验教师ID列表是否为空
  if (proctorForm.value.subjectId === 0) {
    alert('考试科目不能为空！');
    return;
  }
  if (teacherIds.value.length === 0) { // 改为校验多选列表
    alert('监考教师不能为空（至少选择1名）！');
    return;
  }
  if (!proctorForm.value.examTime?.trim()) {
    alert('考试时间不能为空！');
    return;
  }
  if (!proctorForm.value.examPlace?.trim()) {
    alert('考试地点不能为空！');
    return;
  }

  // 构造后端需要的请求体
  const requestData: ProctorRequest = {
    proctorInfo: proctorForm.value,
    teacherIds: teacherIds.value
  };

  try {
    let res;
    if (isEdit.value) {
      res = await request.put('/proctorInfo/update', requestData);
    } else {
      res = await request.post('/proctorInfo/add', requestData);
    }

    if (res.data && res.data.code === 200) {
      alert(res.data.message || '操作成功');
      dialogVisible.value = false;
      teacherIds.value = []; // 重置教师列表
      getProctorInfoList();
    } else {
      alert(res.data?.message || '操作失败');
    }
  } catch (error) {
    console.error('提交异常：', error);
    alert('提交失败，请检查网络');
  }
};

// 单个删除
const handleDelete = async (proctorId: number) => {
  if (!confirm('确定要删除该监考信息吗？删除后无法恢复')) {
    return;
  }

  try {
    const res = await request.delete('/proctorInfo/delete', {
      params: {
        proctorId
      }
    });

    if (res.data && res.data.code === 200) {
      alert(res.data.message || '删除成功');
      getProctorInfoList();
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
    checkedProctorIds.value = proctorInfoList.value.map(item => item.proctorId);
  } else {
    checkedProctorIds.value = [];
  }
};

// 单个勾选
const handleCheckSingle = (proctorId: number) => {
  const index = checkedProctorIds.value.findIndex(id => id === proctorId);
  if (index > -1) {
    checkedProctorIds.value.splice(index, 1);
  } else {
    checkedProctorIds.value.push(proctorId);
  }
};

// 批量删除
const handleBatchDelete = async () => {
  if (checkedProctorIds.value.length === 0) {
    alert('请先勾选要删除的监考信息！');
    return;
  }

  if (!confirm('确定要批量删除勾选的监考信息吗？删除后无法恢复')) {
    return;
  }

  try {
    const res = await request.post('/proctorInfo/batchDelete', checkedProctorIds.value);
    if (res.data && res.data.code === 200) {
      alert(res.data.message || '批量删除成功');
      checkedProctorIds.value = [];
      getProctorInfoList();
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
.admin-proctor-info {
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

.proctor-table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
}

.proctor-table th,
.proctor-table td {
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