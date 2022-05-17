package com.xiaoxin.project_android.activity;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xiaoxin.project_android.entity.Course;

public class CourseViewModel extends ViewModel {
    private MutableLiveData<Course> course;

    public MutableLiveData<Course> getCourse() {
        if (course == null){
            course = new MutableLiveData<>();
        }
        return course;
    }

    public void setCourse(Course course){
        this.course.setValue(course);
    }
}
