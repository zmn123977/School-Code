package com.example.exambackend.service;

import com.example.exambackend.entity.ProctorInfo;
import com.example.exambackend.entity.Teacher;

import java.util.List;
import java.util.Map;

public interface ProctorInfoService {
    // 查询监考列表（支持按科目/教师/地点模糊查询）
    List<ProctorInfo> getProctorInfoList(String keyword);

    String addProctorInfo(ProctorInfo proctorInfo, List<Integer> teacherIds);

    // 新增 teacherIds 参数，支持多教师绑定 ==========
    String updateProctorInfo(ProctorInfo proctorInfo, List<Integer> teacherIds);

    // 单个删除监考信息
    String deleteProctorInfo(Integer proctorId);
    // 批量删除监考信息
    String batchDeleteProctorInfos(List<Integer> proctorIds);
    // 查询所有科目（用于新增/编辑时的下拉选择）
    List<com.example.exambackend.entity.ExamSubject> getAllSubjects();
    // 查询所有教师（用于新增/编辑时的下拉选择）
    List<Teacher> getAllTeachers();

    List<Map<String, Object>> getProctorByTeacherNo(String teacherNo);

    // 查询某监考信息绑定的所有教师ID列表（用于编辑回显） ==========
    List<Integer> getTeacherIdsByProctorId(Integer proctorId);
}