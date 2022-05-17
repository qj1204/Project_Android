package com.xiaoxin.project_android.activity.login;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.dialog.ConfirmDialog;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ModifyPasswordActivity extends BaseActivity {
    @BindView(R.id.modify_password_account)
    EditText accountEdit;
    @BindView(R.id.modify_password_phone)
    EditText phoneEdit;
    @BindView(R.id.modify_password_old_password)
    EditText oldPasswordEdit;
    @BindView(R.id.modify_password_new_password)
    EditText newPasswordEdit;

    private String account;
    private String phone;
    private String newPassword;
    private String oldPassword;
    private int userType = 0;
    private String id;

    private ConfirmDialog confirmDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_password;
    }

    @Override
    protected void initView() {
        ViewUtils.initActionBar(this,"修改密码");
    }

    Handler modifyHandler = new Handler(message -> {
        if(message.what == 1){
            showToast(message.getData().getString("data"));
        } else {
            showToast("修改失败");
        }
        confirmDialog.dismiss();
        finish();
        return false;
    });

    Handler confirmHandler = new Handler(message -> {
        if(message.what == 1){
            confirmDialog = new ConfirmDialog(this, phone);
            confirmDialog.setConfirmSuccessListener(() -> {
                Map<String,String> map = new HashMap<>();
                map.put(userType == 1 ? "teacherId" : "studentId", id);
                map.put("password",newPassword);
                NetUtil.getNetData((userType == 1 ? "teacher" : "student") + "/update", map, modifyHandler);
            });
            confirmDialog.show();
            id = message.getData().getString("data");
            return true;
        } else {
            showToast("手机号输入错误");
            return false;
        }
    });

    @OnClick({R.id.modify_password_submit})
    public void onClick(View v) {
        if(!CommonUtil.isFastClick()){
            return;
        }
        if (v.getId() == R.id.modify_password_submit){
            account = accountEdit.getText().toString();
            phone = phoneEdit.getText().toString();
            oldPassword = oldPasswordEdit.getText().toString();
            newPassword = newPasswordEdit.getText().toString();

            if(account.isEmpty() || phone.isEmpty() || oldPassword.isEmpty() || newPassword.isEmpty() || userType == 0){
                showToast("请补全信息");
            } else if (!oldPassword.equals(newPassword)){
                showToast("密码不一致");
            } else if (!CommonUtil.isPhone(phone)) {
                showToast("电话填写有误");
            } else {
                Map<String,String> map = new HashMap<>();
                map.put("type",String.valueOf(userType));
                map.put("account",account);
                map.put("phone",phone);
                NetUtil.getNetData("account/confirmAccount", map, confirmHandler);
            }
        } else if (v.getId() == R.id.activity_modify_password){
            hideSoftInput();
        }
    }

    @OnCheckedChanged({R.id.modify_password_teacher, R.id.modify_password_student})
    public void onCheckedChanged(CompoundButton v, boolean isChanged) {
        if (v.getId() == R.id.modify_password_teacher){
            if (isChanged) {
                userType = 1;
            }
        } else if (v.getId() == R.id.modify_password_student){
            if (isChanged) {
                userType = 2;
            }
        } else{
            userType = 0;
        }
    }

}