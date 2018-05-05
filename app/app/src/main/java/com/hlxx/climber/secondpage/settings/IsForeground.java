package com.hlxx.climber.secondpage.settings;

import android.content.Context;
import com.hlxx.climber.R;
import com.wenming.library.BackgroundUtil;


public class IsForeground {

    private static int times;

    /**
     * @param context 窗口控件
     * @return FALSE->后台；TRUE->前台(锁屏)
     */
    public static boolean determine(Context context) {
        boolean isForeground = BackgroundUtil.queryUsageStats(context, "com.example.xjw.climber");
        if (!isForeground) {
            times++;
            return false;
        } else {
            return true;
        }
    }

    public static int getTimes() {
        return times;
    }

    public static void setTimes(int times) {
        IsForeground.times = times;
    }

    public static int setClimbBack() {
        switch (times) {
            case 0: {
                return R.drawable.climb_back1;
            }
            case 1: {
                return R.drawable.climb_back2;
            }
            case 2: {
                return R.drawable.climb_back3;
            }
        }
        return R.drawable.climb_back3;
    }
}
