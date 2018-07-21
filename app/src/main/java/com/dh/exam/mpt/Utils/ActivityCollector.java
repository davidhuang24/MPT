package com.dh.exam.mpt.Utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 *活动收集器,用于完全退出APP
 *
 *@author DavidHuang  at 下午3:33 18-5-31
 */
public class ActivityCollector {

    public static List<Activity> activities=new ArrayList<>();
    public static void addActivity(Activity activity){
        activities.add(activity);
    }
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }
    public static void finishAll(){
        for(Activity activity: activities){
            if(!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
