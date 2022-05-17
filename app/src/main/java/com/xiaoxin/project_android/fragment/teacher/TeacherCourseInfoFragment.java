package com.xiaoxin.project_android.fragment.teacher;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
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
import com.squareup.picasso.Picasso;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.activity.CourseViewModel;
import com.xiaoxin.project_android.dialog.CourseModifyDialog;
import com.xiaoxin.project_android.dialog.LoadingDialog;
import com.xiaoxin.project_android.entity.Course;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class TeacherCourseInfoFragment extends Fragment {
    @BindView(R.id.teacher_course_info_avatar)
    ImageView courseAvatar;
    @BindView(R.id.teacher_course_info_name)
    TextView courseName;
    @BindView(R.id.teacher_course_info_code)
    TextView courseCode;
    @BindView(R.id.teacher_course_info_teacher_name)
    TextView teacherName;
    @BindView(R.id.teacher_course_info_teacher_phone)
    TextView teacherPhone;
    @BindView(R.id.teacher_course_info_teacher_email)
    TextView teacherEmail;
    @BindView(R.id.teacher_course_info_introduce)
    TextView courseIntroduce;

    private CourseViewModel viewModel;
    private Course course;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_info, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(CourseViewModel.class);
        // TODO: Use the ViewModel
        course = viewModel.getCourse().getValue();
        initView();
    }

    private void initView(){
        GlideUtil.load(this.getContext(),Static.SERVICE_PATH + course.getCourseAvatar(),courseAvatar,new RequestOptions().circleCrop());

        courseName.setText(course.getCourseName());
        courseCode.setText(course.getCourseCode());
        courseIntroduce.setText(course.getCourseIntroduce());
        teacherEmail.setText(course.getTeacherEmail());
        teacherName.setText(course.getTeacherName());
        teacherPhone.setText(course.getTeacherPhone());
    }

    @OnClick({R.id.teacher_course_info_modify, R.id.teacher_course_info_delete})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.teacher_course_info_modify:
                CourseModifyDialog modifyDialog = new CourseModifyDialog(view.getContext(),course.getCourseName(),course.getCourseIntroduce(),course.getCourseId());
                modifyDialog.setOnDismissListener(dialog -> {
                    courseIntroduce.setText(modifyDialog.getIntroduce());
                    courseName.setText(modifyDialog.getName());
                });
                modifyDialog.show();
                break;
            case R.id.teacher_course_info_delete:
                LoadingDialog dialog = new LoadingDialog(view.getContext());
                dialog.setTitle("警告");
                dialog.setMessage("该操作不可逆，请重复确认");
                dialog.setOnYesClickedListener(v -> {
                    dialog.dismiss();
                    LoadingDialog dialog1 = new LoadingDialog(v.getContext());
                    dialog1.setTitle("删除课程");
                    dialog1.setMessage(StringUtils.getString(R.string.wait_message));
                    dialog1.show();
                    Map<String, String> map = new HashMap<>();
                    map.put("id",String.valueOf(course.getCourseId()));
                    NetUtil.getNetData("course/delete",map,new Handler(msg -> {
                        if (msg.what == 1){
                            dialog1.setOnDismissListener(dialog2 -> getActivity().finish());
                        }
                        dialog1.showSingleButton();
                        dialog1.setMessage(msg.getData().getString("data"));
                        return false;
                    }));
                });
                dialog.show();
                break;
            default:break;
        }
    }

}