package com.xiaoxin.project_android.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class Student implements Serializable {
    private final Integer studentId;
    private final String studentAccount;
    private final String studentPassword;
    private final String studentName;
    private final Integer studentSex;
    private final String studentAvatar;
    private final String studentClass;
    private final String studentFace;
    private final String studentPhone;
    private final String studentEmail;

    private Timestamp joinTime;

    public Integer getStudentId() {
        return studentId;
    }

    public String getStudentAccount() {
        return studentAccount;
    }

    public String getStudentPassword() {
        return studentPassword;
    }

    public String getStudentName() {
        return studentName;
    }

    public Integer getStudentSex() {
        return studentSex;
    }

    public String getStudentAvatar() {
        return studentAvatar;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public String getStudentFace() {
        return studentFace;
    }

    public String getStudentPhone() {
        return studentPhone;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public Student(Integer studentId, String studentAccount, String studentPassword, String studentName, Integer studentSex, String studentAvatar, String studentClass, String studentFace, String studentPhone, String studentEmail) {
        this.studentId = studentId;
        this.studentAccount = studentAccount;
        this.studentPassword = studentPassword;
        this.studentName = studentName;
        this.studentSex = studentSex;
        this.studentAvatar = studentAvatar;
        this.studentClass = studentClass;
        this.studentFace = studentFace;
        this.studentPhone = studentPhone;
        this.studentEmail = studentEmail;
    }

    public Timestamp getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Timestamp joinTime) {
        this.joinTime = joinTime;
    }
}
