package com.xiaoxin.project_android.fragment.student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.activity.CourseViewModel;
import com.xiaoxin.project_android.adapter.StudentLeaveAdapter;
import com.xiaoxin.project_android.dialog.LeaveCreateDialog;
import com.xiaoxin.project_android.fragment.LeaveViewModel;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class StudentCourseLeaveFragment extends Fragment {
    @BindView(R.id.refresh_student_leave)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.content_not_found_layout)
    LinearLayout notFoundLayout;

    private LeaveViewModel mViewModel;
    private CourseViewModel viewModel;

    private Integer courseId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_course_leave, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LeaveViewModel.class);
        // TODO: Use the ViewModel
        viewModel = new ViewModelProvider(getActivity()).get(CourseViewModel.class);
        mViewModel.getLeaveList().observe(getViewLifecycleOwner(), leaves -> {
            if (leaves.size() < 1){
                notFoundLayout.setVisibility(View.VISIBLE);
                return;
            } else {
                notFoundLayout.setVisibility(View.GONE);
                ViewUtils.setRecycler(getActivity(), R.id.recycler_student_leave_list,new StudentLeaveAdapter(leaves));
            }
        });
        courseId = viewModel.getCourse().getValue().getCourseId();

        Map<String, String> map = new HashMap<>();
        map.put("courseId",String.valueOf(courseId));
        map.put("studentId",getActivity().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("id",""));
        NetUtil.getNetData("leave/findAllLeave",map,new Handler(msg -> {
            if (msg.what == 1){
                String data = msg.getData().getString("data");
                mViewModel.updateLeaveList(data);
            }
            Toast.makeText(getContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
            return false;
        }));

        refreshLayout.setColorSchemeColors(ViewUtils.getRefreshColor());
        refreshLayout.setOnRefreshListener(() -> NetUtil.getNetData("leave/findAllLeave",map,new Handler(msg -> {
            if (msg.what == 1){
                String data = msg.getData().getString("data");
                mViewModel.updateLeaveList(data);
                refreshLayout.setRefreshing(false);
            }
            Toast.makeText(getContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
            return false;
        })));
    }

    @OnClick(R.id.student_leave_create)
    public void onClick(View view){
        LeaveCreateDialog dialog = new LeaveCreateDialog(getContext(),courseId);
        dialog.show();
    }

}