package com.example.exambackend.service.impl;

import com.example.exambackend.entity.Teacher;
import com.example.exambackend.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Teacher> getTeacherList(String keyword) {

        StringBuilder sql = new StringBuilder("SELECT * FROM teacher WHERE 1=1");
        List<Object> params = new java.util.ArrayList<>();
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (teacher_name LIKE CONCAT('%', ?, '%') OR teacher_no LIKE CONCAT('%', ?, '%'))");
            params.add(keyword.trim());
            params.add(keyword.trim());
        }
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(Teacher.class), params.toArray());
    }

    @Override
    public String addTeacher(Teacher teacher) {
        if (teacher.getTeacherName() == null || teacher.getTeacherName().trim().isEmpty()) {
            return "教师姓名不能为空";
        }
        if (teacher.getTeacherNo() == null || teacher.getTeacherNo().trim().isEmpty()) {
            return "教师工号不能为空";
        }
        // 新增：密码非空校验（确保每个教师都有独立密码）
        if (teacher.getPassword() == null || teacher.getPassword().trim().isEmpty()) {
            return "教师密码不能为空";
        }
        // 工号唯一性校验：表名改为 teacher，字段名改为 teacher_no
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teacher WHERE teacher_no=?", Integer.class, teacher.getTeacherNo().trim());
        if (count != null && count > 0) {
            return "教师工号已存在";
        }
        // 插入语句：补充 password 字段，适配前端传递的独立密码
        int rows = jdbcTemplate.update("INSERT INTO teacher(teacher_name, teacher_no, password, phone, college) VALUES(?, ?, ?, ?, ?)",
                teacher.getTeacherName().trim(),
                teacher.getTeacherNo().trim(),
                teacher.getPassword().trim(), // 存储前端传递的独立密码
                teacher.getPhone() == null ? "" : teacher.getPhone().trim(),
                teacher.getCollege() == null ? "" : teacher.getCollege().trim());
        return rows > 0 ? "新增教师成功" : "新增教师失败";
    }

    @Override
    public String updateTeacher(Teacher teacher) {
        if (teacher.getTeacherId() == null) {
            return "教师ID不能为空";
        }
        if (teacher.getTeacherName() == null || teacher.getTeacherName().trim().isEmpty()) {
            return "教师姓名不能为空";
        }
        if (teacher.getTeacherNo() == null || teacher.getTeacherNo().trim().isEmpty()) {
            return "教师工号不能为空";
        }
        // 新增：密码非空校验（编辑时也需确保密码不为空）
        if (teacher.getPassword() == null || teacher.getPassword().trim().isEmpty()) {
            return "教师密码不能为空";
        }
        // 存在性校验：表名改为 teacher，字段名改为 teacher_id
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teacher WHERE teacher_id=?", Integer.class, teacher.getTeacherId());
        if (count == null || count == 0) {
            return "教师不存在";
        }
        // 工号唯一性校验（排除当前教师）：表名改为 teacher，字段名改为 teacher_no/teacher_id
        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teacher WHERE teacher_no=? AND teacher_id != ?", Integer.class, teacher.getTeacherNo().trim(), teacher.getTeacherId());
        if (count != null && count > 0) {
            return "教师工号已存在";
        }
        // 更新语句：补充 password 字段，支持修改教师独立密码
        int rows = jdbcTemplate.update("UPDATE teacher SET teacher_name=?, teacher_no=?, password=?, phone=?, college=? WHERE teacher_id=?",
                teacher.getTeacherName().trim(),
                teacher.getTeacherNo().trim(),
                teacher.getPassword().trim(), // 更新教师独立密码
                teacher.getPhone() == null ? "" : teacher.getPhone().trim(),
                teacher.getCollege() == null ? "" : teacher.getCollege().trim(),
                teacher.getTeacherId());
        return rows > 0 ? "编辑教师成功" : "编辑教师失败";
    }

    @Override
    public String deleteTeacher(Integer teacherId) {
        if (teacherId == null) {
            return "教师ID不能为空";
        }
        // 教师存在性校验：表名改为 teacher，字段名改为 teacher_id
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teacher WHERE teacher_id=?", Integer.class, teacherId);
        if (count == null || count == 0) {
            return "教师不存在";
        }
        // 关联校验：监考信息表名从 ProctorInfo 改为 proctor_info，字段名从 teacherId 改为 teacher_id
        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM proctor_info WHERE teacher_id=?", Integer.class, teacherId);
        if (count != null && count > 0) {
            return "该教师存在监考任务，无法删除";
        }
        // 删除语句：表名改为 teacher，字段名改为 teacher_id
        int rows = jdbcTemplate.update("DELETE FROM teacher WHERE teacher_id=?", teacherId);
        return rows > 0 ? "删除教师成功" : "删除教师失败";
    }

    @Override
    public String batchDeleteTeachers(List<Integer> teacherIds) {
        if (teacherIds == null || teacherIds.isEmpty()) {
            return "请选择要删除的教师";
        }
        try {
            // 关联校验：监考信息表名改为 proctor_info，字段名改为 teacher_id
            StringBuilder checkSql = new StringBuilder("SELECT COUNT(*) FROM proctor_info WHERE teacher_id IN (");
            for (int i = 0; i < teacherIds.size(); i++) {
                if (i > 0) checkSql.append(",");
                checkSql.append("?");
            }
            checkSql.append(")");
            Integer count = jdbcTemplate.queryForObject(checkSql.toString(), Integer.class, teacherIds.toArray());
            if (count != null && count > 0) {
                return "部分教师存在监考任务，无法删除";
            }
            // 执行批量删除：教师表名改为 teacher，字段名改为 teacher_id
            StringBuilder sql = new StringBuilder("DELETE FROM teacher WHERE teacher_id IN (");
            for (int i = 0; i < teacherIds.size(); i++) {
                if (i > 0) sql.append(",");
                sql.append("?");
            }
            sql.append(")");
            jdbcTemplate.update(sql.toString(), teacherIds.toArray());
            return "批量删除成功，共删除" + teacherIds.size() + "名教师";
        } catch (Exception e) {
            e.printStackTrace();
            return "批量删除失败：" + e.getMessage();
        }
    }

    @Override
    public Teacher getTeacherByNo(String teacherNo) {
        String sql = "SELECT * FROM teacher WHERE teacher_no=?";
        List<Teacher> teacherList = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Teacher.class), teacherNo);
        return teacherList.isEmpty() ? null : teacherList.get(0);
    }
}