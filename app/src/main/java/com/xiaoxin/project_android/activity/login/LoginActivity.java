package com.xiaoxin.project_android.activity.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.PermissionUtils;
import com.blankj.utilcode.util.StringUtils;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.dialog.LoadingDialog;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class LoginActivity extends BaseActivity {
    @BindView(R.id.login_account)
    EditText accountEdit;
    @BindView(R.id.login_password)
    EditText passwordEdit;

    private int userType;
    private SharedPreferences preferences;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        preferences = getSharedPreferences("localRecord",MODE_PRIVATE);
    }

    @OnClick({R.id.login_register, R.id.login_forget_password, R.id.login_button})
    public void onClick(View v) {
        if(!CommonUtil.isFastClick()){
            return;
        }
        if(!initPermission()){
            return;
        }
        Intent intent = new Intent();

        //点击注册，启动注册activity
        if(v.getId() == R.id.login_register){
            intent.setAction(Static.SELECT_ROLE);
            startActivity(intent);
        //点击忘记密码，启动修改密码activity
        }else if (v.getId() == R.id.login_forget_password){
            intent.setAction(Static.MODIFY_PASSWORD);
            startActivity(intent);
        //点击登录，进行登录操作
        }else if(v.getId() == R.id.login_button){
            if (userType == 0 || accountEdit.getText().toString().isEmpty() || passwordEdit.getText().toString().isEmpty()) {
                showToast("请补全登录信息");
                return;
            }
            LoadingDialog dialog = new LoadingDialog(v.getContext());
            dialog.setTitle("登录操作");
            dialog.setMessage(StringUtils.getString(R.string.wait_message));
            dialog.show();

            Map<String, String> map = new HashMap<>();
            map.put("type", String.valueOf(userType));
            map.put("account", accountEdit.getText().toString());
            map.put("password", passwordEdit.getText().toString());

            NetUtil.getNetData("account/login", map, new Handler(message -> {
                if (message.what == 1){
                    dialog.dismiss();

                    Bundle bundle = message.getData();
                    String data = bundle.getString("data");
                    Log.d("登录信息",data);
                    Intent loginIntent = new Intent(Static.MAIN);
                    //将此次的登录信息记录到SharedPreferences中
                    updateLoginInfo(preferences, data, String.valueOf(userType));
                    startActivity(loginIntent);
                    finish();
                } else{
                    dialog.showSingleButton();
                    dialog.setMessage(message.getData().getString("data"));
                }
                return false;
            }));
        }else if(v.getId() == R.id.activity_login){
            hideSoftInput();
        }
    }

    @OnCheckedChanged({R.id.login_radio_teacher, R.id.login_radio_student})
    public void onCheckedChanged(CompoundButton v, boolean isChanged) {
        if (v.getId() == R.id.login_radio_teacher){
            if (isChanged){
                userType = 1;
            }
        }else if(v.getId() == R.id.login_radio_student){
            if (isChanged){
                userType = 2;
            }
        }else{
            userType = 0;
        }
    }

    public static void updateLoginInfo(SharedPreferences preferences, String data, String type){
        JSONObject jsonObject = JSONObject.parseObject(data);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("userType",type);
        if (type.equals("1")){
            editor.putString("id",jsonObject.getString("teacherId"));
            editor.putString("account",jsonObject.getString("teacherAccount"));
            editor.putString("password",jsonObject.getString("teacherPassword"));
            editor.putString("name",jsonObject.getString("teacherName"));
            editor.putInt("sex",jsonObject.getInteger("teacherSex"));
            editor.putString("phone",jsonObject.getString("teacherPhone"));
            editor.putString("email",jsonObject.getString("teacherEmail"));
            editor.putString("avatar",jsonObject.getString("teacherAvatar"));
        } else {
            editor.putString("id",jsonObject.getString("studentId"));
            editor.putString("account",jsonObject.getString("studentAccount"));
            editor.putString("password",jsonObject.getString("studentPassword"));
            editor.putString("name",jsonObject.getString("studentName"));
            editor.putInt("sex",jsonObject.getInteger("studentSex"));
            editor.putString("phone",jsonObject.getString("studentPhone"));
            editor.putString("email",jsonObject.getString("studentEmail"));
            editor.putString("avatar",jsonObject.getString("studentAvatar"));
            editor.putString("class",jsonObject.getString("studentClass"));
            editor.putString("face",jsonObject.getString("studentFace"));
        }
        editor.apply();
    }

    private boolean initPermission(){
        List<String> permissionList = new ArrayList<>();
        if (!PermissionUtils.isGranted(Manifest.permission.READ_PHONE_STATE)){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (!PermissionUtils.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!PermissionUtils.isGranted(Manifest.permission.ACCESS_FINE_LOCATION)){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            PermissionUtils.permission(permissions).request();
            return false;
        }
        return true;
    }

}