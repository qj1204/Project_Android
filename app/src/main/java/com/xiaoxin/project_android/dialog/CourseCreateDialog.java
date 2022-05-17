package com.xiaoxin.project_android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.MyApplication;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;

import java.util.HashMap;
import java.util.Map;

public class CourseCreateDialog extends Dialog {
    private TextView yes,no,chooseImage,useDefault;
    private EditText nameEdit,introduceEdit;
    public ImageView previewImage;
    public TextView imageState;
    private onChooseClickListener chooseClickListener;

    private String imgPath;
    private LoadingDialog loadingDialog;

    public CourseCreateDialog(@NonNull Context context) {
        super(context, R.style.BottomDialog);
    }

    public interface onChooseClickListener{public void onChooseClick();}

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public void setChooseClickListener(onChooseClickListener chooseClickListener) {
        this.chooseClickListener = chooseClickListener;
    }

    Handler createHandler = new Handler(message -> {
        if (message.what == 1){
            this.dismiss();
            loadingDialog.setMessage("课程验证码为：" + message.getData().getString("data"));
        } else {
            loadingDialog.setMessage(message.getData().getString("data"));
        }
        loadingDialog.showSingleButton();
        return false;
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_course_create);
        setCanceledOnTouchOutside(false);
        loadingDialog = new LoadingDialog(getContext());

        initView();

        initEvent();
    }

    private void initEvent() {
        no.setOnClickListener(v -> {
            this.dismiss();
        });

        yes.setOnClickListener(v -> {
            //执行创建课程操作
            String name = nameEdit.getText().toString();
            String introduce = introduceEdit.getText().toString();
            if (name.isEmpty() || introduce.isEmpty()){
                Toast.makeText(v.getContext(), "请补全信息", Toast.LENGTH_SHORT).show();
                return;
            } else if (imgPath == null){
                Toast.makeText(v.getContext(), "请选择课程封面", Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences preferences = MyApplication.getContext().getSharedPreferences("localRecord",Context.MODE_PRIVATE);
            String id = preferences.getString("id", "");

            Map<String,String> map = new HashMap<>();
            map.put("teacherId",id);
            map.put("name",name);
            map.put("avatar",imgPath);
            //Log.d("imgPath",imgPath);
            map.put("introduce",introduce);
            NetUtil.getNetData("course/addCourse",map,createHandler);
            loadingDialog.setTitle("创建课程");
            loadingDialog.setMessage(StringUtils.getString(R.string.wait_message));
            loadingDialog.show();
        });

        useDefault.setOnClickListener(v -> {
            imgPath = "image/avatars/course-default.png";
            Toast.makeText(v.getContext(), "使用默认头像", Toast.LENGTH_SHORT).show();

            Glide.with(v.getContext())
                    .load(Static.SERVICE_PATH + imgPath)
                    .placeholder(R.drawable.load_img)
                    .error(R.drawable.ic_net_error)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            imageState.setTextColor(ColorUtils.getColor(R.color.cancel_red));
                            imageState.setText("网络异常！图片无法加载");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            imageState.setTextColor(ColorUtils.getColor(R.color.green));
                            imageState.setText("选取成功");
                            return false;
                        }
                    })
                    .into(previewImage);
        });

        chooseImage.setOnClickListener(v -> {
            if (chooseClickListener != null){
                chooseClickListener.onChooseClick();
            }
        });

        introduceEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                KeyboardUtils.showSoftInput();
            } else {
                KeyboardUtils.hideSoftInput(v);
            }
        });

        nameEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                KeyboardUtils.showSoftInput();
            } else {
                KeyboardUtils.hideSoftInput(v);
            }
        });
    }

    private void initView() {
        yes = findViewById(R.id.course_register_yes);
        no = findViewById(R.id.course_register_no);
        nameEdit = findViewById(R.id.course_register_name);
        introduceEdit = findViewById(R.id.course_register_introduce);
        chooseImage = findViewById(R.id.course_register_avatar_choose);
        useDefault = findViewById(R.id.course_register_avatar_default);
        previewImage = findViewById(R.id.course_register_avatar_preview);
        imageState = findViewById(R.id.course_register_avatar_state);
    }

    @Override
    public void show() {
        super.show();
        /**
         * 设置宽度全屏
         */
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width= ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height= ViewGroup.LayoutParams.MATCH_PARENT;
        getWindow().getDecorView().setPadding(0, 0, 0, 0);
        getWindow().setAttributes(layoutParams);
    }
}
