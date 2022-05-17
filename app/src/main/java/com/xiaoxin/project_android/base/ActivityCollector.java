package com.xiaoxin.project_android.base;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityCollector {

    private static List<Activity> activities = new ArrayList<>();

    /**
     * 向List中添加一个活动
     *
     * @param activity 活动
     */
    public static void addActivity(Activity activity) {
        activities.add(activity);
    }
 
    /**
     * 从List中移除活动
     *
     * @param activity 活动
     */
    public static void removeActivity(Activity activity) {
        activities.remove(activity);
    }
 
    /**
     * 将List中存储的活动全部销毁掉
     */
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
