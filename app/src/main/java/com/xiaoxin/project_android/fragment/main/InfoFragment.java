package com.xiaoxin.project_android.fragment.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.Static;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的信息页面
 */
@SuppressLint("NonConstantResourceId")
public class InfoFragment extends Fragment {
    @BindView(R.id.info_detail_account_text)
    TextView accountName;
    @BindView(R.id.info_detail_account)
    TextView account;
    @BindView(R.id.info_detail_class)
    TextView classText;
    @BindView(R.id.info_detail_name)
    TextView name;
    @BindView(R.id.info_detail_sex)
    TextView sex;
    @BindView(R.id.info_detail_phone)
    TextView phone;
    @BindView(R.id.info_detail_email)
    TextView email;
    @BindView(R.id.info_detail_face_layout)
    LinearLayout faceLayout;
    @BindView(R.id.info_detail_class_layout)
    LinearLayout classLayout;
    @BindView(R.id.info_detail_face_prompt)
    LinearLayout promptLayout;
    @BindView(R.id.info_detail_face)
    ImageView face;
    @BindView(R.id.info_detail_avatar)
    ImageView avatar;

    private String type;
    private SharedPreferences preferences;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this,inflate);
        preferences = getActivity().getSharedPreferences("localRecord", Context.MODE_PRIVATE);
        initView();

        return inflate;
    }

    public void initView() {
        Log.d("信息页头像路径", Static.SERVICE_PATH + preferences.getString("avatar",""));
        GlideUtil.load(getContext(),Static.SERVICE_PATH + preferences.getString("avatar",""),avatar,new RequestOptions().circleCrop());

        phone.setText(preferences.getString("phone",""));
        email.setText(preferences.getString("email",""));
        sex.setText(preferences.getInt("sex",0) == 1 ? "男" : "女");
        account.setText(preferences.getString("account",""));
        name.setText(preferences.getString("name",""));
        type = preferences.getString("userType","");
        accountName.setText(type.equals("1") ? "工号" : "学号");
        if (type.equals("1")){
            accountName.setText("工号");
            faceLayout.setVisibility(View.GONE);
            classLayout.setVisibility(View.GONE);
        } else {
            accountName.setText("学号");
            classLayout.setVisibility(View.VISIBLE);
            classText.setText(preferences.getString("class",""));
            String facePath = preferences.getString("face",null);
            if (!StringUtils.isEmpty(facePath)) {
                promptLayout.setVisibility(View.GONE);
                Log.d("信息页人脸路径",Static.SERVICE_PATH + facePath);

                GlideUtil.load(getContext(),Static.SERVICE_PATH + facePath,face,new RequestOptions());
            } else {
                promptLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.info_detail_modify)
    public void onClick(View v){
        if(!CommonUtil.isFastClick()){
            return;
        }
        if(v.getId() == R.id.info_detail_modify) {
            Intent intent = new Intent(Static.MODIFY_INFO);
            startActivityForResult(intent,1);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }
}