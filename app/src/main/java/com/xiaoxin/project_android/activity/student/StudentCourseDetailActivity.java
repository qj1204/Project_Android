package com.xiaoxin.project_android.activity.student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
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
public class StudentCourseDetailActivity extends BaseActivity {

    @BindView(R.id.student_course_bottom)
    BottomNavigationView navigationView;

    private CourseViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_student_course_detail;
    }

    @Override
    protected void initView() {
        viewModel = new ViewModelProvider(this).get(CourseViewModel.class);
        Course course = viewModel.getCourse().getValue();
        ViewUtils.initActionBar(this,course.getCourseName());

        initBottomNavigation();
    }

    @OnClick
    public void onClick(View view) {

    }

    @SuppressLint("ResourceType")
    private void initBottomNavigation(){
        NavController controller = Navigation.findNavController(this,R.id.student_course_fragment);
        NavigationUI.setupWithNavController(navigationView, controller);
    }
}