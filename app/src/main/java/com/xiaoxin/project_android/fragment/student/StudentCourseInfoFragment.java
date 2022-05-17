package com.xiaoxin.project_android.fragment.student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.activity.CourseViewModel;
import com.xiaoxin.project_android.dialog.LoadingDialog;
import com.xiaoxin.project_android.entity.Course;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class StudentCourseInfoFragment extends Fragment {
    @BindView(R.id.teacher_course_info_avatar1)
    ImageView courseAvatar;
    @BindView(R.id.teacher_course_info_name1)
    TextView courseName;
    @BindView(R.id.teacher_course_info_code1)
    TextView courseCode;
    @BindView(R.id.teacher_course_info_teacher_name1)
    TextView teacherName;
    @BindView(R.id.teacher_course_info_teacher_phone1)
    TextView teacherPhone;
    @BindView(R.id.teacher_course_info_teacher_email1)
    TextView teacherEmail;
    @BindView(R.id.teacher_course_info_introduce1)
    TextView courseIntroduce;

    private CourseViewModel viewModel;
    private Course course;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_course_info, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(getActivity()).get(CourseViewModel.class);
        // TODO: Use the ViewModel
        course = viewModel.getCourse().getValue();
        initView();
    }

    private void initView(){
        Log.d("课程信息头像路径",Static.SERVICE_PATH + course.getCourseAvatar());
        GlideUtil.load(getContext(),Static.SERVICE_PATH + course.getCourseAvatar(),courseAvatar,new RequestOptions().circleCrop());
        courseName.setText(course.getCourseName());
        courseCode.setText(course.getCourseCode());
        courseIntroduce.setText(course.getCourseIntroduce());
        teacherEmail.setText(course.getTeacherEmail());
        teacherName.setText(course.getTeacherName());
        teacherPhone.setText(course.getTeacherPhone());
    }

    @OnClick(R.id.student_course_info_delete)
    public void onClick(View view){
        LoadingDialog dialog = new LoadingDialog(view.getContext());
        dialog.setTitle("警告");
        dialog.setMessage("该操作不可逆，请重复确认");
        dialog.setOnYesClickedListener(v -> {
            dialog.dismiss();
            LoadingDialog dialog1 = new LoadingDialog(v.getContext());
            dialog1.setTitle("退出课程");
            dialog1.setMessage(StringUtils.getString(R.string.wait_message));
            dialog1.show();
            Map<String, String> map = new HashMap<>();
            map.put("courseId",String.valueOf(course.getCourseId()));
            map.put("studentId",view.getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("id",""));
            NetUtil.getNetData("courseStudent/deleteCourseStudent",map,new Handler(msg -> {
                if (msg.what == 1){
                    dialog1.setOnDismissListener(dialog2 -> getActivity().finish());
                }
                dialog1.showSingleButton();
                dialog1.setMessage(msg.getData().getString("msg"));
                return false;
            }));
        });
        dialog.show();
    }

}