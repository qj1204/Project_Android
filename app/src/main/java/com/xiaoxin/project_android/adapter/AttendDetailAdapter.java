package com.xiaoxin.project_android.adapter;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ColorUtils;
import com.bumptech.glide.request.RequestOptions;
import com.xiaoxin.project_android.R;
import com.xiaoxin.project_android.dialog.RecordModifyDialog;
import com.xiaoxin.project_android.dialog.ShowImageDialog;
import com.xiaoxin.project_android.entity.Record;
import com.xiaoxin.project_android.utils.GlideUtil;
import com.xiaoxin.project_android.utils.NetUtil;
import com.xiaoxin.project_android.utils.Static;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.ui.companent.CircleImageView;

@SuppressLint("NonConstantResourceId")
public class AttendDetailAdapter extends RecyclerView.Adapter<AttendDetailAdapter.ViewHolder> {
    private List<Record> records;
    private Integer type;
    private onResultChangedListener resultChangedListener;

    public void setResultChangedListener(onResultChangedListener resultChangedListener) {
        this.resultChangedListener = resultChangedListener;
    }

    public interface onResultChangedListener{
        void onResultChanged();
    }

    public AttendDetailAdapter(List<Record> records, Integer type) {
        this.records = records;
        this.type = type;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_record_item, parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Record record = records.get(position);
        holder.name.setText(record.getRecordName());
        holder.account.setText(record.getRecordAccount());

        if (record.getRecordTime() != null){
            holder.time.setText(new SimpleDateFormat(Static.DATE_FORMAT_MINUTE).format(record.getRecordTime()));
            holder.location.setText(record.getRecordLocation());
        } else {
            holder.time.setText("---");
            holder.location.setText("---");
        }

        String temp = "";
        switch (record.getRecordResult()){
            case "0":
                temp = "成 功";
                holder.result.setTextColor(ColorUtils.getColor(R.color.green));
                break;
            case "1":
                temp = "失 败";
                holder.result.setTextColor(ColorUtils.getColor(R.color.cancel_red));
                break;
            case "2":
                temp = "缺 勤";
                holder.result.setTextColor(ColorUtils.getColor(R.color.purple_700));
                break;
            case "3":
                temp = "请 假";
                holder.result.setTextColor(ColorUtils.getColor(R.color.soft_blue));
                break;
        }
        holder.result.setText(temp);

        GlideUtil.load(holder.view.getContext(),Static.SERVICE_PATH + record.getAvatarUrl(),holder.avatar,new RequestOptions().circleCrop());

        if (type == 1 && (record.getRecordResult().equals("1") || record.getRecordResult().equals("0"))) {
            holder.view.setOnClickListener(v -> {
                ShowImageDialog imageDialog = new ShowImageDialog(v.getContext());
                imageDialog.setImage(Static.SERVICE_PATH + record.getRecordPhoto());
                imageDialog.show();
            });
        }

        holder.view.setLongClickable(true);
        final String initial = temp;
        holder.view.setOnLongClickListener(v -> {
            RecordModifyDialog dialog = new RecordModifyDialog(v.getContext(), initial);
            dialog.setYesClickedListener(() -> {
                String result = getResult(dialog.result);
                Map<String, String> map = new HashMap<>();
                map.put("attendId",record.getAttendId());
                map.put("studentId",record.getStudentId());
                map.put("result",result);
                NetUtil.getNetData("record/updateRecord",map,new Handler(msg -> {
                    if (msg.what == 1){
                        record.setRecordResult(result);
                        dialog.setOnDismissListener(dialog1 -> resultChangedListener.onResultChanged());
                    }
                    Toast.makeText(v.getContext(), msg.getData().getString("data"), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    return false;
                }));
            });
            dialog.show();
            return false;
        });

    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;

        @BindView(R.id.record_item_name)
        public TextView name;
        @BindView(R.id.record_item_account)
        public TextView account;
        @BindView(R.id.record_item_time)
        public TextView time;
        @BindView(R.id.record_item_location)
        public TextView location;
        @BindView(R.id.record_item_result)
        public TextView result;
        @BindView(R.id.record_item_avatar)
        public ImageView avatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            ButterKnife.bind(this, view);
        }
    }

    public String getResult(String s){
        switch (s){
            case "成 功":
                return "0";
            case "缺 勤":
                return "2";
            case "请 假":
                return "3";
        }
        return "";
    }
}
