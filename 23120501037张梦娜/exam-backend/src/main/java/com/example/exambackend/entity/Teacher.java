package com.example.exambackend.entity;

/**
 * 教师实体类（对应数据库Teacher表）
 */
public class Teacher {
    // 教师ID（主键自增）
    private Integer teacherId;
    // 教师姓名
    private String teacherName;
    // 教师工号
    private String teacherNo;
    // 联系电话
    private String phone;
    // 所属学院
    private String college;
    // 新增：教师登录密码（由管理员设置）
    private String password;


    public Teacher() {}

    public Teacher(Integer teacherId, String teacherName, String teacherNo, String phone, String college, String password) {
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.teacherNo = teacherNo;
        this.phone = phone;
        this.college = college;
        this.password = password; // 新增：赋值密码字段
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

    public String getTeacherNo() {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    // 新增：password 的 Getter 方法
    public String getPassword() {
        return password;
    }

    // 新增：password 的 Setter 方法
    public void setPassword(String password) {
        this.password = password;
    }
}