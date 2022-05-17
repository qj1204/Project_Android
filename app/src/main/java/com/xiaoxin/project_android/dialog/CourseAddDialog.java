package com.xiaoxin.project_android.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.StringUtils;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class CourseAddDialog extends Dialog {
    @BindView(R.id.course_add_code)
    EditText codeText;

    private final String studentId;

    public CourseAddDialog(@NonNull Context context, String studentId) {
        super(context, R.style.Dialog_Msg);
        this.studentId = studentId;

        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.dialog_course_add, null);
        ButterKnife.bind(this,inflate);
        setContentView(inflate);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @OnClick({R.id.yes, R.id.no})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.yes:
                LoadingDialog loadingDialog = new LoadingDialog(v.getContext());
                loadingDialog.setMessage(StringUtils.getString(R.string.wait_message));
                loadingDialog.setTitle("加入课程");
                loadingDialog.show();

                String code = codeText.getText().toString();
                if(StringUtils.isEmpty(code)){
                    loadingDialog.setMessage("请输入课程码");
                    loadingDialog.showSingleButton();
                    return;
                }else if (code.length() > 0 && code.length() < 6){
                    loadingDialog.setMessage("课程码输入错误");
                    loadingDialog.showSingleButton();
                    return;
                }

                Map<String, String> map = new HashMap<>();
                map.put("code",code);
                NetUtil.getNetData("course/findCourseByCode",map,new Handler(msg -> {
                    if (msg.what == 1){
                        String data = msg.getData().getString("data");
                        if (data == null){
                            loadingDialog.setMessage("未查询到该课程");
                            loadingDialog.showSingleButton();
                        } else {
                            Intent intent = new Intent(Static.STUDENT_COURSE_CONFIRM);
                            intent.putExtra("data",data);
                            v.getContext().startActivity(intent);
                            loadingDialog.dismiss();
                            dismiss();
                        }
                    } else {
                        loadingDialog.setMessage(msg.getData().getString("msg"));
                        loadingDialog.showSingleButton();
                    }
                    return false;
                }));
                break;
            case R.id.no:
                dismiss();
                break;
        }
    }

    @Override
    public void show() {
        super.show();
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height= ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }
}
