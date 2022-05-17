package com.xiaoxin.project_android.fragment.teacher;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xiaoxin.project_android.entity.Statistics;

import java.util.ArrayList;
import java.util.List;

public class TeacherCourseTotalViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Statistics>> statisticsList;

    public MutableLiveData<List<Statistics>> getStatisticsList() {
        if (statisticsList == null){
            statisticsList = new MutableLiveData<>();
        }
        return statisticsList;
    }

    public void updateStatistics(String s){
        List<Statistics> list = new ArrayList<>();
        JSONArray array = JSONArray.parseArray(s);
        Statistics statistics;
        for (int i = 0; i < array.size(); i++) {
            JSONObject object = array.getJSONObject(i);
            statistics = new Statistics(object.getString("studentName"),object.getString("studentAccount"),
                    object.getInteger("successCount"),object.getInteger("failedCount"),
                    object.getInteger("absentCount"),object.getInteger("leaveCount"));
            list.add(statistics);
        }
        statisticsList.setValue(list);
    }
}