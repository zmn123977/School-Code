package com.example.exambackend.entity;

public class ExamSubject {
    private Integer subjectId;
    private String subjectName;
    private String examType;

    public ExamSubject() {
    }

    public ExamSubject(Integer subjectId, String subjectName, String examType) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.examType = examType;
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

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }
}