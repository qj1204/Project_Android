package com.xiaoxin.project_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.StringUtils;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.dialog.LoadingDialog;
import com.xiaoxin.project_android.entity.Attend;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class StudentCourseAttendAdapter extends RecyclerView.Adapter<StudentCourseAttendAdapter.ViewHolder>{
    private List<Attend> attendList;

    public StudentCourseAttendAdapter(List<Attend> attendList) {
        this.attendList = attendList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_attend_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Attend attendList = this.attendList.get(position);
        String method = attendList.getType() == 0 ? "位置定位" : attendList.getType() == 1 ? "人脸识别" : "手势签到";
        holder.method.setText(method);

        SimpleDateFormat format = new SimpleDateFormat(Static.DATE_FORMAT_MINUTE, Locale.CHINA);
        holder.startText.setText(format.format(attendList.getStartTime()));
        long time = attendList.getEndTime().getTime() - attendList.getStartTime().getTime();
        holder.duration.setText(CommonUtil.long2String(time));
        holder.state.setText(attendList.getState());

        holder.view.setOnClickListener(v -> {
            LoadingDialog loadingDialog = new LoadingDialog(v.getContext());
            loadingDialog.setTitle("考勤");
            if (attendList.getState().equals("未开始")) {
                loadingDialog.setMessage("当前考勤任务未开始！");
                loadingDialog.showSingleButton();
                loadingDialog.show();
            } else {
                loadingDialog.setMessage(StringUtils.getString(R.string.wait_message));
                loadingDialog.show();
                Map<String, String> map = new HashMap<>();
                map.put("student_id", v.getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("id", ""));
                map.put("attend_id", String.valueOf(attendList.getAttendId()));
                NetUtil.getNetData("record/findRecordByMap", map, new Handler(msg -> {
                    if (msg.what == 1) {
                        loadingDialog.dismiss();
                        String data = msg.getData().getString("data");
                        JSONArray array = JSON.parseArray(data);
                        JSONObject jsonObject = array.getJSONObject(0);
                        String recordResult = jsonObject.getString("recordResult");

                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        if (attendList.getState().equals("进行中")) {
                            intent.setAction(recordResult.equals("0") || recordResult.equals("3") ? Static.STUDENT_RECORD : Static.STUDENT_DO_RECORD);
                        } else {
                            intent.setAction(Static.STUDENT_RECORD);
                        }
                        bundle.putSerializable("attend", attendList);
                        bundle.putString("record", jsonObject.toJSONString());
                        intent.putExtras(bundle);
                        v.getContext().startActivity(intent);

                    } else {
                        loadingDialog.setMessage(msg.getData().getString("msg"));
                        loadingDialog.showSingleButton();
                    }
                    return false;
                }));
            }
        });
    }

    @Override
    public int getItemCount() {
        return attendList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        @BindView(R.id.attend_item_method)
        public TextView method;
        @BindView(R.id.attend_item_start)
        public TextView startText;
        @BindView(R.id.attend_item_duration)
        public TextView duration;
        @BindView(R.id.attend_item_current_state)
        public TextView state;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);
        }
    }
}
