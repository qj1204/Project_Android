package com.xiaoxin.project_android.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ColorUtils;
import com.bumptech.glide.request.RequestOptions;
import com.squareup.picasso.Picasso;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.entity.Leave;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.MyApplication;
import com.xiaoxin.project_android.utils.Static;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class TeacherLeaveAdapter extends RecyclerView.Adapter<TeacherLeaveAdapter.ViewHolder> {
    private List<Leave> leaves;

    public TeacherLeaveAdapter(List<Leave> leaves) {
        this.leaves = leaves;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_teacher_member_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Leave leave = leaves.get(position);
        holder.name.setText(leave.getStudentName());
        holder.account.setText(leave.getStudentAccount());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Static.DATA_FORMAT_MONTH_DAY_MINUTE, Locale.CHINA);
        String s = simpleDateFormat.format(leave.getLeaveTime()) + "至" + simpleDateFormat.format(leave.getBackTime());
        holder.time.setText(s);

        switch (leave.getApprovalResult()){
            case 0:
                holder.state.setText("未审批");
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

        holder.leaveLinear.setVisibility(View.VISIBLE);
        holder.state.setVisibility(View.VISIBLE);
        holder.arrow.setVisibility(View.GONE);

        GlideUtil.load(MyApplication.getContext(),Static.SERVICE_PATH + leave.getStudentAvatar(), holder.avatar,new RequestOptions().circleCrop());

        holder.view.setOnClickListener(v -> {
            Intent intent = new Intent(Static.TEACHER_LEAVE);
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

        @BindView(R.id.teacher_member_item_avatar)
        public ImageView avatar;
        @BindView(R.id.member_arrow_right)
        public ImageView arrow;
        @BindView(R.id.teacher_member_item_name)
        public TextView name;
        @BindView(R.id.teacher_member_item_account)
        public TextView account;
        @BindView(R.id.member_leave_time)
        public TextView time;
        @BindView(R.id.member_leave_state)
        public TextView state;
        @BindView(R.id.layout_leave_time)
        public LinearLayout leaveLinear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this,view);
        }
    }
}