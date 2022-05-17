package com.xiaoxin.project_android.activity.teacher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.ResourceUtils;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.adapter.AttendDetailAdapter;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.entity.Attend;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class TeacherRecordDetailActivity extends BaseActivity {
    @BindView(R.id.content_not_found_layout)
    LinearLayout notFoundLayout;

    private String data = "[]";
    private Attend attend;

    private TeacherRecordDetailViewModel viewModel;
    private Integer currentType = -1;

    Handler getListHandler = new Handler(msg -> {
        if (msg.what == 1){
            data = msg.getData().getString("data");
            viewModel.setRecordList(data);
        } else {
            showToast(msg.getData().getString("msg"));
        }
        return false;
    });

    @Override
    protected int getLayoutId() {
        return R.layout.activity_record_detail;
    }

    @Override
    protected void initView() {
        ViewUtils.initActionBar(this,"考勤详情");

        Intent intent = getIntent();
        attend = (Attend) intent.getExtras().getSerializable("attend");
        Map<String, String> map = new HashMap<>();
        map.put("attendId",String.valueOf(attend.getAttendId()));

        viewModel = new ViewModelProvider(this).get(TeacherRecordDetailViewModel.class);
        viewModel.getAttendDetailList().observe(this,attendDetailLists -> {
            AttendDetailAdapter attendDetailAdapter = new AttendDetailAdapter(attendDetailLists,attend.getType());
            attendDetailAdapter.setResultChangedListener(() -> refreshView(currentType, map));
            ViewUtils.setRecycler(this, R.id.recycler_record_list, attendDetailAdapter);
            if (attendDetailLists.size() == 0) {
                notFoundLayout.setVisibility(View.VISIBLE);
            } else {
                notFoundLayout.setVisibility(View.GONE);
            }
        });

        NetUtil.getNetData("record/findAllRecord",map,getListHandler);
    }

    @OnClick({R.id.record_type_all, R.id.record_type_success, R.id.record_type_failure, R.id.record_type_absent, R.id.record_type_leave})
    public void onClick(View view){
        refreshButton();
        switch (view.getId()){
            case R.id.record_type_all:
                viewModel.setRecordList(data);
                currentType = -1;
                break;
            case R.id.record_type_success:
                viewModel.updateRecordList(data,2);
                currentType = 2;
                break;
            case R.id.record_type_failure:
                viewModel.updateRecordList(data,1);
                currentType = 1;
                break;
            case R.id.record_type_absent:
                viewModel.updateRecordList(data,0);
                currentType = 0;
                break;
            case R.id.record_type_leave:
                viewModel.updateRecordList(data,3);
                currentType = 3;
                break;
            default:
                break;
        }
        view.setBackground(ResourceUtils.getDrawable(R.color.white));
    }

    public void refreshButton(){
        findViewById(R.id.record_type_all).setBackground(ResourceUtils.getDrawable(R.color.smssdk_transparent));
        findViewById(R.id.record_type_success).setBackground(ResourceUtils.getDrawable(R.color.smssdk_transparent));
        findViewById(R.id.record_type_failure).setBackground(ResourceUtils.getDrawable(R.color.smssdk_transparent));
        findViewById(R.id.record_type_absent).setBackground(ResourceUtils.getDrawable(R.color.smssdk_transparent));
        findViewById(R.id.record_type_leave).setBackground(ResourceUtils.getDrawable(R.color.smssdk_transparent));
    }

    public void refreshView(Integer type,Map<String, String> map){
        NetUtil.getNetData("record/findAllRecord",map,new Handler(msg -> {
            if (msg.what == 1){
                data = msg.getData().getString("data");
                viewModel.setRecordList(data);
                switch (type){
                    case -1:
                        findViewById(R.id.record_type_all).performClick();
                        break;
                    case 0:
                        findViewById(R.id.record_type_absent).performClick();
                        break;
                    case 1:
                        findViewById(R.id.record_type_failure).performClick();
                        break;
                    case 2:
                        findViewById(R.id.record_type_success).performClick();
                        break;
                    case 3:
                        findViewById(R.id.record_type_leave).performClick();
                        break;
                }
            } else {
                showToast(msg.getData().getString("msg"));
            }
            return false;
        }));
    }
}
