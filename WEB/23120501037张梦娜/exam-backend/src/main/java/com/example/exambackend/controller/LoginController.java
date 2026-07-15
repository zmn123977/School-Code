package com.example.exambackend.controller;

import com.example.exambackend.entity.Teacher;
import com.example.exambackend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

// 允许前端跨域请求
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/login")
public class LoginController {

    // 管理员固定账号密码
    private static final String ADMIN_ACCOUNT = "admin";
    private static final String ADMIN_PASSWORD = "123456";

    @Autowired
    private TeacherService teacherService;

    // 登录接口：接收前端传递的角色、账号、密码
    @PostMapping
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> result = new HashMap<>();
        try {
            String role = loginData.get("role");
            String account = loginData.get("account");
            String password = loginData.get("password");

            // 1. 管理员登录校验
            if ("admin".equals(role)) {
                if (ADMIN_ACCOUNT.equals(account) && ADMIN_PASSWORD.equals(password)) {
                    result.put("code", 200);
                    result.put("message", "管理员登录成功");
                    result.put("data", "admin");
                } else {
                    result.put("code", 401);
                    result.put("message", "管理员账号或密码错误");
                }
                return result;
            }

            // 2. 教师登录校验（根据工号查数据库，匹配密码）
            if ("teacher".equals(role)) {
                // 调用TeacherService的getTeacherByNo方法
                Teacher teacher = teacherService.getTeacherByNo(account);
                if (teacher == null) {
                    result.put("code", 404);
                    result.put("message", "教师工号不存在");
                } else if (password.equals(teacher.getPassword())) {
                    result.put("code", 200);
                    result.put("message", "教师登录成功");
                    result.put("data", "teacher");
                } else {
                    result.put("code", 401);
                    result.put("message", "教师密码错误");
                }
                return result;
            }

            // 无效角色
            result.put("code", 400);
            result.put("message", "无效的登录角色");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "登录异常：" + e.getMessage());
        }
        return result;
    }
}