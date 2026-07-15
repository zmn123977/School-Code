package com.example.exambackend.controller;

import com.example.exambackend.entity.ExamSubject;
import com.example.exambackend.service.ExamSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/examSubject")
public class ExamSubjectController {

    @Autowired
    private ExamSubjectService examSubjectService;

    @GetMapping("/list")
    public Map<String, Object> getExamSubjectList(@RequestParam(required = false) String subjectName) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ExamSubject> list = examSubjectService.getExamSubjectList(subjectName);
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

    @PostMapping("/add")
    public Map<String, Object> addExamSubject(@RequestBody ExamSubject examSubject) {
        Map<String, Object> result = new HashMap<>();
        try {
            String msg = examSubjectService.addExamSubject(examSubject);
            result.put("code", 200);
            result.put("message", msg);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "新增失败：" + e.getMessage());
        }
        return result;
    }

    @PutMapping("/update")
    public Map<String, Object> updateExamSubject(@RequestBody ExamSubject examSubject) {
        Map<String, Object> result = new HashMap<>();
        try {
            String msg = examSubjectService.updateExamSubject(examSubject);
            result.put("code", 200);
            result.put("message", msg);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "编辑失败：" + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/delete")
    public Map<String, Object> deleteExamSubject(@RequestParam Integer subjectId) {
        Map<String, Object> result = new HashMap<>();
        try {
            String msg = examSubjectService.deleteExamSubject(subjectId);
            result.put("code", 200);
            result.put("message", msg);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败：" + e.getMessage());
        }
        return result;
    }

    @PostMapping("/batchDelete")
    public Map<String, Object> batchDelete(@RequestBody List<Integer> subjectIds) {
        Map<String, Object> result = new HashMap<>();
        try {
            String msg = examSubjectService.batchDeleteSubjects(subjectIds);
            result.put("code", 200);
            result.put("message", msg);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "批量删除失败：" + e.getMessage());
        }
        return result;
    }
}