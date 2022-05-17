package com.xiaoxin.project_android.activity.student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.dialog.LoadingDialog;
import com.xiaoxin.project_android.entity.Course;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class StudentCourseConfirmActivity extends BaseActivity {
    @BindView(R.id.course_confirm_avatar)
    ImageView courseAvatar;
    @BindView(R.id.course_confirm_name)
    TextView courseName;
    @BindView(R.id.course_confirm_code)
    TextView courseCode;
    @BindView(R.id.course_confirm_teacher_name)
    TextView teacherName;
    @BindView(R.id.course_confirm_teacher_phone)
    TextView teacherPhone;
    @BindView(R.id.course_confirm_teacher_email)
    TextView teacherEmail;
    @BindView(R.id.course_confirm_introduce)
    TextView courseIntroduce;

    private StudentCourseConfirmViewModel viewModel;
    private Course course;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_student_course_confirm;
    }

    @Override
    protected void initView() {
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");

        viewModel = new ViewModelProvider(this).get(StudentCourseConfirmViewModel.class);
        viewModel.getCourse().observe(this, course -> {
            this.course = course;
            initData();
        });
        assert data != null;
        viewModel.updateCourse(data);
        ViewUtils.initActionBar(this,viewModel.getCourse().getValue().getCourseName());
    }

    private void initData(){
        GlideUtil.load(this,Static.SERVICE_PATH + course.getCourseAvatar(),courseAvatar,new RequestOptions().circleCrop());

        courseName.setText(course.getCourseName());
        courseCode.setText(course.getCourseCode());
        courseIntroduce.setText(course.getCourseIntroduce());
        teacherEmail.setText(course.getTeacherEmail());
        teacherName.setText(course.getTeacherName());
        teacherPhone.setText(course.getTeacherPhone());
    }

    @OnClick({R.id.course_confirm_add,R.id.activity_student_course_confirm})
    public void onClick(View v){
        if(!CommonUtil.isFastClick()){
            return;
        }
        if(v.getId() == R.id.course_confirm_add) {
            Map<String, String> map = new HashMap<>();
            map.put("courseCode", course.getCourseCode());
            map.put("studentId", getSharedPreferences("localRecord", MODE_PRIVATE).getString("id", ""));
            LoadingDialog loadingDialog = new LoadingDialog(this);
            loadingDialog.setTitle("加入课程");
            loadingDialog.show();
            NetUtil.getNetData("courseStudent/addCourseStudent", map, new Handler(msg -> {
                if (msg.what == 1) {
                    String s = msg.getData().getString("data");
                    loadingDialog.setOnYesClickedListener(view1 -> {
                        JSONObject data = JSON.parseObject(s);
                        Intent intent = new Intent(Static.STUDENT_COURSE_DETAIL);
                        Bundle bundle = new Bundle();
                        course.setJoinTime(data.getTimestamp("joinTime"));
                        bundle.putSerializable("course", course);
                        intent.putExtras(bundle);
                        view1.getContext().startActivity(intent);
                        loadingDialog.dismiss();
                        finish();
                    });
                    loadingDialog.no.setVisibility(View.INVISIBLE);
                } else {
                    loadingDialog.showSingleButton();
                }
                loadingDialog.setMessage(msg.getData().getString("msg"));
                return false;
            }));
        } else if(v.getId() == R.id.activity_student_course_confirm){
            hideSoftInput();
        }
    }

}