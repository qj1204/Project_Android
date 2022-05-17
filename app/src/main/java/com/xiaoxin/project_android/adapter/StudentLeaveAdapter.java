package com.xiaoxin.project_android.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ColorUtils;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.entity.Leave;
import com.xiaoxin.project_android.utils.Static;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class StudentLeaveAdapter extends RecyclerView.Adapter<StudentLeaveAdapter.ViewHolder>{
        private List<Leave> leaves;

        public StudentLeaveAdapter(List<Leave> leaves) {
            this.leaves = leaves;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_student_leave_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Leave leave = leaves.get(position);
            SimpleDateFormat format = new SimpleDateFormat(Static.DATE_FORMAT_DAY, Locale.CHINA);

            holder.start.setText(format.format(leave.getLeaveTime()));
            holder.end.setText(format.format(leave.getBackTime()));
            Log.d("leaveTime", String.valueOf(leave.getLeaveTime().getTime()));
            Log.d("backTime", String.valueOf(leave.getBackTime().getTime()));
            int i = (int) Math.ceil((double) (leave.getBackTime().getTime() - leave.getLeaveTime().getTime()) / (1000 * 3600));
            holder.duration.setText(i + "小时");

            switch (leave.getApprovalResult()){
                case 0:
                    holder.state.setText("审批中");
                    holder.state.setTextColor(ColorUtils.getColor(R.color.soft_blue));
                    break;
                case 1:
                    holder.state.setText("不批准");
                    holder.state.setTextColor(ColorUtils.getColor(R.color.cancel_red));
                    break;
                case 2:
                    holder.state.setText("批准");
                    holder.state.setTextColor(ColorUtils.getColor(R.color.green));
                    break;
            }

            holder.view.setOnClickListener(v -> {
                Intent intent = new Intent(Static.STUDENT_LEAVE);
                Bundle bundle = new Bundle();
                bundle.putSerializable("leave",leave);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return leaves.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            public View view;

            @BindView(R.id.student_leave_item_start)
            public TextView start;
            @BindView(R.id.student_leave_item_end)
            public TextView end;
            @BindView(R.id.student_leave_item_duration)
            public TextView duration;
            @BindView(R.id.student_leave_item_leave_state)
            public TextView state;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                view = itemView;
                ButterKnife.bind(this, view);
            }
        }
    }