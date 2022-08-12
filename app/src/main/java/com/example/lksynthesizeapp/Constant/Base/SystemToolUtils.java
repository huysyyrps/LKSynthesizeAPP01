package com.example.lksynthesizeapp.Constant.Base;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by dell on 2017/4/24.
 */

public class SystemToolUtils {
    /**
     * @方法说明:判断当前应用程序是否后台运行
     * @方法名称:isBackground
     * @param context
     * @return
     * @返回值:boolean
     */
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                // 后台运行
// 前台运行
                return appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_BACKGROUND;
            }
        }
        return false;
    }

}
