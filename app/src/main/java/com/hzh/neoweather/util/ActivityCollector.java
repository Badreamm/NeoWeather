package com.hzh.neoweather.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * activity管理类 可以随时随地退出所有activity
 */
public class ActivityCollector {
    public static List<Activity> activities = new ArrayList<>();

    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    public static void finishAll(){
        for (Activity activity : activities) {
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
        activities.clear();
    }
}
