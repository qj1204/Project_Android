package com.xiaoxin.project_android.activity.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.entity.AccountTeacher;
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
public class TeacherRegisterActivity extends BaseActivity {

    @BindView(R.id.teacher_register_name)
    EditText nameText;
    @BindView(R.id.teacher_register_account)
    EditText accountText;
    @BindView(R.id.teacher_register_password)
    EditText passwordText;
    @BindView(R.id.teacher_register_confirm)
    EditText confirmText;
    @BindView(R.id.teacher_register_phone)
    EditText phoneText;
    @BindView(R.id.teacher_register_email)
    EditText emailText;

    private Integer sex;
    private AccountTeacher teacher;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teacher_register;
    }

    @Override
    protected void initView() {
        ViewUtils.initActionBar(this,"注 册");
    }

    @OnClick({R.id.teacher_register_start})
    public void onClick(View v) {
        if(!CommonUtil.isFastClick()){
            return;
        }
        if (v.getId() == R.id.teacher_register_start){
            String name = nameText.getText().toString();
            String account = accountText.getText().toString();
            String password = passwordText.getText().toString();
            String phone = phoneText.getText().toString();
            String email = emailText.getText().toString();

            if (name.isEmpty() || account.isEmpty() || password.isEmpty() || sex == null ||
                    phone.isEmpty() || email.isEmpty()){
                showToast("请将完整填写注册信息");
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
            teacher = new AccountTeacher(name, account, password, sex, phone, email);
            Map<String, String> map = new HashMap<>();
            map.put("type","1");
            map.put("account",teacher.getTeacherAccount());

            //先判断账号是否已存在，账号不存在则进行手机号验证
            NetUtil.getNetData("account/confirmAccount", map, new Handler(message -> {
                String data = message.getData().getString("data");
                if ("1".equals(data)){
                    showToast("账号已存在");
                    return false;
                }
                Intent intent = new Intent(Static.TEACHER_CONFIRM_ACTIVITY);
                intent.putExtra("data",teacher);
                startActivity(intent);
                return false;
            }));
        }else if(v.getId() == R.id.activity_teacher_register){
            hideSoftInput();
        }
    }

    @OnCheckedChanged({R.id.teacher_register_male, R.id.teacher_register_female})
    public void onCheckedChanged(CompoundButton v, boolean isChanged) {
        if(v.getId() == R.id.teacher_register_male) {
            if (isChanged) {
                sex = 1;
            }
        }else if(v.getId() == R.id.teacher_register_female) {
            if (isChanged) {
                sex = 0;
            }
        }
    }
}