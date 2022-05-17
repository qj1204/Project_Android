package com.xiaoxin.project_android.fragment.teacher;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxin.project_android.entity.Attend;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class TeacherCourseAttendViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Attend>> attendList;

    public MutableLiveData<List<Attend>> getAttendList() {
        if (attendList == null){
            attendList = new MutableLiveData<>();
        }
        return attendList;
    }

    public void updateAttend(String s){
        List<Attend> lists = new ArrayList<>();
        Attend Attend;
        JSONArray objects = JSONObject.parseArray(s);
        for (int i = 0; i < objects.size(); i++) {
            JSONObject object = (JSONObject) objects.get(i);
            Timestamp start = object.getTimestamp("attendStart");
            Timestamp end =  object.getTimestamp("attendEnd");

            Timestamp current = new Timestamp(System.currentTimeMillis());
            String state = "进行中";
            if (current.before(start)){
                state = "未开始";
            } else if (current.after(end)){
                state = "已结束";
            }
            Integer type = object.getInteger("attendType");
            Attend = new Attend(object.getInteger("attendId"),object.getString("courseId"),start,end,
                    object.getDouble("attendLongitude"),object.getDouble("attendLatitude"),
                    object.getString("attendLocation"),state,type);
            Attend.setGesture(type == 2 ? object.getString("attendGesture") : null);
            lists.add(Attend);
        }
        attendList.setValue(lists);
    }
}