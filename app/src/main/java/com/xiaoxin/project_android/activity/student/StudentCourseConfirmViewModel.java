package com.xiaoxin.project_android.activity.student;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxin.project_android.entity.Course;

public class StudentCourseConfirmViewModel extends ViewModel {
    private MutableLiveData<Course> course;

    public MutableLiveData<Course> getCourse() {
        if (course == null){
            course = new MutableLiveData<>();
        }
        return course;
    }

    public void updateCourse(String s){
        JSONObject o = JSON.parseObject(s);
        JSONObject teacher = JSONObject.parseObject(o.getString("teacher"));
        Course Course = new Course(o.getInteger("courseId"),o.getInteger("teacherId"),
                teacher.getString("teacherName"),o.getString("courseName"),
                o.getString("courseIntroduce"),o.getString("courseCode"),
                o.getString("courseAvatar"));
        Course.setTeacherEmail(teacher.getString("teacherEmail"));
        Course.setTeacherPhone(teacher.getString("teacherPhone"));
        course.setValue(Course);
    }
}
