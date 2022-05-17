package com.xiaoxin.project_android.entity;

import java.io.Serializable;

public class AccountTeacher implements Serializable {
    private String teacherAccount;
    private String teacherPassword;
    private String teacherName;
    private Integer teacherSex;
    private String teacherPhone;
    private String teacherEmail;

    public AccountTeacher(String name, String account, String password, Integer sex, String phone, String email) {
        this.teacherAccount = account;
        this.teacherPassword = password;
        this.teacherName = name;
        this.teacherSex = sex;
        this.teacherPhone = phone;
        this.teacherEmail = email;
    }

    public String getTeacherAccount() {
        return teacherAccount;
    }

    public void setTeacherAccount(String teacherAccount) {
        this.teacherAccount = teacherAccount;
    }

    public String getTeacherPassword() {
        return teacherPassword;
    }

    public void setTeacherPassword(String teacherPassword) {
        this.teacherPassword = teacherPassword;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public Integer getTeacherSex() {
        return teacherSex;
    }

    public void setTeacherSex(Integer teacherSex) {
        this.teacherSex = teacherSex;
    }

    public String getTeacherPhone() {
        return teacherPhone;
    }

    public void setTeacherPhone(String teacherPhone) {
        this.teacherPhone = teacherPhone;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }

    @Override
    public String toString() {
        return "AccountTeacher{" +
                "teacherAccount='" + teacherAccount + '\'' +
                ", teacherPassword='" + teacherPassword + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", teacherSex=" + teacherSex +
                ", teacherPhone='" + teacherPhone + '\'' +
                ", teacherEmail='" + teacherEmail + '\'' +
                '}';
    }
}
