package com.example.exambackend.service;

import com.example.exambackend.entity.Teacher;
import java.util.List;

public interface TeacherService {
    // 新增：实现根据工号查询教师的方法
    Teacher getTeacherByNo(String teacherNo);

    // 查询教师列表（支持按姓名/工号模糊查询）
    List<Teacher> getTeacherList(String keyword);
    // 新增教师
    String addTeacher(Teacher teacher);
    // 编辑教师
    String updateTeacher(Teacher teacher);
    // 单个删除教师
    String deleteTeacher(Integer teacherId);
    // 批量删除教师
    String batchDeleteTeachers(List<Integer> teacherIds);

}