package com.xiaoxin.project_android.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.entity.Statistics;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@SuppressLint("NonConstantResourceId")
public class TotalAdapter extends RecyclerView.Adapter<TotalAdapter.ViewHolder>{
        private List<Statistics> list;

        public TotalAdapter(List<Statistics> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_course_total_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Statistics statistics = list.get(position);
            holder.name.setText(statistics.getStudentName());
            holder.account.setText(statistics.getStudentAccount());
            holder.success.setText(String.valueOf(statistics.getSuccessCount()));
            holder.failed.setText(String.valueOf(statistics.getFailedCount()));
            holder.absent.setText(String.valueOf(statistics.getAbsentCount()));
            holder.leave.setText(String.valueOf(statistics.getLeaveCount()));

            if (position % 2 == 0){
                holder.view.setBackgroundColor(Color.WHITE);
            } else {
                holder.view.setBackgroundColor(Color.parseColor("#d8e3e7"));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            public View view;

            @BindView(R.id.total_item_name)
            public TextView name;
            @BindView(R.id.total_item_account)
            public TextView account;
            @BindView(R.id.total_item_success)
            public TextView success;
            @BindView(R.id.total_item_failed)
            public TextView failed;
            @BindView(R.id.total_item_absent)
            public TextView absent;
            @BindView(R.id.total_item_leave)
            public TextView leave;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                view = itemView;
                ButterKnife.bind(this,view);
            }
        }
    }