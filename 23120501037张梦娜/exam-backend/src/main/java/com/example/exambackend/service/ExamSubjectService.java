package com.example.exambackend.service;

import com.example.exambackend.entity.ExamSubject;
import java.util.List;

public interface ExamSubjectService {
    List<ExamSubject> getExamSubjectList(String subjectName);
    String addExamSubject(ExamSubject examSubject);
    String updateExamSubject(ExamSubject examSubject);
    String deleteExamSubject(Integer subjectId);
    String batchDeleteSubjects(List<Integer> subjectIds);
}