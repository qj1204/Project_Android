package com.xiaoxin.project_android.activity.login;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.hjq.widget.view.CountdownView;
import com.mob.MobSDK;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.entity.AccountTeacher;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.util.Map;

import butterknife.OnClick;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class TeacherConfirmActivity extends BaseActivity {
    //这里用BindView会报空指针，导致闪退
    EditText codeEdit;
    CountdownView resend;
    Button confirm;

    public AccountTeacher teacher;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teacher_confirm;
    }

    /**
     * 处理注册请求，成功则跳转到登陆页面，失败则返回注册界面
     */
    Handler registerHandler = new Handler(message -> {
        Intent intent;
        if (message.what == 1){
            showToast(message.getData().getString("data"));
            intent = new Intent(this,LoginActivity.class);
            //将这个activity放至栈底或者清空栈后再把这个activity压进栈去
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            intent = new Intent(this, TeacherRegisterActivity.class);
        }
        startActivity(intent);
        finish();
        return false;
    });

    /**
     * 处理获得验证码和提交验证码的请求，验证通过发送真正的注册请求，失败则进行提示
     */
    Handler mHandler = new Handler(msg -> {
        if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE && msg.arg2 == SMSSDK.RESULT_COMPLETE){
            showToast("验证成功！！！");
            Map<String, String> map = CommonUtil.object2Map(teacher);
            NetUtil.getNetData("account/registerTeacher", map, registerHandler);
        } else if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE && msg.arg2 == SMSSDK.RESULT_ERROR){
            showToast("验证码错误！！！");
        } else if (msg.arg1 == SMSSDK.EVENT_GET_VERIFICATION_CODE && msg.arg2 == SMSSDK.RESULT_COMPLETE){
            showToast("已发送！！！");
        } else if (msg.arg1 == SMSSDK.EVENT_GET_VERIFICATION_CODE && msg.arg2 == SMSSDK.RESULT_ERROR){
            showToast("发送次数已达上限，请更换手机号或第二天重试！！！");
        }else {
            showToast("出现错误请重试！！！");
        }
        return true;
    });

    /**
     * 短信SDK回调
     * @param event    事件code
     * @param result   结果code
     * @param data     回调的数据对象
     */
    EventHandler eh = new EventHandler(){
        @Override
        public void afterEvent(int event, int result, Object data) {
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            mHandler.sendMessage(msg);
        }
    };

    @Override
    protected void initView() {
        codeEdit = findViewById(R.id.teacher_confirm_code);
        resend = findViewById(R.id.teacher_confirm_resend);
        confirm = findViewById(R.id.teacher_confirm_confirm);

        MobSDK.submitPolicyGrantResult(true,null);

        ViewUtils.initActionBar(this,"手机号验证");
        teacher = (AccountTeacher) getIntent().getSerializableExtra("data");
        SMSSDK.registerEventHandler(eh);
    }

    @OnClick({R.id.teacher_confirm_resend, R.id.teacher_confirm_confirm})
    public void onClick(View v) {
        if(!CommonUtil.isFastClick()){
            return;
        }
        if(v.getId() == R.id.teacher_confirm_resend){
            resend.start();
            SMSSDK.getVerificationCode("86",teacher.getTeacherPhone());
        } else if (v.getId() == R.id.teacher_confirm_confirm){
            if (codeEdit.getText().toString().isEmpty()){
                showToast("验证码不可为空");
            } else {
                SMSSDK.submitVerificationCode("86", teacher.getTeacherPhone(),codeEdit.getText().toString());
            }
        } else if(v.getId() == R.id.activity_teacher_confirm){
            hideSoftInput();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

}