package com.example.xjw.climber;

import android.content.Context;
import com.wenming.library.BackgroundUtil;


class IsForeground {
    private static int times;
    /**
     *
     * @param context 窗口控件
     * @return FALSE->后台；TRUE->前台(锁屏)
     */
    static  boolean determine(Context context){
        boolean isForeground = BackgroundUtil.queryUsageStats(context, "com.example.xjw.climber");
        if (!isForeground) {
            times++;
            return  false;
        }
        else {
            return  true;
        }
    }

    static int getTimes() {
        return times;
    }

}
