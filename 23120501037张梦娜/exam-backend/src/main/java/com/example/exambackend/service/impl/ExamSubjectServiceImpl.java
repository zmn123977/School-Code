package com.example.exambackend.service.impl;

import com.example.exambackend.entity.ExamSubject;
import com.example.exambackend.service.ExamSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamSubjectServiceImpl implements ExamSubjectService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<ExamSubject> getExamSubjectList(String subjectName) {
        StringBuilder sql = new StringBuilder("SELECT * FROM Exam_Subject WHERE 1=1");
        List<Object> params = new java.util.ArrayList<>();
        if (subjectName != null && !subjectName.trim().isEmpty()) {
            sql.append(" AND Subject_Name LIKE CONCAT('%', ?, '%')");
            params.add(subjectName.trim());
        }
        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(ExamSubject.class), params.toArray());
    }

    @Override
    public String addExamSubject(ExamSubject examSubject) {
        if (examSubject.getSubjectName() == null || examSubject.getSubjectName().trim().isEmpty()) {
            return "科目名称不能为空";
        }
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Exam_Subject WHERE Subject_Name=?", Integer.class, examSubject.getSubjectName().trim());
        if (count != null && count > 0) {
            return "科目名称已存在";
        }
        int rows = jdbcTemplate.update("INSERT INTO Exam_Subject(Subject_Name,Exam_Type) VALUES(?,?)",
                examSubject.getSubjectName().trim(), examSubject.getExamType() == null ? "" : examSubject.getExamType().trim());
        return rows > 0 ? "新增科目成功" : "新增科目失败";
    }

    @Override
    public String updateExamSubject(ExamSubject examSubject) {
        if (examSubject.getSubjectId() == null) {
            return "科目编号不能为空";
        }
        if (examSubject.getSubjectName() == null || examSubject.getSubjectName().trim().isEmpty()) {
            return "科目名称不能为空";
        }
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Exam_Subject WHERE Subject_ID=?", Integer.class, examSubject.getSubjectId());
        if (count == null || count == 0) {
            return "科目不存在";
        }
        int rows = jdbcTemplate.update("UPDATE Exam_Subject SET Subject_Name=?,Exam_Type=? WHERE Subject_ID=?",
                examSubject.getSubjectName().trim(), examSubject.getExamType() == null ? "" : examSubject.getExamType().trim(), examSubject.getSubjectId());
        return rows > 0 ? "编辑科目成功" : "编辑科目失败";
    }

    @Override
    public String deleteExamSubject(Integer subjectId) {
        if (subjectId == null) {
            return "科目编号不能为空";
        }
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Exam_Subject WHERE Subject_ID=?", Integer.class, subjectId);
        if (count == null || count == 0) {
            return "科目不存在";
        }
        int rows = jdbcTemplate.update("DELETE FROM Exam_Subject WHERE Subject_ID=?", subjectId);
        return rows > 0 ? "删除科目成功" : "删除科目失败";
    }

    @Override
    public String batchDeleteSubjects(List<Integer> subjectIds) {
        if (subjectIds == null || subjectIds.isEmpty()) {
            return "请选择要删除的科目";
        }
        try {
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < subjectIds.size(); i++) {
                if (i > 0) {
                    placeholders.append(",");
                }
                placeholders.append("?");
            }
            String sql = "DELETE FROM Exam_Subject WHERE Subject_ID IN (" + placeholders + ")";
            jdbcTemplate.update(sql, subjectIds.toArray());
            return "批量删除成功，共删除" + subjectIds.size() + "个科目";
        } catch (Exception e) {
            e.printStackTrace();
            return "批量删除失败：" + e.getMessage();
        }
    }
}