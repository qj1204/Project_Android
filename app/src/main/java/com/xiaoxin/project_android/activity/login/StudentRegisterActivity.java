package com.xiaoxin.project_android.activity.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.entity.AccountStudent;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class StudentRegisterActivity extends BaseActivity {
    @BindView(R.id.account_register_name)
    EditText nameText;
    @BindView(R.id.account_register_account)
    EditText accountText;
    @BindView(R.id.account_register_password)
    EditText passwordText;
    @BindView(R.id.account_register_confirm)
    EditText confirmText;
    @BindView(R.id.account_register_class)
    EditText classText;
    @BindView(R.id.account_register_phone)
    EditText phoneText;
    @BindView(R.id.account_register_email)
    EditText emailText;

    private Integer sex;
    private AccountStudent student;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_student_register;
    }

    @Override
    protected void initView() {
        ViewUtils.initActionBar(this,"注 册");
    }

    @OnClick({R.id.account_register_start})
    public void onClick(View v) {
        if(!CommonUtil.isFastClick()){
            return;
        }
        if (v.getId() == R.id.account_register_start){
            String name = nameText.getText().toString();
            String account = accountText.getText().toString();
            String major = classText.getText().toString();
            String password = passwordText.getText().toString();
            String phone = phoneText.getText().toString();
            String email = emailText.getText().toString();

            if (name.isEmpty() || account.isEmpty() || password.isEmpty() || sex == null ||
                    major.isEmpty() || phone.isEmpty() || email.isEmpty()){
                showToast("请将完整填写注册信息");
                return;
            }
            if (account.length() < 12){
                showToast("学号填写有误");
                return;
            }
            if (!password.equals(confirmText.getText().toString())){
                showToast("两次密码不一致");
                return;
            }
            if (!CommonUtil.isPhone(phone)){
                showToast("电话填写有误");
                return;
            }
            if (!CommonUtil.IsEmail(email)){
                showToast("邮箱填写有误");
                return;
            }
            student = new AccountStudent(name, account, password, sex, major, phone, email);
            Map<String, String> map = new HashMap<>();
            map.put("type","2");
            map.put("account",student.getAccount());

            //先判断账号是否已存在，账号不存在则进行手机号验证
            NetUtil.getNetData("account/confirmAccount", map, new Handler(message -> {
                String data = message.getData().getString("data");
                if ("1".equals(data)){
                    showToast("账号已存在");
                    return false;
                }
                Intent intent = new Intent(Static.CONFIRM_ACTIVITY);
                intent.putExtra("data",student);
                startActivity(intent);
                return false;
            }));
        }else if(v.getId() == R.id.activity_student_register){
            hideSoftInput();
        }
    }

    @OnCheckedChanged({R.id.account_register_male, R.id.account_register_female})
    public void onCheckedChanged(CompoundButton v, boolean isChanged) {
        if(v.getId() == R.id.account_register_male) {
            if (isChanged) {
                sex = 1;
            }
        }else if(v.getId() == R.id.account_register_female) {
            if (isChanged) {
                sex = 0;
            }
        }
    }
}