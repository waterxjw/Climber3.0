package com.example.xjw.climber;

import android.content.Context;
import com.wenming.library.BackgroundUtil;


public class IsForeground {
    private static int times;
    /**
     *
     * @param context 窗口控件
     * @return FALSE->后台；TRUE->前台(锁屏)
     */
    public static  boolean determine(Context context){
        boolean isForeground = BackgroundUtil.queryUsageStats(context, "com.example.xjw.climber");
        if (isForeground == false) {
            times++;
            return  false;
        }
        else {
            return  true;
        }
    }

    public static int getTimes() {
        return times;
    }

}
