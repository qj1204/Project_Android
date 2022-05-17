package com.xiaoxin.project_android.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.entity.Student;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.MyApplication;
import com.xiaoxin.project_android.utils.Static;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder> {
    private List<Student> studentList;
    private Integer courseId;

    public MemberAdapter(List<Student> studentList, Integer courseId) {
        this.studentList = studentList;
        this.courseId = courseId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_teacher_member_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = studentList.get(position);
        holder.name.setText(student.getStudentName());
        holder.account.setText(student.getStudentAccount());

        GlideUtil.load(MyApplication.getContext(),Static.SERVICE_PATH + student.getStudentAvatar(),holder.avatar,new RequestOptions().circleCrop());

        String userType = MyApplication.getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE).getString("userType","");
        if (userType.equals("1")) {
            holder.view.setOnClickListener(v -> {
                Intent intent = new Intent(Static.MEMBER_DETAIL);
                Bundle bundle = new Bundle();
                bundle.putSerializable("student", student);
                intent.putExtras(bundle);
                intent.putExtra("courseId", courseId);
                v.getContext().startActivity(intent);
            });
        } else {
            holder.arrow.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        @BindView(R.id.teacher_member_item_avatar)
        public ImageView avatar;
        @BindView(R.id.teacher_member_item_name)
        public TextView name;
        @BindView(R.id.teacher_member_item_account)
        public TextView account;
        @BindView(R.id.member_arrow_right)
        public ImageView arrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this,view);
        }
    }
}
