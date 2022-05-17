package com.xiaoxin.project_android.fragment.teacher;

import android.annotation.SuppressLint;
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
import com.xiaoxin.project_android.adapter.TotalAdapter;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class TeacherCourseTotalFragment extends Fragment {
    @BindView(R.id.refresh_teacher_total)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.content_not_found_layout)
    LinearLayout notFoundLayout;
    @BindView(R.id.total_layout)
    LinearLayout totalLayout;

    private TeacherCourseTotalViewModel mViewModel;
    private CourseViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_teacher_course_total, container, false);
        ButterKnife.bind(this,inflate);
        return inflate;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TeacherCourseTotalViewModel.class);
        viewModel = new ViewModelProvider(getActivity()).get(CourseViewModel.class);
        // TODO: Use the ViewModel
        mViewModel.getStatisticsList().observe(getViewLifecycleOwner(),statistics -> {
            if (statistics.size() < 1){
                notFoundLayout.setVisibility(View.VISIBLE);
                totalLayout.setVisibility(View.GONE);
            } else {
                notFoundLayout.setVisibility(View.GONE);
                totalLayout.setVisibility(View.VISIBLE);
                ViewUtils.setRecycler(getActivity(), R.id.recycler_course_total_list,new TotalAdapter(statistics));
            }
        });
        Integer courseId = viewModel.getCourse().getValue().getCourseId();
        Map<String, String> map = new HashMap<>();
        map.put("courseId",String.valueOf(courseId));
        NetUtil.getNetData("record/findAllStudentRecord", map, new Handler(msg -> {
            if (msg.what == 1){
                mViewModel.updateStatistics(msg.getData().getString("data"));
            }
            Toast.makeText(getActivity(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
            return false;
        }));
        refreshLayout.setColorSchemeColors(ViewUtils.getRefreshColor());
        refreshLayout.setOnRefreshListener(() -> NetUtil.getNetData("record/findAllStudentRecord",map,new Handler(msg -> {
            if (msg.what == 1){
                mViewModel.updateStatistics(msg.getData().getString("data"));
            }
            Toast.makeText(getActivity(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
            refreshLayout.setRefreshing(false);
            return false;
        })));
    }

}