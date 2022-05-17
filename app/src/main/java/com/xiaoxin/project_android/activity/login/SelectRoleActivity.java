package com.xiaoxin.project_android.activity.login;

import android.content.Intent;
import android.view.View;

import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.Static;
import com.xiaoxin.project_android.utils.ViewUtils;

import butterknife.OnClick;

public class SelectRoleActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_role;
    }

    @Override
    protected void initView() {
        ViewUtils.initActionBar(this,"选择角色");
    }

    @OnClick({R.id.student_role,R.id.teacher_role})
    public void onClick(View v){
        if(!CommonUtil.isFastClick()){
            return;
        }
        Intent intent = new Intent();
        if(v.getId() == R.id.student_role){
            intent.setAction(Static.STUDENT_LOGIN_REGISTER);
            startActivity(intent);
        } else if(v.getId() == R.id.teacher_role){
            intent.setAction(Static.TEACHER_LOGIN_REGISTER);
            startActivity(intent);
        }
    }
}