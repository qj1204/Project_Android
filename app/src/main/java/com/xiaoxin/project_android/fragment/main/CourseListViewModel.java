package com.xiaoxin.project_android.fragment.main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxin.project_android.entity.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseListViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Course>> courseList;

    public MutableLiveData<List<Course>> getCourseList() {
        if (courseList == null){
            courseList = new MutableLiveData<>();
        }
        return courseList;
    }

    //更新recyclerview列表
    public void updateCourses(String s){
        List<Course> courses = new ArrayList<>();
        Course course;
        JSONArray objects = JSONObject.parseArray(s);
        for (int i = 0; i < objects.size(); i++) {
            JSONObject o = (JSONObject) objects.get(i);
            JSONObject teacher = JSONObject.parseObject(o.getString("teacher"));
            course = new Course(o.getInteger("courseId"),o.getInteger("teacherId"),
                    teacher.getString("teacherName"),o.getString("courseName"),
                    o.getString("courseIntroduce"),o.getString("courseCode"),
                    o.getString("courseAvatar"));
            course.setTeacherEmail(teacher.getString("teacherEmail"));
            course.setTeacherPhone(teacher.getString("teacherPhone"));
            course.setJoinTime(o.getTimestamp("joinTime"));
            courses.add(course);
        }
        courseList.setValue(courses);
    }

}