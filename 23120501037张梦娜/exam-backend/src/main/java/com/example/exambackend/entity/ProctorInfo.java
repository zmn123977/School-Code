package com.example.exambackend.entity;

/**
 * 监考信息实体类（对应数据库ProctorInfo表）
 */
public class ProctorInfo {
    // 监考ID（主键自增）
    private Integer proctorId;
    // 考试科目ID（关联Exam_Subject表的subjectId）
    private Integer subjectId;
    // 考试科目名称（冗余字段，方便显示）
    private String subjectName;
    // 教师ID（关联Teacher表的teacherId）
    private Integer teacherId;
    // 教师姓名（冗余字段，方便显示）
    private String teacherName;
    // 考试时间
    private String examTime;
    // 考试地点
    private String examPlace;

    // 无参构造
    public ProctorInfo() {}

    // 有参构造
    public ProctorInfo(Integer proctorId, Integer subjectId, String subjectName, Integer teacherId, String teacherName, String examTime, String examPlace) {
        this.proctorId = proctorId;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.examTime = examTime;
        this.examPlace = examPlace;
    }

    // Getter & Setter
    public Integer getProctorId() {
        return proctorId;
    }

    public void setProctorId(Integer proctorId) {
        this.proctorId = proctorId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public String getExamPlace() {
        return examPlace;
    }

    public void setExamPlace(String examPlace) {
        this.examPlace = examPlace;
    }
}