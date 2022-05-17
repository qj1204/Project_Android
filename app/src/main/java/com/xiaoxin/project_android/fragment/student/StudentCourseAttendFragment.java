package com.xiaoxin.project_android.fragment.student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.activity.CourseViewModel;
import com.xiaoxin.project_android.adapter.StudentCourseAttendAdapter;
import com.xiaoxin.project_android.entity.Course;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

@SuppressLint("NonConstantResourceId")
public class StudentCourseAttendFragment extends Fragment {
    @BindView(R.id.refresh_student_attend1)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.search2)
    EditText searchEdit;
    @BindView(R.id.content_not_found_layout)
    LinearLayout notFoundLayout;

    private StudentCourseAttendViewModel mViewModel;

    Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_course_attend, container, false);
        unbinder = ButterKnife.bind(this,view);
        Course course = (Course) getActivity().getIntent().getExtras().getSerializable("course");
        CourseViewModel courseViewModel = new ViewModelProvider(requireActivity()).get(CourseViewModel.class);
        courseViewModel.getCourse();
        courseViewModel.setCourse(course);
        String id = String.valueOf(course.getCourseId());
        String joinTime = course.getJoinTime().toString();
        Map<String, String> map = new HashMap<>();
        map.put("courseId",id);
        map.put("joinTime",joinTime);
        NetUtil.getNetData("attend/findStudentAttend",map,new Handler(message -> {
            if (message.what == 1) {
                mViewModel.updateAttendList(message.getData().getString("data"));
            } else {
                Toast.makeText(getContext(), message.getData().getString("msg"), Toast.LENGTH_SHORT).show();
            }
            return false;
        }));
        refreshLayout.setColorSchemeColors(ViewUtils.getRefreshColor());
        refreshLayout.setOnRefreshListener(() -> {
            NetUtil.getNetData("attend/findStudentAttend",map,new Handler(message -> {
                if (message.what == 1) {
                    mViewModel.updateAttendList(message.getData().getString("data"));
                } else {
                    Toast.makeText(getContext(), message.getData().getString("msg"), Toast.LENGTH_SHORT).show();
                }
                refreshLayout.setRefreshing(false);
                return false;
            }));
        });

        searchEdit.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER){
                String time = searchEdit.getText().toString();
                Map<String, String> map1 = new HashMap<>();
                map1.put("courseId",id);
                map1.put("time",time);
                map1.put("joinTime",joinTime);
                NetUtil.getNetData("attend/findStudentAttendByTime", map1, new Handler(msg -> {
                    if (msg.what == 1) {
                        mViewModel.updateAttendList(msg.getData().getString("data"));
                    } else {
                        Toast.makeText(getContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }));
            }
            return false;
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(StudentCourseAttendViewModel.class);
        // TODO: Use the ViewModel
        mViewModel.getAttendList().observe(getViewLifecycleOwner(),attendList -> {
            if (attendList.size() < 1){
                notFoundLayout.setVisibility(View.VISIBLE);
            } else {
                notFoundLayout.setVisibility(View.GONE);
                ViewUtils.setRecycler(getActivity(), R.id.recycler_attend_list_student, new StudentCourseAttendAdapter(attendList));
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}