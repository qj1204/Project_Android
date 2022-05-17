package com.xiaoxin.project_android.activity.student;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.ColorUtils;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.base.BaseActivity;
import com.xiaoxin.project_android.entity.Attend;
import com.xiaoxin.project_android.entity.Record;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.Static;
import com.xiaoxin.project_android.utils.ViewUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

@SuppressLint("NonConstantResourceId")
public class StudentRecordActivity extends BaseActivity {
    @BindView(R.id.student_record_name)
    TextView nameText;
    @BindView(R.id.student_record_account)
    TextView accountText;
    @BindView(R.id.student_record_location)
    TextView locationText;
    @BindView(R.id.student_record_start_time)
    TextView startText;
    @BindView(R.id.student_record_end_time)
    TextView endText;
    @BindView(R.id.student_record_my_time)
    TextView myTimeText;
    @BindView(R.id.student_record_my_location)
    TextView myLocationText;
    @BindView(R.id.student_record_detail_layout)
    LinearLayout detailLayout;
    @BindView(R.id.student_record_result)
    TextView resultText;
    @BindView(R.id.student_record_face)
    ImageView faceImage;
    @BindView(R.id.face_layout)
    LinearLayout faceLayout;

    private Attend attend;
    private Record record;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_student_record;
    }

    @Override
    protected void initView() {
        ViewUtils.initActionBar(this,"考勤记录");

        Intent intent = getIntent();
        attend = (Attend)intent.getExtras().getSerializable("attend");
        record = getRecord(intent.getExtras().getString("record"));
        initData();
    }

    private void initData() {
        nameText.setText(record.getRecordName());
        accountText.setText(record.getRecordAccount());

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        locationText.setText(attend.getLocation());
        startText.setText(format.format(attend.getStartTime()));
        endText.setText(format.format(attend.getEndTime()));

        if (record.getRecordResult().equals("0") || record.getRecordResult().equals("1")){
            if (record.getRecordResult().equals("0")) {
                resultText.setText("签到成功");
                resultText.setTextColor(ColorUtils.getColor(R.color.green));
            } else {
                resultText.setText("签到失败");
                resultText.setTextColor(ColorUtils.getColor(R.color.cancel_red));
            }

            myLocationText.setText(record.getRecordLocation() == null ? "--" : record.getRecordLocation());
            myTimeText.setText(record.getRecordTime() == null ? "--" : format.format(record.getRecordTime()));
            if (record.getRecordPhoto() != null) {
                Log.d("NET-->photo:", Static.SERVICE_PATH + record.getRecordPhoto());
                faceLayout.setVisibility(View.VISIBLE);
                GlideUtil.load(this,Static.SERVICE_PATH + record.getRecordPhoto(),faceImage,new RequestOptions());
            } else {
                faceLayout.setVisibility(View.GONE);
            }
        } else {
            if (record.getRecordResult().equals("3")){
                resultText.setText("已请假");
                resultText.setTextColor(ColorUtils.getColor(R.color.button_blue));
            } else {
                resultText.setText("缺勤");
                resultText.setTextColor(ColorUtils.getColor(R.color.purple_700));
            }
            detailLayout.setVisibility(View.GONE);
        }
    }

    private Record getRecord(String s){
        SharedPreferences preferences = getSharedPreferences("localRecord", MODE_PRIVATE);
        JSONObject object = JSONObject.parseObject(s);
        Timestamp timestamp = null;
        if (object.getTimestamp("recordTime") != null){
            timestamp = new Timestamp((object.getTimestamp("recordTime").getTime() + 8000 * 3600));
        }
        Record record = new Record(preferences.getString("avatar",""),timestamp,
                preferences.getString("name",""), preferences.getString("account",""),
                object.getString("recordResult"),object.getString("recordLocation"));
        record.setRecordPhoto(object.getString("recordPhoto"));
        return record;
    }

    @OnClick({R.id.activity_student_record})
    public void onClick(View v) {
        if(!CommonUtil.isFastClick()){
            return;
        }
        if(v.getId() == R.id.activity_student_record){
            hideSoftInput();
        }
    }
}