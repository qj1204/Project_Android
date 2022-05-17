package com.xiaoxin.project_android.adapter;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.SizeUtils;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.entity.Course;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.MyApplication;
import com.xiaoxin.project_android.utils.Static;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
     private List<Course> courseList;

     public CourseAdapter(List<Course> courseList) {
         this.courseList = courseList;
     }

     @NonNull
     @Override
     public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_course_item, parent, false);
         return new ViewHolder(view);
     }

     @SuppressLint("CheckResult")
     @Override
     public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         Course course = courseList.get(position);
         holder.lecturer.setText(course.getTeacherName());
         holder.name.setText(course.getCourseName());

         RequestOptions options = new RequestOptions();
         options.transform(new RoundedCornersTransformation(30,0));
         Log.d("课程列表头像路径",Static.SERVICE_PATH + course.getCourseAvatar());
         GlideUtil.load(MyApplication.getContext(),Static.SERVICE_PATH + course.getCourseAvatar(),holder.img,options);

         holder.view.setOnClickListener(v -> {
             SharedPreferences localRecord = v.getContext().getSharedPreferences("localRecord", Context.MODE_PRIVATE);
             String userType = localRecord.getString("userType", "");
             Intent intent = new Intent();
             if (userType.equals("2")){
                 intent.setAction(Static.STUDENT_COURSE_DETAIL);
             }else {
                 intent.setAction(Static.TEACHER_COURSE_DETAIL);
             }
             Bundle bundle = new Bundle();
             bundle.putSerializable("course",course);
             intent.putExtras(bundle);
             v.getContext().startActivity(intent);
         });
     }

     @Override
     public int getItemCount() {
         return courseList.size();
     }

     @SuppressLint("NonConstantResourceId")
     static class ViewHolder extends RecyclerView.ViewHolder {
         public View view;

         @BindView(R.id.course_list_avatar)
         public ImageView img;
         @BindView(R.id.course_list_course_name)
         public TextView name;
         @BindView(R.id.course_list_teacher_name)
         public TextView lecturer;

         public ViewHolder(@NonNull View itemView) {
             super(itemView);
             view = itemView;
             ButterKnife.bind(this, view);
         }
     }
 }
