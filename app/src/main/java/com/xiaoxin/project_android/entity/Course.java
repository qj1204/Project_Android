package com.xiaoxin.project_android.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class Course implements Serializable {
    private final Integer courseId;
    private final Integer teacherId;
    private final String teacherName;
    private String teacherPhone;
    private String teacherEmail;
    private final String courseName;
    private final String courseIntroduce;
    private final String courseCode;
    private final String courseAvatar;

    private Timestamp joinTime;

    public Course(Integer courseId, Integer teacherId, String teacherName, String courseName, String courseIntroduce, String courseCode, String courseAvatar) {
        this.courseId = courseId;
        this.teacherId = teacherId;
        this.teacherName = teacherName;
        this.courseName = courseName;
        this.courseIntroduce = courseIntroduce;
        this.courseCode = courseCode;
        this.courseAvatar = courseAvatar;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public String getTeacherName() {
        return teacherName;
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

    public String getCourseName() {
        return courseName;
    }

    public String getCourseIntroduce() {
        return courseIntroduce;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseAvatar() {
        return courseAvatar;
    }

    public Timestamp getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Timestamp joinTime) {
        this.joinTime = joinTime;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseId=" + courseId +
                ", teacherId=" + teacherId +
                ", teacherName='" + teacherName + '\'' +
                ", teacherPhone='" + teacherPhone + '\'' +
                ", teacherEmail='" + teacherEmail + '\'' +
                ", courseName='" + courseName + '\'' +
                ", courseIntroduce='" + courseIntroduce + '\'' +
                ", courseCode='" + courseCode + '\'' +
                ", courseAvatar='" + courseAvatar + '\'' +
                ", joinTime=" + joinTime +
                '}';
    }
}
