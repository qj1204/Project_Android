package com.xiaoxin.project_android.activity.teacher;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.StringUtils;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.dialog.LoadingDialog;
import com.xiaoxin.project_android.entity.Leave;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class TeacherLeaveActivity extends BaseActivity {
    @BindView(R.id.leave_detail_name)
    TextView nameText;
    @BindView(R.id.leave_detail_account)
    TextView accountText;
    @BindView(R.id.leave_detail_phone)
    TextView phoneText;
    @BindView(R.id.leave_detail_date_start)
    TextView startText;
    @BindView(R.id.leave_detail_date_end)
    TextView endText;
    @BindView(R.id.leave_detail_reason)
    TextView reasonText;
    @BindView(R.id.leave_detail_refuse)
    RadioButton refuseRadio;
    @BindView(R.id.leave_detail_agree)
    RadioButton agreeRadio;
    @BindView(R.id.leave_detail_remark)
    EditText remarkText;

    private Leave leave;
    private Integer approvalResult = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teacher_leave;
    }

    @Override
    protected void initView() {
        ViewUtils.initActionBar(this,"审批");

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

        if (leave.getApprovalResult() > 0){
            if (leave.getApprovalResult() == 1) {
                refuseRadio.performClick();
                approvalResult = 1;
            } else {
                agreeRadio.performClick();
                approvalResult = 2;
            }
            remarkText.setText(leave.getApprovalRemark());
        } else {
            remarkText.setHint("请输入备注");
        }
    }

    @OnClick({R.id.leave_detail_submit})
    public void onClick(View v) {
        if(!CommonUtil.isFastClick()){
            return;
        }
        if(v.getId() == R.id.leave_detail_submit) {
            if (approvalResult < 1) {
                showToast("未填写审批结果");
                return;
            }
            Timestamp submitTime = new Timestamp(System.currentTimeMillis());

            LoadingDialog dialog = new LoadingDialog(this);
            dialog.setTitle("审批");
            dialog.setMessage(StringUtils.getString(R.string.wait_message));
            dialog.show();

            Map<String, String> map = new HashMap<>();
            map.put("leaveId", String.valueOf(leave.getLeaveId()));
            map.put("time", submitTime.toString());
            map.put("result", String.valueOf(approvalResult));
            map.put("remark", remarkText.getText() == null ? "" : remarkText.getText().toString());
            NetUtil.getNetData("leave/updateLeave", map, new Handler(message -> {
                dialog.setMessage(message.getData().getString("data"));
                dialog.showSingleButton();
                if (message.what == 1) {
                    dialog.setOnDismissListener(dialog1 -> {
                        finish();
                    });
                }
                return false;
            }));
        } else if(v.getId() == R.id.activity_teacher_leave){
            hideSoftInput();
        }
    }

    @OnCheckedChanged({R.id.leave_detail_refuse, R.id.leave_detail_agree})
    public void onChanged(CompoundButton view, boolean isChanged){
        switch (view.getId()){
            case R.id.leave_detail_agree:
                if (isChanged){
                    approvalResult = 2;
                }
                break;
            case R.id.leave_detail_refuse:
                if (isChanged){
                    approvalResult = 1;
                }
                break;
        }
    }
}