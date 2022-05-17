package com.xiaoxin.project_android.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.request.RequestOptions;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.ViewUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class ShowImageDialog extends Dialog {
    @BindView(R.id.image1)
    ImageView imageView;

    public ShowImageDialog(@NonNull Context context) {
        super(context, R.style.Dialog_Msg);
        setCanceledOnTouchOutside(true);
        setCancelable(true);

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_show_image, null);
        ButterKnife.bind(this,view);
        setContentView(view);

        setViewHeightByWidth();
    }

    @OnClick({R.id.image1, R.id.image_layout})
    public void onClick(){
        dismiss();
    }

    public void setImage(String path){
        GlideUtil.load(getContext(),path,imageView,new RequestOptions());
    }

    public void setViewHeightByWidth() {
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {

				int width = imageView.getMeasuredWidth();

                android.view.ViewGroup.LayoutParams lp = imageView.getLayoutParams();
                lp.height = width;
                imageView.setLayoutParams(lp);

                final ViewTreeObserver vto1 = imageView.getViewTreeObserver();
                vto1.removeOnPreDrawListener(this);

                return true;
            }
        };
        vto.addOnPreDrawListener(preDrawListener);
    }

    @Override
    public void show() {
        super.show();
        ViewUtils.show(getWindow());
    }
}
