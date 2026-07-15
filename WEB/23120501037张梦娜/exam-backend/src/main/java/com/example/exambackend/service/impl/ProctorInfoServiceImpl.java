package com.example.exambackend.service.impl;

import com.example.exambackend.entity.ExamSubject;
import com.example.exambackend.entity.ProctorInfo;
import com.example.exambackend.entity.Teacher;
import com.example.exambackend.service.ProctorInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ProctorInfoServiceImpl implements ProctorInfoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<ProctorInfo> getProctorInfoList(String keyword) {
        // 调整SQL语法顺序（先加查询条件，后分组），修复查询报错
        StringBuilder sql = new StringBuilder("SELECT " +
                "p.proctor_id AS proctorId, " +
                "p.subject_id AS subjectId, " +
                "p.subject_name AS subjectName, " +
                "p.teacher_id AS teacherId, " + // 保留原有单个教师ID，兼容旧逻辑
                // 关键：用GROUP_CONCAT拼接所有监考教师姓名，用“、”分隔
                "GROUP_CONCAT(t.teacher_name SEPARATOR '、') AS teacherName, " +
                "p.exam_time AS examTime, " +
                "p.exam_place AS examPlace " +
                "FROM proctor_info p " +
                // 关联监考-教师关联表
                "LEFT JOIN proctor_teacher_relation r ON p.proctor_id = r.proctor_id " +
                // 关联教师表，获取教师姓名
                "LEFT JOIN teacher t ON r.teacher_id = t.teacher_id " +
                "WHERE 1=1 "); // 先写WHERE条件，后续拼接关键词查询

        List<Object> params = new java.util.ArrayList<>();
        // 保留原有模糊查询逻辑，仅调整拼接位置（在分组之前）
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (p.subject_name LIKE CONCAT('%', ?, '%') OR t.teacher_name LIKE CONCAT('%', ?, '%') OR p.exam_place LIKE CONCAT('%', ?, '%')) ");
            params.add(keyword.trim());
            params.add(keyword.trim());
            params.add(keyword.trim());
        }

        // 最后添加GROUP BY，确保查询条件先生效，修复SQL语法错误
        sql.append(" GROUP BY p.proctor_id");

        return jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<>(ProctorInfo.class), params.toArray());
    }

    // 重写 addProctorInfo 方法（支持多教师，添加 @Transactional 事务） ======================
    @Override
    @Transactional // 事务保证：新增监考信息 + 绑定教师 要么同时成功，要么同时回滚
    public String addProctorInfo(ProctorInfo proctorInfo, List<Integer> teacherIds) {
        // 1. 基础非空校验（调整：校验教师ID列表，不再校验单个teacherId）
        if (proctorInfo.getSubjectId() == null) {
            return "考试科目不能为空";
        }
        if (teacherIds == null || teacherIds.isEmpty()) {
            return "请至少选择一名监考教师";
        }
        if (proctorInfo.getExamTime() == null || proctorInfo.getExamTime().trim().isEmpty()) {
            return "考试时间不能为空";
        }
        if (proctorInfo.getExamPlace() == null || proctorInfo.getExamPlace().trim().isEmpty()) {
            return "考试地点不能为空";
        }

        // 2. 校验科目是否存在
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM exam_subject WHERE subject_id=?", Integer.class, proctorInfo.getSubjectId());
        if (count == null || count == 0) {
            return "考试科目不存在";
        }

        // 3. 校验所有选择的教师是否存在
        for (Integer teacherId : teacherIds) {
            count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teacher WHERE teacher_id=?", Integer.class, teacherId);
            if (count == null || count == 0) {
                return "教师ID为" + teacherId + "的监考教师不存在";
            }
        }

        // 4. 获取科目名称（用于存入proctor_info表）
        ExamSubject subject = jdbcTemplate.queryForObject("SELECT subject_id AS subjectId, subject_name AS subjectName FROM exam_subject WHERE subject_id=?", new BeanPropertyRowMapper<>(ExamSubject.class), proctorInfo.getSubjectId());

        // 5. 新增监考信息（proctor_info表：暂时用第一个教师的信息填充，不影响核心功能，后续可优化）
        Teacher firstTeacher = jdbcTemplate.queryForObject("SELECT teacher_id AS teacherId, teacher_name AS teacherName FROM teacher WHERE teacher_id=?", new BeanPropertyRowMapper<>(Teacher.class), teacherIds.get(0));
        // 插入proctor_info表，获取自增的proctorId
        String insertProctorSql = "INSERT INTO proctor_info(subject_id, subject_name, teacher_id, teacher_name, exam_time, exam_place) VALUES(?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(insertProctorSql,
                proctorInfo.getSubjectId(),
                subject.getSubjectName(),
                firstTeacher.getTeacherId(),
                firstTeacher.getTeacherName(),
                proctorInfo.getExamTime().trim(),
                proctorInfo.getExamPlace().trim());

        // 6. 查询刚新增的监考信息ID（proctor_id）
        Integer proctorId = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);

        // 7. 批量插入关联表（proctor_teacher_relation），实现多教师绑定
        String insertRelationSql = "INSERT INTO proctor_teacher_relation(proctor_id, teacher_id) VALUES(?, ?)";
        for (Integer teacherId : teacherIds) {
            jdbcTemplate.update(insertRelationSql, proctorId, teacherId);
        }

        return "新增监考信息（含" + teacherIds.size() + "名监考教师）成功";
    }

    // 重写 updateProctorInfo 方法（支持多教师，添加 @Transactional 事务） ======================
    @Override
    @Transactional // 事务保证：修改监考信息 + 重新绑定教师 要么同时成功，要么同时回滚
    public String updateProctorInfo(ProctorInfo proctorInfo, List<Integer> teacherIds) {
        // 1. 基础非空校验
        if (proctorInfo.getProctorId() == null) {
            return "监考ID不能为空";
        }
        if (proctorInfo.getSubjectId() == null) {
            return "考试科目不能为空";
        }
        if (teacherIds == null || teacherIds.isEmpty()) {
            return "请至少选择一名监考教师";
        }
        if (proctorInfo.getExamTime() == null || proctorInfo.getExamTime().trim().isEmpty()) {
            return "考试时间不能为空";
        }
        if (proctorInfo.getExamPlace() == null || proctorInfo.getExamPlace().trim().isEmpty()) {
            return "考试地点不能为空";
        }

        // 2. 校验监考信息是否存在
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM proctor_info WHERE proctor_id=?", Integer.class, proctorInfo.getProctorId());
        if (count == null || count == 0) {
            return "监考信息不存在";
        }

        // 3. 校验科目是否存在
        count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM exam_subject WHERE subject_id=?", Integer.class, proctorInfo.getSubjectId());
        if (count == null || count == 0) {
            return "考试科目不存在";
        }

        // 4. 校验所有选择的教师是否存在
        for (Integer teacherId : teacherIds) {
            count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teacher WHERE teacher_id=?", Integer.class, teacherId);
            if (count == null || count == 0) {
                return "教师ID为" + teacherId + "的监考教师不存在";
            }
        }

        // 5. 获取科目名称和第一个教师信息（用于更新proctor_info表）
        ExamSubject subject = jdbcTemplate.queryForObject("SELECT subject_id AS subjectId, subject_name AS subjectName FROM exam_subject WHERE subject_id=?", new BeanPropertyRowMapper<>(ExamSubject.class), proctorInfo.getSubjectId());
        Teacher firstTeacher = jdbcTemplate.queryForObject("SELECT teacher_id AS teacherId, teacher_name AS teacherName FROM teacher WHERE teacher_id=?", new BeanPropertyRowMapper<>(Teacher.class), teacherIds.get(0));

        // 6. 更新proctor_info表的基础信息
        String updateProctorSql = "UPDATE proctor_info SET subject_id=?, subject_name=?, teacher_id=?, teacher_name=?, exam_time=?, exam_place=? WHERE proctor_id=?";
        jdbcTemplate.update(updateProctorSql,
                proctorInfo.getSubjectId(),
                subject.getSubjectName(),
                firstTeacher.getTeacherId(),
                firstTeacher.getTeacherName(),
                proctorInfo.getExamTime().trim(),
                proctorInfo.getExamPlace().trim(),
                proctorInfo.getProctorId());

        // 7. 先删除该监考信息原有的教师关联记录
        String deleteRelationSql = "DELETE FROM proctor_teacher_relation WHERE proctor_id=?";
        jdbcTemplate.update(deleteRelationSql, proctorInfo.getProctorId());

        // 8. 重新批量插入新的教师关联记录
        String insertRelationSql = "INSERT INTO proctor_teacher_relation(proctor_id, teacher_id) VALUES(?, ?)";
        for (Integer teacherId : teacherIds) {
            jdbcTemplate.update(insertRelationSql, proctorInfo.getProctorId(), teacherId);
        }

        return "编辑监考信息（含" + teacherIds.size() + "名监考教师）成功";
    }

    @Override
    public String deleteProctorInfo(Integer proctorId) {
        // 原有逻辑完全保留，不做任何修改（外键已配置ON DELETE CASCADE，删除监考信息时自动删除关联的教师记录）
        if (proctorId == null) {
            return "监考ID不能为空";
        }
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM proctor_info WHERE proctor_id=?", Integer.class, proctorId);
        if (count == null || count == 0) {
            return "监考信息不存在";
        }
        int rows = jdbcTemplate.update("DELETE FROM proctor_info WHERE proctor_id=?", proctorId);
        return rows > 0 ? "删除监考信息成功" : "删除监考信息失败";
    }

    @Override
    public String batchDeleteProctorInfos(List<Integer> proctorIds) {
        // 原有逻辑完全保留，不做任何修改（外键已配置ON DELETE CASCADE，批量删除时自动删除关联的教师记录）
        if (proctorIds == null || proctorIds.isEmpty()) {
            return "请选择要删除的监考信息";
        }
        try {
            StringBuilder sql = new StringBuilder("DELETE FROM proctor_info WHERE proctor_id IN (");
            for (int i = 0; i < proctorIds.size(); i++) {
                if (i > 0) sql.append(",");
                sql.append("?");
            }
            sql.append(")");
            jdbcTemplate.update(sql.toString(), proctorIds.toArray());
            return "批量删除成功，共删除" + proctorIds.size() + "条监考信息";
        } catch (Exception e) {
            e.printStackTrace();
            return "批量删除失败：" + e.getMessage();
        }
    }

    @Override
    public List<ExamSubject> getAllSubjects() {
        // 原有逻辑完全保留，不做任何修改
        return jdbcTemplate.query("SELECT subject_id AS subjectId, subject_name AS subjectName FROM exam_subject", new BeanPropertyRowMapper<>(ExamSubject.class));
    }

    @Override
    public List<Teacher> getAllTeachers() {
        // 原有逻辑完全保留，不做任何修改
        return jdbcTemplate.query("SELECT teacher_id AS teacherId, teacher_name AS teacherName FROM teacher", new BeanPropertyRowMapper<>(Teacher.class));
    }

    @Override
    public List<Map<String, Object>> getProctorByTeacherNo(String teacherNo) {
        // 关键优化：关联查询，支持多教师监考场景下，教师查询自己的监考信息
        String sql = "SELECT " +
                "p.subject_name AS subjectName, " +
                "t.teacher_name AS teacherName, " +
                "p.exam_time AS examTime, " +
                "p.exam_place AS examPlace " +
                "FROM proctor_info p " +
                "JOIN proctor_teacher_relation r ON p.proctor_id = r.proctor_id " +
                "JOIN teacher t ON r.teacher_id = t.teacher_id " +
                "WHERE t.teacher_name = ?"; // 匹配教师姓名（与原有逻辑一致，若需用工号可调整为t.teacher_no = ?）
        return jdbcTemplate.queryForList(sql, teacherNo);
    }

    // 实现新增的 getTeacherIdsByProctorId 方法（用于编辑页面回显已选教师） ======================
    @Override
    public List<Integer> getTeacherIdsByProctorId(Integer proctorId) {
        if (proctorId == null) {
            return new java.util.ArrayList<>();
        }
        String sql = "SELECT teacher_id FROM proctor_teacher_relation WHERE proctor_id=?";
        // 查询该监考信息绑定的所有教师ID
        return jdbcTemplate.queryForList(sql, new Object[]{proctorId}, Integer.class);
    }
}