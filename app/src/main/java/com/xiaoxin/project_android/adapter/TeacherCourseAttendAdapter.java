package com.xiaoxin.project_android.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.entity.Attend;
import com.xiaoxin.project_android.utils.CommonUtil;
import com.xiaoxin.project_android.utils.Static;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class TeacherCourseAttendAdapter extends RecyclerView.Adapter<TeacherCourseAttendAdapter.ViewHolder> {
    private List<Attend> attendList;

    public TeacherCourseAttendAdapter(List<Attend> attendList) {
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
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            if (currentTime.before(attendList.getStartTime())){
                Toast.makeText(v.getContext(), "考勤尚未开始", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(Static.TEACHER_RECORD_DETAIL);
            Bundle bundle = new Bundle();
            bundle.putSerializable("attend",attendList);
            intent.putExtras(bundle);
            v.getContext().startActivity(intent);
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
            ButterKnife.bind(this,view);
        }
    }
}
