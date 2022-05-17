package com.xiaoxin.project_android.activity.student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ColorUtils;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.dialog.LoadingDialog;
import com.xiaoxin.project_android.entity.Leave;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class StudentLeaveDetailActivity extends BaseActivity {
    @BindView(R.id.leave_detail_name1)
    TextView nameText;
    @BindView(R.id.leave_detail_account1)
    TextView accountText;
    @BindView(R.id.leave_detail_phone1)
    TextView phoneText;
    @BindView(R.id.leave_detail_date_start1)
    TextView startText;
    @BindView(R.id.leave_detail_date_end1)
    TextView endText;
    @BindView(R.id.leave_detail_reason1)
    TextView reasonText;
    @BindView(R.id.leave_detail_result)
    TextView resultText;
    @BindView(R.id.leave_detail_remark1)
    EditText remarkText;
    @BindView(R.id.leave_detail_remark_layout)
    LinearLayout layout;
    @BindView(R.id.leave_detail_delete)
    Button deleteButton;

    private Leave leave;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_student_leave_detail;
    }

    @Override
    protected void initView() {
        ViewUtils.initActionBar(this,"请假详情");

        Intent intent = getIntent();
        leave = (Leave) intent.getExtras().getSerializable("leave");

        initData();
    }

    private void initData() {
        nameText.setText(leave.getStudentName());
        accountText.setText(leave.getStudentAccount());
        phoneText.setText(leave.getStudentPhone());
        SimpleDateFormat format = new SimpleDateFormat(Static.DATE_FORMAT_MINUTE, Locale.CHINA);
        startText.setText(format.format(leave.getLeaveTime()));
        endText.setText(format.format(leave.getBackTime()));
        reasonText.setText(leave.getLeaveReason());

        String remark = leave.getApprovalRemark() == null ? "无备注信息" : leave.getApprovalRemark();
        switch (leave.getApprovalResult()){
            case 0:
                resultText.setText("审批中");
                resultText.setTextColor(ColorUtils.getColor(R.color.soft_blue));
                layout.setVisibility(View.GONE);
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(v -> {
                    //删除申请
                    LoadingDialog dialog = new LoadingDialog(v.getContext());
                    dialog.setTitle("撤销申请");
                    dialog.show();
                    Map<String, String> map = new HashMap<>();
                    map.put("leaveId",String.valueOf(leave.getLeaveId()));
                    NetUtil.getNetData("leave/deleteLeave",map,new Handler(msg -> {
                        dialog.setMessage(msg.getData().getString("msg"));
                        dialog.showSingleButton();
                        return false;
                    }));
                });
                break;
            case 1:
                resultText.setText("不批准");
                resultText.setTextColor(ColorUtils.getColor(R.color.cancel_red));
                remarkText.setText(remark);
                break;
            case 2:
                resultText.setText("批准");
                resultText.setTextColor(ColorUtils.getColor(R.color.green));
                remarkText.setText(remark);
                break;
        }
    }

    @OnClick
    public void onClick(View view) {

    }
}