package com.example.exambackend.controller;

import com.example.exambackend.entity.ExamSubject;
import com.example.exambackend.entity.ProctorInfo;
import com.example.exambackend.entity.Teacher;
import com.example.exambackend.service.ProctorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/proctorInfo")
public class ProctorInfoController {

    @Autowired
    private ProctorInfoService proctorInfoService;

    @GetMapping("/list")
    public Map<String, Object> getProctorInfoList(@RequestParam(required = false) String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ProctorInfo> list = proctorInfoService.getProctorInfoList(keyword);
            result.put("code", 200);
            result.put("message", "获取监考信息列表成功");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    // 新增接收教师ID列表的DTO
    static class ProctorAddRequest {
        private ProctorInfo proctorInfo; // 原有监考信息
        private List<Integer> teacherIds; // 新增的教师ID列表

        public ProctorInfo getProctorInfo() {
            return proctorInfo;
        }

        public void setProctorInfo(ProctorInfo proctorInfo) {
            this.proctorInfo = proctorInfo;
        }

        public List<Integer> getTeacherIds() {
            return teacherIds;
        }

        public void setTeacherIds(List<Integer> teacherIds) {
            this.teacherIds = teacherIds;
        }
    }

    @PostMapping("/add")
    public Map<String, Object> addProctorInfo(@RequestBody ProctorAddRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用service的多教师新增方法
            String msg = proctorInfoService.addProctorInfo(request.getProctorInfo(), request.getTeacherIds());
            result.put("code", 200);
            result.put("message", msg);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "新增失败：" + e.getMessage());
        }
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> updateProctorInfo(@RequestBody ProctorAddRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用service的多教师编辑方法
            String msg = proctorInfoService.updateProctorInfo(request.getProctorInfo(), request.getTeacherIds());
            result.put("code", 200);
            result.put("message", msg);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "编辑失败：" + e.getMessage());
        }
        return result;
    }

    // 新增接口（查询指定监考信息绑定的教师ID列表，用于前端编辑回显） ======================
    @GetMapping("/getTeacherIdsByProctorId")
    public Map<String, Object> getTeacherIdsByProctorId(@RequestParam Integer proctorId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Integer> teacherIds = proctorInfoService.getTeacherIdsByProctorId(proctorId);
            result.put("code", 200);
            result.put("message", "获取监考绑定教师ID列表成功");
            result.put("data", teacherIds);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    @DeleteMapping("/delete")
    public Map<String, Object> deleteProctorInfo(@RequestParam Integer proctorId) {
        Map<String, Object> result = new HashMap<>();
        try {
            String msg = proctorInfoService.deleteProctorInfo(proctorId);
            result.put("code", 200);
            result.put("message", msg);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败：" + e.getMessage());
        }
        return result;
    }

    @PostMapping("/batchDelete")
    public Map<String, Object> batchDeleteProctorInfos(@RequestBody List<Integer> proctorIds) {
        Map<String, Object> result = new HashMap<>();
        try {
            String msg = proctorInfoService.batchDeleteProctorInfos(proctorIds);
            result.put("code", 200);
            result.put("message", msg);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "批量删除失败：" + e.getMessage());
        }
        return result;
    }

    @GetMapping("/getAllSubjects")
    public Map<String, Object> getAllSubjects() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ExamSubject> list = proctorInfoService.getAllSubjects();
            result.put("code", 200);
            result.put("message", "获取科目列表成功");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    @GetMapping("/getAllTeachers")
    public Map<String, Object> getAllTeachers() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Teacher> list = proctorInfoService.getAllTeachers();
            result.put("code", 200);
            result.put("message", "获取教师列表成功");
            result.put("data", list);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取失败：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }

    @GetMapping("/teacher")
    public Map<String, Object> getProctorByTeacherNo(@RequestParam String teacherNo) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 调用修改后的service方法，返回字段名与前端匹配的结果
            List<Map<String, Object>> proctorList = proctorInfoService.getProctorByTeacherNo(teacherNo);
            result.put("code", 200);
            result.put("message", "获取个人监考信息成功");
            result.put("data", proctorList);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取个人监考信息异常：" + e.getMessage());
            result.put("data", null);
        }
        return result;
    }
}