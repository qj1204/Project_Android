package com.xiaoxin.project_android.activity.teacher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.activity.CourseViewModel;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.entity.Course;
import com.xiaoxin.project_android.utils.ViewUtils;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class TeacherCourseDetailActivity extends BaseActivity {

    @BindView(R.id.teacher_course_bottom)
    BottomNavigationView navigationView;

    private CourseViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teacher_course_detail;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        //执行网络上传
        Course course = (Course) intent.getExtras().getSerializable("course");

        //模拟数据
        /*JSONObject o = JSONObject.parseObject(data);
        JSONObject teacher = JSONObject.parseObject(o.getString("teacher"));
        Course course = new Course(o.getInteger("courseId"),o.getString("teacherId"),
                teacher.getString("teacherName"),o.getString("courseName"),
                o.getString("courseIntroduce"),o.getString("courseCode"),
                o.getString("courseAvatar"));
        course.setTeacherEmail(teacher.getString("teacherEmail"));
        course.setTeacherPhone(teacher.getString("teacherPhone"));*/


        ViewUtils.initActionBar(this,course.getCourseName());

        viewModel = new ViewModelProvider(this).get(CourseViewModel.class);

        //先初始化course
        viewModel.getCourse();

        viewModel.setCourse(course);

        initBottomNavigation();

    }

    //实现fragment之间的跳转
    @SuppressLint("ResourceType")
    private void initBottomNavigation(){
        NavController controller = Navigation.findNavController(this, R.id.teacher_course_fragment);
        NavigationUI.setupWithNavController(navigationView,controller);
    }

    @OnClick
    public void onClick(View view) {

    }
}
