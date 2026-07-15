package com.example.exambackend.controller;

import com.example.exambackend.entity.Teacher;
import com.example.exambackend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @GetMapping("/list")
    public Map<String, Object> getTeacherList(@RequestParam(required = false) String keyword) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Teacher> list = teacherService.getTeacherList(keyword);
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

    @PostMapping("/add")
    public Map<String, Object> addTeacher(@RequestBody Teacher teacher) {
        Map<String, Object> result = new HashMap<>();
        try {
            String msg = teacherService.addTeacher(teacher);
            result.put("code", 200);
            result.put("message", msg);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "新增失败：" + e.getMessage());
        }
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> updateTeacher(@RequestBody Teacher teacher) {
        Map<String, Object> result = new HashMap<>();
        try {
            String msg = teacherService.updateTeacher(teacher);
            result.put("code", 200);
            result.put("message", msg);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "编辑失败：" + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/delete")
    public Map<String, Object> deleteTeacher(@RequestParam Integer teacherId) {
        Map<String, Object> result = new HashMap<>();
        try {
            String msg = teacherService.deleteTeacher(teacherId);
            result.put("code", 200);
            result.put("message", msg);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败：" + e.getMessage());
        }
        return result;
    }

    @PostMapping("/batchDelete")
    public Map<String, Object> batchDeleteTeachers(@RequestBody List<Integer> teacherIds) {
        Map<String, Object> result = new HashMap<>();
        try {
            String msg = teacherService.batchDeleteTeachers(teacherIds);
            result.put("code", 200);
            result.put("message", msg);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "批量删除失败：" + e.getMessage());
        }
        return result;
    }
}