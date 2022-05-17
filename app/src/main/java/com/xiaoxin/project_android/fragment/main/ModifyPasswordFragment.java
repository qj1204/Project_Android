package com.xiaoxin.project_android.fragment.main;

import static com.blankj.utilcode.util.ResourceUtils.getDrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.ResourceUtils;
import com.mob.MobSDK;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.NetUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

@SuppressLint("NonConstantResourceId")
public class ModifyPasswordFragment extends Fragment {
    @BindView(R.id.modify_password_phone1)
    EditText phoneEdit;
    @BindView(R.id.modify_password_old_password1)
    EditText oldPasswordEdit;
    @BindView(R.id.modify_password_new_password1)
    EditText newPasswordEdit;
    @BindView(R.id.modify_password_code)
    EditText codeEdit;
    @BindView(R.id.modify_password_send_confirm)
    TextView resend;

    Unbinder unbinder;

    public int time = 60;
    public Timer timer;
    private String phone = "";
    private String password;

    private boolean sendCode = false;

    private onModifySuccessListener successListener;

    public void setSuccessListener(onModifySuccessListener successListener) {
        this.successListener = successListener;
    }

    Handler mHandler = new Handler(msg -> {
        if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE && msg.arg2 == SMSSDK.RESULT_COMPLETE){
            Toast.makeText(getContext(), "验证成功！！！", Toast.LENGTH_SHORT).show();
            Map<String, String> map = new HashMap<>();
            map.put("teacherId",getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("id",""));
            map.put("password",password);
            NetUtil.getNetData("teacher/update",map,new Handler(msg1 -> {
                Toast.makeText(getContext(), msg.getData().getString("msg"), Toast.LENGTH_SHORT).show();
                if (msg1.what == 1){
                    //跳转页面
                    SharedPreferences.Editor localRecord = getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).edit();
                    localRecord.putString("password",password);
                    localRecord.apply();
                    if (successListener != null){
                        successListener.onModifySuccess();
                    }
                }
                return false;
            }));
        } else if (msg.arg1 == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE && msg.arg2 == SMSSDK.RESULT_ERROR){
            Toast.makeText(getContext(), "验证码错误！！！", Toast.LENGTH_SHORT).show();
        }
        return true;
    });

    Handler handler = new Handler(msg -> {
        if (msg.what == 1){
            resend.setText("重新发送");
            resend.setBackground(getDrawable(R.drawable.style_ellipse_blue));
            resend.setEnabled(true);
            timer.cancel();
        } else {
            resend.setText("重新发送(" + time + "s)");
            resend.setBackground(getDrawable(R.drawable.style_ellipse_resend));
            resend.setEnabled(false);
        }
        return false;
    });

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobSDK.submitPolicyGrantResult(true,null);

        SMSSDK.registerEventHandler(eh);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_modify_password, container, false);
        unbinder = ButterKnife.bind(this,view);
        return view;
    }

    @OnClick({R.id.modify_password_send_confirm, R.id.modify_password_send})
    public void onClick(View view){
        if(!CommonUtil.isFastClick()){
            return;
        }
        switch (view.getId()){
            case R.id.modify_password_send_confirm:
                getConfirmCode(view);
                break;
            case R.id.modify_password_send:
                if (!CommonUtil.isPhone(phone)){
                    Toast.makeText(view.getContext(), "手机号错误", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!phone.equals(getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("phone",""))){
                    Toast.makeText(view.getContext(), "不是预留手机号", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (oldPasswordEdit.getText().toString().length() < 6){
                    Toast.makeText(view.getContext(), "密码长度低于6", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!oldPasswordEdit.getText().toString().equals(newPasswordEdit.getText().toString())){
                    Toast.makeText(view.getContext(), "两次密码输入不一致", Toast.LENGTH_SHORT).show();
                    break;
                }
                if (!sendCode){
                    Toast.makeText(view.getContext(), "未获取验证码", Toast.LENGTH_SHORT).show();
                    break;
                }
                password = newPasswordEdit.getText().toString();
                modifyPassword(view);
                break;
        }
    }

    private void modifyPassword(View view) {
        if (codeEdit.getText().toString().isEmpty()){
            Toast.makeText(view.getContext(), "验证码不可为空", Toast.LENGTH_SHORT).show();
        } else {
            SMSSDK.submitVerificationCode("86",phone,codeEdit.getText().toString());
        }
    }

    private void getConfirmCode(View view) {
        phone = phoneEdit.getText().toString();
        if (!CommonUtil.isPhone(phone)){
            Toast.makeText(view.getContext(), "手机号格式错误", Toast.LENGTH_SHORT).show();
            return;
        }
        time = 60;
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                if (time != 0){
                    time--;
                    message.what = 0;
                } else {
                    message.what = 1;
                }
                handler.sendMessage(message);
            }
        }, 0, 1000);
        sendCode = true;
        SMSSDK.getVerificationCode("86",phone);
    }

    public interface onModifySuccessListener{
        void onModifySuccess();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}