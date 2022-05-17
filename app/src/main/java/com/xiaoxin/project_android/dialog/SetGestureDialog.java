package com.xiaoxin.project_android.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.ihsg.patternlocker.OnPatternChangeListener;
import com.github.ihsg.patternlocker.PatternLockerView;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class SetGestureDialog extends Dialog {
    @BindView(R.id.locker)
    PatternLockerView lockerView;
    @BindView(R.id.gesture_button_layout)
    LinearLayout buttonLayout;

    private String rightGesture;
    private String currentGesture;

    private onYesClickedListener yesClickedListener;

    public void setYesClickedListener(onYesClickedListener yesClickedListener) {
        this.yesClickedListener = yesClickedListener;
    }

    public void setRightGesture(String rightGesture) {
        this.rightGesture = rightGesture;
        buttonLayout.setVisibility(View.GONE);
    }

    public SetGestureDialog(@NonNull Context context) {
        super(context, R.style.Dialog_Msg);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_set_gesture, null);
        ButterKnife.bind(this,inflate);
        setContentView(inflate);

        lockerView.setOnPatternChangedListener(new OnPatternChangeListener() {
            @Override
            public void onStart(@NotNull PatternLockerView patternLockerView) {
                lockerView.updateStatus(false);
            }

            @Override
            public void onChange(@NotNull PatternLockerView patternLockerView, @NotNull List<Integer> list) {

            }

            @Override
            public void onComplete(@NotNull PatternLockerView patternLockerView, @NotNull List<Integer> list) {
                String s = CommonUtil.list2String(list);
                if (rightGesture == null) {
                    currentGesture = s;
                } else {
                    boolean right = s.equals(rightGesture);
                    lockerView.updateStatus(!right);
                    Toast.makeText(context, "手势" + (right ? "正确" : "错误"), Toast.LENGTH_SHORT).show();
                    if (right) {
                        yesClickedListener.yesClicked();
                    }
                }
            }

            @Override
            public void onClear(@NotNull PatternLockerView patternLockerView) {

            }
        });
    }

    @OnClick({R.id.yes, R.id.no})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.yes:
                if (currentGesture.length() <= 4){
                    Toast.makeText(view.getContext(), "点数请勿低于5", Toast.LENGTH_SHORT).show();
                    break;
                }
                yesClickedListener.yesClicked(currentGesture);
                break;
            case R.id.no:
                dismiss();
                break;
        }
    }

    public interface onYesClickedListener{
        void yesClicked(String list);
        void yesClicked();
    }

    @Override
    public void show() {
        super.show();
        ViewUtils.show(getWindow());
    }
}
