package com.xiaoxin.project_android.fragment.teacher;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import com.blankj.utilcode.util.PermissionUtils;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.activity.CourseViewModel;
import com.xiaoxin.project_android.adapter.TeacherCourseAttendAdapter;
import com.xiaoxin.project_android.dialog.AttendCreateDialog;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

@SuppressLint("NonConstantResourceId")
public class TeacherCourseAttendFragment extends Fragment {
    @BindView(R.id.refresh_teacher_attend)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.search)
    EditText searchEdit;
    @BindView(R.id.content_not_found_layout)
    LinearLayout notFoundLayout;

    private TeacherCourseAttendViewModel mViewModel;
    private CourseViewModel viewModel;

    //创建新考勤任务的对话框
    private AttendCreateDialog attendCreateDialog;

    Unbinder unbinder;

    Handler attendListHandler = new Handler(message -> {
        if (message.what == 1){
            /*if (message.getData().getString("data").equals("[]")){
                Toast.makeText(getContext(), "查询结果为空", Toast.LENGTH_SHORT).show();
            }*/
            mViewModel.updateAttend(message.getData().getString("data"));
        } else {
            Toast.makeText(getContext(), message.getData().getString("msg"), Toast.LENGTH_SHORT).show();
        }
        refreshLayout.setRefreshing(false);
        return false;
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_course_attend, container, false);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(TeacherCourseAttendViewModel.class);
        viewModel = new ViewModelProvider(getActivity()).get(CourseViewModel.class);
        // TODO: Use the ViewModel
        mViewModel.getAttendList().observe(getViewLifecycleOwner(), attendLists -> {
            if (attendLists.size() < 1){
                notFoundLayout.setVisibility(View.VISIBLE);
            } else {
                notFoundLayout.setVisibility(View.GONE);
                ViewUtils.setRecycler(getActivity(), R.id.recycler_attend_list, new TeacherCourseAttendAdapter(attendLists));
            }
        });
        String id = String.valueOf(viewModel.getCourse().getValue().getCourseId());
        Map<String, String> map = new HashMap<>();
        map.put("courseId",id);
        NetUtil.getNetData("attend/findAttendByCourseId",map,attendListHandler);
        refreshLayout.setColorSchemeColors(ViewUtils.getRefreshColor());
        refreshLayout.setOnRefreshListener(() -> {
            NetUtil.getNetData("attend/findAttendByCourseId",map,new Handler(message -> {
                if (message.what == 1){
                    if (message.getData().getString("data").equals("[]")){
                        Toast.makeText(getContext(), "查询结果为空", Toast.LENGTH_SHORT).show();
                    }
                    mViewModel.updateAttend(message.getData().getString("data"));
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
                NetUtil.getNetData("attend/findAttendByTime", map1, attendListHandler);
            }
            return false;
        });
    }

    @OnClick(R.id.attend_create_button)
    public void onClick(View view){
        if (view.getId() == R.id.attend_create_button) {
            attendCreateDialog = new AttendCreateDialog(requireContext(), viewModel.getCourse().getValue().getCourseId());
            attendCreateDialog.setChooseClickListener(() -> {
                //请求权限进入地图activity
                List<String> permissionList = new ArrayList<>();
                if (!PermissionUtils.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                }
                if (!PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)) {
                    permissionList.add(Manifest.permission.READ_PHONE_STATE);
                }
                if (!PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                if (!permissionList.isEmpty()) {
                    String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                    PermissionUtils.permission(permissions).request();
                } else {
                    Intent intent = new Intent(Static.MAP_CHOOSE);
                    startActivityForResult(intent, 1);
                }
            });
            attendCreateDialog.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    if (data.getStringExtra("location") != null) {
                        attendCreateDialog.updateLocation(data.getStringExtra("location"));
                        attendCreateDialog.setLatitude(data.getDoubleExtra("latitude", 0));
                        attendCreateDialog.setLongitude(data.getDoubleExtra("longitude", 0));
                    } else {
                        attendCreateDialog.updateLocation("未选择坐标");
                    }
                }
            }
        }
    }
}